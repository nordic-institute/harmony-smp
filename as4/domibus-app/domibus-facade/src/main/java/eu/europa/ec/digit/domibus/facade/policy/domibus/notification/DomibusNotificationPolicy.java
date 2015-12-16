package eu.europa.ec.digit.domibus.facade.policy.domibus.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.digit.domibus.core.service.message.MessageNotificationService;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;
import eu.europa.ec.digit.domibus.facade.policy.domibus.NotificationPolicy;

@Component
public class DomibusNotificationPolicy extends NotificationPolicy {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	private MessageNotificationService messageNotificationService = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	protected void log(MessageBO message, NotificationServiceContext notificationServiceContext) {
		// Not implemented yet.
	}

	@Override
	protected void notify(MessageBO message, NotificationServiceContext notificationServiceContext) {
		messageNotificationService.notify(message, notificationServiceContext);
	}

	/* ---- Getters and Setters ---- */

	public MessageNotificationService getMessageNotificationService() {
		return messageNotificationService;
	}

	public void setMessageNotificationService(MessageNotificationService messageNotificationService) {
		this.messageNotificationService = messageNotificationService;
	}

}
