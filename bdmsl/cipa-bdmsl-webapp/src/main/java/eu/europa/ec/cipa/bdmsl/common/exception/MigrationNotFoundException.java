package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class MigrationNotFoundException extends TechnicalException {

    public MigrationNotFoundException(String message) {
        super(IErrorCodes.MIGRATION_NOT_FOUND_ERROR, message);
    }


    public MigrationNotFoundException(String message, Throwable t) {
        super(IErrorCodes.MIGRATION_NOT_FOUND_ERROR, message, t);
    }
}
