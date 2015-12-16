package eu.europa.ec.digit.domibus.core.service.message;

import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;

/**
 *
 * @author Vincent Dijkstra
 *
 */
public interface MessageSubmissionService {

    /**
     * Processes the incoming message, the actual submission.
     *
     * @param messageObject internal message object
     * @return messageObject with updated message identifier
     */
    public MessageBO submit(final MessageBO messageObject);

}
