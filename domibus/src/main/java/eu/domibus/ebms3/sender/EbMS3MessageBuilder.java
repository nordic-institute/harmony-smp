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

package eu.domibus.ebms3.sender;

import eu.domibus.common.MSHRole;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.dao.AttachmentDAO;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.*;
import eu.domibus.ebms3.common.CompressionService;
import eu.domibus.ebms3.common.MessageIdGenerator;
import eu.domibus.ebms3.sender.exception.SendMessageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Service
public class EbMS3MessageBuilder {


    private static final Log LOG = LogFactory.getLog(EbMS3MessageBuilder.class);
    private final eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory ebMS3Of = new eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory();
    @Autowired
    private MessageFactory messageFactory;
    @Autowired
    private AttachmentDAO attachmentDAO;
    @Autowired
    @Qualifier(value = "jaxbContextMessagingOnly")
    private JAXBContext jaxbContext;
    @Autowired
    private DocumentBuilderFactory documentBuilderFactory;
    @Autowired
    private MessageIdGenerator messageIdGenerator;

    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public SOAPMessage buildSOAPMessage(SignalMessage signalMessage, LegConfiguration leg) throws EbMS3Exception {
        return this.buildSOAPMessage(null, signalMessage, leg);

    }

    public SOAPMessage buildSOAPMessage(UserMessage userMessage, LegConfiguration leg) throws EbMS3Exception {
        return this.buildSOAPMessage(userMessage, null, leg);
    }

    //TODO: If Leg is used in future releases we have to update this method
    public SOAPMessage buildSOAPFaultMessage(Error ebMS3error) throws EbMS3Exception {
        SignalMessage signalMessage = new SignalMessage();
        signalMessage.getError().add(ebMS3error);

        SOAPMessage soapMessage = this.buildSOAPMessage(signalMessage, null);

        try {
            // An ebMS signal does not require any SOAP Body: if the SOAP Body is not empty, it MUST be ignored by the MSH, as far as interpretation of the signal is concerned.
            //TODO: locale is static
            soapMessage.getSOAPBody().addFault(SOAPConstants.SOAP_RECEIVER_FAULT, "An error occurred while processing your request. Please check the message header for more details.", Locale.ENGLISH);
        } catch (SOAPException e) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0004, "An error occurred while processing your request. Please check the message header for more details.", e, MSHRole.RECEIVING);
        }

        return soapMessage;
    }

    public SOAPMessage buildSOAPMessage(UserMessage userMessage, SignalMessage signalMessage, LegConfiguration leg) throws EbMS3Exception {
        String messageId = null;
        SOAPMessage message;
        try {
            message = this.messageFactory.createMessage();

            final Messaging messaging = this.ebMS3Of.createMessaging();
            if (userMessage != null) {
                if (userMessage.getMessageInfo() != null && userMessage.getMessageInfo().getTimestamp() == null) {
                    userMessage.getMessageInfo().setTimestamp(new Date());
                }
                messageId = userMessage.getMessageInfo().getMessageId();
                messaging.setUserMessage(userMessage);
                for (PartInfo partInfo : userMessage.getPayloadInfo().getPartInfo()) {
                    this.attachPayload(partInfo, message);
                }

            }
            if (signalMessage != null) {
                MessageInfo msgInfo = new MessageInfo();

                messageId = this.messageIdGenerator.generateMessageId();
                msgInfo.setMessageId(messageId);
                msgInfo.setTimestamp(new Date());


                signalMessage.setMessageInfo(msgInfo);
            }
            messaging.setSignalMessage(signalMessage);
            this.jaxbContext.createMarshaller().marshal(messaging, message.getSOAPHeader());
            message.saveChanges();

        } catch (final SAXParseException e) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0001, "Payload in body must be valid XML", messageId, e, null);
        } catch (final JAXBException | SOAPException | ParserConfigurationException | IOException | SAXException ex) {
            throw new SendMessageException(ex);
        }
        return message;
    }

    private void attachPayload(PartInfo partInfo, SOAPMessage message) throws ParserConfigurationException, SOAPException, IOException, SAXException {
        String mimeType = null;
        boolean compressed = false;
        for (Property prop : partInfo.getPartProperties().getProperties()) {
            if (Property.MIME_TYPE.equals(prop.getName())) {
                mimeType = prop.getValue();
            }
            if (CompressionService.COMPRESSION_PROPERTY_KEY.equals(prop.getName()) && CompressionService.COMPRESSION_PROPERTY_VALUE.equals(prop.getValue())) {
                compressed = true;
            }
        }
        byte[] binaryData = this.attachmentDAO.loadBinaryData(partInfo.getEntityId());
        DataSource dataSource = new ByteArrayDataSource(binaryData, compressed ? CompressionService.COMPRESSION_PROPERTY_VALUE : mimeType);
        DataHandler dataHandler = new DataHandler(dataSource);
        if (partInfo.isInBody() && mimeType != null && mimeType.toLowerCase().contains("xml")) { //TODO: respect empty soap body config
            this.documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder builder = this.documentBuilderFactory.newDocumentBuilder();
            message.getSOAPBody().addDocument(builder.parse(dataSource.getInputStream()));
            partInfo.setHref(null);
            return;
        }
        AttachmentPart attachmentPart = message.createAttachmentPart(dataHandler);
        attachmentPart.setContentId(partInfo.getHref());
        message.addAttachmentPart(attachmentPart);
    }
}
