package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 23/06/2015.
 */
public class BadRequestException extends TechnicalException {

    public BadRequestException(String message) {
        super(IErrorCodes.BAD_REQUEST_ERROR, message);
    }

    public BadRequestException(String message, Throwable t) {
        super(IErrorCodes.BAD_REQUEST_ERROR, message, t);
    }
}