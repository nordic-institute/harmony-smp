package eu.europa.ec.cipa.bdmsl.business;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.PrepareChangeCertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.WildcardBO;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.security.cert.X509Certificate;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IManageCertificateBusiness {

    void validateChangeCertificate(PrepareChangeCertificateBO prepareChangeCertificateBO) throws
            BusinessException, TechnicalException;

    CertificateBO extractCertificate(String publicKey) throws BusinessException, TechnicalException;

    Long createNewCertificate(CertificateBO certificateBO) throws BusinessException, TechnicalException;

    void updateCertificate(CertificateBO certificateBO) throws BusinessException, TechnicalException;

    X509Certificate getX509Certificate(String publicKey) throws BusinessException, TechnicalException;

    void checkMigrationDate(X509Certificate certificate, PrepareChangeCertificateBO prepareChangeCertificateBO) throws BusinessException, TechnicalException;

    CertificateBO findCertificate(String certificateId) throws BusinessException, TechnicalException;

    int changeCertificates() throws BusinessException, TechnicalException;

    WildcardBO findWildCard(String scheme, String currentCertificate) throws BusinessException, TechnicalException;
}
