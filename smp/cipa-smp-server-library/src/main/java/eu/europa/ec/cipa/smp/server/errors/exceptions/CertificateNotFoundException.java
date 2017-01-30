package eu.europa.ec.cipa.smp.server.errors.exceptions;

public class CertificateNotFoundException extends Exception {

    public CertificateNotFoundException(String message) {
        super(message);
    }

    public CertificateNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}