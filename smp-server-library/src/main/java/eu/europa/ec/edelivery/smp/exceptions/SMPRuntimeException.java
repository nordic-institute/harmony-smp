package eu.europa.ec.edelivery.smp.exceptions;

/**
 *
 */
public class SMPRuntimeException  extends RuntimeException  {
    private ErrorCode errorCode;

    public SMPRuntimeException(ErrorCode errorCode, Object ... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    public SMPRuntimeException(ErrorCode errorCode, Throwable th, Object ... args) {
        super(errorCode.getMessage(args), th);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
