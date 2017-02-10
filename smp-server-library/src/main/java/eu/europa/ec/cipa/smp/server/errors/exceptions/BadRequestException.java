package eu.europa.ec.cipa.smp.server.errors.exceptions;

import eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode;

/**
 * Created by migueti on 13/01/2017.
 */
public class BadRequestException extends RuntimeException {
    private ErrorBusinessCode errorBusinessCode;

    public BadRequestException(ErrorBusinessCode errorBusinessCode, String sMsg) {
        super(sMsg);
        this.errorBusinessCode = errorBusinessCode;
    }

    public ErrorBusinessCode getErrorBusinessCode() {
        return errorBusinessCode;
    }
}
