package eu.europa.ec.cipa.smp.server.exception;

import eu.europa.ec.cipa.smp.server.exception.common.TechnicalException;

/**
 * Created by feriaad on 23/06/2015.
 */
public class CertificateNotFoundException extends TechnicalException {

    public CertificateNotFoundException(String message) {
        super(message);
    }

    public CertificateNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}