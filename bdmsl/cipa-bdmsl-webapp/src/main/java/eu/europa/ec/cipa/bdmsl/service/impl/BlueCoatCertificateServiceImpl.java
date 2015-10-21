package eu.europa.ec.cipa.bdmsl.service.impl;

import eu.europa.ec.cipa.bdmsl.common.bo.CertificateDomainBO;
import eu.europa.ec.cipa.bdmsl.common.exception.CertificateRevokedException;
import eu.europa.ec.cipa.bdmsl.security.CertificateDetails;
import eu.europa.ec.cipa.bdmsl.service.IBlueCoatCertificateService;
import eu.europa.ec.cipa.bdmsl.service.ICRLVerifierService;
import eu.europa.ec.cipa.bdmsl.service.ICipaService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.service.AbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by feriaad on 18/06/2015.
 */
@Component
public class BlueCoatCertificateServiceImpl extends AbstractServiceImpl implements IBlueCoatCertificateService {

    @Autowired
    private ICipaService cipaService;

    @Autowired
    private ICRLVerifierService crlVerifierService;

    /**
     * Validate the certificate.
     *
     * @param certificate The certificate to validate.
     * @return <code>true</code> if valid, <code>false</code> otherwise.
     */
    public boolean isBlueCoatClientCertificateValid(final CertificateDetails certificate) throws TechnicalException, BusinessException {
        boolean result = false;
        Date today = Calendar.getInstance().getTime();
        if (!today.after(certificate.getValidFrom().getTime()) || !today.before(certificate.getValidTo().getTime())) {
            DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
            loggingService.securityLog(LogEvents.SEC_CERTIFICATE_EXPIRED, df.format(today), df.format(certificate.getValidFrom().getTime()), df.format(certificate.getValidTo().getTime()));
        } else {
            // We look into the database to search if the issuer belongs to the list of known and authorized list of root certificate aliases
            final CertificateDomainBO certDomainBO = cipaService.findDomain(certificate.getIssuer());
            if (certDomainBO != null) {
                try {
                    crlVerifierService.verifyCertificateCRLs(certificate.getSerial(), certDomainBO.getCrl());
                    result = true;
                } catch (final CertificateRevokedException exc) {
                    loggingService.securityLog(LogEvents.SEC_REVOKED_CERTIFICATE, certificate.getSubject());
                }
            } else {
                loggingService.securityLog(LogEvents.SEC_UNKNOWN_CERTIFICATE, certificate.getSubject());
            }
        }
        return result;
    }
}
