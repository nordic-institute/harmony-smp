package eu.europa.ec.cipa.smp.server.services.impl;

import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import eu.europa.ec.cipa.smp.server.exception.CertificateNotFoundException;
import eu.europa.ec.cipa.smp.server.exception.CertificateRevokedException;
import eu.europa.ec.cipa.smp.server.exception.common.TechnicalException;
import eu.europa.ec.cipa.smp.server.security.CertificateDetails;
import eu.europa.ec.cipa.smp.server.services.IBlueCoatCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rodrfla on 18/01/2017
 */
@Component(value = "blueCoatCertificateServiceImpl")
@Scope("prototype")
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
            logger.error(String.format("SEC_UNAUTHORIZED_ACCESS | Certificate Subject %s", certificate.getSubject()));
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    public void validateBlueCoatClientCertificate(final CertificateDetails certificate) throws TechnicalException {
        dateCertificateChecking(certificate);
        databaseCertificateChecking(certificate);
    }

    private void dateCertificateChecking(CertificateDetails certificate) throws TechnicalException {
        Date today = Calendar.getInstance().getTime();
        if ((certificate.getValidFrom() != null && !today.after(certificate.getValidFrom().getTime())) ||
                (certificate.getValidTo() != null && !today.before(certificate.getValidTo().getTime()))) {
            DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
            logger.info(String.format("SEC_CERTIFICATE_EXPIRED | Date: %s, Certificate valid from: %s, Certificate valid to: %s", df.format(today), df.format(certificate.getValidFrom().getTime()), df.format(certificate.getValidTo().getTime())));
            throw new CertificateRevokedException("The certificate is revoked.");
        }
    }

    private void databaseCertificateChecking(CertificateDetails certificate) throws TechnicalException {
        logger.info(String.format("Checking Certificate into the DB. Issuer: %s, Subject: %s", certificate.getIssuer(), certificate.getSubject()));

        String errorMessage = String.format("SEC_UNKNOWN_CERTIFICATE | Certificate Issuer: %s, Subject: %s", certificate.getIssuer(), certificate.getSubject());
        if (certificate.getCertificateId() == null) {
            logger.error(errorMessage);
            throw new CertificateNotFoundException("Certificate Id must be not null.");
        }

        final IDataManager aDataManager = DataManagerFactory.getInstance();
        final DBUser dbUser = aDataManager.getCurrentEntityManager().find(DBUser.class, certificate.getCertificateId());

        //TODO I think checking only the username might be not enough. WE should have more validation here
        if (dbUser == null) {
            logger.error(errorMessage);
            throw new CertificateNotFoundException(String.format("Certificate %s not found",certificate.getCertificateId()));
        }
    }
}
