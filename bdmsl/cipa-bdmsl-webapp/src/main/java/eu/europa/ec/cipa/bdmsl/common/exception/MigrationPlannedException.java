package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class MigrationPlannedException extends TechnicalException {

    public MigrationPlannedException(String message) {
        super(IErrorCodes.MIGRATION_PLANNED_ERROR, message);
    }


    public MigrationPlannedException(String message, Throwable t) {
        super(IErrorCodes.MIGRATION_PLANNED_ERROR, message, t);
    }
}
