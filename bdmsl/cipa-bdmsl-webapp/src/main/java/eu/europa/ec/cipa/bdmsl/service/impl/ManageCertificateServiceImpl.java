package eu.europa.ec.cipa.bdmsl.service.impl;

import eu.europa.ec.cipa.bdmsl.business.IManageCertificateBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.PrepareChangeCertificateBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.bdmsl.service.IManageCertificateService;
import eu.europa.ec.cipa.bdmsl.service.IX509CertificateService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.service.AbstractServiceImpl;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Created by feriaad on 12/06/2015.
 */
@Service(value = "manageCertificateService")
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class ManageCertificateServiceImpl extends AbstractServiceImpl implements IManageCertificateService {

    @Autowired
    private IManageCertificateBusiness manageCertificateBusiness;

    @Autowired
    private IX509CertificateService x509CertificateService;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_SMP')")
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void prepareChangeCertificate(PrepareChangeCertificateBO prepareChangeCertificateBO) throws BusinessException, TechnicalException {
        // validate the data contained in the request
        manageCertificateBusiness.validateChangeCertificate(prepareChangeCertificateBO);

        // validate the certificate
        X509Certificate newCert = manageCertificateBusiness.getX509Certificate(prepareChangeCertificateBO.getPublicKey());
        if (!x509CertificateService.isClientX509CertificateValid(new X509Certificate[]{newCert})) {
            throw new CertificateAuthenticationException("The new certificate is not trusted");
        }

        // If the migrationDate element is empty, then the "Valid From" date is extracted from the certificate and is used as the migrationDate.
        if (prepareChangeCertificateBO.getMigrationDate() == null) {
            prepareChangeCertificateBO.setMigrationDate(DateUtils.toCalendar(newCert.getNotBefore()));
        }

        // Check if the migration date is valid for the given certificate
        manageCertificateBusiness.checkMigrationDate(newCert, prepareChangeCertificateBO);

        // extract information from the certificate
        CertificateBO newCertificateBO = manageCertificateBusiness.extractCertificate(prepareChangeCertificateBO.getPublicKey());

        // create or update the new certificate in the DB
        Long newCertId;
        CertificateBO existingNewCertificate = manageCertificateBusiness.findCertificate(newCertificateBO.getCertificateId());
        if (existingNewCertificate == null) {
            newCertId = manageCertificateBusiness.createNewCertificate(newCertificateBO);
        } else {
            newCertId = existingNewCertificate.getId();
            newCertificateBO.setId(existingNewCertificate.getId());
            manageCertificateBusiness.updateCertificate(newCertificateBO);
        }

        // update the current certificate
        prepareChangeCertificateBO.getCurrentCertificate().setNewCertificateId(newCertId);
        prepareChangeCertificateBO.getCurrentCertificate().setMigrationDate(prepareChangeCertificateBO.getMigrationDate());

        manageCertificateBusiness.updateCertificate(prepareChangeCertificateBO.getCurrentCertificate());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void changeCertificates() throws BusinessException, TechnicalException{
        try {
            int count = manageCertificateBusiness.changeCertificates();
            loggingService.businessLog(LogEvents.BUS_CERTIFICATE_CHANGE_JOB_SUCCESS, Integer.toString(count));
        } catch (Exception exc) {
            loggingService.businessLog(LogEvents.BUS_CERTIFICATE_CHANGE_JOB_FAILED, exc);
            throw exc;
        }
    }
}

