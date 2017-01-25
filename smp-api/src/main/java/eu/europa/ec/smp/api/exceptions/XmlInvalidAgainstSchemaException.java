package eu.europa.ec.smp.api.exceptions;

/**
 * Created by migueti on 19/01/2017.
 */
public class XmlInvalidAgainstSchemaException extends Exception {

    public XmlInvalidAgainstSchemaException(String message) {
        super(message);
    }

    public XmlInvalidAgainstSchemaException(String message, Exception e) {
        super(message, e);
    }
}
