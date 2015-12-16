package eu.europa.ec.digit.domibus.facade.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.digit.domibus.facade.policy.domibus.notification.DomibusNotificationPolicy;

@Service
public class NotificationFacadeImpl implements NotificationFacade {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	private DomibusNotificationPolicy notificationPolicy = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Transactional (propagation = Propagation.REQUIRED, readOnly = false)
	@Override
	public void notify(String messageIdentifier) {
		this.notificationPolicy.process(messageIdentifier);
	}

	/* ---- Getters and Setters ---- */

	public DomibusNotificationPolicy getNotificationPolicy() {
		return notificationPolicy;
	}

	public void setNotificationPolicy(DomibusNotificationPolicy notificationPolicy) {
		this.notificationPolicy = notificationPolicy;
	}


}
