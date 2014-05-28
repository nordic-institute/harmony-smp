package eu.europa.ec.cipa.as2wrapper.test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;



public class TestSendService {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		ClientConfig config = new DefaultClientConfig();
//		SSLContext sslContext = null;
//		SecureRestClientTrustManager secureRestClientTrustManager = new SecureRestClientTrustManager();
//		try
//		{
//			sslContext = SSLContext.getInstance("SSL");
//			sslContext.init(null, new javax.net.ssl.TrustManager[] { secureRestClientTrustManager }, null);
//		}
//		catch (Exception e)
//		{
//			
//		}
//		config.getProperties().put(com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new com.sun.jersey.client.urlconnection.HTTPSProperties(getHostnameVerifier(), sslContext));
//	    Client client = Client.create(config);
//	    WebResource service = client.resource("http://localhost:8080/cipa-as2-access-point-wrapper/rest/send");
//	    
//	    MessageMetaDataType metaData = new MessageMetaDataType();
//	    DocumentInfoType documentInfo = new DocumentInfoType();
//	    documentInfo.setCreationDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//	    documentInfo.setInstanceIdentifier("111111");
//	    documentInfo.setStandard("222222");
//	    documentInfo.setType("Invoice");
//	    documentInfo.setTypeVersion("version2.1");
//	    metaData.setDocumentInfo(documentInfo);
//	    metaData.setDocumentId("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0");
//	    metaData.setDocumentScheme("busdox-docid-qns");
//	    metaData.setProcessId("urn:www.cenbii.eu:profile:bii04:ver1.0");
//	    metaData.setProcessScheme("cenbii-procid-ubl");
//	    ParticipantType recipient = new ParticipantType();
//	    recipient.setValue("mend");
//	    recipient.setScheme("busdox-actorid-upis");
//	    metaData.setRecipient(recipient);
//	    ParticipantType sender = new ParticipantType();
//	    sender.setValue("APP_1000000002");
//	    metaData.setSender(sender); 
//	    
//	    Object document;
//	    try
//	    {
//		    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//			Document doc = docBuilder.newDocument();
//			Element rootElement = doc.createElement("document");
//			doc.appendChild(rootElement);
//			Element tag1 = doc.createElement("tag1");
//			tag1.appendChild(doc.createTextNode("value11111111"));
//			rootElement.appendChild(tag1);
//			Element tag2 = doc.createElement("tag2");
//			tag2.appendChild(doc.createTextNode("value22222222"));
//			rootElement.appendChild(tag2);
//			document = doc.getDocumentElement();
//	    	
//	    	//another way of building the document, from a String
////	        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><document></document>";  
////
////	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
////	        DocumentBuilder builder;  
////	        try  
////	        {  
////	            builder = factory.newDocumentBuilder();  
////	            Document doc = builder.parse( new InputSource( new StringReader( xmlString ) ) );
////	            document.setDocument(doc.getDocumentElement());
////	        } catch (Exception e) {  
////	            e.printStackTrace();  
////	        } 
//	    	
//	    }
//	    catch (Exception e)
//	    {
//	    	document = new Object();
//	    }
//
//	    
//	    RequestType request = new RequestType();
//	    request.setMetaData(metaData);
//	    request.setDocument(document);
//	    
//	    ClientResponse response = service.post(ClientResponse.class, request);    //.type(MediaType.TEXT_XML)
//		//Response r2 = service.path("rest").path("todos").type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, form);
//	    
//	    response.getStatus();
		    
	}
	
	
	private static HostnameVerifier getHostnameVerifier()
	{
		return new HostnameVerifier()
		{
			@Override
			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession)
			{
				return true;
			}
		};
	}

}



class SecureRestClientTrustManager implements X509TrustManager
{ 
	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
	{
	}
	 
	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
	{
	}
	 
	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return new X509Certificate[0];
	}
	 
	public boolean isClientTrusted(X509Certificate[] arg0)
	{
		return true;
	}
	 
	public boolean isServerTrusted(X509Certificate[] arg0)
	{
		return true;
	}
	 
	}


