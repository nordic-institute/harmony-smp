package eu.europa.ec.digit.domibus.core.mapper.ebms;

import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;
import eu.europa.ec.digit.domibus.core.mapper.AbstractMapper;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.domibus.MessageHeaderBO;
import eu.europa.ec.digit.domibus.domain.domibus.MessageIdBO;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.submission.Submission;

@Component
public class SubmissionMapper {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    @Autowired
    private SubmissionHeaderMapper submissionHeaderMapper = null;

    @Autowired
    private SubmissionPayloadMapper submissionPayloadMapper = null;


    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public Submission mapTo(MessageBO messageBO) throws IOException {

        Submission submission = new Submission();

        MessageHeaderBO messageHeaderBO = messageBO.getHeader();
        submission = submissionHeaderMapper.mapTo(messageHeaderBO);
        submission = submissionPayloadMapper.mapBodyTo(messageBO.getBody(), submission);
        submission = submissionPayloadMapper.mapAttachmentTo(messageBO.getPayloads(), submission);
        return submission;
    }

    public MessageBO mapFrom(Submission submission) {

        MessageBO messageBO = new MessageBO();

        messageBO.setMessageId(new MessageIdBO());
        messageBO.getMessageId().setMessageId(submission.getMessageId());
        messageBO.setHeader(submissionHeaderMapper.mapFrom(submission));

        try {
            messageBO = submissionPayloadMapper.mapFrom(submission, messageBO);
        } catch (IOException exception) {
            throw new DomibusProgramException("message.domibus.program.error.mapper.002");
        }
        return messageBO;
    }

}
