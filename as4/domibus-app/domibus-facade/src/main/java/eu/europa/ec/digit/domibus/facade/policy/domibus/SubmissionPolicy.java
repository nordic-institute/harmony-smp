package eu.europa.ec.digit.domibus.facade.policy.domibus;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.digit.domibus.common.exception.DomibusValidationException;
import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.service.message.MessageSubmissionService;
import eu.europa.ec.digit.domibus.core.service.message.MessageValidationService;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;

/**
 *
 * @author Vincent Dijkstra
 *
 * @param <T> class of the incoming message
 */
public abstract class SubmissionPolicy<T> {

    /* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

    /* ---- Instance Variables ---- */

	@Autowired
	private MessageSubmissionService messageSubmissionService = null;

	@Autowired
	private MessageValidationService messageValidationService = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public final T process(T message) {

    	// Convert message to internal message object
    	log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageConversionService");
        MessageBO messageObject = this.convert(message);

        // Validate the incoming message
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageValidationService");
        this.validate(messageObject);

        // Log incoming request
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageLogService");
        this.log(messageObject);

        // Invoke submission service
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageSubmissionService");
        messageObject = this.submit(messageObject);

        // Convert internal message into result message
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageConversionService");
        message = this.convert(messageObject);

        // Send a response
        this.send(message);

        return message;
    }

    /**
     * Converts the incoming message to a {@link MessageBO} object that is used for
     * internal processing.
     *
     * @param message incoming message
     * @return internal message object
     */
    protected abstract MessageBO convert(T message);

    /**
     * Validates the {@link MessageBO} and guarantees that all mandatory
     * elements are present in the message.
     *
     * @param messageObject internal message object
     * @throws DomibusValidationException for an invalid message
     */
    protected final void validate(MessageBO messageObject) {
    	messageValidationService.validate(messageObject);
    }

    /**
     * Log the message for tracing purposes.
     *
     * @param messageObject internal message object
     */
    protected abstract void log(MessageBO messageObject);

    /**
     * Processes the incoming message, the actual submission.
     *
     * @param messageObject internal message object
     * @return messageObject with updated message identifier
     */
    protected final MessageBO submit(MessageBO messageObject) {
    	return messageSubmissionService.submit(messageObject);
    }

    /**
     * Converts the internal message to the original message class.
     *
     * @param messageObject an internal messageObject
     * @return original message object
     */
    protected abstract T convert(MessageBO messageObject);

    /**
     * Send a response. This method can be implemented for those who need to send an
     * a-synchronous response to the original caller of the service. It is in particular
     * useful for access via a queing mechanism.
     *
     * @param message the actual response
     */
    protected abstract void send(T message);

    /* ---- Getters and Setters ---- */

	public MessageSubmissionService getMessageSubmissionService() {
		return messageSubmissionService;
	}

	public void setMessageSubmissionService(MessageSubmissionService messageSubmissionService) {
		this.messageSubmissionService = messageSubmissionService;
	}

	public MessageValidationService getMessageValidationService() {
		return messageValidationService;
	}

	public void setMessageValidationService(MessageValidationService messageValidationService) {
		this.messageValidationService = messageValidationService;
	}

}
