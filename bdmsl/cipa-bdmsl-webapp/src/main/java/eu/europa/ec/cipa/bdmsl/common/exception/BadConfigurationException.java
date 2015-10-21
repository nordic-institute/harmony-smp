package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 23/06/2015.
 */
public class BadConfigurationException extends TechnicalException {

    public BadConfigurationException(String message) {
        super(IErrorCodes.BAD_CONFIGURATION_ERROR, message);
    }

    public BadConfigurationException(String message, Throwable t) {
        super(IErrorCodes.BAD_CONFIGURATION_ERROR, message, t);
    }
}