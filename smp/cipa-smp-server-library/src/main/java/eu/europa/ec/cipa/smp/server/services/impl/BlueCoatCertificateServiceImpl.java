package eu.europa.ec.cipa.smp.server.services.impl;

import eu.europa.ec.cipa.smp.server.exception.CertificateNotFoundException;
import eu.europa.ec.cipa.smp.server.exception.CertificateRevokedException;
import eu.europa.ec.cipa.smp.server.exception.common.TechnicalException;
import eu.europa.ec.cipa.smp.server.security.CertificateDetails;
import eu.europa.ec.cipa.smp.server.services.IBlueCoatCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rodrfla on 18/01/2017
 */
@Component
public class BlueCoatCertificateServiceImpl implements IBlueCoatCertificateService {
    private static final Logger logger = LoggerFactory.getLogger(BlueCoatCertificateServiceImpl.class);

    /**
     * Validate the certificate.
     *
     * @param certificate The certificate to validate.
     * @return <code>true</code> if valid, <code>false</code> otherwise.
     */
    public boolean isBlueCoatClientCertificateValid(final CertificateDetails certificate) {
        try {
            validateBlueCoatClientCertificate(certificate);
            return true;
        } catch (Exception exc) {
            logger.error(String.format("SEC_UNAUTHORIZED_ACCESS | %s", certificate.getSubject()));
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    public void validateBlueCoatClientCertificate(final CertificateDetails certificate) throws TechnicalException {
        Date today = Calendar.getInstance().getTime();
        Boolean isCertValid = null;
        if ((certificate.getValidFrom()!= null && !today.after(certificate.getValidFrom().getTime())) || (certificate.getValidTo()!= null &&!today.before(certificate.getValidTo().getTime()))) {
            DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
            logger.info(String.format("SEC_CERTIFICATE_EXPIRED | Date: %s, Certificate valid from: %s, Certificate valid to: %s", df.format(today), df.format(certificate.getValidFrom().getTime()), df.format(certificate.getValidTo().getTime())));
            throw new CertificateRevokedException("The certificate is revoked.");
        } else {
            logger.info(String.format("Certificate Issuer: %s, Subject: %s", certificate.getIssuer(), certificate.getSubject()));
            isCertValid = isCertificateTrusted(certificate);

            if (isCertValid == null) {
                logger.info(String.format("SEC_UNKNOWN_CERTIFICATE | Certificate Issuer: %s, Subject: %s", certificate.getIssuer(), certificate.getSubject()));
                throw new CertificateNotFoundException("Certificate not found");
            }
        }
    }

    private Boolean isCertificateTrusted(CertificateDetails certificate) throws TechnicalException {
        //  CertificateDomainBO certDomainBO = bdmslService.findDomain(certificate.getIssuer());
//        if (certDomainBO != null && certDomainBO.isRootCA()) {
//            return isCertificateValidForCertificateCRLs(certDomainBO, certificate);
//        }

        //Null means it is not rootCA, validation should be ignored.
        return null;
    }
}
