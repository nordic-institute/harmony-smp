package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface ICRLVerifierService {
    void verifyCertificateCRLs(X509Certificate cert) throws TechnicalException, BusinessException;

    List<String> getCrlDistributionPoints(X509Certificate cert) throws TechnicalException, BusinessException;

    void verifyCertificateCRLs(String serial, String crlDistributionPointURL) throws TechnicalException, BusinessException;
}
