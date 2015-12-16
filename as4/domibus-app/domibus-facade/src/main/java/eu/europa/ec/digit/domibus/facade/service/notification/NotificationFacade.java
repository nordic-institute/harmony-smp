package eu.europa.ec.digit.domibus.facade.service.notification;

public interface NotificationFacade {

	/**
	 * Notifies a message to an interested party.
	 *
	 * @param messageIdentifier unique identifier of a message
	 */
	public void notify(String messageIdentifier);

}
