package eu.europa.ec.digit.domibus.base.dao.ws;

import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

public interface WSDAO<T, R> {

	/**
	 * Sends given message to the correct endpoint. It does this by first selecting
	 * the correct endpoint, and the by selecting the endpoint submission policy
	 * for given message.
	 *
	 * @param message a XML string representing the BRIS message
	 * @param messageContext contextual parameters for given message

	 * @return acknowledgment of correct reception of message
	 */
	public R submit(T message, NotificationServiceContext serviceContext);
}
