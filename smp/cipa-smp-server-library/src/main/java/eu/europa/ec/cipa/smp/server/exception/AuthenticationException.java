package eu.europa.ec.cipa.smp.server.exception;

import eu.europa.ec.cipa.smp.server.exception.common.TechnicalException;

/**
 * Created by rodrfla on 18/01/2017.
 */
public class AuthenticationException extends TechnicalException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable t) {
        super(message, t);
    }
}
