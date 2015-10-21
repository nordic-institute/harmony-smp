package eu.europa.ec.cipa.bdmsl.dao.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.dao.AbstractDAOImpl;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.bdmsl.dao.entity.CertificateEntity;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by feriaad on 15/06/2015.
 */
@Repository
public class CertificateDAOImpl extends AbstractDAOImpl implements ICertificateDAO {

    @Override
    public CertificateBO findCertificateByCertificateId(String certificateId) throws TechnicalException {
        CertificateBO resultBO = null;
        Query query = getEntityManager().createQuery("SELECT cert from CertificateEntity cert where cert.certificateId = :certificateId").setParameter("certificateId", certificateId);
        List<CertificateEntity> results = query.getResultList();
        CertificateEntity resultEntity;
        if (!results.isEmpty()) {
            resultEntity = results.get(0);
            resultBO = mapperFactory.getMapperFacade().map(resultEntity, CertificateBO.class);
        } else {
            loggingService.debug("No entry found for certificate " + certificateId);
        }

        return resultBO;
    }

    @Override
    public Long createCertificate(CertificateBO certificateBO) throws TechnicalException {
        CertificateEntity certificateEntity = mapperFactory.getMapperFacade().map(certificateBO, CertificateEntity.class);
        super.persist(certificateEntity);
        return certificateEntity.getId();
    }

    @Override
    public void updateCertificate(CertificateBO certificateBO) throws TechnicalException {
        CertificateEntity certificateEntity = getEntityManager().find(CertificateEntity.class, certificateBO.getId());
        certificateEntity.setCertificateId(certificateBO.getCertificateId());
        certificateEntity.setPemEncoding(certificateBO.getPemEncoding());
        if (certificateBO.getNewCertificateId() != null) {
            certificateEntity.setNewCertificate(getEntityManager().find(CertificateEntity.class, certificateBO.getNewCertificateId()));
        }
        certificateEntity.setNewCertificateChangeDate(certificateBO.getMigrationDate());
        certificateEntity.setValidFrom(certificateBO.getValidFrom());
        certificateEntity.setValidTo(certificateBO.getValidTo());
        super.merge(certificateEntity);
    }

    @Override
    public List<CertificateBO> findCertificatesToChange(Calendar currentDate) throws TechnicalException {
        // This method is used in the context of a job that can be run on a clustered environment. To avoid concurrency issues, we do here a SELECT FOR UPDATE
        Query query = getEntityManager().createQuery("SELECT cert from CertificateEntity cert where cert.newCertificateChangeDate <= :currentDate")
                .setParameter("currentDate", currentDate).setLockMode(LockModeType.PESSIMISTIC_WRITE);

        List<CertificateEntity> results = query.getResultList();
        List<CertificateBO> resultList = new ArrayList<>();
        if (!results.isEmpty()) {
            resultList = mapperFactory.getMapperFacade().mapAsList(results, CertificateBO.class);
        }
        return resultList;
    }

    @Override
    public void delete(CertificateBO certificateBO) throws TechnicalException {
        getEntityManager().createQuery("DELETE from CertificateEntity where id = :id")
                .setParameter("id", certificateBO.getId())
                .executeUpdate();
    }

}
