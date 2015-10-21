package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.security.cert.X509Certificate;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface IX509CertificateService {
    boolean isClientX509CertificateValid(final X509Certificate[] certificate) throws TechnicalException, BusinessException;

    X509Certificate getCertificate(final X509Certificate[] requestCerts) throws TechnicalException, BusinessException;

    String getTrustedRootCertificateDN(X509Certificate[] certificates) throws TechnicalException;

    String calculateCertificateId(final X509Certificate cert) throws TechnicalException;
}
