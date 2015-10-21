package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class GenericTechnicalException extends TechnicalException {

    public GenericTechnicalException(String message) {
        super(IErrorCodes.GENERIC_TECHNICAL_ERROR, message);
    }

    public GenericTechnicalException(String message, Throwable t) {
        super(IErrorCodes.GENERIC_TECHNICAL_ERROR, message, t);
    }
}
