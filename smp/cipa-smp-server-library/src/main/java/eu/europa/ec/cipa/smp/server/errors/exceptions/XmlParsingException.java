package eu.europa.ec.cipa.smp.server.errors.exceptions;

/**
 * Occurs when tried to parse malformed XML message.
 * Created by gutowpa on 06/01/2017.
 */
public class XmlParsingException extends RuntimeException {

    public XmlParsingException(Exception e) {
        super(e);
    }

}
