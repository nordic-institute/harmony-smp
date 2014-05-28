package eu.europa.ec.cipa.as2wrapper.send;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.security.Principal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import backend.ecodex.org._1_1.BackendInterface;
import backend.ecodex.org._1_1.BackendService11;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;
import backend.ecodex.org._1_1.SendResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.server.AS2Agent;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.Splash;
import de.mendelson.util.security.BCCryptoHelper;
import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointPartnerInterface;
import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.as2wrapper.util.KeystoreUtil;
import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.MessageMetadata;
import eu.europa.ec.cipa.transport.start.client.AccessPointClient;
import eu.europa.ec.cipa.transport.start.client.AccessPointClientSendResult;


public class SendWebService extends HttpServlet
{

	Properties properties = PropertiesUtil.getProperties();
	BackendService11 holodeck_service = null;  //created as object variable to avoid loading the holodeck service WSDL everytime we call it.
	
	private static String defaultDocumentTypeScheme = "busdox-docid-qns";
	private static final String defaultProcessTypeScheme = "cenbii-procid-ubl";  //TODO: is this the default value, or "cenbiimeta-procid-ubl" ?
	private static String PROTOCOL_START = "busdox-transport-start";
	private static String PROTOCOL_AS2 = "busdox-transport-as2-ver1p0";
	private static String PROTOCOL_EBMS = "ebms3-as4";
	
	private static Cache<String[], EndpointType> cache = CacheBuilder.newBuilder()
		       .maximumSize(200)
		       .expireAfterWrite(24, TimeUnit.HOURS)
		       .build();
	
	
	public SendWebService() {}
	
	
	public void init()
	{
		
		try
		{
			if (properties.getProperty(PropertiesUtil.HOLODECK_ENDPOINT_ACTIVATED).equalsIgnoreCase("true"))
				holodeck_service = new BackendService11();
		}
		catch (Exception e)
		{
			System.out.println("ERROR - Unable to integrate with Holodeck endpoint: " + e.getMessage());
		}
		
		
        try
        {
        	if (properties.getProperty(PropertiesUtil.AS2_ENDPOINT_ACTIVATED).equalsIgnoreCase("true"))
        	{
//	        	//-------JUST FOR TEST PURPOSES, TO START THE GUI--------
//	            Splash splash = new Splash("/de/mendelson/comm/as2/client/Splash.jpg");
//	            AffineTransform transform = new AffineTransform();
//	            splash.setTextAntiAliasing(false);
//	            transform.setToScale(1.0, 1.0);
//	            splash.addDisplayString(new Font("Verdana", Font.BOLD, 11),
//	                    7, 262, AS2ServerVersion.getFullProductName(),
//	                    new Color(0x65, 0xB1, 0x80), transform);
//	            splash.setVisible(true);
//	            splash.toFront();
//	        	//--------END OF JUST FOR TEST PURPOSES------------------
        	
            
            
	            //register the database drivers for the VM
	            Class.forName("org.hsqldb.jdbcDriver");
	            //initialize the security provider
	            BCCryptoHelper helper = new BCCryptoHelper();
	            helper.initialize();
	            AS2Server as2Server = new AS2Server(false, false);
	            //TODO:this line is just for the GUI, remove later
	            //AS2Agent agent = new AS2Agent(as2Server);
	            
	            String className = properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS);
	            IAS2EndpointPartnerInterface partnerInterface = (IAS2EndpointPartnerInterface) Class.forName(className).newInstance();
	            partnerInterface.configureLocalStationIfNeeded();
	            
	            
//	            //-------JUST FOR TEST PURPOSES, TO START THE GUI--------
//	            AS2Gui gui = new AS2Gui(splash, "localhost");
//	            gui.setVisible(true);
//	            splash.destroy();
//	            splash.dispose();
//	            //--------END OF JUST FOR TEST PURPOSES------------------

        	}
        }
        catch (Exception e)
        {
        	System.out.println("AS2 server couldn't be initialized: " + e.getMessage());
        }
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		resp.setContentType("text/plain");
		resp.getWriter().write("GET requests are not supported");
		resp.getWriter().flush();
		resp.getWriter().close();		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		Map<String,String> resultMap = null;
		try
		{
			//we extract the metadata we need from the SBDH header, and store the message on disk
			resultMap = treatSBDHrequest(req.getInputStream());
		}
		catch(Exception e)
		{
			prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "An error occured while treating the SBDH request:\n" + e.getMessage());
			return;
		}
		
		String receiverIdentifier = (String) resultMap.get("receiverIdentifier");
		String receiverScheme = (String) resultMap.get("receiverScheme");
		String senderIdentifier = (String) resultMap.get("senderIdentifier");
		String senderScheme = (String) resultMap.get("senderScheme");
		String documentId = (String) resultMap.get("documentIdentifier");
		String processId = (String) resultMap.get("processIdentifier");
		
		
		//If the receiver's metadata is not in our cache, we download it from the SMP
		EndpointType endpoint;
		try
		{
			String[] key = new String[3];
			key[0]=receiverIdentifier;
			key[1]=documentId;
			key[2]=processId;
			endpoint = cache.getIfPresent(key);
			if (endpoint == null)
			{
				SMPServiceCaller aSMPClient;
				ParticipantIdentifierType recipient = new ParticipantIdentifierType();
				recipient.setValue(receiverIdentifier);
				recipient.setScheme(receiverScheme);
			    DocumentIdentifierType documentIdentifier = new DocumentIdentifierType();
			    documentIdentifier.setValue(documentId);
			    documentIdentifier.setScheme(defaultDocumentTypeScheme);
			    ProcessIdentifierType process = new ProcessIdentifierType();
			    process.setValue(processId);
			    process.setScheme(defaultProcessTypeScheme);
		    	
			    if ("DIRECT_SMP".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SMP_MODE)))
			    {
			    	aSMPClient = new SMPServiceCaller (new URI (properties.getProperty(PropertiesUtil.SMP_URL)));
			    	endpoint = aSMPClient.getEndpoint(recipient, documentIdentifier, process);
			    }
			    else if ("PRODUCTION".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SMP_MODE)))
			    {
			    	aSMPClient = new SMPServiceCaller (recipient, ESML.PRODUCTION);
			    	endpoint = aSMPClient.getEndpoint(recipient, documentIdentifier, process);
			    }
			    else  //NO_SMP mode
			    {
			    	String className = properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS);
			    	IAS2EndpointPartnerInterface partnerInterface = (IAS2EndpointPartnerInterface) Class.forName(className).newInstance();
			    	endpoint = partnerInterface.getPartnerData(receiverIdentifier);
			    }
			    
			    if (endpoint==null || endpoint.getEndpointReference()==null)
			    	throw new Exception("Couldn't successfully retrieve the receiver's metadata");
			    cache.put(key, endpoint);
			}
			
			
			//TODO: should the AP check receiver's certificate trust & validity? Do it here or for AS2 only?
			
			
			//get which protocol the receiver uses, and forward to the right AP accordingly
			String protocol = endpoint.getTransportProfile();
			String result="";
			if (protocol.equals(PROTOCOL_AS2))
			{
				//get the actual AS2-From and AS2-To identifiers from the certificates
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				//in many cases, the certificate stored in the SMP doesn't include the proper prefixes & suffixes
				String cert_aux = endpoint.getCertificate();
				if (!cert_aux.startsWith("-----BEGIN CERTIFICATE-----"))
					cert_aux = "-----BEGIN CERTIFICATE-----\n" + cert_aux + "\n-----END CERTIFICATE-----";
				endpoint.setCertificate(cert_aux);
				X509Certificate receiverCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert_aux.getBytes()));
				String AS2To = KeystoreUtil.extractCN(receiverCert);
				KeystoreUtil keystoreAccess = new KeystoreUtil();
				X509Certificate ourCert = keystoreAccess.getApCertificate();
				String AS2From = KeystoreUtil.extractCN(ourCert);
				
				String className = properties.getProperty(PropertiesUtil.SEND_INTERFACE_IMPLEMENTATION_CLASS);
				IAS2EndpointSendInterface sendInterface = (IAS2EndpointSendInterface) Class.forName(className).newInstance();
				
				result = sendInterface.send(AS2From, AS2To, (String)resultMap.get("instanceIdentifier"), (String)resultMap.get("tempFilePath"), endpoint);	
			}
			if (protocol.equals(PROTOCOL_START))
			{
				ParticipantIdentifierType sender = new ParticipantIdentifierType();
				sender.setScheme(senderScheme);
				sender.setValue(senderIdentifier);
				ParticipantIdentifierType receiver = new ParticipantIdentifierType();
				receiver.setScheme(receiverScheme);
				receiver.setValue(receiverIdentifier);
				DocumentIdentifierType documentType = new DocumentIdentifierType();
				documentType.setScheme(defaultDocumentTypeScheme);
				documentType.setValue(documentId);
				ProcessIdentifierType processType = new ProcessIdentifierType();
				processType.setScheme(defaultProcessTypeScheme);
				processType.setValue(processId);
				MessageMetadata metadata = new MessageMetadata ("uuid:" + UUID.randomUUID ().toString (), "peppol-channel", sender, receiver, documentType, processType);
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilderFactory.setNamespaceAware(true);
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(new File((String)resultMap.get("tempFilePath")));
				AccessPointClientSendResult apResult = AccessPointClient.send (W3CEndpointReferenceUtils.getAddress(endpoint.getEndpointReference()), metadata, doc);
				if (apResult.isFailure())
				{
					if (apResult.getErrorMessageCount()>0)
						result = apResult.getAllErrorMessages().get(0);
					else
						result = "An error occured while communicating with the receiver access point.";
				}
				else
					result = "";
			}
			if (protocol.equals(PROTOCOL_EBMS))
			{
				BackendInterface holodeck_interface = holodeck_service.getBackendPort();
//				BindingProvider bp = (BindingProvider)holodeck_interface;
//	            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://server1.test.com:8282/HumanRessources/EmployeeServiceService");

				Messaging ebMSHeaderInfo = buildEBMSHeaderInfo(receiverIdentifier, senderIdentifier, endpoint);
				SendRequest request = buildRequest((String)resultMap.get("tempFilePath"));				
				
				SendResponse response = null;
				try
				{
					response = holodeck_interface.sendMessage(request, ebMSHeaderInfo);
				}
				catch (Exception e)
				{
					result = "Unable to send message through Holodeck: " + e.getMessage()!=null && !e.getMessage().isEmpty()? e.getMessage() : e.getCause().getMessage();;
					throw new Exception(result);
				}
				
				if (response.getMessageID()==null || response.getMessageID().size()==0)
					result = "";
				else
					result = response.getMessageID().get(0);   //TODO: are we sure there are only error messages in the response? maybe OK responses are returned as well...
			}
			
			
			if (result != null && !result.isEmpty())
			{
				prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", result);
				return;
			}
			else
				resp.setStatus(HttpServletResponse.SC_OK);
		}
		catch (Exception e)
		{
			prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", e.getMessage());
			return;
		}			
	}
	
	
	
	private Messaging buildEBMSHeaderInfo(String AS2To, String AS2From, EndpointType endpoint)
	{
		Messaging result = new Messaging();
		
		UserMessage userMessage = new UserMessage();
		
		//message properties
		MessageProperties messageProperties = new MessageProperties();
        Property finalRecipient = new Property();
        finalRecipient.setName("finalRecipient");
        finalRecipient.setValue(AS2To);
        Property originalSender = new Property();
        originalSender.setName("originalSender");
        originalSender.setValue(AS2From);
        Property endpointAddress = new Property();
        endpointAddress.setName("EndpointAddress");
        endpointAddress.setValue(W3CEndpointReferenceUtils.getAddress(endpoint.getEndpointReference()));
        
        List<Property> listProperties = messageProperties.getProperty();
        listProperties.add(finalRecipient);
        listProperties.add(originalSender);
        listProperties.add(endpointAddress);
        
        userMessage.setMessageProperties(messageProperties);
        
        
        //party Info
        PartyInfo partyInfo = new PartyInfo();
        From paramFrom = new From();
        paramFrom.setRole("XXXXX");
        PartyId partyIdFrom = new PartyId();
        partyIdFrom.setType("XXXXX");
        partyIdFrom.setValue("XXXXX");
        List<PartyId> listPartyId = paramFrom.getPartyId();
        listPartyId.add(partyIdFrom);
        partyInfo.setFrom(paramFrom);
        To paramTo = new To();
        paramTo.setRole("XXXXX");
        PartyId partyIdTo = new PartyId();
        partyIdTo.setType("XXXXX");
        partyIdTo.setValue("XXXXX");
        List<PartyId> listPartyId2 = paramTo.getPartyId();
        listPartyId2.add(partyIdTo);
        partyInfo.setTo(paramTo);
        
        userMessage.setPartyInfo(partyInfo);
        
        
        //collaboration info
        CollaborationInfo collaborationInfo = new CollaborationInfo();
        collaborationInfo.setAction("XXXXXX");
        Service service = new Service();
        service.setType("XXXXXXX");
        service.setValue("XXXXXXX");
        collaborationInfo.setService(service);
        collaborationInfo.setConversationId("XXXXXX");
        
        userMessage.setCollaborationInfo(collaborationInfo);
        
        
        //message info
        MessageInfo info = new MessageInfo();
        //TODO: here they only set RefToMessageId, but I think it's not necessary for us?
        userMessage.setMessageInfo(info);
        
        
        //payload info
        userMessage.setPayloadInfo(new PayloadInfo());
        
        
        List<UserMessage> listUserMessage = result.getUserMessage();
        listUserMessage.add(userMessage);
        return result;
	}
	
	
	
	private SendRequest buildRequest(String pathToFile) throws Exception
	{
		SendRequest request = new SendRequest();
		PayloadType payload = new PayloadType();

		RandomAccessFile f = new RandomAccessFile(pathToFile, "r");
		byte[] content = new byte[(int)f.length()];
		f.read(content);
		payload.setValue(content);
		payload.setContentType("text/xml");
        payload.setPayloadId("#_" + UUID.randomUUID().toString());
		
		request.setBodyload(payload);
		
		return request;
	}
	
	
	private Map<String,String> treatSBDHrequest (InputStream input) throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SBDHHandler handler = new SBDHHandler();
		
		saxParser.parse(input, handler);
		
		return handler.getResultMap();
	}
	
	
	private void prepareResponse(HttpServletResponse resp, int responseCode, String contentType, String message)
	{
		try
		{
			resp.setStatus(responseCode);
			resp.setContentType(contentType);
			resp.getWriter().write(message);
		}
		catch (Exception e)
		{
			System.err.println("SendWebservice.prepareResponse(): Error trying to get the response writer");
		}
		return;
	}
	
	
	private class SBDHHandler extends DefaultHandler
	{
		
		private FileOutputStream file;
		private PrintStream stream;
		private String position = "";
		private String scheme;
		private String scopeType;
		private Map<String,String> resultMap;
		
		private static final String senderIdentifierPosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>Sender>Identifier";
		private static final String receiverIdentifierPosition= ">StandardBusinessDocument>StandardBusinessDocumentHeader>Receiver>Identifier";
		private static final String instanceIdentifierPosition= ">StandardBusinessDocument>StandardBusinessDocumentHeader>DocumentIdentification>InstanceIdentifier";
		private static final String businessScopeTypePosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>BusinessScope>Scope>Type";
		private static final String businessScopeInstanceIdentifierPosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>BusinessScope>Scope>InstanceIdentifier";
		
		
		public Map<String,String> getResultMap()
		{
			return this.resultMap;
		}
		
	    public void startDocument() throws SAXException
	    {
	    	try
	    	{
	    		int randomInt = 1000000 + (int)(Math.random() * 9000000);    //  Min + (int)(Math.random() * ((Max - Min) + 1))       , Min = 1000000, Max = 9999999
	    		String tempFilePath = properties.getProperty(PropertiesUtil.TEMP_FOLDER_PATH);
	    		if (!tempFilePath.endsWith("/") && !tempFilePath.endsWith("\\"))
	    			tempFilePath+="/";
	    		tempFilePath += randomInt;
	    		file = new FileOutputStream(tempFilePath);
	    		stream = new PrintStream(file);
	    		
	    		resultMap = new HashMap<String,String>();
	    		resultMap.put("tempFilePath", tempFilePath);
	    	}
	    	catch (Exception e)
	    	{
	    		throw new SAXException(e);
	    	}
	    }

	    public void endDocument() throws SAXException
	    {
	    	try
	    	{
	    		stream.flush();
	    		stream.close();
	    		file.flush();
	    		file.close();
	    	}
	    	catch (Exception e)
	    	{
	    		throw new SAXException(e);
	    	}
	    }
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			String tag = localName!=null && !localName.isEmpty()? localName : qName;
		
			stream.print("<" + tag);
			
			position += ">" + tag;
			
			if (attributes!=null)
			{
				String attName;
				for (int i=0 ; i<attributes.getLength() ; i++)
				{
					attName = attributes.getLocalName(i)!=null ? attributes.getLocalName(i) : attributes.getQName(i);
					stream.print(' ');
					stream.print(attName);
					stream.print('=');
					stream.print('"' + attributes.getValue(i) + '"');
					
					if (attName.equalsIgnoreCase("Authority"))
						scheme = attributes.getValue(i);
				}
			}
			
			stream.print(">");
		}
	 
		public void endElement(String uri, String localName,String qName) throws SAXException
		{
			String tag = localName!=null && !localName.isEmpty()? localName : qName;
			
			stream.print("</" + tag +  ">");
			
			position = position.substring(0, position.lastIndexOf('>')); 
		}
	 
		public void characters(char ch[], int start, int length) throws SAXException
		{
			if (position.equalsIgnoreCase(senderIdentifierPosition))
			{
				resultMap.put("senderIdentifier", new String(ch, start, length));
				resultMap.put("senderScheme", scheme);
			}
			if (position.equalsIgnoreCase(receiverIdentifierPosition))
			{
				resultMap.put("receiverIdentifier", new String(ch, start, length));
				resultMap.put("receiverScheme", scheme);
			}
			if (position.equalsIgnoreCase(instanceIdentifierPosition))
			{
				resultMap.put("instanceIdentifier", new String(ch, start, length));
			}			
			if (position.equalsIgnoreCase(businessScopeTypePosition))
			{
				scopeType = new String(ch, start, length);
			}
			if (position.equalsIgnoreCase(businessScopeInstanceIdentifierPosition))
			{
				if (scopeType.equalsIgnoreCase("DOCUMENTID"))
					resultMap.put("documentIdentifier", new String(ch, start, length));
				if (scopeType.equalsIgnoreCase("PROCESSID"))
					resultMap.put("processIdentifier", new String(ch, start, length));
			}	
				
			
			//stream.print(new String(ch, start, length)); let's try to avoid the creation of potentially big objects
				for (int offset = 0 ; offset < length ; offset++)
					stream.print(ch[start+offset]);
		}
	}
	
	
//	private StandardBusinessDocument transformIntoSBDHRequest(RequestType request)
//	{
//		ObjectFactory objFactory = new ObjectFactory();
//		StandardBusinessDocument sbdh = objFactory.createStandardBusinessDocument(); 
//		
//		StandardBusinessDocumentHeader sbdhHeader = objFactory.createStandardBusinessDocumentHeader();
//		sbdhHeader.setHeaderVersion("1.3");
//		
//		BusinessScope sbdhBusinessScope = new BusinessScope();
//		List<Scope> scopes = sbdhBusinessScope.getScope();
//		Scope scope = new Scope();
//		scope.setType("DOCUMENTID");
//		scope.setInstanceIdentifier(request.getMetaData().getDocumentId());
//		scopes.add(scope);
//		scope = new Scope();
//		scope.setType("PROCESSID");
//		scope.setInstanceIdentifier(request.getMetaData().getProcessId());
//		scopes.add(scope);
//		sbdhHeader.setBusinessScope(sbdhBusinessScope);
//		
//		List<Partner> senders = sbdhHeader.getSender();
//		Partner partner = new Partner();
//		PartnerIdentification partnerIdentification = new PartnerIdentification();
//		partnerIdentification.setAuthority(request.getMetaData().getSender().getScheme());
//		partnerIdentification.setValue(request.getMetaData().getSender().getValue());
//		partner.setIdentifier(partnerIdentification);
//		senders.add(partner);
//		
//		List<Partner> receivers = sbdhHeader.getReceiver();
//		partner = new Partner();
//		partnerIdentification = new PartnerIdentification();
//		partnerIdentification.setAuthority(request.getMetaData().getRecipient().getScheme());
//		partnerIdentification.setValue(request.getMetaData().getRecipient().getValue());
//		partner.setIdentifier(partnerIdentification);
//		receivers.add(partner);
//		
//		DocumentIdentification sbdhDocId = new DocumentIdentification();
//		GregorianCalendar cal_aux = new GregorianCalendar();
//	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    Date date;
//	    try
//	    {
//	    	date = sdf.parse(request.getMetaData().getDocumentInfo().getCreationDateAndTime());
//	    }
//	    catch (Exception e)
//	    {
//	    	date = new Date();
//	    }
//	    cal_aux.setTime(date);
//		XMLGregorianCalendar cal;
//		try
//		{
//			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal_aux);
//		}
//		catch (Exception e)
//		{
//			cal = null;
//		}
//		sbdhDocId.setCreationDateAndTime(cal);
//		sbdhDocId.setInstanceIdentifier(request.getMetaData().getDocumentInfo().getInstanceIdentifier());
//		sbdhDocId.setStandard(request.getMetaData().getDocumentInfo().getStandard());
//		sbdhDocId.setType(request.getMetaData().getDocumentInfo().getType());
//		sbdhDocId.setTypeVersion(request.getMetaData().getDocumentInfo().getTypeVersion());
//		sbdhHeader.setDocumentIdentification(sbdhDocId);
//		
//		sbdh.setStandardBusinessDocumentHeader(sbdhHeader);
//		sbdh.setAny(request.getDocument());
//		
//		return sbdh;
//	}
	
}
