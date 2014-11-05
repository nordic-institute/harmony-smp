package eu.europa.ec.cipa.dispatcher.servlet.as2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.HostnameVerifier;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointDBInterface;
import eu.europa.ec.cipa.dispatcher.ocsp.OCSPValidator;
import eu.europa.ec.cipa.dispatcher.util.KeystoreUtil;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;


public class AS2ReceiverServlet extends HttpServlet
{

	private static final Logger s_aLogger = LoggerFactory.getLogger (AS2ReceiverServlet.class);
	Properties properties;

	boolean debug=false;
	
	
	public void init() throws UnavailableException
	{

		properties = PropertiesUtil.getProperties(null);

		if (properties == null)
		{
			s_aLogger.error("Error initializing AS2 receiver servlet: Couldn't load necessary configuration file");
			throw new UnavailableException("Couldn't load necessary configuration file");
		}

		
		if ("DEBUG".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SERVER_MODE, "DEBUG")))
			debug = true;
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		if (isIncomingAS2Message(req))
			handleIncomingAS2Message(req, resp);
		else
		{
			try
			{
				s_aLogger.error(HttpServletResponse.SC_BAD_REQUEST+ "Missing necessary AS2 headers");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing necessary AS2 headers");
			}
			catch (Exception e)
			{
				s_aLogger.error("Internal Server Error occured" + e);
			}
			
			return;
		}
	}
	
	
	
	
	private boolean isIncomingAS2Message(HttpServletRequest req)
	{
		if (req.getHeader("as2-from")!=null && req.getHeader("as2-to")!=null && req.getHeader("message-id")!=null && /* req.getHeader("recipient-address")!=null && */ req.getHeader("disposition-notification-to")!=null)
			return true;
		return false;
	}	
	
	private void handleIncomingAS2Message(HttpServletRequest req, HttpServletResponse resp)
	{
		String as2_from = req.getHeader("as2-from");
		String mdnURL = req.getHeader("disposition-notification-to");
				
		try
		{
			//we wrap the inputstream into a ByteArrayInputStream so we can read it multiple times (the AS2 endpoint will need to read it later)
			ByteArrayOutputStream buffer = copyToMarkableBuffer(req.getInputStream());
			ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
			
	        //extract the certificate from the request
	        X509CertificateObject cert = retrieveCertificate(input, resp);
			if (cert == null)
			{
				s_aLogger.error(HttpServletResponse.SC_BAD_REQUEST+ "Unable to retrieve certificate from incoming message");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to retrieve certificate from incoming message");
				return;
			}
						
			//check the certificate was signed by our AP CA, if not, it's not a valid request
			String trustError = checkSignatureTrust((X509Certificate) cert);
			if (trustError != null)
			{
				s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED+ "Untrusted/invalid signing certificate");
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
				s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED+ "The certificate used to sign is not yet valid");
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign is not yet valid");
				return;
			}
			catch (CertificateExpiredException e)
			{
				s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED+ "The certificate used to sign has expired");
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign has expired");
				return;				
			}
			
			//OCSP validation of the certificate used to sign this message
			boolean valid = true;
			if ("true".equalsIgnoreCase(properties.getProperty(PropertiesUtil.OCSP_VALIDATION_ACTIVATED)))
			{
				try
				{
					valid = OCSPValidator.certificateValidate(cert);
				}
				catch (Exception e)
				{
					s_aLogger.error("Unable to validate the incoming certificate", e);
					valid = false;
				}
				
				if (!valid)
				{
					s_aLogger.error( "The certificate used to sign has been revoked");
					resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign has been revoked");
					return;
				}
			}
			
			
			//find the certificate's CN
			String commonName = extractCN(cert);
					
			if (!commonName.equals(as2_from))
			{
				s_aLogger.error( "The as2-from header value in your message doesn't match your certificate's Common Name");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The as2-from header value in your message doesn't match your certificate's Common Name");
			}
			
			//make the request's inputstream available to be read again
			input.reset();

			
			//check that the AS2 endpoint knows the sender, if not, create a new Partner
			String className = properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS);
			IAS2EndpointDBInterface partnerInterface = (IAS2EndpointDBInterface) Class.forName(className).newInstance();
			if (!partnerInterface.isPartnerKown(commonName))
				partnerInterface.createNewPartner(commonName, commonName, "", mdnURL, (X509Certificate)cert); //we dont know the partner's endpointUrl at this point, so we can't provide it.	

			//now we finally redirect the request to the AS2 endpoint
			forwardToAS2Endpoint(req, resp, buffer);

		}
		catch (Exception e)
		{
			try
			{
				s_aLogger.error( "Internal server Error occured",e);
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
		catch (Exception e) {
			s_aLogger.error( "Internal server Error occured",e);
		}
        
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
				if (part_aux.getContentType()!=null && ((part_aux.getContentType().toLowerCase().contains("application/pkcs7-signature")) || (part_aux.getContentType().toLowerCase().contains("smime-type") && part_aux.getContentType().toLowerCase().contains("signed-data") && (part_aux.getContentType().toLowerCase().indexOf("signed-data") - part_aux.getContentType().toLowerCase().indexOf("smime-type") < 15)))) //Content-Type=application/pkcs7-signature; name=smime.p7s; smime-type=signed-data
					part = part_aux;
			}
			if (part==null)
			{
		        return null;
			}
			
			//we extract the certificate chain used to sign the message from the signature
			CMSSignedData signedData = new CMSSignedData(part.getInputStream());
			CertStore certStore = signedData.getCertificatesAndCRLs("Collection", "BC");
			Collection col = certStore.getCertificates(null);
			Iterator it = col.iterator();
			if (it.hasNext())
				cert = it.next(); //we assume the main certificate is always the first one
		}
		catch (Exception e)
		{
			try
			{ 
				s_aLogger.error( "Internal server Error occured",e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()); }
			catch (Exception f) {
				s_aLogger.error( "Internal server Error occured",f);
				
			}
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
	    	  s_aLogger.error( "Error occured while cgecking electronic signature",e);
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
			try { 
				s_aLogger.error( "Internal server Error occured",e);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()); }
			catch (Exception f) {
				s_aLogger.error( "Internal server Error occured",f);
			}
		}
	}
    
}
