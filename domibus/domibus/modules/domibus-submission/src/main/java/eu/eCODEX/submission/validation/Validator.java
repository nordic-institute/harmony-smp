package eu.eCODEX.submission.validation;

import eu.eCODEX.submission.validation.exception.ValidationException;

/**
 * Validator is an interface for all Validators inside the e-CODEX backend architecture.
 *
 * @param <T>
 */
public interface Validator<T> {

    /**
     * This method validates the given object. If the validation fails, an excpetion is thrown.
     *
     * @param message
     * @throws ValidationException
     */
    public void validate(T message) throws ValidationException;
}
