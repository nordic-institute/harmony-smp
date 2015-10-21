package eu.europa.ec.cipa.bdmsl.business.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.business.IManageCertificateBusiness;
import eu.europa.ec.cipa.bdmsl.business.IX509CertificateBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.CertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.PrepareChangeCertificateBO;
import eu.europa.ec.cipa.bdmsl.common.bo.WildcardBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.bdmsl.dao.ISmpDAO;
import eu.europa.ec.cipa.bdmsl.dao.IWildcardDAO;
import eu.europa.ec.cipa.common.business.AbstractBusinessImpl;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by feriaad on 15/06/2015.
 */
@Component
public class ManageCertificateBusinessImpl extends AbstractBusinessImpl implements IManageCertificateBusiness {

    @Autowired
    private ICertificateDAO certificateDAO;

    @Autowired
    private IX509CertificateBusiness x509CertificateBusiness;

    @Autowired
    private ISmpDAO smpDAO;

    @Autowired
    private IWildcardDAO wildcardDAO;

    @Override
    public void validateChangeCertificate(PrepareChangeCertificateBO prepareChangeCertificateBO) throws BusinessException, TechnicalException {
        if (Strings.isNullOrEmpty(prepareChangeCertificateBO.getPublicKey())) {
            throw new BadRequestException("The public key can't be null or empty");
        }
    }

    @Override
    public void checkMigrationDate(X509Certificate certificate, PrepareChangeCertificateBO prepareChangeCertificateBO) throws BusinessException, TechnicalException {
        if (prepareChangeCertificateBO.getMigrationDate() != null && new Date().after(prepareChangeCertificateBO.getMigrationDate().getTime())) {
            throw new BadRequestException("The migration date can't be in the past");
        }
        if (!prepareChangeCertificateBO.getMigrationDate().getTime().after(certificate.getNotBefore()) || !prepareChangeCertificateBO.getMigrationDate().getTime().before(certificate.getNotAfter())) {
            throw new BadRequestException("The migration date must be within " + certificate.getNotBefore() + " and " + certificate.getNotAfter());
        }
    }

    @Override
    public X509Certificate getX509Certificate(String publicKey) throws BadRequestException {
        try {
            InputStream is = new ByteArrayInputStream(publicKey.getBytes());
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
        } catch (CertificateException exc) {
            throw new BadRequestException("The new certificate is not valid: " + publicKey, exc);
        }
    }

    @Override
    public CertificateBO extractCertificate(String publicKey) throws BusinessException, TechnicalException {
        X509Certificate newCert = getX509Certificate(publicKey);
        CertificateBO certificateBO = new CertificateBO();
        certificateBO.setPemEncoding(publicKey);
        certificateBO.setValidFrom(DateUtils.toCalendar(newCert.getNotBefore()));
        certificateBO.setValidTo(DateUtils.toCalendar(newCert.getNotAfter()));
        certificateBO.setCertificateId(x509CertificateBusiness.calculateCertificateId(newCert));
        return certificateBO;
    }

    @Override
    public Long createNewCertificate(CertificateBO certificateBO) throws BusinessException, TechnicalException {
        return certificateDAO.createCertificate(certificateBO);
    }

    @Override
    public void updateCertificate(CertificateBO certificateBO) throws BusinessException, TechnicalException {
        certificateDAO.updateCertificate(certificateBO);
    }

    @Override
    public CertificateBO findCertificate(String certificateId) throws BusinessException, TechnicalException {
        return certificateDAO.findCertificateByCertificateId(certificateId);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int changeCertificates() throws BusinessException, TechnicalException {
        int count = 0;
        // List all certificates with a migration date in the past or at the present day
        List<CertificateBO> certificateBOList = certificateDAO.findCertificatesToChange(Calendar.getInstance());
        if (certificateBOList != null && !certificateBOList.isEmpty()) {
            for (CertificateBO certificateBO : certificateBOList) {
                loggingService.info("Migrating from certificate " + certificateBO.getId() + " to " + certificateBO.getNewCertificateId());
                // change the certificate for all the SMP
                loggingService.info("Changing certificate for SMP from " + certificateBO.getId() + " to " + certificateBO.getNewCertificateId());
                smpDAO.changeCertificateForSMP(certificateBO.getId(), certificateBO.getNewCertificateId());

                // change the wildcard authorization
                loggingService.info("Changing certificate for the wildcard authorization from " + certificateBO.getId() + " to " + certificateBO.getNewCertificateId());
                wildcardDAO.changeWildcardAuthorization(certificateBO.getId(), certificateBO.getNewCertificateId());

                // delete the old certificate
                loggingService.info("Removing old certificate " + certificateBO.getId());
                certificateDAO.delete(certificateBO);

                count++;
            }
        } else {
            loggingService.info("No certificates must be changed");
        }
        return count;
    }

    @Override
    public WildcardBO findWildCard(String scheme, String currentCertificate) throws BusinessException, TechnicalException {
        CertificateBO certificateBO = certificateDAO.findCertificateByCertificateId(currentCertificate);
        return wildcardDAO.findWildcard(scheme, certificateBO);
    }
}
