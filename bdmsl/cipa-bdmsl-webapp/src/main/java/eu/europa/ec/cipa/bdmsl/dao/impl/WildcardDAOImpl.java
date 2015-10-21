package eu.europa.ec.cipa.bdmsl.dao.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.WildcardBO;
import eu.europa.ec.cipa.bdmsl.dao.AbstractDAOImpl;
import eu.europa.ec.cipa.bdmsl.dao.IWildcardDAO;
import eu.europa.ec.cipa.bdmsl.dao.entity.AllowedWildcardEntity;
import eu.europa.ec.cipa.bdmsl.dao.entity.AllowedWildcardEntityPK;
import eu.europa.ec.cipa.bdmsl.dao.entity.CertificateEntity;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

/**
 * Created by feriaad on 15/06/2015.
 */
@Repository
public class WildcardDAOImpl extends AbstractDAOImpl implements IWildcardDAO {

    @Override
    public void changeWildcardAuthorization(Long oldId, Long newCertificateId) throws TechnicalException {
        getEntityManager().createQuery("UPDATE AllowedWildcardEntity SET certificate.id = :newCertificateId, lastUpdateDate = :lastUpdateDate WHERE certificate.id = :oldId")
                .setParameter("oldId", oldId)
                .setParameter("newCertificateId", newCertificateId)
                .setParameter("lastUpdateDate", Calendar.getInstance())
                .executeUpdate();
    }

    @Override
    public WildcardBO findWildcard(String scheme, CertificateBO certificateBO) throws TechnicalException {
        WildcardBO resultBO = null;
        AllowedWildcardEntityPK pk = new AllowedWildcardEntityPK();
        pk.setCertificate(getEntityManager().find(CertificateEntity.class, certificateBO.getId()));
        pk.setScheme(scheme);
        AllowedWildcardEntity resultWildcardEntity = getEntityManager().find(AllowedWildcardEntity.class, pk);
        if (resultWildcardEntity != null) {
            resultBO = mapperFactory.getMapperFacade().map(resultWildcardEntity, WildcardBO.class);
        } else {
            loggingService.debug("No wildcard authorization found for scheme " + scheme + " and certificate " + certificateBO.getCertificateId());
        }

        return resultBO;
    }
}
