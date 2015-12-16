package eu.europa.ec.digit.domibus.core.policy.notification;

import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

public abstract class NotificationPolicy {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
	/**
	 * Notifies a message to the interested party.
	 *
	 * @param message message to be notified
	 * @param notificationServiceContext contextual parameters for this service request
	 */
	public abstract void notify(MessageBO message, NotificationServiceContext notificationServiceContext);

	/* ---- Getters and Setters ---- */

}
