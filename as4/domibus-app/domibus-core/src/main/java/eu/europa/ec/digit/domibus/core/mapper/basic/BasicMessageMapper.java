package eu.europa.ec.digit.domibus.core.mapper.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import eu.europa.ec.digit.domibus.common.aggregate.components.AddressInfoType;
import eu.europa.ec.digit.domibus.common.aggregate.components.AttachmentType;
import eu.europa.ec.digit.domibus.common.aggregate.components.AttachmentsType;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayBody;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayHeader;
import eu.europa.ec.digit.domibus.common.aggregate.components.MessageContentType;
import eu.europa.ec.digit.domibus.common.aggregate.components.MessageInfoType;
import eu.europa.ec.digit.domibus.common.aggregate.components.ReceiverType;
import eu.europa.ec.digit.domibus.common.aggregate.components.SenderType;
import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;
import eu.europa.ec.digit.domibus.common.util.xml.XMLUtils;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.domibus.MessageHeaderBO;
import eu.europa.ec.digit.domibus.domain.domibus.PartyBO;
import eu.europa.ec.digit.domibus.domain.domibus.PayloadBO;
import eu.europa.ec.digit.domibus.domain.domibus.PropertyBO;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;

@Component
public class BasicMessageMapper {

    /* ---- Constants ---- */
    public static final String MIME_TYPE = "MimeType";
    public static final String FILE_NAME = "FileName";
    public static final String ATTACHMENT_CONTENT_TYPE = "application/octet-stream";


    /* ---- Instance Variables ---- */
    @Autowired
    private Environment environment = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */
    public MessageBO mapTo(GatewayEnvelope envelope) {
        GatewayHeader gatewayHeader = envelope.getGatewayHeader();
        GatewayBody gatewayBody = envelope.getGatewayBody();
        MessageBO messageBO = new MessageBO();

        try {
            messageBO.setHeader(messageHeader(gatewayHeader));
            messageBO.setBody(this.payload(gatewayBody.getMessageContent()));
            messageBO.add(attachments(gatewayBody.getAttachments()));
            return messageBO;
        } catch (IOException exception) {
            throw new DomibusProgramException("message.domibus.parsing.error.progam.002");
        } catch (TransformerException transformerException) {
            throw new DomibusProgramException("message.domibus.parsing.error.progam.002");
        }
    }

    private MessageHeaderBO messageHeader(GatewayHeader gatewayHeader) {
        MessageInfoType messageInfo = gatewayHeader.getMessageInfo();
        AddressInfoType addressInfo = gatewayHeader.getAddressInfo();

        MessageHeaderBO messageHeader = new MessageHeaderBO();
        messageHeader.setConversationId(messageInfo.getCorrelationId());
        messageHeader.setRefToMessageId(messageInfo.getMessageId());
        messageHeader.setFromParty(fromParty(addressInfo.getSender()));
        messageHeader.setToParty(toParty(addressInfo.getReceiver()));
        messageHeader.setAction(emptyOrNull(environment.getProperty("domibus.message.header.action")));
        messageHeader.setService(emptyOrNull(environment.getProperty("domibus.message.header.service")));
        messageHeader.setServiceType(emptyOrNull(environment.getProperty("domibus.message.header.service.type")));
        messageHeader.setAgreementRef(emptyOrNull(environment.getProperty("domibus.message.header.agreement")));
        messageHeader.setAgreementRefType(emptyOrNull(environment.getProperty("domibus.message.header.agreement.type")));

        return messageHeader;
    }

    private PartyBO fromParty(SenderType sender) {
        PartyBO party = new PartyBO();
        party.setId(sender.getId());
        party.setRole(emptyOrNull(environment.getProperty("domibus.party.from.role")));
        party.setType(emptyOrNull(environment.getProperty("domibus.party.from.type")));
        return party;
    }

    private PartyBO toParty(ReceiverType receiver) {
        PartyBO party = new PartyBO();
        party.setId(receiver.getId());
        party.setRole(emptyOrNull(environment.getProperty("domibus.party.to.role")));
        party.setType(emptyOrNull(environment.getProperty("domibus.party.to.type")));
        return party;
    }

    private PayloadBO payload(MessageContentType messageContent) throws TransformerException {
        PayloadBO payloadBO = new PayloadBO();
        payloadBO.setId(environment.getProperty("domibus.message.content.id"));
        payloadBO.setProperties(setMimeType());

        // Convert payload into InputStream
        Document doc = messageContent.getAny().getOwnerDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), outputTarget);
        payloadBO.setData(new ByteArrayInputStream(outputStream.toByteArray()));
        return payloadBO;
    }

    private Set<PropertyBO> setMimeType() {
        Set<PropertyBO> set = new HashSet<PropertyBO>();
        PropertyBO mimeType = new PropertyBO();
        mimeType.setName(MIME_TYPE);
        mimeType.setValue("text/xml");
        set.add(mimeType);
        return set;
    }

    private Set<PayloadBO> attachments(AttachmentsType attachments) throws IOException {
        Set<PayloadBO> set = new HashSet<PayloadBO>();
        if (attachments != null) {
            for (AttachmentType attachment : attachments.getAttachment()) {
                PayloadBO payloadBO = new PayloadBO();
                payloadBO.setProperties(properties(attachment));
                payloadBO.setId(environment.getProperty("domibus.message.attachment.id"));
                payloadBO.setData(attachment.getValue().getInputStream());
                set.add(payloadBO);
            }
        }
        return set;
    }

    private Set<PropertyBO> properties(AttachmentType attachment) {
        Set<PropertyBO> set = new HashSet<PropertyBO>();
        PropertyBO mimeType = new PropertyBO();
        mimeType.setName(MIME_TYPE);
        mimeType.setValue(attachment.getContentType());
        set.add(mimeType);
        PropertyBO fileName = new PropertyBO();
        fileName.setName(FILE_NAME);
        fileName.setValue(attachment.getFileName());
        set.add(fileName);
        return set;
    }

    public GatewayEnvelope mapFrom(MessageBO messageBO) {

        try {
            GatewayHeader gatewayHeader = new GatewayHeader();
            gatewayHeader.setMessageInfo(this.messageInfo(messageBO.getHeader()));

            // Set the messageID
            gatewayHeader.getMessageInfo().setMessageId(messageBO.getMessageId().getMessageId());

            gatewayHeader.setAddressInfo(addressInfo(messageBO.getHeader()));

            GatewayBody gatewayBody = new GatewayBody();
            gatewayBody.setMessageContent(this.messageContent(messageBO.getBody()));
            gatewayBody.setAttachments(this.attachments(messageBO.getPayloads()));

            return new GatewayEnvelope(gatewayHeader, gatewayBody);
        } catch (IOException exception) {
            throw new DomibusProgramException("message.domibus.program.error.mapper.002");
        }
    }

    private MessageInfoType messageInfo(MessageHeaderBO messageHeaderBO) {
        MessageInfoType messageInfo = new MessageInfoType();
        messageInfo.setCorrelationId(messageHeaderBO.getConversationId());
        //messageInfo.setMessageId(messageHeaderBO.getRefToMessageId());
        messageInfo.setTimestamp(XMLUtils.getDateTimeNow());
        return messageInfo;
    }

    private MessageContentType messageContent(PayloadBO payloadBO) {
        MessageContentType messageContent = new MessageContentType();
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document document = builder.parse(payloadBO.getData());
            messageContent.setAny(document.getDocumentElement());
            return messageContent;
        } catch (Exception e) {
            throw new DomibusProgramException("message.domibus.parsing.error.progam.002");
        }
    }

    private AttachmentsType attachments(Set<PayloadBO> payloads) throws IOException {
        AttachmentsType attachments = new AttachmentsType();
        if (payloads != null) {
            for (PayloadBO payloadBO : payloads) {
                this.mimeType(payloadBO.getProperties());
                AttachmentType attachment = new AttachmentType();
                attachment.setContentType(this.mimeType(payloadBO.getProperties()));
                attachment.setFileName(this.fileName(payloadBO.getProperties()));
                attachment.setValue(dataHandler(payloadBO));
                attachments.getAttachment().add(attachment);
            }
        }
        return attachments;
    }

    private String mimeType(Set<PropertyBO> properties) {
        if (properties != null) {
            for (PropertyBO property : properties) {
                if (MIME_TYPE.equals(property.getName())) {
                    return property.getValue();
                }
            }
        }
        return null;
    }

    private String fileName(Set<PropertyBO> properties) {
        if (properties != null) {
            for (PropertyBO property : properties) {
                if (FILE_NAME.equals(property.getName())) {
                    return property.getValue();
                }
            }
        }
        return null;
    }

    private DataHandler dataHandler(PayloadBO payloadBO) throws IOException {
        ByteArrayDataSource ds = new ByteArrayDataSource(payloadBO.getData(), ATTACHMENT_CONTENT_TYPE);
        return new DataHandler(ds);
    }

    private AddressInfoType addressInfo(MessageHeaderBO messageHeaderBO) {
        AddressInfoType addressInfo = new AddressInfoType();
        ReceiverType receiver = new ReceiverType();
        receiver.setId(messageHeaderBO.getToParty().getId());
        addressInfo.setReceiver(receiver);
        SenderType sender = new SenderType();
        sender.setId(messageHeaderBO.getFromParty().getId());
        addressInfo.setSender(sender);
        return addressInfo;

    }

    private String emptyOrNull(String property) {
        if (property.isEmpty()) {
            return null;
        } else {
            return property;
        }
    }

    /* ---- Getters and Setters ---- */
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
