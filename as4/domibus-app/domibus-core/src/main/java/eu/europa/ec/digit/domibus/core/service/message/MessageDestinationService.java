package eu.europa.ec.digit.domibus.core.service.message;

import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

public interface MessageDestinationService {

	/**
	 * Sets the destination on the {@link NotificationServiceContext} for given message.
	 *
	 * @param messageBO the message to notify
	 * @param notificationServiceContext a contextual set of parameters the notification service
	 */
	public void destination(MessageBO messageBO, NotificationServiceContext notificationServiceContext);
}
