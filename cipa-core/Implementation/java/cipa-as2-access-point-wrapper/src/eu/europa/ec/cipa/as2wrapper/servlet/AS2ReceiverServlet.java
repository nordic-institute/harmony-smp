package eu.europa.ec.cipa.as2wrapper.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.HostnameVerifier;
import javax.security.auth.x500.X500Principal;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

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


import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointPartnerInterface;
import eu.europa.ec.cipa.as2wrapper.util.KeystoreUtil;
import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;


public class AS2ReceiverServlet extends HttpServlet
{

	Properties properties;

	boolean debug=false;
	
	
	public void init() throws UnavailableException
	{

		properties = PropertiesUtil.initializeProperties(getServletContext());

		if (properties == null)
		{
			System.err.println("Error initializing AS2 wrapper: Couldn't load necessary configuration file");
			throw new UnavailableException("Couldn't load necessary configuration file");
		}

		
		if ("DEBUG".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SERVER_MODE, "DEBUG")))
			debug = true;
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		if (isMDN(req))
			handleIncomingMDN(req, resp);
		else if (isIncomingAS2Message(req))
			handleIncomingAS2Message(req, resp);
		else
		{
			try
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing necessary AS2 headers");
			}
			catch (Exception e)
			{}
			
			return;
		}
	}
	
	
	private boolean isMDN(HttpServletRequest req)
	{
		if (req.getHeader("as2-from")!=null && req.getHeader("as2-to")!=null && req.getHeader("message-id")!=null &&
				req.getHeader("disposition-notification-to")==null && req.getHeader("disposition-notification-options")==null && req.getHeader("recipient-address")==null)
			return true;
		return false;
	}
	
	
	private boolean isIncomingAS2Message(HttpServletRequest req)
	{
		if (req.getHeader("as2-from")!=null && req.getHeader("as2-to")!=null && req.getHeader("message-id")!=null && req.getHeader("recipient-address")!=null && req.getHeader("disposition-notification-to")!=null)
			return true;
		return false;
	}
	
	
	/** Just takes care the message has been signed by a PEPPOL certificate, and that the CN matches the expected one
	 */
	private void handleIncomingMDN(HttpServletRequest req, HttpServletResponse resp)
	{
		try
		{
			//we wrap the inputstream into a ByteArrayInputStream so we can read it multiple times (the AS2 endpoint will need to read it later)
			ByteArrayOutputStream buffer = copyToMarkableBuffer(req.getInputStream());
			ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
			
	        //extract the certificate from the request
	        X509CertificateObject cert = retrieveCertificate(input, resp);
			Principal principal = ((X509CertificateObject)cert).getSubjectDN();
			if (principal==null)
				principal = ((X509CertificateObject)cert).getSubjectX500Principal();
						
			//check the certificate was signed by our AP CA, if not, it's not a valid MDN
			String trustError = checkSignatureTrust((X509Certificate) cert);
			if (trustError != null)
			{
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Untrusted/invalid signing certificate");
				return;
			}
			
			//find the certificate's CN
			String commonName = extractCN(principal);
					
			//TODO: we MUST check the signing certificate's CN is the same as in the SMP metadata (cached in our database), but if we see it's not, what should we do? Responding with an error to DestAP won't have any effect, as it has already processed the original message we sent. 
			
			//make the request's inputstream available to be read again
			input.reset();			

			//now we finally redirect the request to the AS2 endpoint
			forwardToAS2Endpoint(req, resp, buffer);

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
			
			return;
		}
	}
	
	
	private void handleIncomingAS2Message(HttpServletRequest req, HttpServletResponse resp)
	{
		String as2_from = req.getHeader("as2-from");
		String mdnURL = req.getHeader("disposition-notification-to");
//		String mdnOption = req.getHeader("disposition-notification-options"); //if requesting signed MDN, this will exist and have a value like: signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1, md5
//		boolean signedMDN = (mdnOption!=null && !mdnOption.isEmpty());
//		String receiptOption = req.getHeader("receipt-delivery-option");      //if requesting async MDN, this will exist and will have the URL to respond to.
//		boolean syncMDN = (receiptOption==null || receiptOption.isEmpty());
				
		try
		{
			//we wrap the inputstream into a ByteArrayInputStream so we can read it multiple times (the AS2 endpoint will need to read it later)
			ByteArrayOutputStream buffer = copyToMarkableBuffer(req.getInputStream());
			ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
			
	        //extract the certificate from the request
	        X509CertificateObject cert = retrieveCertificate(input, resp);
			Principal principal = ((X509CertificateObject)cert).getSubjectDN();
			if (principal==null)
				principal = ((X509CertificateObject)cert).getSubjectX500Principal();
						
			//check the certificate was signed by our AP CA, if not, it's not a valid request
			String trustError = checkSignatureTrust((X509Certificate) cert);
			if (trustError != null)
			{
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Untrusted/invalid signing certificate");
				return;
			}
			
			//find the certificate's CN
			String commonName = extractCN(principal);
					
			//TODO: if the CN is not equals to the AS2-from we should reject the message: shall we do it here or let the AS2 endpoint do it?
			
			//make the request's inputstream available to be read again
			input.reset();

			
			//check that the AS2 endpoint knows the sender, if not, create a new Partner
			String className = properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS);
			IAS2EndpointPartnerInterface partnerInterface = (IAS2EndpointPartnerInterface) Class.forName(className).newInstance();
			if (!partnerInterface.isPartnerKown(commonName))
				partnerInterface.createNewPartner(commonName, as2_from, "", mdnURL, (X509Certificate)cert); //we dont know the partner's endpointUrl at this point, so we can't provide it.	

			//now we finally redirect the request to the AS2 endpoint
			forwardToAS2Endpoint(req, resp, buffer);

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
			
			return;
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
	
	
	private X509CertificateObject retrieveCertificate(ByteArrayInputStream input, HttpServletResponse resp)
	{
		Object cert = null;
		
		//we try to find the signature located as a MIME part inside the message
		try
		{
			input.markSupported();
			input.mark(Integer.MAX_VALUE);
			MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(input, "multipart/signed")); //when the message is only signed and not encrypted, here it works with both "multipart/signed" and "application/pkcs7-signature"
			Part part_aux, part=null;
			for (int i=0 ; i < multipart.getCount() ; i++)
			{
				part_aux = multipart.getBodyPart(i);
				if (part_aux.getContentType()!=null && part_aux.getContentType().contains("mime-type=signed-data")) //Content-Type=application/pkcs7-signature; name=smime.p7s; smime-type=signed-data
					part = part_aux;
			}
			if (part==null)
			{
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing signature in incoming message");
		        return null;
			}
			
			//we extract the certificate chain used to sign the message from the signature
			CMSSignedData signedData = new CMSSignedData(part.getInputStream());
			CertStore certStore = signedData.getCertificatesAndCRLs("Collection", "BC");
			Collection col = certStore.getCertificates(null);
			Iterator it = col.iterator();
			if (it.hasNext())
				cert = it.next(); //TODO: can we be sure the main certificate is always the first one?
		}
		catch (Exception e)
		{
			try
			{ resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()); }
			catch (Exception f) {}
			return null;
		}
		
		return (X509CertificateObject) cert;
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
	
	
	private String extractCN(Principal principal)
	{
		String commonName = null;
		String[] names = principal.getName().split(",");
		for (String s: names)
			if (s.startsWith("CN="))
				commonName = s.substring(3);
		
		return commonName;
	}
	
	
	private void forwardToAS2Endpoint(HttpServletRequest req, HttpServletResponse resp, ByteArrayOutputStream buffer)
	{
		
		try
		{
			String url = properties.getProperty(PropertiesUtil.AS2_ENDPOINT_URL); 
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
				if (!header.equals("Content-Length"))
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
    
}
