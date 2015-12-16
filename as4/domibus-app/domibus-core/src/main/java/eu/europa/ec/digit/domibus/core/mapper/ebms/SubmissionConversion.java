package eu.europa.ec.digit.domibus.core.mapper.ebms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.submission.Submission;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.domibus.MessageHeaderBO;
import eu.europa.ec.digit.domibus.domain.domibus.MessageIdBO;
import eu.europa.ec.digit.domibus.domain.domibus.PartyBO;
import eu.europa.ec.digit.domibus.domain.domibus.PayloadBO;
import eu.europa.ec.digit.domibus.domain.domibus.PropertyBO;

@Component
public class SubmissionConversion {
	
	/* ---- Constants ---- */
	private final Logger log = new Logger(getClass());
	
	/* ---- Instance Variables ---- */
	
	/* ---- Constructors ---- */
	
	/* ---- Business Methods ---- */
	
    public void convertBOToSubmission(final MessageBO messageBO, Submission submission) throws IOException {
 
        submission.setAction(messageBO.getHeader().getAction());
        submission.setAgreementRef(messageBO.getHeader().getAgreementRef());
        submission.setAgreementRefType(messageBO.getHeader().getAgreementRefType());
        submission.setService(messageBO.getHeader().getService());
        submission.setServiceType(messageBO.getHeader().getServiceType());
        submission.setRefToMessageId(messageBO.getHeader().getRefToMessageId());
        submission.setConversationId(messageBO.getHeader().getConversationId());
        convertBOToSubmissionPartyTo(messageBO.getHeader().getToParty(), submission);
        convertBOToSubmissionPartyFrom(messageBO.getHeader().getFromParty(), submission);
        convertBOToSubmissionMessageProperties(messageBO.getHeader().getMessageProperties(), submission);
        convertBOToSubmissionBody(messageBO.getBody(), submission);
        convertBOToSubmissionPayloads(messageBO.getPayloads(), submission);
    }

    public void convertSubmissionToBO(final Submission submission, MessageBO messageBO) {
        log.info("Converting submission message to BO message");

        convertSubmissionToBOHeader(submission, messageBO);
        convertSubmissionToBOMessageId(submission, messageBO);
        convertSubmissionToBOBodyAndPayloads(submission, messageBO);
    }

    private void convertSubmissionToBOBodyAndPayloads(Submission submission, MessageBO messageBO) {
        log.info("Convert Body and Payloads");
        for (final Submission.Payload payload : submission.getPayloads()) {
            PayloadBO payloadBO = new PayloadBO();
            payloadBO.setId(payload.getContentId());
            payloadBO.setSchemaLocation(payload.getSchemaLocation());
            payloadBO.setDescription(payload.getDescription());
            Set<PropertyBO> propertiesBO = new HashSet<>();
            for (final Map.Entry<Object, Object> entry : payload.getPayloadProperties().entrySet()) {
                propertiesBO.add(new PropertyBO(entry.getKey().toString(), entry.getValue().toString()));
            }
            payloadBO.setProperties(propertiesBO);
            byte[] data = payload.getPayloadData();
            payloadBO.setData(new ByteArrayInputStream(data));
            if (payload.isInBody()) {
                messageBO.setBody(payloadBO);
            } else {
                messageBO.add(payloadBO);
            }
        }
    }

    private void convertSubmissionToBOMessageId(Submission submission, MessageBO messageBO) {
        log.info("Convert MessageId");
        MessageIdBO messageIdBO = new MessageIdBO();
        messageIdBO.setMessageId(submission.getMessageId());
        messageBO.setMessageId(messageIdBO);
    }

    private void convertSubmissionToBOHeader(Submission submission, MessageBO messageBO) {
        log.info("Converting message header");
        MessageHeaderBO messageHeaderBO = new MessageHeaderBO();
        messageHeaderBO.setAction(submission.getAction());
        messageHeaderBO.setAgreementRef(submission.getAgreementRef());
        messageHeaderBO.setAgreementRefType(submission.getAgreementRefType());
        messageHeaderBO.setConversationId(submission.getConversationId());
        messageHeaderBO.setRefToMessageId(submission.getRefToMessageId());
        messageHeaderBO.setService(submission.getService());
        messageHeaderBO.setServiceType(submission.getServiceType());
        convertSubmissionToBOPartyTo(submission, messageHeaderBO);
        convertSubmissionToBOPartyFrom(submission, messageHeaderBO);
        convertSubmissionToBOMessageProperties(submission, messageHeaderBO);

        messageBO.setHeader(messageHeaderBO);
    }

    private void convertSubmissionToBOMessageProperties(Submission submission, MessageHeaderBO messageHeaderBO) {
        Set<PropertyBO> propertiesBO = new HashSet<>();
        log.info("Convert Properties");
        for (final Map.Entry<Object, Object> propertyEntry : submission.getMessageProperties().entrySet()) {
            final PropertyBO propertyBO = new PropertyBO();
            propertyBO.setName(propertyEntry.getKey().toString());
            propertyBO.setValue(propertyEntry.getValue().toString());
            propertiesBO.add(propertyBO);
        }

        messageHeaderBO.setMessageProperties(propertiesBO);
    }

    private void convertSubmissionToBOPartyFrom(Submission submission, MessageHeaderBO messageHeaderBO) {
        log.info("Converting From party");
        assert !CollectionUtils.isEmpty(submission.getFromParties()) : "Error, no FROM party";
        assert submission.getFromParties().size() > 1 : "Error, more than one FROM party is not allowed";
        
        Submission.Party from = submission.getFromParties().iterator().next();
        messageHeaderBO.setFromParty(new PartyBO(from.getPartyId(), from.getPartyIdType(), submission.getFromRole()));
    }

    private void convertSubmissionToBOPartyTo(Submission submission, MessageHeaderBO messageHeaderBO) {
        log.info("Converting To party");
        assert !CollectionUtils.isEmpty(submission.getToParties()) : "Error, no TO party";
        assert submission.getToParties().size() > 1 : "Error, more than one TO party is not allowed";

        Submission.Party to = submission.getToParties().iterator().next();
        messageHeaderBO.setToParty(new PartyBO(to.getPartyId(), to.getPartyIdType(), submission.getToRole()));
    }

    private void convertBOToSubmissionPartyTo(PartyBO to, Submission messageSubmission) {
    	assert to != null;
        messageSubmission.addToParty(to.getId(), to.getType());
        messageSubmission.setToRole(to.getRole());
    }

    private void convertBOToSubmissionPartyFrom(PartyBO from, Submission messageSubmission) {
        assert from != null;
        messageSubmission.addFromParty(from.getId(), from.getType());
        messageSubmission.setFromRole(from.getRole());
    }

    private void convertBOToSubmissionMessageProperties(Set<PropertyBO> properties, Submission submission) {
        if (CollectionUtils.isEmpty(properties)) {
            log.warn("Message properties are empty");
            return;
        }
        log.info("Converting message properties");
        for (PropertyBO property : properties) {
            submission.addMessageProperty(property.getName(), property.getValue());
        }
    }

    private void convertBOToSubmissionBody(PayloadBO body, Submission submission) throws IOException {
        // body is not required
        if (body == null || body.getData() == null) {
            log.info("Message body is empty");
            return;
        }

        log.info("Converting bodyload");
        String schemaLocation = body.getSchemaLocation();
        String contentId = null;
        Properties payloadProperties = new Properties();
        if (!CollectionUtils.isEmpty(body.getProperties())) {
            for (PropertyBO propertyBO : body.getProperties()) {
                payloadProperties.setProperty(propertyBO.getName(), propertyBO.getValue());
            }
        }
        boolean inBody = true;
        byte[] payloadData = IOUtils.toByteArray(body.getData());

        assert payloadData != null;
        assert payloadData.length != 0;
        
        submission.addPayload(contentId, payloadData, payloadProperties, inBody, null, schemaLocation);
    }

    private void convertBOToSubmissionPayloads(Set<PayloadBO> payloads, Submission submission) throws IOException {
        if (CollectionUtils.isEmpty(payloads)) {
            log.info("There are no payloads for this message.");
            return;
        }
        for (PayloadBO payloadBO : payloads) {
            if (payloadBO.getData() == null) {
                log.info("Empty payload " + payloadBO.getId());
            }

            log.info(String.format("Converting payload %s", payloadBO.getId()));
            Properties payloadProperties = new Properties();
            if (!CollectionUtils.isEmpty(payloadBO.getProperties())) {
                for (PropertyBO propertyBO : payloadBO.getProperties()) {
                    payloadProperties.setProperty(propertyBO.getName(), propertyBO.getValue());
                }
            }

            byte[] payloadData = IOUtils.toByteArray(payloadBO.getData());

            assert payloadData != null;
            assert payloadData.length != 0;
            submission.addPayload(payloadBO.getId(), payloadData, payloadProperties, false, null, null);
        }
    }
}
