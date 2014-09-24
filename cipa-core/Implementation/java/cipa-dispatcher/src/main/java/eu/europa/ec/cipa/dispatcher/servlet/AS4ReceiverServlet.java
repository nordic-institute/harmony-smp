package eu.europa.ec.cipa.dispatcher.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.HostnameVerifier;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.util.encoders.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.domibus.ebms3.config.PModePool;
import eu.domibus.ebms3.config.Producer;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus.AS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.ocsp.OCSPValidator;
import eu.europa.ec.cipa.dispatcher.util.KeystoreUtil;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

public class AS4ReceiverServlet extends HttpServlet
{

	Properties properties;
	
	boolean debug=false;

	public void init() throws UnavailableException
	{

		properties = PropertiesUtil.getProperties(getServletContext());

		if (properties == null)
		{
			System.err.println("Error initializing AS4 receiver servlet: Couldn't load necessary configuration file");
			throw new UnavailableException("Couldn't load necessary configuration file");
		}
		
		if ("DEBUG".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SERVER_MODE, "DEBUG")))
			debug = true;
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		if (isIncomingAS4Message(req))
			handleIncomingAS4Message(req, resp);
		else
		{
			try
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing necessary AS4 headers");
			}
			catch (Exception e)
			{}
			
			return;
		}
	}
	
	
	private boolean isIncomingAS4Message(HttpServletRequest req)
	{
		//TODO: what are the necessary fields in AS4?
		//if (req.getHeader("as2-from")!=null && req.getHeader("as2-to")!=null && req.getHeader("message-id")!=null && /* req.getHeader("recipient-address")!=null && */ req.getHeader("disposition-notification-to")!=null)
			return true;
		//else
		//	return false;
	}	
	
	
	private void handleIncomingAS4Message(HttpServletRequest req, HttpServletResponse resp)
	{
				
		try
		{
			//we wrap the inputstream into a ByteArrayInputStream so we can read it multiple times (the AS2 endpoint will need to read it later)
			ByteArrayOutputStream buffer = copyToMarkableBuffer(req.getInputStream());
			ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
			
	        //parse and extract input info
	        Map inputMap = parseInput(input, resp);
	        
	        //check certificate trust
	        byte [] binaryCert = Base64.decode((String)inputMap.get("certificate"));
	        X509Certificate cert = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(binaryCert));
			if (cert == null)
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to retrieve certificate from incoming message");
				return;
			}
						
			//check the certificate was signed by our AP CA, if not, it's not a valid request
			String trustError = checkSignatureTrust(cert);			
			if (trustError != null)
			{
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Untrusted/invalid signing certificate");
				return;
			}
			
			//check the certificate is not expired
			try
			{
				cert.checkValidity();			
			}
			catch (CertificateNotYetValidException e)
			{
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign is not yet valid");
				return;
			}
			catch (CertificateExpiredException e)
			{
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign has expired");
				return;				
			}
			
			
			//find the certificate's CN
			String commonName = extractCN(cert);
					
			if (!commonName.equals((String) inputMap.get("senderIdentifier")))
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The Sender Identifier value in your message doesn't match your certificate's Common Name");
			}
			
			//make the request's inputstream available to be read again
			input.reset();
			
			//check that the AS4 endpoint knows the sender, if not, create a new Partner
			AS4GatewayInterface as4Interface = new AS4GatewayInterface();
			PModePool pModePool = as4Interface.getPmodePool();
			boolean producerKnown = false;
			for (Producer prod : pModePool.getProducers())
			{
				if (prod.getName().equalsIgnoreCase(commonName))
				{
					producerKnown = true;
					break;
				}
			}
			if (!producerKnown)
				//as4Interface.createPartner(commonName, (String)inputMap.get("processIdentifier"), (String)inputMap.get("documentIdentifier"), null, cert); //TODO: is it possible to get the sender's endpointURL at this point?	

			//now we finally redirect the request to the AS4 endpoint
			forwardToAS4Endpoint(req, resp, buffer);

		}
		catch (Exception e)
		{
			try
			{
				System.out.println(e.getMessage());
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			catch (IOException ioE)
			{}
		}
	}
	
	
	private	ByteArrayOutputStream copyToMarkableBuffer(InputStream in)
	{
		ByteArrayOutputStream buffer = null;
		try
		{
			buffer = new ByteArrayOutputStream();
	        byte temp[] = new byte[0x10000];
	        for(int count = in.read(temp); count > 0; count = in.read(temp))
	            buffer.write(temp, 0, count);
	        in.close();
		}
		catch (Exception e) {}
        
        return buffer;
	}
	
	
	private Map parseInput(ByteArrayInputStream input, HttpServletResponse resp)
	{
		try
		{
			input.markSupported();
			input.mark(Integer.MAX_VALUE);
			MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(input, "multipart/signed")); //when the message is only signed and not encrypted, here it works with both "multipart/signed" and "application/pkcs7-signature"
			Part part_aux, part=null;
			for (int i=0 ; i < multipart.getCount() ; i++)
			{
				part_aux = multipart.getBodyPart(i);
				if (part_aux.getContentType()!=null && ((part_aux.getContentType().toLowerCase().contains("application/soap+xml"))))  //this is the only part we are interested in
					part = part_aux;
			}
			if (part==null)
			{
		        return null;
			}
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			AS4Handler handler = new AS4Handler();
			
			saxParser.parse(part.getInputStream(), handler);
			
			return handler.getResultMap();
		}
		catch (Exception e)
		{
			try
			{ resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()); }
			catch (Exception f) {}
			return null;
		}
	}
	
	
	/** checks if the user-provided certificate is valid and has been signed by the peppol AP CA certificate
	 * @return an error message if the certificate is expired or not trusted, null if everything is ok. Throws exception if there was a problem loading the necessary keystore.
	 */
	private String checkSignatureTrust (X509Certificate userCert) throws Exception
	{
		KeystoreUtil keystoreAccess = new KeystoreUtil();
		X509Certificate caCert = keystoreAccess.getApCaCertificate();
		
		  // Verify the current certificate using the issuer certificate
	      try
	      {
	    	  userCert.verify (caCert.getPublicKey());
	      }
	      catch (final Exception e)
	      {
	        return e.getMessage ();
	      }
	
	      // Check time validity
	      try
	      {
	    	  userCert.checkValidity ();
	      }
	      catch (final Exception e)
	      {
	        return e.getMessage ();
	      }
	      
	      return null; //means everything went well
	}
	
	
	private String extractCN(X509Certificate cert)
	{
		Principal principal = cert.getSubjectDN();
		if (principal==null)
			principal = cert.getSubjectX500Principal();
		
		String commonName = null;
		String[] names = principal.getName().split(",");
		for (String s: names)
			if (s.trim().startsWith("CN="))
				commonName = s.trim().substring(3);
		
		return commonName.trim();
	}
	
	
	private void forwardToAS4Endpoint(HttpServletRequest req, HttpServletResponse resp, ByteArrayOutputStream buffer)
	{
		
		try
		{
			String url = properties.getProperty(PropertiesUtil.AS4_ENDPOINT_URL); 
			DefaultHttpClient httpclient = new DefaultHttpClient();
	        HttpPost post = new HttpPost(url);
	        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, debug? 3000000 : 30000); //3000sec for tests purposes, 30sec in production
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, debug? 3000000 : 30000); //3000sec for tests purposes, 30sec in production
						
			//setting up SSL connection with the AS2 endpoint if specified in properties
			if (url.startsWith("https"))
			{
				KeyStore trustStore  = KeyStore.getInstance("JKS");        
				FileInputStream instream = new FileInputStream(new File(properties.getProperty(PropertiesUtil.SSL_TRUSTSTORE)));
				try {
				    trustStore.load(instream, properties.getProperty(PropertiesUtil.SSL_TRUSTSTORE_PASSWORD).toCharArray());
				} finally {
				    instream.close();
				}
	
				SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
				
		        if (debug)  //if we are in debug, do not verify that the signer of the SSL certificate is the appropiate one (so we can use self signed certificates in tests)
		        {
		        	HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		        	socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		        }
				
		        //we retrieve the SSL port from the URL specified by the user in the properties
		        String port = url.substring(url.indexOf("://")+3);
		        port = port.substring(0, port.indexOf("/"));
		        port = port.split(":")[1];
				Scheme sch = new Scheme("https", Integer.parseInt(port), socketFactory);
				httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			}
						
			Enumeration<String> headers = req.getHeaderNames();
			String header;
			while (headers.hasMoreElements())
			{
				header = headers.nextElement();
				if (!header.equalsIgnoreCase("Content-Length"))
					post.addHeader(header, req.getHeader(header));
			}
			
			this.getClass().getResource("/");
			
	        ByteArrayEntity entity = new ByteArrayEntity(buffer.toByteArray());
	        post.setEntity(entity);
	        OutputStream out = resp.getOutputStream();
	        HttpResponse response = httpclient.execute(post);
	        HttpEntity resEntity = response.getEntity();
	        Header[] _headers = response.getAllHeaders();
	        for (Header _header : _headers) {
	        	resp.setHeader(_header.getName(), _header.getValue());
	        }
	        resEntity.writeTo(out);
	        out.close();
		}
		catch (Exception e)
		{
			try { resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()); }
			catch (Exception f) {}
		}
	}
	
	
	
	
	private class AS4Handler extends DefaultHandler
	{

		private String position = "";
		private Map<String,String> resultMap;
		
		private static final String certificatePosition = ">soapenv:Envelope>soapenv:Header>wsse:Security>wsse:BinarySecurityToken";
		private static final String senderIdentifierPosition = ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:PartyInfo>eb:From>eb:PartyId";
		private static final String receiverIdentifierPosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:PartyInfo>eb:To>eb:PartyId";
		private static final String instanceIdentifierPosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:CollaborationInfo>eb:ConversationId";
		private static final String processTypePosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:CollaborationInfo>eb:Service";
		private static final String documentTypePosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:CollaborationInfo>eb:Action";
		
		
		public Map<String,String> getResultMap()
		{
			return this.resultMap;
		}
		
	    public void startDocument() throws SAXException
	    {
    		resultMap = new HashMap<String,String>();
	    }

	    public void endDocument() throws SAXException
	    {
	    }
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			String tag = localName!=null && !localName.isEmpty()? localName : qName;
			
			position += ">" + tag;
		}
	 
		public void endElement(String uri, String localName,String qName) throws SAXException
		{
			position = position.substring(0, position.lastIndexOf('>')); 
		}
	 
		public void characters(char ch[], int start, int length) throws SAXException
		{
			if (position.equalsIgnoreCase(senderIdentifierPosition))
			{
				resultMap.put("senderIdentifier", new String(ch, start, length));
			}
			else if (position.equalsIgnoreCase(receiverIdentifierPosition))
			{
				resultMap.put("receiverIdentifier", new String(ch, start, length));
			}
			else if (position.equalsIgnoreCase(instanceIdentifierPosition))
			{
				resultMap.put("instanceIdentifier", new String(ch, start, length));
			}			
			else if (position.equalsIgnoreCase(certificatePosition))
			{
				resultMap.put("certificate", new String(ch, start, length));
			}
			else if (position.equalsIgnoreCase(processTypePosition))
			{
				resultMap.put("processIdentifier", new String(ch, start, length));
			}
			else if (position.equalsIgnoreCase(documentTypePosition))
			{
				resultMap.put("documentIdentifier", new String(ch, start, length));
			}
		}
	}	
	
	
	
}
