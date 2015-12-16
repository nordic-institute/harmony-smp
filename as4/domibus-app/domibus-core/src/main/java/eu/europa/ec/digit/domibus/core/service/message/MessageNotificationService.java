package eu.europa.ec.digit.domibus.core.service.message;

import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

public interface MessageNotificationService {

	/**
	 * Notifies a message to the interested party.
	 *
	 * @param message message to be notified
	 * @param notificationServiceContext contextual parameters for this service request
	 */
	public void notify(MessageBO message, NotificationServiceContext notificationServiceContext);
}
