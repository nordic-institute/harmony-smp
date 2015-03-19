package eu.europa.ec.cipa.dispatcher.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;


import backend.ecodex.org._1_1.BackendInterface;
import backend.ecodex.org._1_1.BackendService11;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;
import backend.ecodex.org._1_1.SendResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointDBInterface;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.AS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.service.AS4PModeService;
import eu.europa.ec.cipa.dispatcher.handler.SBDHHandler;
import eu.europa.ec.cipa.dispatcher.util.KeystoreUtil;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;

public class SendServlet extends HttpServlet {

	private static final Logger s_aLogger = Logger.getLogger (SendServlet.class);
	
	Properties properties = PropertiesUtil.getProperties();
	BackendService11 domibusService = null;
	IAS2EndpointDBInterface partnerInterface;
	// created as object variable to avoid loading the domibus
	// service WSDL everytime we call it.

	private static String defaultDocumentTypeScheme = "busdox-docid-qns";
	private static final String defaultProcessTypeScheme = "cenbii-procid-ubl";
	// TODO: is this the default value, or "cenbiimeta-procid-ubl" ?
	private static String PROTOCOL_AS2 = "busdox-transport-as2-ver1p0";
	private static String PROTOCOL_EBMS = "ebms3-as4";

	private Cache<String, EndpointType> cache = CacheBuilder.newBuilder().maximumSize(Integer.parseInt(properties.getProperty(PropertiesUtil.CACHE_MAX_NUMBER_ENTRIES)))
			.expireAfterWrite(Integer.parseInt(properties.getProperty(PropertiesUtil.CACHE_EXPIRE_AFTER_HOURS)), TimeUnit.HOURS).build();

	public SendServlet() {
	}

	public void init() {
	}

	public void destroy() {
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		resp.setContentType("text/plain");
		resp.getWriter().write("GET requests are not supported");
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, String> resultMap = null;
		try {
			System.out.print(req);
			// we extract the metadata we need from the SBDH header, and store
			// the message on disk
			resultMap = treatSBDHrequest(req.getInputStream());
		} catch (Exception e) {
			s_aLogger.error("An error occured while treating the SBDH request:\n" + e.getMessage(), e);
			prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "An error occured while treating the SBDH request:\n" + e.getMessage());
			return;
		}

		String receiverIdentifier = (String) resultMap.get("receiverIdentifier");
		String receiverScheme = (String) resultMap.get("receiverScheme");
		String senderIdentifier = (String) resultMap.get("senderIdentifier");
		String senderScheme = (String) resultMap.get("senderScheme");
		String documentId = (String) resultMap.get("documentIdentifier");
		String processId = (String) resultMap.get("processIdentifier");
		String correlationId = (String) resultMap.get("correlationId");
		if (correlationId == null) {
			correlationId = UUID.randomUUID().toString();
		}
		String missingField = null;
		if (receiverIdentifier == null || receiverIdentifier.isEmpty())
			missingField = "receiverIdentifier";
		else if (senderIdentifier == null || senderIdentifier.isEmpty())
			missingField = "senderIdentifier";
		else if (documentId == null || documentId.isEmpty())
			missingField = "documentIdentifier";
		else if (processId == null || processId.isEmpty())
			missingField = "processIdentifier";
		if (missingField != null) {
			s_aLogger.error("The mandatory field '" + missingField + "' could not be found in the SBDH header");
			prepareResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "The necessary field '" + missingField + "' could not be found in the SBDH header");
			return;
		}

		// If the receiver's metadata is not in our cache, we download it from
		// the SMP
		EndpointType endpoint;
		try {
			String key = receiverScheme + receiverIdentifier + documentId + processId;
			endpoint = cache.getIfPresent(key);
			if (endpoint == null) {
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

				if ("DIRECT_SMP".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SMP_MODE))) {
					aSMPClient = new SMPServiceCaller(new URI(properties.getProperty(PropertiesUtil.SMP_URL)));
					endpoint = getEndpoint(aSMPClient, recipient, documentIdentifier, process);
				} else if ("PRODUCTION".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SMP_MODE))) {
					if (properties.getProperty(PropertiesUtil.DNS_ZONE) != null && !properties.getProperty(PropertiesUtil.DNS_ZONE).isEmpty())
					{
						aSMPClient = new SMPServiceCaller(recipient, properties.getProperty(PropertiesUtil.DNS_ZONE));
					}
					else
					{
						aSMPClient = new SMPServiceCaller(recipient, ESML.PRODUCTION);
					}
					endpoint = getEndpoint(aSMPClient, recipient, documentIdentifier, process);

				} else // NO_SMP mode
				{
					String className = properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS);
					partnerInterface = (IAS2EndpointDBInterface) Class.forName(className).newInstance();
					endpoint = partnerInterface.getPartnerData(receiverIdentifier);
				}

				if (endpoint == null || endpoint.getEndpointReference() == null){
					s_aLogger.error("Couldn't successfully retrieve the receiver's metadata");
					throw new Exception("Couldn't successfully retrieve the receiver's metadata");
				}
				cache.put(key, endpoint);
			}

			// TODO: should the AP check the receiver's certificate trust &
			// validity? Do it here or for AS2 only?

			// get which protocol the receiver uses, and forward to the right AP
			// accordingly
			String protocol = endpoint.getTransportProfile();
			String result = "";
			if (protocol.equals(PROTOCOL_AS2) && !properties.getProperty(PropertiesUtil.AS2_ENDPOINT_PREFERENCE_ORDER).equals("0")) {
				// get the actual AS2-From and AS2-To identifiers from the
				// certificates
				X509Certificate receiverCert = getReceiverCertificate(endpoint);
				String AS2To = KeystoreUtil.extractCN(receiverCert);
				String as2KeystorePath = properties.getProperty(PropertiesUtil.AS2_TRUSTSTORE_PATH);
				String as2KeystorePassword = properties.getProperty(PropertiesUtil.AS2_TRUSTSTORE_PASSWORD);
				KeystoreUtil keystoreAccess = new KeystoreUtil(as2KeystorePath, as2KeystorePassword);
				X509Certificate ourCert = keystoreAccess.getApCertificate();
				String AS2From = KeystoreUtil.extractCN(ourCert);

				String className = properties.getProperty(PropertiesUtil.SEND_INTERFACE_IMPLEMENTATION_CLASS);
				IAS2EndpointSendInterface sendInterface = (IAS2EndpointSendInterface) Class.forName(className).newInstance();
				if (partnerInterface == null) {
					partnerInterface = (IAS2EndpointDBInterface) Class.forName(properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS)).newInstance();
				}
				partnerInterface.configureLocalStationIfNeeded();
				String auxiliaryMessageName = resultMap.get("tempFilePath");
				auxiliaryMessageName = auxiliaryMessageName.substring(auxiliaryMessageName.lastIndexOf('/') + 1, auxiliaryMessageName.length());

				result = sendInterface.send(AS2From, AS2To, auxiliaryMessageName, resultMap.get("tempFilePath"), endpoint);
			} else if (protocol.equals(PROTOCOL_EBMS) && !properties.getProperty(PropertiesUtil.EBMS_ENDPOINT_PREFERENCE_ORDER).equals("0")) {
				X509Certificate receiverCert = getReceiverCertificate(endpoint);

				// updates the AS4 truststore with the receiver certificate
				String as4TruststorePath = properties.getProperty(PropertiesUtil.AS4_TRUSTSTORE_PATH);
				String as4TruststorePassword = properties.getProperty(PropertiesUtil.AS4_TRUSTSTORE_PASSWORD);
				KeystoreUtil as4Truststore = new KeystoreUtil(as4TruststorePath, as4TruststorePassword);
				String receiverGW = KeystoreUtil.extractCN(receiverCert);
				as4Truststore.installNewPartnerCertificate(receiverCert, receiverGW);

				// retrieve the CN from the dispatcher (sender)
				String dispatcherKeystorePath = properties.getProperty(PropertiesUtil.DISPATCHER_KEYSTORE_PATH);
				String dispatcherKeystorePassword = properties.getProperty(PropertiesUtil.DISPATCHER_KEYSTORE_PASSWORD);
				KeystoreUtil dispatcherKeystore = new KeystoreUtil(dispatcherKeystorePath, dispatcherKeystorePassword);
				String senderGW = KeystoreUtil.extractCN(dispatcherKeystore.getApCertificate());

				AS4PModeService service = new AS4PModeService();
				service.createPartner(senderGW, receiverGW, processId, documentId, W3CEndpointReferenceUtils.getAddress(endpoint.getEndpointReference()));
				if (domibusService == null)
					try {
						domibusService = new BackendService11(new URL(properties.getProperty(PropertiesUtil.EBMS_WSDL_PATH)));
					} catch (Exception e) {
						s_aLogger.error("ERROR - Unable to integrate with Domibus endpoint: ", e);
						prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Unable to integrate with AS4 endpoint: " + e.getMessage());
						domibusService = null;
						return;
					}

				BackendInterface domibusInterface = domibusService.getBackendPort();

				Messaging ebMSHeaderInfo = buildEBMSHeaderInfo(receiverGW, senderGW, processId, documentId, correlationId, endpoint, senderIdentifier, receiverIdentifier);
				SendRequest request = buildRequest(resultMap.get("tempFile2Path"));

				SendResponse response = null;
				try {
					response = domibusInterface.sendMessage(request, ebMSHeaderInfo);
				} catch (Exception e) {
					s_aLogger.error("Unable to send message through Domibus: " + e.getMessage(), e);
					result = "Unable to send message through Domibus: " + e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : e.getCause().getMessage();
					throw new Exception(result);
				}

				if (response.getMessageID() == null || response.getMessageID().size() == 0)
					result = "";
				else
					result = response.getMessageID().get(0);
				// TODO: are we sure there are only error messages in the
				// response?
				// maybe OK responses are returned as well...
				// s_aLogger.error(
				// "sending through EBMS protocol not available.");
				prepareResponse(resp, HttpServletResponse.SC_CREATED, "text/plain", "Message sucessfully sent using as4.");
				return;
			} else {
				s_aLogger.error("There's no activated protocol in this Access Point able to communicate with the receiver.");
				prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "There's no activated protocol in this Access Point able to communicate with the receiver.");
				return;
			}

			if (result != null && !result.isEmpty()) {
				s_aLogger.error(result);
				prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", result);
				return;
			} else
				resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			s_aLogger.error(e.getMessage(), e);
			prepareResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : e.getCause().getMessage());
			return;
		} finally {
			// delete temp files created at the beginning in case it couldn't be
			// sent
			File temp;
			if (resultMap != null && resultMap.get("tempFilePath") != null && !resultMap.get("tempFilePath").isEmpty()) {
				temp = new File(resultMap.get("tempFilePath"));
				if (temp.exists())
					temp.delete();
			}
			if (resultMap != null && resultMap.get("tempFile2Path") != null && !resultMap.get("tempFile2Path").isEmpty()) {
				temp = new File(resultMap.get("tempFile2Path"));
				if (temp.exists())
					temp.delete();
			}
		}
	}

	private X509Certificate getReceiverCertificate(EndpointType endpoint) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		// in many cases, the certificate stored in the SMP doesn't include the
		// proper prefixes & suffixes
		String cert_aux = endpoint.getCertificate();
		if (!cert_aux.startsWith("-----BEGIN CERTIFICATE-----"))
			cert_aux = "-----BEGIN CERTIFICATE-----\n" + cert_aux + "\n-----END CERTIFICATE-----";
		endpoint.setCertificate(cert_aux);
		X509Certificate receiverCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert_aux.getBytes()));
		return receiverCert;
	}

	private Messaging buildEBMSHeaderInfo(String receiver, String sender, String processId, String documentId, String correlationId, EndpointType endpoint, String initialSender, String initialReceiver) {

		String userServiceName = processId.concat("_").concat(documentId).concat("_").concat(receiver).concat("_").concat(AS4GatewayInterface.PMODE_ROLE);

		Messaging result = new Messaging();

		UserMessage userMessage = new UserMessage();

		// message properties
		MessageProperties messageProperties = new MessageProperties();
		Property finalRecipient = new Property();
		finalRecipient.setName("finalRecipient");
		finalRecipient.setValue(initialReceiver);
		Property originalSender = new Property();
		originalSender.setName("originalSender");
		originalSender.setValue(initialSender);
		Property endpointAddress = new Property();
		endpointAddress.setName("EndpointAddress");
		endpointAddress.setValue(W3CEndpointReferenceUtils.getAddress(endpoint.getEndpointReference()));

		List<Property> listProperties = messageProperties.getProperty();
		listProperties.add(finalRecipient);
		listProperties.add(originalSender);
		listProperties.add(endpointAddress);

		userMessage.setMessageProperties(messageProperties);

		// party Info
		PartyInfo partyInfo = new PartyInfo();
		From paramFrom = new From();
		paramFrom.setRole(AS4GatewayInterface.PMODE_ROLE);
		PartyId partyIdFrom = new PartyId();
		partyIdFrom.setType(AS4GatewayInterface.ID_TYPE);
		partyIdFrom.setValue(sender);
		List<PartyId> listPartyId = paramFrom.getPartyId();
		listPartyId.add(partyIdFrom);
		partyInfo.setFrom(paramFrom);
		To paramTo = new To();
		paramTo.setRole(AS4GatewayInterface.PMODE_ROLE);
		PartyId partyIdTo = new PartyId();
		partyIdTo.setType(AS4GatewayInterface.ID_TYPE);
		partyIdTo.setValue(receiver);
		List<PartyId> listPartyId2 = paramTo.getPartyId();
		listPartyId2.add(partyIdTo);
		partyInfo.setTo(paramTo);

		userMessage.setPartyInfo(partyInfo);

		// collaboration info
		CollaborationInfo collaborationInfo = new CollaborationInfo();
		collaborationInfo.setAction(documentId);
		Service service = new Service();
		/* service.setType("XXXXXXX"); */
		service.setValue(processId);
		collaborationInfo.setService(service);

		// Which one needs to be over here ?
		collaborationInfo.setConversationId(correlationId);

		userMessage.setCollaborationInfo(collaborationInfo);

		// message info
		MessageInfo info = new MessageInfo();
		// TODO: here they only set RefToMessageId, but I think it's not
		// necessary for us?
		userMessage.setMessageInfo(info);

		// payload info
		PayloadInfo p = new PayloadInfo();

		Property prop = new Property();
		prop.setName("MimeType");
		prop.setValue("application/xml");
		
		PartProperties pProp = new PartProperties();
		pProp.getProperty().add(prop);

		Description desc = new Description();
		desc.setValue("#bodyload");

		PartInfo pInfo = new PartInfo();
		pInfo.setHref("#bodyload");

		pInfo.setDescription(desc);
		pInfo.setPartProperties(pProp);

		p.getPartInfo().add(pInfo);
		userMessage.setPayloadInfo(p);

		List<UserMessage> listUserMessage = result.getUserMessage();
		listUserMessage.add(userMessage);
		return result;
	}

	private SendRequest buildRequest(String pathToFile) throws Exception {
		SendRequest request = new SendRequest();
		PayloadType payload = new PayloadType();
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(pathToFile, "r");
			byte[] content = new byte[(int) f.length()];
			f.read(content);
			payload.setValue(content);
			payload.setContentType("application/xml");
			payload.setPayloadId("#bodyload");

			request.setBodyload(payload);
		} catch (Exception e) {
			throw e;
		} finally {
			if (f != null)
				f.close();
		}

		return request;
	}

	protected Map<String, String> treatSBDHrequest(InputStream input) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
		SBDHHandler handler = new SBDHHandler();
		saxParser.parse(input, handler);
		return handler.getResultMap();
	}

	private EndpointType getEndpoint(SMPServiceCaller aSMPClient, ParticipantIdentifierType recipient, DocumentIdentifierType documentIdentifier, ProcessIdentifierType process) throws Exception {
		SignedServiceMetadataType metadata;
		List<ProcessType> allProcesses;
		List<EndpointType> endpoints = null;

		metadata = aSMPClient.getServiceRegistration(recipient, documentIdentifier);
		allProcesses = metadata.getServiceMetadata().getServiceInformation().getProcessList().getProcess();
		for (ProcessType processType : allProcesses) {
			if (IdentifierUtils.areIdentifiersEqual(processType.getProcessIdentifier(), process))
				endpoints = processType.getServiceEndpointList().getEndpoint();
		}

		if (endpoints == null)
			return null;

		if (endpoints.size() == 1)
			return endpoints.get(0);
		else {
			List<String> orderedProtocols = new ArrayList<String>();
			try {
				int orderAS2 = Integer.parseInt(properties.getProperty(PropertiesUtil.AS2_ENDPOINT_PREFERENCE_ORDER));
				int orderEBMS = Integer.parseInt(properties.getProperty(PropertiesUtil.EBMS_ENDPOINT_PREFERENCE_ORDER));
				if (orderAS2 != 0) {
					if (orderEBMS != 0) {
						if (orderAS2 > orderEBMS){
							orderedProtocols.add(PROTOCOL_AS2);
							orderedProtocols.add(PROTOCOL_EBMS);
						} else {
							orderedProtocols.add(PROTOCOL_EBMS);
							orderedProtocols.add(PROTOCOL_AS2);
						}
					} else {
						orderedProtocols.add(PROTOCOL_AS2);
					}
				} else {
					if (orderEBMS != 0) {
						orderedProtocols.add(PROTOCOL_EBMS);
					} else {
						s_aLogger.error("There is an error in dispatcher config - Endpoint preference order not defined");
					}
				}
			} catch (NumberFormatException e) {
				s_aLogger.error("There is an error in dispatcher config - Endpoint preference order doesn't contain only numbers");
				return null;
			}

			for (String protocol : orderedProtocols)
				for (EndpointType endpoint : endpoints) {
					if (protocol.equalsIgnoreCase(endpoint.getTransportProfile()))
						return endpoint;
				}
		}

		return null;

	}

	private void prepareResponse(HttpServletResponse resp, int responseCode, String contentType, String message) {
		try {
			resp.setStatus(responseCode);
			resp.setContentType(contentType);
			resp.getWriter().write(message);
			resp.getWriter().flush();
		} catch (Exception e) {
			s_aLogger.error("SendWebservice.prepareResponse(): Error trying to get the response writer", e);
		}
		return;
	}

}
