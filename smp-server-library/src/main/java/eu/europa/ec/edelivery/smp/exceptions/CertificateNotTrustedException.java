package eu.europa.ec.edelivery.smp.exceptions;

import java.security.cert.CertificateException;

public class CertificateNotTrustedException extends CertificateException {

    public CertificateNotTrustedException() {
    }

    public CertificateNotTrustedException(String msg) {
        super(msg);
    }
}
