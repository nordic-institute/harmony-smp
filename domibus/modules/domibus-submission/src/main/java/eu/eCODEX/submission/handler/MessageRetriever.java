package eu.eCODEX.submission.handler;

import eu.eCODEX.submission.handler.impl.DbMessageHandler;
import eu.eCODEX.submission.validation.exception.ValidationException;

import java.util.Collection;

/**
 * Implementations of this interface handle the retrieval of messages from holodeck to the backend.
 *
 * @param <T> Data transfer object (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the backend and holodeck
 * @author Christian Koch
 * @see DbMessageHandler
 */

public interface MessageRetriever<T> {

    /**
     * provides the message with the corresponding messageId
     *
     * @param messageId the messageId of the message to retrieve
     * @param target    the object the message is stored in
     * @return the message object with the given messageId
     * @throws eu.eCODEX.submission.validation.exception.ValidationException
     */
    public T downloadMessage(String messageId, T target) throws ValidationException;

    /**
     * provides a list of messageIds which have not been downloaded yet
     *
     * @return a list of messages that have not been downloaded yet
     */
    public Collection<String> listPendingMessages();

    /**
     * provides the first message that has been not downloaded yet
     *
     * @param target the object the message is stored in
     * @return the first message that has been not downloaded yet
     */
    public T downloadNextMessage(T target) throws ValidationException;
}
