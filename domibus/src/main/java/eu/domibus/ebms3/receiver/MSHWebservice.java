/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.ebms3.receiver;

import eu.domibus.common.MSHRole;
import eu.domibus.common.MessageStatus;
import eu.domibus.common.configuration.model.Action;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.configuration.model.Mpc;
import eu.domibus.common.configuration.model.ReplyPattern;
import eu.domibus.common.dao.MessageLogDao;
import eu.domibus.common.dao.MessagingDao;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.MessageType;
import eu.domibus.common.model.logging.MessageLogEntry;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import eu.domibus.common.validators.PayloadProfileValidator;
import eu.domibus.common.validators.PropertyProfileValidator;
import eu.domibus.ebms3.common.CompressionService;
import eu.domibus.ebms3.common.MessageIdGenerator;
import eu.domibus.ebms3.common.TimestampDateFormatter;
import eu.domibus.ebms3.common.dao.PModeProvider;
import eu.domibus.ebms3.sender.MSHDispatcher;
import eu.domibus.submission.BackendConnector;
import eu.domibus.submission.MessageMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.attachment.AttachmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.w3c.dom.Node;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.*;
import javax.xml.ws.soap.SOAPBinding;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This method is responsible for the receiving of ebMS3 messages and the sending of signal messages like receipts or ebMS3 errors in return
 *
 * @author Christian Koch
 * @author Stefan Müller
 * @since 3.0
 */

@WebServiceProvider(portName = "mshPort", serviceName = "mshService")
@ServiceMode(Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class MSHWebservice implements Provider<SOAPMessage> {
    private static final Log LOG = LogFactory.getLog(MSHWebservice.class);

    @Autowired
    private MessagingDao messagingDao;

    @Autowired
    private MessageFactory messageFactory;

    @Autowired
    private MessageLogDao messageLogDao;

    private JAXBContext jaxbContext;

    @Autowired
    private TransformerFactory transformerFactory;

    @Autowired
    private PModeProvider pModeProvider;

    @Autowired
    private TimestampDateFormatter timestampDateFormatter;

    @Autowired
    private CompressionService compressionService;

    @Autowired
    private MessageIdGenerator messageIdGenerator;

    @Autowired
    private PayloadProfileValidator payloadProfileValidator;

    @Autowired
    private PropertyProfileValidator propertyProfileValidator;

    @Resource(name = "backends")
    private List<BackendConnector> backends;

    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    @Override
    @Transactional
    public SOAPMessage invoke(final SOAPMessage request) {
        SOAPMessage responseMessage = null;

        String pmodeKey = null;
        try {
            //FIXME: use a consistent way of property exchange between JAXWS and CXF message model. This: PropertyExchangeInterceptor
            pmodeKey = (String) request.getProperty(MSHDispatcher.PMODE_KEY_CONTEXT_PROPERTY);
        } catch (SOAPException e) {
            //this error should never occur because pmode handling is done inside the in-interceptorchain
            LOG.error("Cannot find PModeKey property for incoming Message");
            assert false;
        }

        LegConfiguration legConfiguration = pModeProvider.getLegConfiguration(pmodeKey);
        String messageId = "";
        try (StringWriter sw = new StringWriter()) {
            if (MSHWebservice.LOG.isDebugEnabled()) {

                this.transformerFactory.newTransformer().transform(
                        new DOMSource(request.getSOAPPart()),
                        new StreamResult(sw));

                MSHWebservice.LOG.debug(sw.toString());
                MSHWebservice.LOG.debug("received attachments:");
                Iterator i = request.getAttachments();
                while (i.hasNext()) {
                    MSHWebservice.LOG.debug(i.next());
                }
            }
            Messaging messaging = this.getMessaging(request);
            checkCharset(messaging);

            boolean messageExists = legConfiguration.getReceptionAwareness().getDuplicateDetection() && this.checkDuplicate(messaging);
            if (!messageExists && !(eu.domibus.common.configuration.model.Service.PING_SERVICE.equals(legConfiguration.getService().getValue())
                    && Action.PING_ACTION.equals(legConfiguration.getAction().getValue()))) { // ping messages are not stored/delivered
                messageId = this.persistReceivedMessage(request, legConfiguration, pmodeKey, messaging);
            }
            responseMessage = this.generateReceipt(request, legConfiguration, messageExists);


            if (!messageExists) {
                this.notifyBackends(messageId, legConfiguration);
            }

        } catch (TransformerException | SOAPException | JAXBException | IOException e) {
            throw new RuntimeException(e);
        } catch (EbMS3Exception e) {
            throw new WebServiceException(e);
        }

        return responseMessage;
    }


    /**
     * Required for AS4_TA_12
     *
     * @param messaging
     * @throws EbMS3Exception
     */
    private void checkCharset(Messaging messaging) throws EbMS3Exception {
        for (PartInfo partInfo : messaging.getUserMessage().getPayloadInfo().getPartInfo()) {
            for (Property property : partInfo.getPartProperties().getProperties()) {
                if (Property.CHARSET.equals(property.getName()) && !Property.CHARSET_PATTERN.matcher(property.getValue()).matches()) {
                    throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0003, property.getValue() + " is not a valid Charset", messaging.getUserMessage().getMessageInfo().getMessageId(), null, MSHRole.RECEIVING);
                }
            }
        }
    }

    /**
     * If message with same messageId is already in the database return <code>true</code> else <code>false</code>
     *
     * @param messaging
     * @return result of duplicate check
     */
    private Boolean checkDuplicate(Messaging messaging) {

        return messageLogDao.findByMessageId(messaging.getUserMessage().getMessageInfo().getMessageId(), MSHRole.RECEIVING) != null;


    }

    @Async
    private void notifyBackends(String messageId, LegConfiguration legConfiguration) {
        MessageMetadata metadata = new MessageMetadata(messageId, legConfiguration.getService(), legConfiguration.getAction(), MessageMetadata.Type.INBOUND);
        for (BackendConnector backend : this.backends) {
            if (backend.isResponsible(metadata)) {
                backend.messageNotification(metadata);
                break;
            }
        }


    }

    /**
     * Handles Receipt generation for a incoming message
     *
     * @param request          the incoming message
     * @param legConfiguration processing information of the message
     * @param duplicate        indicates whether or not the message is a duplicate
     * @return the response message to the incoming request message
     * @throws EbMS3Exception if generation of receipt was not successful
     */
    private SOAPMessage generateReceipt(SOAPMessage request, LegConfiguration legConfiguration, Boolean duplicate) throws EbMS3Exception {
        SOAPMessage responseMessage = null;

        assert legConfiguration != null;

        if (legConfiguration.getReliability() == null) {
            return responseMessage;
        }

        if (ReplyPattern.RESPONSE.equals(legConfiguration.getReliability().getReplyPattern())) {
            MSHWebservice.LOG.debug("Checking reliability for incoming message");
            try {
                responseMessage = this.messageFactory.createMessage();
                Source messageToReceiptTransform = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("./xslt/GenerateAS4Receipt.xsl"));
                Transformer transformer = this.transformerFactory.newTransformer(messageToReceiptTransform);
                Source requestMessage = request.getSOAPPart().getContent();
                transformer.setParameter("messageid", this.messageIdGenerator.generateMessageId());
                transformer.setParameter("timestamp", this.timestampDateFormatter.generateTimestamp());
                transformer.setParameter("nonRepudiation", Boolean.toString(legConfiguration.getReliability().isNonRepudiation()));

                DOMResult domResult = new DOMResult();

                transformer.transform(requestMessage, domResult);
                responseMessage.getSOAPPart().setContent(new DOMSource(domResult.getNode()));

//                transformer.transform(requestMessage, new DOMResult(responseMessage.getSOAPPart().getEnvelope()));
            } catch (TransformerConfigurationException | SOAPException e) {
                // this cannot happen
                assert false;
                throw new RuntimeException(e);
            } catch (TransformerException e) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0201, "Could not generate Receipt. Check security header and non-repudiation settings", null, e, MSHRole.RECEIVING);
            }
        }

        return responseMessage;
    }

    /**
     * This method persists incoming messages into the database (and handles decompression before)
     *
     * @param request          the message to persist
     * @param legConfiguration processing information for the message
     * @throws SOAPException
     * @throws JAXBException
     * @throws TransformerException
     * @throws IOException
     * @throws EbMS3Exception
     */
    //TODO: improve error handling
    private String persistReceivedMessage(SOAPMessage request, LegConfiguration legConfiguration, String pmodeKey, Messaging messaging) throws SOAPException, JAXBException, TransformerException, EbMS3Exception {


        boolean bodyloadFound = false;
        for (PartInfo partInfo : messaging.getUserMessage().getPayloadInfo().getPartInfo()) {
            String cid = partInfo.getHref();
            MSHWebservice.LOG.debug("looking for attachment with cid: " + cid);
            boolean payloadFound = false;
            if (cid == null || cid.isEmpty()) {
                if (bodyloadFound) {
                    throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0003, "More than one Partinfo without CID found", messaging.getUserMessage().getMessageInfo().getMessageId(), null, MSHRole.RECEIVING);
                }
                bodyloadFound = true;
                payloadFound = true;
                partInfo.setInBody(true);
                Node bodyContent = (((Node) request.getSOAPBody().getChildElements().next()));
                Source source = new DOMSource(bodyContent);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Result result = new StreamResult(out);
                Transformer transformer = this.transformerFactory.newTransformer();
                transformer.transform(source, result);
                partInfo.setBinaryData(out.toByteArray());
            }
            @SuppressWarnings("unchecked")
            Iterator<AttachmentPart> attachmentIterator = request.getAttachments();
            AttachmentPart attachmentPart;
            while (attachmentIterator.hasNext() && !payloadFound) {

                attachmentPart = attachmentIterator.next();
                //remove square brackets from cid for further processing
                attachmentPart.setContentId(AttachmentUtil.cleanContentId(attachmentPart.getContentId()));
                MSHWebservice.LOG.debug("comparing with: " + attachmentPart.getContentId());
                if (attachmentPart.getContentId().equals(AttachmentUtil.cleanContentId(cid))) {
                    partInfo.setBinaryData(attachmentPart.getRawContentBytes());
                    partInfo.setInBody(false);
                    payloadFound = true;
                }
            }
            if (!payloadFound) {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0011, "No Attachment found for cid: " + cid + " of message: " + messaging.getUserMessage().getMessageInfo().getMessageId(), messaging.getUserMessage().getMessageInfo().getMessageId(), null, MSHRole.RECEIVING);
            }
        }

        boolean compressed = this.compressionService.handleDecompression(messaging.getUserMessage(), legConfiguration);
        this.payloadProfileValidator.validate(messaging, pmodeKey);
        this.propertyProfileValidator.validate(messaging, pmodeKey);

        MSHWebservice.LOG.debug("Compression for message with id: " + messaging.getUserMessage().getMessageInfo().getMessageId() + " applied: " + compressed);
        MessageLogEntry messageLogEntry = new MessageLogEntry();
        messageLogEntry.setMessageId(messaging.getUserMessage().getMessageInfo().getMessageId());
        messageLogEntry.setMessageType(MessageType.USER_MESSAGE);
        messageLogEntry.setMshRole(MSHRole.RECEIVING);
        messageLogEntry.setReceived(new Date());
        String mpc = messaging.getUserMessage().getMpc();
        messageLogEntry.setMpc((mpc == null || mpc.isEmpty())? Mpc.DEFAULT_MPC:mpc);
        messageLogEntry.setMessageStatus(MessageStatus.RECEIVED);

        this.messageLogDao.create(messageLogEntry);
        this.messagingDao.create(messaging);
        return messageLogEntry.getMessageId();

    }

    private Messaging getMessaging(SOAPMessage request) throws SOAPException, JAXBException {
        Node messagingXml = (Node) request.getSOAPHeader().getChildElements(ObjectFactory._Messaging_QNAME).next();
        Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller(); //Those are not thread-safe, therefore a new one is created each call
        @SuppressWarnings("unchecked") JAXBElement<Messaging> root = (JAXBElement<Messaging>) unmarshaller.unmarshal(messagingXml);
        return root.getValue();
    }
}
