package eu.eCODEX.submission.validation.exception;

/**
 * Indicates a violation of a validation rule that occured while validating a message
 *
 * @see eu.eCODEX.submission.validation.Validator
 */
public class ValidationException extends Exception {
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

}
