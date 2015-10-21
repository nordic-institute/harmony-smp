package eu.europa.ec.cipa.bdmsl.business.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.business.IManageServiceMetadataBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.MigrationRecordBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.common.exception.MigrationPlannedException;
import eu.europa.ec.cipa.bdmsl.common.exception.SmpNotFoundException;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.bdmsl.dao.IMigrationDAO;
import eu.europa.ec.cipa.bdmsl.dao.ISmpDAO;
import eu.europa.ec.cipa.bdmsl.security.CertificateDetails;
import eu.europa.ec.cipa.common.business.AbstractBusinessImpl;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by feriaad on 15/06/2015.
 */
@Component
public class ManageServiceMetadataBusinessImpl extends AbstractBusinessImpl implements IManageServiceMetadataBusiness {

    private static final String DOMAIN_IDENTIFIER = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
    private static final String DOMAIN_NAME_RULE = "(" + DOMAIN_IDENTIFIER + ")((\\.)(" + DOMAIN_IDENTIFIER + "))*";
    private static final String IP_V4_RULE = "(\\p{Digit})+(\\.)(\\p{Digit})+(\\.)(\\p{Digit})+(\\.)(\\p{Digit})+";

    @Autowired
    private ISmpDAO smpDAO;

    @Autowired
    private ICertificateDAO certificateDAO;

    @Autowired
    private IMigrationDAO migrationDAO;

    @Override
    public ServiceMetadataPublisherBO read(String id) throws
            BusinessException, TechnicalException {
        return smpDAO.findSMP(id);
    }

    @Override
    public void validateSMPData(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException {
        this.validateSMPId(smpBO.getSmpId());

        if (Strings.isNullOrEmpty(smpBO.getLogicalAddress()))
            throw new BadRequestException("Logical address must not be null or empty.");

        if (Strings.isNullOrEmpty(smpBO.getPhysicalAddress()))
            throw new BadRequestException("Physical address must not be null or empty.");

        try {
            new URL(smpBO.getLogicalAddress());
        } catch (final MalformedURLException ex) {
            throw new BadRequestException("Logical address is malformed: " + ex.getMessage(), ex);
        }

        if (!smpBO.getPhysicalAddress().matches(IP_V4_RULE)) {
            throw new BadRequestException("Physical address is malformed: " + smpBO.getPhysicalAddress());
        }

    }

    @Override
    public void verifySMPNotExist(String smpId) throws BusinessException, TechnicalException {
        ServiceMetadataPublisherBO smp = smpDAO.findSMP(smpId);
        if (smp != null) {
            throw new BadRequestException("The SMP '" + smpId + "' already exists.");
        }
    }

    @Override
    public void createSMP(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException {
        smpDAO.createSMP(smpBO, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public void createCurrentCertificate() throws BusinessException, TechnicalException {
        CertificateDetails details = (CertificateDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        CertificateBO certificateBO = new CertificateBO();
        certificateBO.setValidFrom(details.getValidFrom());
        certificateBO.setValidTo(details.getValidTo());
        certificateBO.setCertificateId(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!Strings.isNullOrEmpty(details.getPemEncoding())) {
            certificateBO.setPemEncoding(details.getPemEncoding());
        }
        certificateDAO.createCertificate(certificateBO);
    }

    @Override
    public CertificateBO findCertificate(String name) throws BusinessException, TechnicalException {
        return certificateDAO.findCertificateByCertificateId(name);
    }

    @Override
    public ServiceMetadataPublisherBO verifySMPExist(String smpId) throws BusinessException, TechnicalException {
        ServiceMetadataPublisherBO smp = smpDAO.findSMP(smpId);
        if (smp == null) {
            throw new SmpNotFoundException("The SMP '" + smpId + "' doesn't exist.");
        }
        return smp;
    }

    @Override
    public void deleteSMP(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException {
        smpDAO.deleteSMP(smpBO);
    }

    @Override
    public void validateSMPId(String smpId) throws BusinessException, TechnicalException {
        if (Strings.isNullOrEmpty(smpId)) {
            throw new BadRequestException("Publisher ID cannot be 'null' or empty");
        }
        if (!smpId.matches(DOMAIN_NAME_RULE)) {
            throw new BadRequestException("Publisher ID is malformed: " + smpId);
        }

        if (smpId.length() > 253) {
            throw new BadRequestException("Publisher ID total length > 253 : " + smpId + " : " + smpId.length());
        }

        final String[] parts = smpId.split("\\.");
        if (parts != null) {
            for (final String part : parts) {
                if (part.length() > 63) {
                    throw new BadRequestException("Publisher ID part length > 63 : " + smpId);
                }
            }
        }
        loggingService.debug("SMP id '" + smpId + "' is valid");
    }

    @Override
    public void updateSMP(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException {
        smpDAO.updateSMP(smpBO);
    }

    @Override
    public void checkNoMigrationPlanned(String smpId) throws BusinessException, TechnicalException {
        List<MigrationRecordBO> migrationRecordBOs = migrationDAO.findMigrationsRecordsForSMP(smpId);
        if (migrationRecordBOs!= null && !migrationRecordBOs.isEmpty()) {
            throw new MigrationPlannedException("A migration is planned for the SMP " + smpId + ". Please contact your system administrator.");
        }
    }
}
