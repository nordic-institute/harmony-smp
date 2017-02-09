package eu.europa.ec.cipa.smp.server.errors.exceptions;

public class CertificateRevokedException extends Exception {

    public CertificateRevokedException(String message) {
        super(message);
    }

    public CertificateRevokedException(String message, Throwable t) {
        super(message, t);
    }
}
