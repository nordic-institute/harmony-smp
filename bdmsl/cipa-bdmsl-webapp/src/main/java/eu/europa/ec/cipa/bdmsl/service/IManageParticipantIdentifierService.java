package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.common.bo.*;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IManageParticipantIdentifierService {
    ParticipantListBO list(PageRequestBO pageRequestBO) throws
            BusinessException, TechnicalException;

    void create(ParticipantBO participantBO) throws
            BusinessException, TechnicalException;

    void createList(ParticipantListBO participantListBO) throws
            BusinessException, TechnicalException;

    void delete(ParticipantBO participantBO) throws
            BusinessException, TechnicalException;

    void deleteList(ParticipantListBO participantListBO) throws
            BusinessException, TechnicalException;

    void prepareToMigrate(MigrationRecordBO prepareToMigrateBO) throws
            BusinessException, TechnicalException;

    void migrate(MigrationRecordBO migrateBO) throws
            BusinessException, TechnicalException;

    ParticipantListBO list() throws
            BusinessException, TechnicalException;

    void delete(String smpId, List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException;
}
