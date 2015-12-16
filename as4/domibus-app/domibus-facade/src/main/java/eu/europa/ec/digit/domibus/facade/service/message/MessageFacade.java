package eu.europa.ec.digit.domibus.facade.service.message;

public interface MessageFacade<T> {

	/**
	 * Submits and processes a new message request.
	 *
	 * @param messageObject message to be processed
	 * @return an updated message with message identifier
	 */
	public T submit(T messageObject);

	/**
	 * Retrieves a message for given messageIdentifier
	 * @param messageIdentifier of a message
	 * @return a message
	 */
	public T retrieve(String messageIdentifier);

}
