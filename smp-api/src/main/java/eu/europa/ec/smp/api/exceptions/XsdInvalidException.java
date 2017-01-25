package eu.europa.ec.smp.api.exceptions;

/**
 * Created by migueti on 19/01/2017.
 */
public class XsdInvalidException extends Exception {

    public XsdInvalidException(String message) {
        super(message);
    }

    public XsdInvalidException(String message, Exception e) {
        super(message, e);
    }
}
