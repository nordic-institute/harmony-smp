package eu.europa.ec.edelivery.smp.error.exceptions;

import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Smp ResponseStatusException extension to hold also smp business error code. Exception is used for REST API "Fault" responses
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPResponseStatusException extends ResponseStatusException {
    private ErrorBusinessCode errorBusinessCode;

    public SMPResponseStatusException(ErrorBusinessCode errorBusinessCode, HttpStatus httpStatus, String sMsg) {
        super(httpStatus, sMsg);
        this.errorBusinessCode = errorBusinessCode;
    }

    public ErrorBusinessCode getErrorBusinessCode() {
        return errorBusinessCode;
    }

}
