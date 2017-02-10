package eu.europa.ec.cipa.smp.server.errors.exceptions;


public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable t) {
        super(message, t);
    }
}
