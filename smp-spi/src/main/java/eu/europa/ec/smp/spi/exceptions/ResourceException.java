package eu.europa.ec.smp.spi.exceptions;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 *
 * The external validation library throws the exception if the payload validation does not pass.
 */
public class ResourceException extends Exception {
    public enum ErrorCode {
        PARSE_ERROR,
        PROCESS_ERROR,
        INVALID_RESOURCE,
        INVALID_PARAMETERS,
        INTERNAL_ERROR,
    }

    ErrorCode errorCode;
    public ResourceException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public ResourceException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
