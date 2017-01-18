package eu.europa.ec.cipa.smp.server.exception.common;

/**
 * Created by rodrfla on 18/01/2017
 */
public class TechnicalException extends Exception {

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable t) {
        super(message, t);
    }
}