package eu.europa.ec.digit.domibus.facade.policy.domibus;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.digit.domibus.common.exception.DomibusValidationException;
import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.service.message.MessageRetrievalService;
import eu.europa.ec.digit.domibus.core.service.message.MessageValidationService;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;

/**
 *
 * @author Vincent Dijkstra
 *
 * @param <T> class of the incoming message
 */
public abstract class RetrievalPolicy<T> {

    /* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

    /* ---- Instance Variables ---- */

	@Autowired
	private MessageRetrievalService messageRetrievalService = null;

	@Autowired
	private MessageValidationService messageValidationService = null;


    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public final T process(String messageIdentifier) {

    	// Convert message to internal message object
    	log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageRetrievalService");
        MessageBO messageObject = this.retrieve(messageIdentifier);

        // Validate the incoming message
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageValidationService");
        this.validate(messageObject);

        // Log incoming request
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageLogService");
        this.log(messageObject);

        // Convert internal message into result message
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageConversionService");
        T message = this.convert(messageObject);

        // Send a response
        this.send(message);

        return message;
    }


	/**
	 * Returns a message for given message identifier.
	 *
	 * @param messageIdentifier a message identifier
	 * @return a message
	 */
    protected final MessageBO retrieve(String messageIdentifier) {
    	return messageRetrievalService.retrieve(messageIdentifier);
    }

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
     * Converts the internal message to the original message class.
     *
     * @param messageObject an internal messageObject
     * @return original message object
     */
    protected abstract T convert(MessageBO messageObject);

    /**
     * Send a response. This method can be implemented for those who need to send an
     * a-synchronous response to the original caller of the service. It is in particular
     * useful for access via a queueing mechanism.
     *
     * @param message the actual response
     */
    protected abstract void send(T message);

    /* ---- Getters and Setters ---- */

	public MessageRetrievalService getMessageRetrievalService() {
		return messageRetrievalService;
	}

	public void setMessageRetrievalService(MessageRetrievalService messageRetrievalService) {
		this.messageRetrievalService = messageRetrievalService;
	}


	public MessageValidationService getMessageValidationService() {
		return messageValidationService;
	}


	public void setMessageValidationService(MessageValidationService messageValidationService) {
		this.messageValidationService = messageValidationService;
	}

}
