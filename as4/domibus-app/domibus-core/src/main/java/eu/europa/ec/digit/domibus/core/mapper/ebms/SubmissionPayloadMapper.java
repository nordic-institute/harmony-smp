package eu.europa.ec.digit.domibus.core.mapper.ebms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.submission.Submission;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.domibus.PayloadBO;
import org.springframework.core.env.Environment;

@Component
public class SubmissionPayloadMapper {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */
    @Autowired
    protected Environment environment = null;

    @Autowired
    private SubmissionPayloadPropertiesMapper submissionPayloadPropertiesMapper = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */
    public Submission mapBodyTo(PayloadBO payloadBO, Submission submission) throws IOException {
        if (payloadBO != null) {
            Set<PayloadBO> payloadsBO = new HashSet<>();
            payloadsBO.add(payloadBO);
            return mapPayloadTo(payloadsBO, submission, false);
        } else {
            return submission;
        }
    }

    public Submission mapAttachmentTo(Set<PayloadBO> payloadsBO, Submission submission) throws IOException {
        return mapPayloadTo(payloadsBO, submission, false);
    }

    private Submission mapPayloadTo(Set<PayloadBO> payloadsBO, Submission submission, boolean inBody)
            throws IOException {

        for (PayloadBO payloadBO : payloadsBO) {
            Properties payloadProperties = submissionPayloadPropertiesMapper.mapTo(payloadBO.getProperties());

            byte[] payloadData = null;
            payloadData = IOUtils.toByteArray(payloadBO.getData());

            submission.addPayload(payloadBO.getId(), payloadData, payloadProperties, inBody, payloadBO.getDescription(),
                    payloadBO.getSchemaLocation());
        }
        return submission;
    }

    public MessageBO mapFrom(Submission submission, MessageBO messageBO) throws IOException {

        for (final Submission.Payload payload : submission.getPayloads()) {
            PayloadBO payloadBO = new PayloadBO();
            payloadBO.setId(payload.getContentId());
            payloadBO.setSchemaLocation(payload.getSchemaLocation());
            payloadBO.setDescription(payload.getDescription());
            payloadBO.setProperties(submissionPayloadPropertiesMapper.mapFrom(payload.getPayloadProperties()));

            byte[] data = payload.getPayloadData();
            payloadBO.setData(new ByteArrayInputStream(data));
            if (environment.getProperty("domibus.message.content.id").compareToIgnoreCase(payload.getContentId()) == 0) {
                messageBO.setBody(payloadBO);
            } else {
                messageBO.add(payloadBO);
            }
        }
        return messageBO;
    }

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */
}
