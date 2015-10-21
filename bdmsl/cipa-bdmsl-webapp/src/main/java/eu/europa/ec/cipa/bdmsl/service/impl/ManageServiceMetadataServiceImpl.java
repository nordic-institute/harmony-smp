package eu.europa.ec.cipa.bdmsl.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import eu.europa.ec.cipa.bdmsl.business.IManageParticipantIdentifierBusiness;
import eu.europa.ec.cipa.bdmsl.business.IManageServiceMetadataBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.common.exception.SmpDeleteException;
import eu.europa.ec.cipa.bdmsl.common.exception.SmpNotFoundException;
import eu.europa.ec.cipa.bdmsl.common.exception.UnauthorizedException;
import eu.europa.ec.cipa.bdmsl.service.IManageParticipantIdentifierService;
import eu.europa.ec.cipa.bdmsl.service.IManageServiceMetadataService;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsClientService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
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

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class ManageServiceMetadataServiceImpl extends AbstractServiceImpl implements IManageServiceMetadataService {

    @Autowired
    private IManageServiceMetadataBusiness manageServiceMetadataBusiness;

    @Autowired
    private IManageParticipantIdentifierService manageParticipantIdentifierService;

    @Autowired
    private IManageParticipantIdentifierBusiness manageParticipantIdentifierBusiness;

    @Autowired
    private IDnsClientService dnsClientService;

    /**
     * Number of participants to delete at once
     */
    private final static int NUMBER_PARTICIPANTS_DELETE = 300;

    @Value("${dnsClient.enabled}")
    private String dnsEnabled;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void create(final ServiceMetadataPublisherBO smpBO) throws
            BusinessException, TechnicalException {
        // Validate input data
        manageServiceMetadataBusiness.validateSMPData(smpBO);

        // Then make sure that the smp does not exist.
        manageServiceMetadataBusiness.verifySMPNotExist(smpBO.getSmpId());

        // Save the service metadata
        CertificateBO certificateBO = manageServiceMetadataBusiness.findCertificate(smpBO.getCertificateId());
        if (certificateBO == null) {
            manageServiceMetadataBusiness.createCurrentCertificate();
        }

        manageServiceMetadataBusiness.createSMP(smpBO);

        if (Boolean.parseBoolean(dnsEnabled)) {
            // If DNS create fails, then an exception is thrown and the database import is rolled back
            dnsClientService.createDNSRecordsForSMP(smpBO);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void delete(String smpId) throws BusinessException, TechnicalException {
        // Validate input data
        manageServiceMetadataBusiness.validateSMPId(smpId);

        // Then make sure that the smp exists.
        ServiceMetadataPublisherBO smpBO = manageServiceMetadataBusiness.verifySMPExist(smpId);

        List<ParticipantBO> participants = manageParticipantIdentifierBusiness.findParticipantsForSMP(smpBO);

        // Check that no migration is planned for this SMP
        manageServiceMetadataBusiness.checkNoMigrationPlanned(smpId);

        String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
        if (smpBO.getCertificateId().equals(currentCertificate)) {
            // find all participants for this SMP and delete them
            try {
                if (participants != null) {
                /* we delete by batch of 300 participants to avoid an exception in the DNS because the maximum size of a message is 65k.
                If the number of participants is big, then the size of 65k is exceeded and the update is refused */
                    List<List<ParticipantBO>> subParticipantsList = Lists.partition(participants, NUMBER_PARTICIPANTS_DELETE);

                    // This method requires a new transaction to reduce the risk of de-synchronization between the DNS and the database
                    for (List<ParticipantBO> participantBOList : subParticipantsList) {
                        manageParticipantIdentifierService.delete(smpBO.getSmpId(), participantBOList);
                        loggingService.businessLog(LogEvents.BUS_PARTICIPANT_LIST_DELETED, participantBOList.toString());
                    }
                }
            } catch (Exception exc) {
                throw new SmpDeleteException("The SMP couldn't be deleted but some of its participants have been deleted. Please see the logs to know which participants have been deleted", exc);
            }

            // all the participants have been deleted
            manageServiceMetadataBusiness.deleteSMP(smpBO);

            if (Boolean.parseBoolean(dnsEnabled)) {
                // If DNS deletion fails, then an exception is thrown and the database import is rolled back
                dnsClientService.deleteDNSRecordsForSMP(smpBO);
            }
        } else {
            throw new UnauthorizedException("The SMP " + smpBO.getSmpId() + " was not created with the current certificate " + smpBO.getCertificateId());
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void update(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException {
        // Validate input data
        manageServiceMetadataBusiness.validateSMPData(smpBO);

        // Then make sure that the smp exists.
        ServiceMetadataPublisherBO existingSmpBO = manageServiceMetadataBusiness.verifySMPExist(smpBO.getSmpId());

        if (existingSmpBO.getCertificateId().equals(smpBO.getCertificateId())) {
            manageServiceMetadataBusiness.updateSMP(smpBO);
            if (Boolean.parseBoolean(dnsEnabled)) {
                // If DNS update fails, then an exception is thrown and the database import is rolled back
                dnsClientService.updateDNSRecordsForSMP(smpBO);
            }
        } else {
            throw new UnauthorizedException("The SMP " + smpBO.getSmpId() + " was not created with the current certificate " + smpBO.getCertificateId());
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional
    public ServiceMetadataPublisherBO read(String id) throws
            BusinessException, TechnicalException {

        if (Strings.isNullOrEmpty(id)) {
            throw new BadRequestException("The SMP ID must not be null");
        }

        ServiceMetadataPublisherBO resultBO = manageServiceMetadataBusiness.read(id);
        if (resultBO == null) {
            throw new SmpNotFoundException("The SMP with id " + id + " couldn't be found");
        } else {
            String currentCertificate = SecurityContextHolder.getContext().getAuthentication().getName();
            if (resultBO.getCertificateId().equals(currentCertificate)) {
                return resultBO;
            } else {
                throw new UnauthorizedException("The SMP " + resultBO.getSmpId() + " was not created with the certificate " + currentCertificate);
            }
        }
    }
}

