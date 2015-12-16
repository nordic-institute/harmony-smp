package eu.europa.ec.digit.domibus.base.dao.ws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import eu.europa.ec.digit.domibus.base.policy.NotificationEndpointPolicy;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

@Repository ("wsDAO")
public class WSDAOImpl<T, R> implements WSDAO<T, R> {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	@Qualifier ("notificationEndpointSelectionPolicy")
	private Object notificationEndpointSelectionPolicy = null;

	/* ---- Business Methods ---- */

	@Override
	@SuppressWarnings ("unchecked")
	public R submit(T messageObject, NotificationServiceContext notificationServiceContext) {
		return ((Map<String, NotificationEndpointPolicy<T, R>>)notificationEndpointSelectionPolicy).
				get(notificationServiceContext.getDestination()).submit(messageObject);
	}

	/* ---- Getters and Setters ---- */

	public Object getNotificationEndpointSelectionPolicy() {
		return notificationEndpointSelectionPolicy;
	}

	public void setNotificationEndpointSelectionPolicy(Object notificationEndpointSelectionPolicy) {
		this.notificationEndpointSelectionPolicy = notificationEndpointSelectionPolicy;
	}


}
