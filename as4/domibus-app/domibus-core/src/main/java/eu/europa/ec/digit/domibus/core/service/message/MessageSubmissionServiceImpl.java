package eu.europa.ec.digit.domibus.core.service.message;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.domibus.submission.Submission;
import eu.domibus.submission.handler.MessageSubmitter;
import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;
import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.mapper.ebms.SubmissionConversion;
import eu.europa.ec.digit.domibus.core.mapper.ebms.SubmissionMapper;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.domibus.MessageIdBO;

@Service
public class MessageSubmissionServiceImpl implements MessageSubmissionService {

    /* ---- Constants ---- */
    private final Logger log = new Logger(getClass());

    /* ---- Instance Variables ---- */
    @Autowired
    private MessageSubmitter<Submission> messageSubmitter = null;

    @Autowired
    private SubmissionConversion submissionConversion = null;

    @Autowired
    private SubmissionMapper submissionMapper = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */
    @Override
    public final MessageBO submit(MessageBO messageBO) {
        Submission submission = new Submission();
        try {
            submission = submissionMapper.mapTo(messageBO);
        } catch (IOException ioException) {
            throw new DomibusProgramException("message.domibus.io.error.program.002", ioException);
        }

        String messageId = null;
        try {
            messageId = messageSubmitter.submit(submission);
        } catch (Exception exc) {
            log.businessLog(LogEvent.BUS_SUBMISSION_FAILED, exc.getMessage());
            log.error(exc.getMessage(), exc);
            throw new DomibusProgramException("message.domibus.ebms.error.program.003", exc);
        }
        // Create response
        messageBO = submissionMapper.mapFrom(submission);
        MessageIdBO messageIdBO = new MessageIdBO();
        messageIdBO.setMessageId(messageId);
        messageBO.setMessageId(messageIdBO);

        log.businessLog(LogEvent.BUS_SUBMISSION_SUCCESSFUL, messageBO.toString());
        return messageBO;
    }

    /* ---- Getters and Setters ---- */
    public MessageSubmitter<Submission> getMessageSubmitter() {
        return messageSubmitter;
    }

    public void setMessageSubmitter(MessageSubmitter<Submission> messageSubmitter) {
        this.messageSubmitter = messageSubmitter;
    }

    public SubmissionConversion getSubmissionConversion() {
        return submissionConversion;
    }

    public void setSubmissionConversion(SubmissionConversion submissionConversion) {
        this.submissionConversion = submissionConversion;
    }

}
