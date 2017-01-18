package eu.europa.ec.cipa.smp.server.exception;

import eu.europa.ec.cipa.smp.server.exception.common.TechnicalException;

/**
 * Created by feriaad on 16/06/2015.
 */
public class CertificateRevokedException extends TechnicalException {

    public CertificateRevokedException(String message) {
        super(message);
    }

    public CertificateRevokedException(String message, Throwable t) {
        super(message, t);
    }
}
