/*
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.start.server;

import com.sun.xml.ws.rx.mc.api.MakeConnectionSupported;
import eu.peppol.start.client.accesspointClient;
import eu.peppol.start.exception.AccessPointClientException;
import eu.peppol.start.exception.DocumentTypeNotAcceptedException;
import eu.peppol.start.exception.LookupException;
import eu.peppol.start.exception.PingMessageException;
import eu.peppol.start.exception.TransportException;
import eu.peppol.start.exception.UnknownEndpointException;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.peppol.start.metadata.MessageMetadata;
import eu.peppol.start.sml.SMLLookup;
import eu.peppol.start.soap.SOAPHeaderObject;
import eu.peppol.start.soap.handler.SOAPInboundHandler;
import eu.peppol.start.transport.ReceiverChannel;
import eu.peppol.start.util.Configuration;

import eu.peppol.start.util.Util;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import org.busdox._2010._02.channel.fault.StartException;
import org.busdox.transport.Identifiers.Identifiers;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Resource;

/**
 * WebService implementation.
 *
 * @author  Jose Gorvenia Narvaez(jose@alfa1lab.com)
 *          Dante Malaga(dante@alfa1lab.com)
 *          Marcelo Tataje Salinas(marcelo@alfa1lab.com)
 *          George Reátegui Ravina (jorge@alfa1lab.com)
 */
@WebService(serviceName = "accesspointService",
portName = "ResourceBindingPort",
endpointInterface = "org.w3._2009._02.ws_tra.Resource",
targetNamespace = "http://www.w3.org/2009/02/ws-tra",
wsdlLocation = "WEB-INF/wsdl/start-wsdl.wsdl")
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@HandlerChain(file = "soap-handlers.xml")
@Addressing
@MakeConnectionSupported
public class accesspointService {

    /**
     * Name of the service.
     */
    public static final String SERVICE_NAME = "accesspointService";
    /**
     * Certificate format.
     */
    private static final String CERT_X509 = "X.509";
    /**
     * Keystore file.
     */
    private static final String KEY_PATH = "server.keystore";
    /**
     * Keystore password.
     */
    private static final String KEY_PASS = "server.Keystore.password";
    /**
     * Configuration for properties file.
     */
    private static Configuration conf;
    /**
     * SML address.
     */
    private static String smlServiceAddress;
    /**
     * Context of the Web Service.
     */
    @javax.annotation.Resource
    private static WebServiceContext webServiceContext;
    /**
     * Logger to follow this class behavior.
     */
    private static org.apache.log4j.Logger logger4J =
            org.apache.log4j.Logger.getLogger(accesspointService.class);

    /**
     * Constructor of the service in which configuration loader is initialized
     * to retrieve properties from config files.
     */
    public accesspointService() {
        conf = Configuration.getInstance();
        smlServiceAddress = conf.getProperty("sml.service.address");
    }

    public org.w3._2009._02.ws_tra.GetResponse get(org.w3._2009._02.ws_tra.Get body) {
        throw new UnsupportedOperationException("Not supported by the current implementation according to the specifications");
    }

    public org.w3._2009._02.ws_tra.PutResponse put(org.w3._2009._02.ws_tra.Put body) {
        throw new UnsupportedOperationException("Not supported by the current implementation according to the specifications");
    }

    public org.w3._2009._02.ws_tra.DeleteResponse delete(org.w3._2009._02.ws_tra.Delete body) {
        throw new UnsupportedOperationException("Not supported by the current implementation according to the specifications");
    }

    /**
     * Create a response to send the document and the metadata.
     * @param body containing the binary data and document.
     * @return CreateResponse data type if all data is correct.
     * @throws FaultMessage if any problem or mismatch is found while
     * processing information.
     */
    @Action(input = "http://www.w3.org/2009/02/ws-tra/Create",
    output = "http://www.w3.org/2009/02/ws-tra/CreateResponse",
    fault = {
        @FaultAction(className = org.w3._2009._02.ws_tra.FaultMessage.class,
        value = "http://busdox.org/2010/02/channel/fault")})
    public org.w3._2009._02.ws_tra.CreateResponse create(org.w3._2009._02.ws_tra.Create body)
            throws FaultMessage {

        String ourAPUrl = null;
        String recipientAPUrl = null;

        SOAPHeaderObject soapHeader = SOAPInboundHandler.SOAPHEADER;

        try {
            //Check if it is a Ping Message
            if (!isPingMessage(soapHeader)) {

                MessageMetadata metadata = new MessageMetadata(soapHeader);
                X509Certificate receiverCert = null;

                try {
                    
                    /*Get the Current AP Url from the Web Service Application Context. */
                    ourAPUrl = getOwnUrl();
                    
                    /*Get the recipient AP url from performing a Metadata Lookup. */
                    recipientAPUrl = getAccessPointAddress(ourAPUrl, metadata);
                    logger4J.info("Our Endpoint Address: " + ourAPUrl);
                    logger4J.info("Recipient Endpoint Address: " + recipientAPUrl);

                    /*Get the recipient AP Metadata stored in the Metadata Certificate Entry. */
                    String certEntry = getAccessPointCertificate(metadata);
                    logger4J.debug("Recipient Certificate Entry: \n" + certEntry);

                    /* Convert Recipient Metadata Certificate into X509 format. */
                    receiverCert = Util.generateX509Certificate(certEntry);

                } catch (UnknownEndpointException ex) {
                    logger4J.error(ex.getMessage(), ex);
                    Logger.getLogger(accesspointService.class.getName()).log(Level.SEVERE, ex.getMessage());

                    StartException startEx = new StartException();
                    startEx.setAction(conf.getProperty("fault.action"));
                    startEx.setDetails(conf.getProperty("fault.unknownendpoint.detail"));
                    startEx.setFaultcode(conf.getProperty("fault.code"));
                    startEx.setFaultstring(conf.getProperty("fault.unknownendpoint.reason"));

                    throw new FaultMessage(ex.getMessage(), startEx);
                } catch (DocumentTypeNotAcceptedException ex) {
                    logger4J.error(ex.getMessage(), ex);
                    Logger.getLogger(accesspointService.class.getName()).log(Level.SEVERE, ex.getMessage());

                    StartException startEx = new StartException();
                    startEx.setAction(conf.getProperty("fault.action"));
                    startEx.setDetails(conf.getProperty("fault.documenttypenotaccepted.detail"));
                    startEx.setFaultcode(conf.getProperty("fault.code"));
                    startEx.setFaultstring(conf.getProperty("fault.documenttypenotaccepted.reason"));

                    throw new FaultMessage(ex.getMessage(), startEx);
                } catch (LookupException ex) {
                    logger4J.error(ex.getMessage(), ex);
                    Logger.getLogger(accesspointService.class.getName()).log(Level.SEVERE, ex.getMessage());

                    StartException startEx = new StartException();
                    startEx.setAction(conf.getProperty("fault.action"));
                    startEx.setDetails(conf.getProperty("fault.servererror.detail"));
                    startEx.setFaultcode(conf.getProperty("fault.code"));
                    startEx.setFaultstring(conf.getProperty("fault.servererror.reason"));

                    String errorMSG = conf.getProperty("error.message.server.send");
                    throw new FaultMessage(errorMSG, startEx);
                }

                /* Check Sender and Receiver AP to evaluate Local or Remote Delivery. */
                if (isTheSameURL(recipientAPUrl, ourAPUrl)) {
                    logger4J.info("This is a local request - storage directly: " + metadata.getRecipientValue());
                    try {
                        deliverLocally(metadata, body);
                    } catch (TransportException ex) {
                        logger4J.error(ex.getMessage(), ex);
                        Logger.getLogger(accesspointService.class.getName()).log(Level.SEVERE, ex.getMessage());

                        StartException startEx = new StartException();
                        startEx.setAction(conf.getProperty("fault.action"));
                        startEx.setDetails(conf.getProperty("fault.servererror.detail"));
                        startEx.setFaultcode(conf.getProperty("fault.code"));
                        startEx.setFaultstring(conf.getProperty("fault.servererror.reason"));

                        throw new FaultMessage(ex.getMessage(), startEx);
                    }
                } else {
                    logger4J.info("This is a request for a remote AccessPoint: " + recipientAPUrl);
                    try {
                        /*The ping method is used before sending to retrieve the Certificate from the Receiver
                        AP in order to validate it against the SMP Certificate Metadata. If they dismatch a
                         * SOAP fault will be thrown before sending the message.
                         */
                        pingRemoteService(body, recipientAPUrl, receiverCert);

                        /*If the ping message validation is successful then the message is delivered Remotely. */
                        deliverRemotely(metadata, body, recipientAPUrl, receiverCert);
                    } catch (AccessPointClientException ex) {
                        logger4J.error(ex.getMessage(), ex);
                        Logger.getLogger(accesspointService.class.getName()).log(Level.SEVERE, ex.getMessage());

                        StartException startEx = new StartException();
                        startEx.setAction(conf.getProperty("fault.action"));
                        startEx.setDetails(conf.getProperty("fault.servererror.detail"));
                        startEx.setFaultcode(conf.getProperty("fault.code"));
                        startEx.setFaultstring(conf.getProperty("fault.servererror.reason"));

                        throw new FaultMessage(ex.getMessage(), startEx);
                    } catch (RuntimeException ex) {
                        logger4J.error(ex.getMessage(), ex);
                        Logger.getLogger(accesspointService.class.getName()).log(Level.SEVERE, ex.getMessage());

                        StartException startEx = new StartException();
                        startEx.setAction(conf.getProperty("fault.action"));
                        startEx.setDetails(conf.getProperty("fault.securityerror.reason"));
                        startEx.setFaultcode(conf.getProperty("fault.code"));
                        startEx.setFaultstring(ex.getMessage());

                        throw new FaultMessage(ex.getMessage(), startEx);
                    }

                }
                logger4J.info("Transaction Complete:"
                        + "\n\tSender: " + metadata.getSenderValue()
                        + "\n\tRecipient: " + metadata.getRecipientValue()
                        + "\n\tDocument: " + metadata.getDocumentIdValue());
            }
        } catch (PingMessageException ex) {
            StartException startEx = new StartException();
            startEx.setAction(conf.getProperty("fault.action"));
            startEx.setDetails(conf.getProperty("fault.servererror.detail"));
            startEx.setFaultcode(conf.getProperty("fault.code"));
            startEx.setFaultstring(conf.getProperty("fault.servererror.reason"));

            throw new FaultMessage(ex.getMessage(), startEx);
        }
        return new CreateResponse();
    }

    /**
     * Retrieve the url of the sender endpoint retrieved from the web service Context.
     * @return String with the adress of the sender accesspoint.
     */
    public static String getOwnUrl() {

        ServletRequest servletRequest = (ServletRequest) webServiceContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);

        String contextPath = ((ServletContext) webServiceContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT)).getContextPath();
        String thisAPUrl = servletRequest.getScheme()
                + "://" + servletRequest.getServerName()
                + ":" + servletRequest.getLocalPort()
                + contextPath + '/';
        
        return thisAPUrl + SERVICE_NAME;        
    }

    /**
     * Retrieve the destination endpoint address.
     * @param senderAPUrl as the url adress of the sender endpoint.
     * @param metadata as the data of participants and identifiers.
     * @return String data type with the endpoint address.
     */
    public String getAccessPointAddress(String senderAPUrl, MessageMetadata metadata)
            throws UnknownEndpointException, DocumentTypeNotAcceptedException, LookupException {

        SMLLookup sml = SMLLookup.getInstance();

        return sml.getEndpointAddress(smlServiceAddress,
                metadata.getRecipientScheme(),
                metadata.getRecipientValue(),
                metadata.getDocumentIdScheme(),
                metadata.getDocumentIdValue());
    }

    /**
     * Retrieve the certificate from the recipient Metadata.
     * @param metadata as the metadata of the recipient.
     * @param metadata as the data of participants and identifiers.
     * @return String data type with the endpoint address.
     */
    public String getAccessPointCertificate(MessageMetadata metadata)
            throws UnknownEndpointException, DocumentTypeNotAcceptedException, LookupException {

        SMLLookup sml = SMLLookup.getInstance();

        String cert = sml.getEndpointCertificate(smlServiceAddress,
                metadata.getRecipientScheme(),
                metadata.getRecipientValue(),
                metadata.getDocumentIdScheme(),
                metadata.getDocumentIdValue(),
                metadata.getProcessIdScheme(),
                metadata.getProcessIdValue());

        return Util.completeCertificateEntry(cert);
    }

    /**
     * Validates if the sender URL is equals to recipient URL.
     * @param recipientAPUrl represents the recipient.
     * @param senderAPUrl represents the sender.
     * @return true if sender and reciever are the same. If not, false.
     */
    private boolean isTheSameURL(String recipientAPUrl, String senderAPUrl) {
        return recipientAPUrl.indexOf(senderAPUrl) >= 0;
    }

    /**
     * Validates if the operation is a ping and not a sending process.
     * @param header which is the SOAP header as part of the envelope.
     * @return true if is a ping, false if it is another process.
     */
    private boolean isPingMessage(SOAPHeaderObject header) {

        boolean looksLikePingMessage = false;
        boolean pingProtocolCheck = true;
        StringBuffer headerErrors = new StringBuffer();

        if (header.getSenderIdentifier() != null
                && header.getSenderIdentifier().getScheme() != null
                && header.getSenderIdentifier().getScheme().equals(Identifiers.PING_SENDER_SCHEME)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append(" Sender scheme invalid.");
        }

        if (header.getSenderIdentifier() != null
                && header.getSenderIdentifier().getValue() != null
                && header.getSenderIdentifier().getValue().equals(Identifiers.PING_SENDER_VALUE)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append("Sender id invalid.");
        }

        if (header.getRecipientIdentifier() != null
                && header.getRecipientIdentifier().getScheme() != null
                && header.getRecipientIdentifier().getScheme().equals(Identifiers.PING_RECPIENT_SCHEME)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append("Recipient sheme invalid.");
        }

        if (header.getRecipientIdentifier() != null
                && header.getRecipientIdentifier().getValue() != null
                && header.getRecipientIdentifier().getValue().equals(Identifiers.PING_RECIPIENT_VALUE)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append(" Recipient id invalid.");
        }

        if (header.getDocumentIdentifier() != null
                && header.getDocumentIdentifier().getValue() != null
                && header.getDocumentIdentifier().getValue().equals(Identifiers.PING_DOCUMENT_VALUE)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append(" Document value invalid.");
        }

        if (header.getProcessIdentifier() != null
                && header.getProcessIdentifier().getScheme() != null
                && header.getProcessIdentifier().getScheme().equals(Identifiers.PING_PROCESS_SCHEME)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append(" Process scheme invalid.");
        }

        if (header.getProcessIdentifier() != null
                && header.getProcessIdentifier().getValue() != null
                && header.getProcessIdentifier().getValue().equals(Identifiers.PING_PROCESS_VALUE)) {
            looksLikePingMessage = true;

        } else {
            pingProtocolCheck = false;
            headerErrors.append(" Process value invalid.");
        }

        if (looksLikePingMessage && !pingProtocolCheck) {
            String errorMSG = conf.getProperty("error.message.server.ping");
            throw new PingMessageException(errorMSG + headerErrors.toString());
        }
        logger4J.info("looksLikePingMessage: " + looksLikePingMessage);
        logger4J.info("pingProtocolCheck: " + pingProtocolCheck);
        return looksLikePingMessage;
    }

    /**
     * Deliver to same accesspoint by copying the document in local folder.
     * @param metadata which contains data of identifiers.
     * @param body which contains document and binary data.
     */
    public void deliverLocally(MessageMetadata metadata, Create body)
            throws TransportException {

        String channelId = metadata.getRecipientValue();

        if (channelId != null) {
            metadata.setChannelId(channelId);
        }

        List< Object> objects = body.getAny();

        if (objects != null && objects.size() == 1) {
            Element element = (Element) objects.iterator().next();
            Document businessDocument = element.getOwnerDocument();

            ServletContext context = (ServletContext) webServiceContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

            ReceiverChannel receiverChannel = new ReceiverChannel();
            receiverChannel.deliverMessage(context, metadata, businessDocument);
        }
    }

    /**
     * Sending directly to a Remote AccessPoint Service
     * @param metadata
     * @param body
     * @param recipientAPUrl
     */
    public void deliverRemotely(MessageMetadata metadata,
            Create body, String recipientAPUrl,
            X509Certificate metaCert) throws AccessPointClientException {

        accesspointClient client = accesspointClient.getInstance();
        client.setMetadataCertificate(metaCert);
        client.printSOAPLogging(false);
        Resource port = client.getPort(recipientAPUrl);
        logger4J.debug("Sending message...");
        client.send(port, metadata.getSoapHeader(), body);
        logger4J.debug("Message Sent");
    }

    /**
     * Execute a ping to a remote service.
     * @param recipientAPUrl represents the address of the recipient Accesspoint.
     * @param metaCert represents the SMP certificate.
     */
    public void pingRemoteService(Create body, String recipientAPUrl, X509Certificate metaCert) {
        accesspointClient client = accesspointClient.getInstance();
        MessageMetadata metadata = new MessageMetadata();
        client.setMetadataCertificate(metaCert);
        client.printSOAPLogging(false);
        Resource port = client.getPort(recipientAPUrl);
        logger4J.debug("Sending ping message...");
        client.send(port, metadata.getSoapHeader(), body);
        logger4J.debug("Ping Message Sent");
    }
}