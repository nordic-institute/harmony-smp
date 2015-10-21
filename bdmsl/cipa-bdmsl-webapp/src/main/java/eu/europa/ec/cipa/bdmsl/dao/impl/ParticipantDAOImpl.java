package eu.europa.ec.cipa.bdmsl.dao.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.common.bo.PageRequestBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.dao.AbstractDAOImpl;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.dao.entity.ParticipantIdentifierEntity;
import eu.europa.ec.cipa.bdmsl.dao.entity.SmpEntity;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by feriaad on 15/06/2015.
 */
@Repository
public class ParticipantDAOImpl extends AbstractDAOImpl implements IParticipantDAO {

    @Override
    public void createParticipant(ParticipantBO participantBO) throws TechnicalException {
        // map the business object to a JPA entity
        ParticipantIdentifierEntity participantIdentifierEntity = mapperFactory.getMapperFacade().map(participantBO, ParticipantIdentifierEntity.class);
        // now associate the SMP entity
        participantIdentifierEntity.setSmp(getEntityManager().find(SmpEntity.class, participantBO.getSmpId()));
        super.persist(participantIdentifierEntity);
    }

    @Override
    public ParticipantBO findParticipant(ParticipantBO participantBO) throws TechnicalException {
        ParticipantBO resultBO = null;
        Query query = getEntityManager().createQuery("SELECT part from ParticipantIdentifierEntity part where part.participantId = :participantId and part.scheme = :scheme")
                .setParameter("participantId", participantBO.getParticipantId())
                .setParameter("scheme", participantBO.getScheme());
        List<ParticipantIdentifierEntity> results = query.getResultList();
        ParticipantIdentifierEntity resultEntity = null;
        if (!results.isEmpty()) {
            resultEntity = results.get(0);
            resultBO = mapperFactory.getMapperFacade().map(resultEntity, ParticipantBO.class);
        } else {
            loggingService.debug("No entry found for participant " + participantBO);
        }
        return resultBO;
    }

    @Override
    public void deleteParticipant(ParticipantBO participantBO) throws TechnicalException {
        getEntityManager().createQuery("DELETE from ParticipantIdentifierEntity where participantId = :participantId and scheme = :scheme")
                .setParameter("participantId", participantBO.getParticipantId())
                .setParameter("scheme", participantBO.getScheme())
                .executeUpdate();
    }

    public List<ParticipantBO> listParticipant(PageRequestBO pageRequestBO, int participantPerPageCount) throws TechnicalException {

        // The NextPageIdentifier starts with 1
        int page = 0;
        if (!Strings.isNullOrEmpty(pageRequestBO.getPage())) {
            page = Integer.valueOf(pageRequestBO.getPage()) - 1;
        }

        List<ParticipantIdentifierEntity> resultList = getEntityManager().createQuery("SELECT part from ParticipantIdentifierEntity part where part.smp.smpId =:smpId")
                .setParameter("smpId", pageRequestBO.getSmpId())
                .setFirstResult(page * Integer.valueOf(participantPerPageCount))
                .setMaxResults(participantPerPageCount)
                .getResultList();

        return mapperFactory.getMapperFacade().mapAsList(resultList, ParticipantBO.class);
    }

    @Override
    public void updateParticipant(ParticipantBO participantBO, final String oldSmpId) throws TechnicalException {
        ParticipantIdentifierEntity participantIdentifierEntity = getEntityManager().createQuery("SELECT part from ParticipantIdentifierEntity part where part.participantId = :participantId and part.scheme = :scheme and part.smp.smpId = :smpId", ParticipantIdentifierEntity.class)
                .setParameter("participantId", participantBO.getParticipantId())
                .setParameter("scheme", participantBO.getScheme())
                .setParameter("smpId", oldSmpId).getSingleResult();
        // now associate the SMP entity
        participantIdentifierEntity.setSmp(getEntityManager().find(SmpEntity.class, participantBO.getSmpId()));
        super.merge(participantIdentifierEntity);
    }

    @Override
    public List<ParticipantBO> listParticipants() throws TechnicalException {
        List<ParticipantIdentifierEntity> resultList = getEntityManager().createQuery("SELECT part from ParticipantIdentifierEntity part")
                .getResultList();
        return mapperFactory.getMapperFacade().mapAsList(resultList, ParticipantBO.class);
    }

    @Override
    public List<ParticipantBO> findParticipantsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException {
        List<ParticipantIdentifierEntity> resultList = getEntityManager().createQuery("SELECT part from ParticipantIdentifierEntity part where part.smp.smpId = :smpId")
                .setParameter("smpId", smpBO.getSmpId())
                .getResultList();

        return mapperFactory.getMapperFacade().mapAsList(resultList, ParticipantBO.class);
    }

    @Override
    public List<ParticipantBO> findParticipants(Map<String, List<String>> mapParticipants) throws TechnicalException {
        List<ParticipantBO> resultBOList = new ArrayList<>();
        for (String scheme : mapParticipants.keySet()) {
            List<ParticipantIdentifierEntity> resultEntityList = getEntityManager().createQuery("SELECT part from ParticipantIdentifierEntity part where part.scheme = :scheme and part.participantId in :participantIdList")
                    .setParameter("scheme", scheme)
                    .setParameter("participantIdList", mapParticipants.get(scheme))
                    .getResultList();
            resultBOList.addAll(mapperFactory.getMapperFacade().mapAsList(resultEntityList, ParticipantBO.class));
        }
        return resultBOList;
    }

    @Override
    public void deleteParticipants(Map<String, List<String>> mapParticipants) throws TechnicalException {
        for (String scheme : mapParticipants.keySet()) {
            getEntityManager().createQuery("DELETE from ParticipantIdentifierEntity part where part.scheme = :scheme and part.participantId in :participantIdList")
                    .setParameter("participantIdList", mapParticipants.get(scheme))
                    .setParameter("scheme", scheme)
                    .executeUpdate();
        }
    }
}

