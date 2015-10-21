package eu.europa.ec.cipa.bdmsl.service.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.business.IX509CertificateBusiness;
import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.bdmsl.common.exception.CertificateRevokedException;
import eu.europa.ec.cipa.bdmsl.service.ICRLVerifierService;
import eu.europa.ec.cipa.bdmsl.service.IX509CertificateService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.service.AbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by feriaad on 18/06/2015.
 */
@Service
public class X509CertificateServiceImpl extends AbstractServiceImpl implements IX509CertificateService {

    @Autowired
    private ICRLVerifierService crlVerifierService;

    @Autowired
    private IX509CertificateBusiness x509CertificateBusiness;

    @Override
    public boolean isClientX509CertificateValid(final X509Certificate[] certificates) throws TechnicalException, BusinessException {
        // We look into the database to search if the issuer belongs to the list of authorized root certificate aliases
        boolean result = false;
        String trustedRootDN = this.getTrustedRootCertificateDN(certificates);
        if (!Strings.isNullOrEmpty(trustedRootDN)) {
            Date today = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
            for (X509Certificate cert : certificates) {
                try {
                    // crl from the certificate
                    crlVerifierService.verifyCertificateCRLs(cert);
                    cert.checkValidity();
                    result = true;
                } catch (final CertificateRevokedException exc) {
                    result = false;
                    loggingService.error(exc.getMessage(), exc);
                    loggingService.securityLog(LogEvents.SEC_REVOKED_CERTIFICATE, cert.getSubjectX500Principal().toString());
                } catch (final CertificateAuthenticationException exc) {
                    result = false;
                    loggingService.error(exc.getMessage(), exc);
                } catch (CertificateExpiredException exc) {
                    result = false;
                    loggingService.error(exc.getMessage(), exc);
                    loggingService.securityLog(LogEvents.SEC_CERTIFICATE_EXPIRED, df.format(today), df.format(cert.getNotBefore().getTime()), df.format(cert.getNotAfter().getTime()));
                } catch (CertificateNotYetValidException exc) {
                    result = false;
                    loggingService.error(exc.getMessage(), exc);
                    loggingService.securityLog(LogEvents.SEC_CERTIFICATE_NOT_YET_VALID, df.format(today), df.format(cert.getNotBefore().getTime()), df.format(cert.getNotAfter().getTime()));
                }
            }
        } else {
            String certificateSubjects = "";
            for (int i = 0; i < certificates.length; i++) {
                certificateSubjects += certificates[i].getSubjectX500Principal().toString();
                if (i != certificates.length -1) {
                    certificateSubjects += ", ";
                }
            }
            loggingService.securityLog(LogEvents.SEC_UNKNOWN_CERTIFICATE, certificateSubjects);
        }

        return result;
    }

    @Override
    public String getTrustedRootCertificateDN(X509Certificate[] certificates) throws TechnicalException {
        return x509CertificateBusiness.getTrustedRootCertificateDN(certificates);
    }

    @Override
    public X509Certificate getCertificate(final X509Certificate[] requestCerts) throws TechnicalException, BusinessException {
        return x509CertificateBusiness.getCertificate(requestCerts);
    }

    @Override
    public String calculateCertificateId(final X509Certificate cert) throws TechnicalException {
        return x509CertificateBusiness.calculateCertificateId(cert);
    }
}
