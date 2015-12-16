package eu.europa.ec.digit.domibus.core.service.message;

import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;

/**
 *
 * @author Vincent Dijkstra
 *
 */
public interface MessageRetrievalService {

	/**
	 * Returns a message for given message identifier.
	 *
	 * @param messageIdentifier a message identifier
	 * @return a message
	 */
    public MessageBO retrieve(String messageIdentifier);

}
