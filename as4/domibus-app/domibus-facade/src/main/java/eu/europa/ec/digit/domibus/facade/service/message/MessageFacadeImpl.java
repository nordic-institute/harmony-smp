package eu.europa.ec.digit.domibus.facade.service.message;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.digit.domibus.facade.policy.domibus.RetrievalPolicy;
import eu.europa.ec.digit.domibus.facade.policy.domibus.SubmissionPolicy;

public class MessageFacadeImpl<T> implements MessageFacade<T> {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    private SubmissionPolicy<T> submissionPolicy = null;
    private RetrievalPolicy<T> retrievalPolicy = null;

    /* ---- Business Methods ---- */

    @Transactional (propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public T submit(T message) {
        return submissionPolicy.process(message);
    }

    @Transactional (propagation = Propagation.REQUIRED, readOnly = false)
	@Override
	public T retrieve(String messageIdentifier) {
		return retrievalPolicy.process(messageIdentifier);
	}

    /* ---- Getters and Setters ---- */

	public SubmissionPolicy<T> getSubmissionPolicy() {
		return submissionPolicy;
	}

	public void setSubmissionPolicy(SubmissionPolicy<T> submissionPolicy) {
		this.submissionPolicy = submissionPolicy;
	}

	public RetrievalPolicy<T> getRetrievalPolicy() {
		return retrievalPolicy;
	}

	public void setRetrievalPolicy(RetrievalPolicy<T> retrievalPolicy) {
		this.retrievalPolicy = retrievalPolicy;
	}

}
