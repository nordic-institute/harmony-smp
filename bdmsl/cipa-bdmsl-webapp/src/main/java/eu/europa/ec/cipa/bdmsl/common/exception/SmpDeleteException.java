package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class SmpDeleteException extends TechnicalException {

    public SmpDeleteException(String message) {
        super(IErrorCodes.SMP_DELETE_ERROR, message);
    }


    public SmpDeleteException(String message, Throwable t) {
        super(IErrorCodes.SMP_DELETE_ERROR, message, t);
    }
}
