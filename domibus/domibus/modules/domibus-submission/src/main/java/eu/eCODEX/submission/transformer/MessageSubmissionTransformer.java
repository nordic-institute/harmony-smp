package eu.eCODEX.submission.transformer;


import eu.domibus.ebms3.submit.EbMessage;
import eu.eCODEX.submission.transformer.impl.BackendInterfaceMessageTransformer;
import eu.eCODEX.submission.validation.Validator;
import eu.eCODEX.submission.validation.exception.ValidationException;

/**
 * Implementations of this interface transsform a message of type {@literal <T>} to an object of type {@link EbMessage}
 *
 * @param <T> Data transfer object (http://en.wikipedia.org/wiki/Data_transfer_object) transported between the backend and holodeck
 * @see BackendInterfaceMessageTransformer
 */
public interface MessageSubmissionTransformer<T> {

    /**
     * transforms the typed object to an EbMessage
     *
     * @param messageData the message to be transformed
     * @return the transformed message
     * @throws eu.eCODEX.submission.validation.exception.ValidationException
     */
    public EbMessage transformToEbMessage(T messageData) throws ValidationException;

    /**
     * The validator called after the message was transformed
     *
     * @param validator
     */
    public void setPostSubmissionValidator(Validator<EbMessage> validator);

    /**
     * The validator called before the message will be transformed
     *
     * @param validator
     */
    public void setPreSubmissionValidator(Validator<T> validator);
}
