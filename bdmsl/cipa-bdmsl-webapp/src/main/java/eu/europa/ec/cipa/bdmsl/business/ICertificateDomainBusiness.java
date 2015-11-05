package eu.europa.ec.cipa.bdmsl.business;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface ICertificateDomainBusiness {

    /**
     * This method find a certificate, whatever the order of the fields
     * @param rootCertificateAlias
     * @return
     * @throws TechnicalException
     */
    CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException;

    List<CertificateDomainBO> findAll() throws TechnicalException;
}
