package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface ICipaService {
    /**
     * Find the domain for the given root certificate alias
     *
     * @param rootCertificateAlias the root certificate alias
     * @return The CertificateDomainBO object or null if it couldn't be found
     * @throws TechnicalException a technical exception
     */
    CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException;

    /**
     * Returns all the CertificateDomainBO objects
     *
     * @return all the CertificateDomainBO objects
     * @throws TechnicalException a technical exception
     */
    List<CertificateDomainBO> findAll() throws TechnicalException;

    /**
     * Clears all the caches
     *
     * @throws TechnicalException a technical exception
     */
    void clearCache() throws TechnicalException;
}
