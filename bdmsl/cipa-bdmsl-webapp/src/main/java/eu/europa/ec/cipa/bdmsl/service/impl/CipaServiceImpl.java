package eu.europa.ec.cipa.bdmsl.service.impl;

import eu.europa.ec.cipa.bdmsl.business.ICertificateDomainBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.service.ICipaService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.service.AbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
@Service
@Transactional(readOnly = true)
public class CipaServiceImpl extends AbstractServiceImpl implements ICipaService {

    @Autowired
    private ICertificateDomainBusiness certificateDomainBusiness;

    @Override
    public CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException {
        return certificateDomainBusiness.findDomain(rootCertificateAlias);
    }

    @Override
    public List<CertificateDomainBO> findAll() throws TechnicalException {
        return certificateDomainBusiness.findAll();
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP', 'ROLE_ADMIN')")
    @CacheEvict(value = { "crlByUrl", "crlByCert", "domainById", "allDomains" }, allEntries = true)
    public void clearCache() throws TechnicalException {
        //do nothing, the cleaning is done by annotations
    }
}

