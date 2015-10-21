package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.Calendar;
import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface ICertificateDAO {
    CertificateBO findCertificateByCertificateId(String certificateId) throws TechnicalException;

    Long createCertificate(CertificateBO certificateBO) throws TechnicalException;

    void updateCertificate(CertificateBO certificateBO) throws TechnicalException;

    List<CertificateBO> findCertificatesToChange(Calendar calendar) throws TechnicalException;

    void delete(CertificateBO certificateBO) throws TechnicalException;

}
