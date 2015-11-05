package eu.europa.ec.cipa.bdmsl.business;

import eu.europa.ec.cipa.bdmsl.common.bo.*;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IManageParticipantIdentifierBusiness {

    void validateParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException;

    void createParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException;

    ParticipantBO findParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException;

    void deleteParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException;

    void validatePageRequest(PageRequestBO pageRequestBO) throws BusinessException, TechnicalException;

    ParticipantListBO list(PageRequestBO pageRequestBO) throws BusinessException, TechnicalException;

    void validateMigrationRecord(MigrationRecordBO prepareToMigrateBO) throws BusinessException, TechnicalException;

    void prepareToMigrate(MigrationRecordBO prepareToMigrateBO) throws BusinessException, TechnicalException;

    void performMigration(MigrationRecordBO migrateBO) throws BusinessException, TechnicalException;

    MigrationRecordBO findNonMigratedRecord(MigrationRecordBO migrateBO) throws BusinessException, TechnicalException;

    ParticipantListBO list() throws BusinessException, TechnicalException;

    List<ParticipantBO> findParticipantsForSMP(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException;

    List<ParticipantBO> findParticipants(List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException;

    void deleteParticipants(List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException;

    void validateParticipantBOList(ParticipantListBO participantListBO) throws BusinessException, TechnicalException;

    void checkNoMigrationPlanned(String smpId, List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException;
}
