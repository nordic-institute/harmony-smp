package eu.europa.ec.digit.domibus.core.service.message;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.policy.notification.NotificationPolicy;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

@Service
public class MessageNotificationServiceImpl implements MessageNotificationService {

	/* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

	/* ---- Instance Variables ---- */

	@Autowired
	@Qualifier ("notificationSelectionPolicy")
	private Object notificationSelectionPolicy = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	@SuppressWarnings ("unchecked")
	public void notify(MessageBO message, NotificationServiceContext notificationServiceContext) {
		log.info("Pushing message to endpoint.");
		((Map<String, NotificationPolicy>)notificationSelectionPolicy).
			get(notificationServiceContext.getDestination()).notify(message, notificationServiceContext);
		log.info("Successfully notified endpoint.");
	}

	/* ---- Getters and Setters ---- */

	public Object getNotificationSelectionPolicy() {
		return notificationSelectionPolicy;
	}

	public void setNotificationSelectionPolicy(Object notificationSelectionPolicy) {
		this.notificationSelectionPolicy = notificationSelectionPolicy;
	}

}
