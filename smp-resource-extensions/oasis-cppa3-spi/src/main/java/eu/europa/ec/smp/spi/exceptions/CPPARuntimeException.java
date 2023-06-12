package eu.europa.ec.smp.spi.exceptions;

public class CPPARuntimeException extends RuntimeException{

    public enum ErrorCode {
        INITIALIZE_ERROR,
        PARSE_ERROR,
    }

    final ErrorCode errorCode;
    public CPPARuntimeException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public CPPARuntimeException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }
}
