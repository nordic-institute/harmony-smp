package eu.europa.ec.cipa.bdmsl.common.exception;

import eu.europa.ec.cipa.bdmsl.common.IErrorCodes;
import eu.europa.ec.cipa.common.exception.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ParticipantNotFoundException extends TechnicalException {

    public ParticipantNotFoundException(String message) {
        super(IErrorCodes.PARTICIPANT_NOT_FOUND_ERROR, message);
    }


    public ParticipantNotFoundException(String message, Throwable t) {
        super(IErrorCodes.PARTICIPANT_NOT_FOUND_ERROR, message, t);
    }
}
