package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class SmpNotFoundException extends TechnicalException {

    public SmpNotFoundException(String message) {
        super(IErrorCodes.SMP_NOT_FOUND_ERROR, message);
    }


    public SmpNotFoundException(String message, Throwable t) {
        super(IErrorCodes.SMP_NOT_FOUND_ERROR, message, t);
    }
}
