package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class SIG0Exception extends TechnicalException {

    public SIG0Exception(String message) {
        super(IErrorCodes.SIG0_ERROR, message);
    }

    public SIG0Exception(String message, Throwable t) {
        super(IErrorCodes.SIG0_ERROR, message, t);
    }
}
