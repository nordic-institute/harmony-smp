package eu.europa.ec.digit.domibus.core.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.domibus.submission.Submission;
import eu.domibus.submission.handler.MessageRetriever;
import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;
import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.mapper.ebms.SubmissionMapper;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;

@Service
public class MessageRetrievalServiceImpl implements MessageRetrievalService {

	/* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

	/* ---- Instance Variables ---- */

    @Autowired
    private MessageRetriever<Submission> retriever = null;

    @Autowired
    private SubmissionMapper submissionMapper = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	public MessageBO retrieve(String messageIdentifier) {
        Submission submission = null;
        try {
            submission = retriever.downloadMessage(messageIdentifier);
            log.businessLog(LogEvent.BUS_RETRIEVAL_SUCCESSFUL);
        } catch (Exception exception) {
            log.businessLog(LogEvent.BUS_RETRIEVAL_FAILED, exception.getMessage());
            log.error(exception.getMessage(), exception);
            throw new DomibusProgramException("message.domibus.program.error.ebms.004", exception);
        }

        MessageBO messageBO = submissionMapper.mapFrom(submission);
        log.businessLog(LogEvent.BUS_CONVERSION_SUCCESSFUL, messageBO.toString());

        return messageBO;
    }
	
	/* ---- Getters and Setters ---- */

	public MessageRetriever<Submission> getRetriever() {
		return retriever;
	}

	public void setRetriever(MessageRetriever<Submission> retriever) {
		this.retriever = retriever;
	}

	public SubmissionMapper getSubmissionMapper() {
		return submissionMapper;
	}

	public void setSubmissionMapper(SubmissionMapper submissionMapper) {
		this.submissionMapper = submissionMapper;
	}



}
