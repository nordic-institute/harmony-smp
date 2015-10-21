package eu.europa.ec.cipa.bdmsl.dao.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.common.bo.MigrationRecordBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.exception.DuplicateParticipantException;
import eu.europa.ec.cipa.bdmsl.dao.AbstractDAOImpl;
import eu.europa.ec.cipa.bdmsl.dao.IMigrationDAO;
import eu.europa.ec.cipa.bdmsl.dao.entity.MigrateEntity;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by feriaad on 15/06/2015.
 */
@Repository
public class MigrationDAOImpl extends AbstractDAOImpl implements IMigrationDAO {

    @Override
    public MigrationRecordBO findMigrationRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException {
        List<MigrateEntity> resultEntityList = getEntityManager().createQuery("SELECT m FROM MigrateEntity m where m.scheme = :scheme and m.participantId = :participantId", MigrateEntity.class)
                .setParameter("scheme", migrationRecordBO.getScheme())
                .setParameter("participantId", migrationRecordBO.getParticipantId())
                .getResultList();
        if (resultEntityList != null && resultEntityList.size() > 1) {
            throw new DuplicateParticipantException("There are more than one migration record for the given participant : " + migrationRecordBO.getScheme() + "/" + migrationRecordBO.getParticipantId());
        } else if (resultEntityList != null && resultEntityList.size() == 1) {
            return mapperFactory.getMapperFacade().map(resultEntityList.get(0), MigrationRecordBO.class);
        } else {
            return null;
        }
    }

    @Override
    public void updateMigrationRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException {
        MigrateEntity resultEntity = getEntityManager().createQuery("SELECT m FROM MigrateEntity m where m.scheme = :scheme and m.participantId = :participantId and m.oldSmpId = :oldSmpId", MigrateEntity.class)
                .setParameter("scheme", migrationRecordBO.getScheme())
                .setParameter("participantId", migrationRecordBO.getParticipantId())
                .setParameter("oldSmpId", migrationRecordBO.getOldSmpId())
                .getSingleResult();
        resultEntity.setMigrationKey(migrationRecordBO.getMigrationCode());
        if (!Strings.isNullOrEmpty(migrationRecordBO.getNewSmpId())) {
            resultEntity.setNewSmpId(migrationRecordBO.getNewSmpId());
        }
        resultEntity.setMigrated(migrationRecordBO.isMigrated());
        super.merge(resultEntity);
    }

    @Override
    public void createMigrationRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException {
        MigrateEntity migrateEntity = mapperFactory.getMapperFacade().map(migrationRecordBO, MigrateEntity.class);
        super.persist(migrateEntity);
    }

    @Override
    public List<MigrationRecordBO> findMigrationsRecordsForParticipants(String smpId, List<ParticipantBO> participantBOList) throws TechnicalException {
        Collection participantIds = CollectionUtils.collect(participantBOList, new Transformer() {
            @Override
            public Object transform(Object input) {
                return ((ParticipantBO) input).getParticipantId();
            }
        });
        List<MigrateEntity> resultEntityList = getEntityManager().createQuery("SELECT m FROM MigrateEntity m where m.oldSmpId = :smpId and m.participantId in :participantIds and m.migrated = :migrated", MigrateEntity.class)
                .setParameter("smpId", smpId)
                .setParameter("participantIds", participantIds)
                .setParameter("migrated", Boolean.FALSE)
                .getResultList();
        return mapperFactory.getMapperFacade().mapAsList(resultEntityList, MigrationRecordBO.class);
    }

    @Override
    public List<MigrationRecordBO> findMigrationsRecordsForSMP(String smpId) throws TechnicalException {
        List<MigrateEntity> resultEntityList = getEntityManager().createQuery("SELECT m FROM MigrateEntity m where m.oldSmpId = :smpId and m.migrated = :migrated", MigrateEntity.class)
                .setParameter("smpId", smpId)
                .setParameter("migrated", Boolean.FALSE)
                .getResultList();
        return mapperFactory.getMapperFacade().mapAsList(resultEntityList, MigrationRecordBO.class);
    }
}
