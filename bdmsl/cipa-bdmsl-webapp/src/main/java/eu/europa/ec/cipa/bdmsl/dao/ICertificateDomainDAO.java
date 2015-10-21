package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface ICertificateDomainDAO {
    CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException;

    List<CertificateDomainBO> findAll() throws TechnicalException;
}
