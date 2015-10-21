package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class DuplicateParticipantException extends TechnicalException {

    public DuplicateParticipantException(String message) {
        super(IErrorCodes.DUPLICATE_PARTICIPANT_ERROR, message);
    }


    public DuplicateParticipantException(String message, Throwable t) {
        super(IErrorCodes.DUPLICATE_PARTICIPANT_ERROR, message, t);
    }
}
