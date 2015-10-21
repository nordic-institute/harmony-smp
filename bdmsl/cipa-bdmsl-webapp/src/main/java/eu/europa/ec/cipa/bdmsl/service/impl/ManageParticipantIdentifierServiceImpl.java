package eu.europa.ec.cipa.bdmsl.service.impl;

import eu.europa.ec.cipa.bdmsl.business.IManageCertificateBusiness;
import eu.europa.ec.cipa.bdmsl.business.IManageParticipantIdentifierBusiness;
import eu.europa.ec.cipa.bdmsl.business.IManageServiceMetadataBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.*;
import eu.europa.ec.cipa.bdmsl.common.exception.*;
import eu.europa.ec.cipa.bdmsl.service.IManageParticipantIdentifierService;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsClientService;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.service.AbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
@Service
@Transactional(readOnly = true)
public class ManageParticipantIdentifierServiceImpl extends AbstractServiceImpl implements IManageParticipantIdentifierService {

    @Autowired
    private IManageParticipantIdentifierBusiness manageParticipantIdentifierBusiness;

    @Autowired
    private IManageServiceMetadataBusiness manageServiceMetadataBusiness;

    @Autowired
    private IManageCertificateBusiness manageCertificateBusiness;

    @Autowired
    private IDnsClientService dnsClientService;

    @Value("${dnsClient.enabled}")
    private String dnsEnabled;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional
    public ParticipantListBO list(PageRequestBO pageRequestBO) throws BusinessException, TechnicalException {
        // Validate the input data
        manageParticipantIdentifierBusiness.validatePageRequest(pageRequestBO);

        // Check that the smp exists.
        ServiceMetadataPublisherBO smpBO = manageServiceMetadataBusiness.verifySMPExist(pageRequestBO.getSmpId());
        if (smpBO == null) {
            throw new SmpNotFoundException("The SMP " + pageRequestBO.getSmpId() + " couldn't be found");
        }

        // check that the user owns the SMP
        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (smpBO.getCertificateId().equals(currentCertificate)) {
            return manageParticipantIdentifierBusiness.list(pageRequestBO);
        } else {
            throw new UnauthorizedException("The SMP " + pageRequestBO.getSmpId() + " was not created with the certificate " + currentCertificate);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void create(ParticipantBO participantBO) throws BusinessException, TechnicalException {

        // Validate the input data
        manageParticipantIdentifierBusiness.validateParticipant(participantBO);

        // Then make sure that the smp exists.
        ServiceMetadataPublisherBO existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(participantBO.getSmpId());

        // Check if the participant already exists
        ParticipantBO existingParticipantBO = manageParticipantIdentifierBusiness.findParticipant(participantBO);
        if (existingParticipantBO != null) {
            throw new BadRequestException("The participant identifier '" + participantBO.getParticipantId() + "' does already exist for the scheme " + participantBO.getScheme());
        }

        // check that the user owns the SMP
        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (existingSmpBO.getCertificateId().equals(currentCertificate)) {
            // Is it a wildcard record ?
            if ("*".equals(participantBO.getParticipantId())) {
                loggingService.debug("The participant Identifier is a wildcard");
                // check that the user is authorized to create a wildcard association
                WildcardBO wildcardBO = manageCertificateBusiness.findWildCard(participantBO.getScheme(), currentCertificate);

                if (wildcardBO == null) {
                    throw new UnauthorizedException("The certificate " + currentCertificate + " is not allowed to create wild card record for the scheme " + participantBO.getScheme());
                } else {
                    loggingService.debug("The certificate " + currentCertificate + " is allowed to create a wildcard record for the scheme " + participantBO.getScheme());
                }
            }

            manageParticipantIdentifierBusiness.createParticipant(participantBO);

            if (Boolean.parseBoolean(dnsEnabled)) {
                // If DNS create fails, then an exception is thrown and the database import is rolled back
                dnsClientService.createDNSRecordsForParticipant(participantBO);
            }
        } else {
            throw new UnauthorizedException("The SMP " + participantBO.getSmpId() + " was not created with the certificate " + currentCertificate);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void createList(ParticipantListBO participantListBO) throws BusinessException, TechnicalException {
        manageParticipantIdentifierBusiness.validateParticipantBOList(participantListBO);
        ServiceMetadataPublisherBO existingSmpBO = null;
        // perform checks on every participant
        for (ParticipantBO participantBO : participantListBO.getParticipantBOList()) {
            // Validate the input data
            manageParticipantIdentifierBusiness.validateParticipant(participantBO);

            // Then make sure that the smp exists.
            existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(participantBO.getSmpId());

            // Check if the participant already exists
            ParticipantBO existingParticipantBO = manageParticipantIdentifierBusiness.findParticipant(participantBO);
            if (existingParticipantBO != null) {
                throw new BadRequestException("The participant identifier '" + participantBO.getParticipantId() + "' does already exist for the scheme " + participantBO.getScheme());
            }

            // check that the user owns the SMP
            String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
            if (existingSmpBO.getCertificateId().equals(currentCertificate)) {
                // Is it a wildcard record ?
                if ("*".equals(participantBO.getParticipantId())) {
                    loggingService.debug("The participant Identifier is a wildcard");
                    // check that the user is authorized to create a wildcard association
                    WildcardBO wildcardBO = manageCertificateBusiness.findWildCard(participantBO.getScheme(), currentCertificate);

                    if (wildcardBO == null) {
                        throw new UnauthorizedException("The certificate " + currentCertificate + " is not allowed to create wild card record for the scheme " + participantBO.getScheme());
                    } else {
                        loggingService.debug("The certificate " + currentCertificate + " is allowed to create a wildcard record for the scheme " + participantBO.getScheme());
                    }
                }
            } else {
                throw new UnauthorizedException("The SMP " + participantBO.getSmpId() + " was not created with the certificate " + currentCertificate);
            }
        }

        for (ParticipantBO participantBO : participantListBO.getParticipantBOList()) {
            // The participants list is valid, we can now perform the insertions
            manageParticipantIdentifierBusiness.createParticipant(participantBO);
        }

        if (Boolean.parseBoolean(dnsEnabled)) {
            // If DNS create fails, then an exception is thrown and the database import is rolled back
            dnsClientService.createDNSRecordsForParticipants(participantListBO.getParticipantBOList());
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void delete(ParticipantBO participantBO) throws BusinessException, TechnicalException {

        // Validate the input data
        manageParticipantIdentifierBusiness.validateParticipant(participantBO);

        // Then make sure that the smp exists.
        ServiceMetadataPublisherBO existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(participantBO.getSmpId());

        // Check if the participant already exists
        if (manageParticipantIdentifierBusiness.findParticipant(participantBO) == null) {
            throw new ParticipantNotFoundException("The participant identifier '" + participantBO.getParticipantId() + "' doesn't exist for the scheme " + participantBO.getScheme());
        }

        // Check that no migration is planned for any participant
        manageParticipantIdentifierBusiness.checkNoMigrationPlanned(participantBO.getSmpId(), Arrays.asList(new ParticipantBO[] { participantBO}));

        // check that the user owns the SMP
        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (existingSmpBO.getCertificateId().equals(currentCertificate)) {
            manageParticipantIdentifierBusiness.deleteParticipant(participantBO);

            if (Boolean.parseBoolean(dnsEnabled)) {
                // If DNS deletion fails, then an exception is thrown and the database import is rolled back
                dnsClientService.deleteDNSRecordsForParticipant(participantBO);
            }
        } else {
            throw new UnauthorizedException("The SMP " + existingSmpBO.getSmpId() + " was not created with the certificate " + currentCertificate);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void delete(String smpId, List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException {

        // Validate the input data
        for (ParticipantBO participantBO : participantBOList) {
            manageParticipantIdentifierBusiness.validateParticipant(participantBO);
        }
        manageServiceMetadataBusiness.validateSMPId(smpId);

        // Then make sure that the smp exists.
        ServiceMetadataPublisherBO existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(smpId);

        // Check if the participant already exists
        List<ParticipantBO> existingParticipantBOList = manageParticipantIdentifierBusiness.findParticipants(participantBOList);
        if (existingParticipantBOList == null || existingParticipantBOList.size() != participantBOList.size()) {
            throw new ParticipantNotFoundException("At least one of the participants doesn't exist in the list " + participantBOList);
        }

        // Check that no migration is planned for any participant
        manageParticipantIdentifierBusiness.checkNoMigrationPlanned(smpId, participantBOList);

        // check that the user owns the SMP
        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (existingSmpBO.getCertificateId().equals(currentCertificate)) {
            manageParticipantIdentifierBusiness.deleteParticipants(participantBOList);

            if (Boolean.parseBoolean(dnsEnabled)) {
                // If DNS deletion fails, then an exception is thrown and the database import is rolled back
                dnsClientService.deleteDNSRecordsForParticipants(participantBOList);
            }
        } else {
            throw new UnauthorizedException("The SMP " + existingSmpBO.getSmpId() + " was not created with the certificate " + currentCertificate);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteList(ParticipantListBO participantListBO) throws BusinessException, TechnicalException {
        manageParticipantIdentifierBusiness.validateParticipantBOList(participantListBO);
        // assuming that all the participants are linked to the same SMP
        this.delete(participantListBO.getParticipantBOList().get(0).getSmpId(), participantListBO.getParticipantBOList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void prepareToMigrate(MigrationRecordBO prepareToMigrateBO) throws BusinessException, TechnicalException {
        // Validate the input data
        manageParticipantIdentifierBusiness.validateMigrationRecord(prepareToMigrateBO);

        // verify the SMP exists
        ServiceMetadataPublisherBO existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(prepareToMigrateBO.getOldSmpId());

        // Check if the participant exists
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setScheme(prepareToMigrateBO.getScheme());
        participantBO.setSmpId(prepareToMigrateBO.getOldSmpId());
        participantBO.setParticipantId(prepareToMigrateBO.getParticipantId());
        ParticipantBO existingParticipantBO = manageParticipantIdentifierBusiness.findParticipant(participantBO);
        if (existingParticipantBO == null) {
            throw new ParticipantNotFoundException("The participant identifier '" + prepareToMigrateBO.getParticipantId() + "' doesn't exist for the scheme " + prepareToMigrateBO.getScheme() + " for the smp " + prepareToMigrateBO.getOldSmpId());
        }

        // check that the user owns the SMP
        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (existingSmpBO.getCertificateId().equals(currentCertificate)) {
            manageParticipantIdentifierBusiness.prepareToMigrate(prepareToMigrateBO);
        } else {
            throw new UnauthorizedException("The SMP " + prepareToMigrateBO.getOldSmpId() + " was not created with the certificate " + currentCertificate);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void migrate(MigrationRecordBO migrateBO) throws BusinessException, TechnicalException {
        // Validate the input data
        manageParticipantIdentifierBusiness.validateMigrationRecord(migrateBO);

        // verify the SMP exists
        ServiceMetadataPublisherBO existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(migrateBO.getNewSmpId());

        //find the migration record created by the prepareToMigrate service
        MigrationRecordBO existingMigrationRecord = manageParticipantIdentifierBusiness.findMigrationRecord(migrateBO);
        if (existingMigrationRecord == null) {
            throw new MigrationNotFoundException("No migration record found. Please call the prepareToMigrate service prior to the Migrate service.");
        }

        // Check if the participant still exists
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setParticipantId(migrateBO.getParticipantId());
        participantBO.setScheme(migrateBO.getScheme());
        participantBO.setSmpId(existingMigrationRecord.getOldSmpId());
        ParticipantBO existingParticipantBO = manageParticipantIdentifierBusiness.findParticipant(participantBO);
        if (existingParticipantBO == null) {
            throw new ParticipantNotFoundException("The participant identifier '" + migrateBO.getParticipantId() + "' doesn't exist for the scheme " + migrateBO.getScheme() + " for the smp " + migrateBO.getNewSmpId());
        }

        // check that the user owns the SMP
        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (existingSmpBO.getCertificateId().equals(currentCertificate)) {
            if (!existingMigrationRecord.getMigrationCode().equals(migrateBO.getMigrationCode())) {
                throw new UnauthorizedException("The migration key doesn't match the expected one");
            }

            // Actually perform the migration
            migrateBO.setOldSmpId(existingMigrationRecord.getOldSmpId());
            manageParticipantIdentifierBusiness.performMigration(migrateBO);

            if (Boolean.parseBoolean(dnsEnabled)) {
                // If DNS update fails, then an exception is thrown and the database import is rolled back
                participantBO.setSmpId(migrateBO.getNewSmpId());
                dnsClientService.updateDNSRecordsForParticipant(participantBO);
            }
        } else {
            throw new UnauthorizedException("The SMP " + migrateBO.getNewSmpId() + " was not created with the certificate " + currentCertificate);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_PYP')")
    @Transactional
    public ParticipantListBO list() throws BusinessException, TechnicalException {
        return manageParticipantIdentifierBusiness.list();
    }
}

