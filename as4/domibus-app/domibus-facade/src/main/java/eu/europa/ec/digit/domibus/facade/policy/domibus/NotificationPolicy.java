package eu.europa.ec.digit.domibus.facade.policy.domibus;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.service.message.MessageDestinationService;
import eu.europa.ec.digit.domibus.core.service.message.MessageRetrievalService;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.MessageServiceType;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

/**
 *
 * @author Vincent Dijkstra
 *
 * @param <T> class of the incoming message
 */
public abstract class NotificationPolicy {

    /* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

    /* ---- Instance Variables ---- */

	@Autowired
	private MessageRetrievalService messageRetrievalService = null;

	@Autowired
	private MessageDestinationService messageDestinationService = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public final void process(String messageIdentifier) {
       	NotificationServiceContext notificationServiceContext = new NotificationServiceContext(MessageServiceType.NOTIFICATION);

    	// Retrieve message for messageIdentifier
    	log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageRetrievalService");
        MessageBO message = this.retrieve(messageIdentifier, notificationServiceContext);

        this.destination(message, notificationServiceContext);

        // Log incoming request
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "MessageLogService");
        this.log(message, notificationServiceContext);

        // Send a response
        log.businessLog(LogEvent.BUS_SERVICE_CALL, "NotificationService");
        this.notify(message, notificationServiceContext);
    }

	/**
	 * Returns a message for given message identifier.
	 *
	 * @param messageIdentifier a message identifier
	 * @param notificationServiceContext parameters for this service request
	 * @return a message
	 *
	 */
    protected final MessageBO retrieve(String messageIdentifier, NotificationServiceContext notificationServiceContext) {
    	return messageRetrievalService.retrieve(messageIdentifier);
    }

    /**
     * Set the actual destination, the endpoint, on the {@link NotificationServiceContext}. This information
     * can later on be used to route the message to its correct end destination.
     *
     * @param message the notification message
     * @param notificationServiceContext parameters for this service request
     */
    protected final void destination(MessageBO message, NotificationServiceContext notificationServiceContext) {
    	messageDestinationService.destination(message, notificationServiceContext);
    }

    /**
     * Log the message for tracing purposes.
     *
     * @param message internal message object
     * @param notificationServiceContext parameters for this service request
     */
    protected abstract void log(MessageBO message, NotificationServiceContext notificationServiceContext);

    /**
     * Notifies the message to the actual receiver.
     *
     * @param message the actual message
     * @param notificationServiceContext parameters for this service request
     */
    protected abstract void notify(MessageBO message, NotificationServiceContext notificationServiceContext);

    /* ---- Getters and Setters ---- */

	public MessageRetrievalService getMessageRetrievalService() {
		return messageRetrievalService;
	}

	public void setMessageRetrievalService(MessageRetrievalService messageRetrievalService) {
		this.messageRetrievalService = messageRetrievalService;
	}

	public MessageDestinationService getMessageDestinationService() {
		return messageDestinationService;
	}

	public void setMessageDestinationService(MessageDestinationService messageDestinationService) {
		this.messageDestinationService = messageDestinationService;
	}

}
