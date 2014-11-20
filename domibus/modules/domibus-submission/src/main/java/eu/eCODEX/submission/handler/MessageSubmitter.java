package eu.eCODEX.submission.handler;

import eu.eCODEX.submission.handler.impl.DbMessageHandler;
import eu.eCODEX.submission.validation.exception.ValidationException;

/**
 * Implementations of this interface handle the submission of messages from the backend to holodeck.
 *
 * @param <T> Data transfer object (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the backend and holodeck
 * @author Christian Koch
 * @see DbMessageHandler
 */
public interface MessageSubmitter<T> {

    /**
     * Submits a message to holodeck to be processed.
     *
     * @param messageData the message to be processed
     * @return the messageId of the submitted message
     * @throws eu.eCODEX.submission.validation.exception.ValidationException
     */
    public String submit(T messageData) throws ValidationException;
}
