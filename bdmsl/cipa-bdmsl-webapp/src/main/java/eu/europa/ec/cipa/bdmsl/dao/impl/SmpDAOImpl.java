package eu.europa.ec.cipa.bdmsl.dao.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.dao.AbstractDAOImpl;
import eu.europa.ec.cipa.bdmsl.dao.ISmpDAO;
import eu.europa.ec.cipa.bdmsl.dao.entity.CertificateEntity;
import eu.europa.ec.cipa.bdmsl.dao.entity.SmpEntity;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.Calendar;

/**
 * Created by feriaad on 15/06/2015.
 */
@Repository
public class SmpDAOImpl extends AbstractDAOImpl implements ISmpDAO {

    /**
     * @see eu.europa.ec.cipa.bdmsl.dao.ISmpDAO#findSMP(String)
     */
    @Override
    public ServiceMetadataPublisherBO findSMP(String id) throws TechnicalException {
        ServiceMetadataPublisherBO resultBO = null;
        SmpEntity resultSmpEntity = getEntityManager().find(SmpEntity.class, id);
        if (resultSmpEntity != null) {
            resultBO = mapperFactory.getMapperFacade().map(resultSmpEntity, ServiceMetadataPublisherBO.class);
        } else {
            loggingService.debug("No SMP found for id " + id);
        }

        return resultBO;
    }

    @Override
    public void createSMP(ServiceMetadataPublisherBO smpBO, String certificateId) throws TechnicalException {
        // find the certificate
        Query query = getEntityManager().createQuery("SELECT cert from CertificateEntity cert where cert.certificateId = :certificateId").setParameter("certificateId", certificateId);
        CertificateEntity certificateEntity = (CertificateEntity) query.getSingleResult();

        SmpEntity smpEntity = mapperFactory.getMapperFacade().map(smpBO, SmpEntity.class);
        smpEntity.setCertificate(certificateEntity);

        super.persist(smpEntity);

    }

    @Override
    public void deleteSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException {
        getEntityManager().remove(getEntityManager().find(SmpEntity.class, smpBO.getSmpId()));
        //getEntityManager().createQuery("DELETE from SmpEntity where smpId = :smpId").setParameter("smpId", smpBO.getSmpId()).executeUpdate();
    }

    @Override
    public void updateSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException {
        SmpEntity smpEntity = getEntityManager().find(SmpEntity.class, smpBO.getSmpId());
        smpEntity.setEndpointLogicalAddress(smpBO.getLogicalAddress());
        smpEntity.setEndpointPhysicalAddress(smpBO.getPhysicalAddress());
        super.merge(smpEntity);
    }

    @Override
    public void changeCertificateForSMP(Long oldId, Long newCertificateId) throws TechnicalException {
        getEntityManager().createQuery("UPDATE SmpEntity SET certificate.id = :newCertificateId, lastUpdateDate = :lastUpdateDate WHERE certificate.id = :oldId")
                .setParameter("oldId", oldId)
                .setParameter("newCertificateId", newCertificateId)
                .setParameter("lastUpdateDate", Calendar.getInstance())
                .executeUpdate();
    }
}

