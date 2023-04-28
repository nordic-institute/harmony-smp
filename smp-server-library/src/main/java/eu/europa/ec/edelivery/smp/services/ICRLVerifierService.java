package eu.europa.ec.edelivery.smp.services;

import java.math.BigInteger;
import java.security.cert.CertificateParsingException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;

public interface ICRLVerifierService {
    void verifyCertificateCRLs(X509Certificate cert) throws CertificateRevokedException, CertificateParsingException;

    void verifyCertificateCRLs(String serial, String crlDistributionPointURL) throws CertificateRevokedException;

    void verifyCertificateCRLs(BigInteger serial, String crlDistributionPointURL) throws CertificateRevokedException;
}
