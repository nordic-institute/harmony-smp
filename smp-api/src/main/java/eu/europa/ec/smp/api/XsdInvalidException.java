package eu.europa.ec.smp.api;

import org.xml.sax.SAXException;

/**
 * Created by migueti on 19/01/2017.
 */
public class XsdInvalidException extends SAXException {

    public XsdInvalidException(String message) {
        super(message);
    }
}
