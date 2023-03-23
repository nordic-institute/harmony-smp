package eu.europa.ec.smp.spi.exceptions;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 *
 * The external validation library throws the exception if the payload validation does not pass.
 */
public class SignatureException extends Exception {
    public enum ErrorCode {
        CONFIGURATION_ERROR,
        SIGNATURE_ERROR,
        INVALID_PARAMETERS,
        INTERNAL_ERROR,
    }

    ErrorCode errorCode;
    public SignatureException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public SignatureException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
