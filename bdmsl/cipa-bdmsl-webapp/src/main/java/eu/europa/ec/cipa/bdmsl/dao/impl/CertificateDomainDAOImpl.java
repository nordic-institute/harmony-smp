package eu.europa.ec.cipa.bdmsl.dao.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.dao.AbstractDAOImpl;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDomainDAO;
import eu.europa.ec.cipa.bdmsl.dao.entity.CertificateDomainEntity;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feriaad on 15/06/2015.
 */
@Repository
public class CertificateDomainDAOImpl extends AbstractDAOImpl implements ICertificateDomainDAO {

    @Override
    @Cacheable("domainById")
    public CertificateDomainBO findDomain(String rootCertificateAlias) throws TechnicalException {
        CertificateDomainBO resultBO = null;
        CertificateDomainEntity resultEntity = getEntityManager().find(CertificateDomainEntity.class, rootCertificateAlias);
        if (resultEntity != null) {
            resultBO = mapperFactory.getMapperFacade().map(resultEntity, CertificateDomainBO.class);
        } else {
            loggingService.debug("No entry found for certificate root alias " + rootCertificateAlias);
        }

        return resultBO;
    }

    @Override
    @Cacheable("allDomains")
    public List<CertificateDomainBO> findAll() throws TechnicalException {
        List<CertificateDomainEntity> resultEntityList = getEntityManager().createQuery("SELECT c FROM CertificateDomainEntity c", CertificateDomainEntity.class).getResultList();
        List<CertificateDomainBO> resultBOList = new ArrayList<CertificateDomainBO>();
        if (resultEntityList != null && !resultEntityList.isEmpty()) {
            for (CertificateDomainEntity certificateDomainEntity : resultEntityList) {
                resultBOList.add(mapperFactory.getMapperFacade().map(certificateDomainEntity, CertificateDomainBO.class));
            }
        }
        return resultBOList;
    }
}
