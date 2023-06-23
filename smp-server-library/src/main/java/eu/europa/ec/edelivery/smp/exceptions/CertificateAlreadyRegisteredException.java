package eu.europa.ec.edelivery.smp.exceptions;

import java.security.cert.CertificateException;

public class CertificateAlreadyRegisteredException extends CertificateException {

    public CertificateAlreadyRegisteredException(String msg) {
        super(msg);
    }
}
