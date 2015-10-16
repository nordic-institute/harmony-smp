package eu.eCODEX.submission.transformer;

import eu.domibus.ebms3.submit.EbMessage;
import eu.eCODEX.submission.transformer.impl.BackendInterfaceMessageTransformer;
import eu.eCODEX.submission.validation.Validator;
import eu.eCODEX.submission.validation.exception.ValidationException;

/**
 * Implementations of this interface transsform a message of type {@link EbMessage} to an object of type {@literal <T>}
 *
 * @param <U> Data transfer object (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the backend and holodeck
 * @see BackendInterfaceMessageTransformer
 */
public interface MessageRetrievalTransformer<U> {

    /**
     * transforms the EbMessage to the typed object
     *
     * @param message the message to be transformed
     * @param target  The Object the message is transformed to
     * @return the transformed message
     * @throws eu.eCODEX.submission.validation.exception.ValidationException
     */
    public U transformFromEbMessage(EbMessage message, U target) throws ValidationException;

    /**
     * The validator called after the message was transformed
     *
     * @param validator
     */
    public void setPostRetrievalValidator(Validator<U> validator);

    /**
     * The validator called before the message will be transformed
     *
     * @param validator
     */
    public void setPreRetrievalValidator(Validator<EbMessage> validator);
}
