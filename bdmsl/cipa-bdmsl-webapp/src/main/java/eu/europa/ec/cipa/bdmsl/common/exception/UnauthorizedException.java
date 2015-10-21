package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class UnauthorizedException extends TechnicalException {

    public UnauthorizedException(String message) {
        super(IErrorCodes.UNAUTHORIZED_ERROR, message);
    }

    public UnauthorizedException(String message, Throwable t) {
        super(IErrorCodes.UNAUTHORIZED_ERROR, message, t);
    }
}
