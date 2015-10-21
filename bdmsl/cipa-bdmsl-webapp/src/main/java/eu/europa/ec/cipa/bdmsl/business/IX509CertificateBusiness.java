package eu.europa.ec.cipa.bdmsl.business;

import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.security.cert.X509Certificate;

/**
 * Created by feriaad on 14/07/2015.
 */
public interface IX509CertificateBusiness {

    X509Certificate getCertificate(final X509Certificate[] requestCerts) throws TechnicalException, BusinessException;

    String getTrustedRootCertificateDN(X509Certificate[] certificates) throws TechnicalException;

    String calculateCertificateId(final X509Certificate cert) throws TechnicalException;
}
