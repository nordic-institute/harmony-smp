package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.common.bo.MigrationRecordBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IMigrationDAO {
    MigrationRecordBO findMigratedRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException;

    MigrationRecordBO findNonMigratedRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException;

    void updateMigrationRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException;

    void createMigrationRecord(MigrationRecordBO migrationRecordBO) throws TechnicalException;

    List<MigrationRecordBO> findMigrationsRecordsForParticipants(String smpId, List<ParticipantBO> participantBOList) throws TechnicalException;

    List<MigrationRecordBO> findMigrationsRecordsForSMP(String smpId) throws TechnicalException;

    void performMigration(MigrationRecordBO migrationRecordBO) throws TechnicalException;
}
