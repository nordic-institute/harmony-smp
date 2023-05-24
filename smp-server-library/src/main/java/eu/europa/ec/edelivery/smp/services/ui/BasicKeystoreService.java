package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ICRLVerifierService;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.security.cert.*;

import static eu.europa.ec.edelivery.smp.logging.SMPMessageCode.SEC_USER_CERT_INVALID;

public class BasicKeystoreService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(BasicKeystoreService.class);

    protected static final String CERT_ERROR_MSG_NOT_TRUSTED = "Certificate is not trusted!";
    protected static final String CERT_ERROR_MSG_REVOKED = "Certificate is revoked!";
    protected static final String CERT_ERROR_MSG_EXPIRED = "Certificate is expired!";
    protected static final String CERT_ERROR_MSG_NOT_YET_VALID = "Certificate is not yet valid!";
    protected static final String CERT_ERROR_MSG_NOT_VALIDATED = "Certificate not validated!";


    ICRLVerifierService crlVerifierService;

    public BasicKeystoreService(ICRLVerifierService verifyCertificateCRLs) {
        this.crlVerifierService = verifyCertificateCRLs;
    }

    public void basicCertificateValidation(X509Certificate cert, CertificateRO cro) {
        // first expect the worst
        cro.setInvalid(true);
        cro.setInvalidReason(CERT_ERROR_MSG_NOT_VALIDATED);
        try {
            // test if certificate is valid
            cert.checkValidity();
            // check CRL - it is using only HTTP or https
            if (crlVerifierService!=null) {
                crlVerifierService.verifyCertificateCRLs(cert);
            }
            cro.setInvalid(false);
            cro.setInvalidReason(null);
        } catch (CertificateExpiredException ex) {
            LOG.securityError(SEC_USER_CERT_INVALID, cro.getCertificateId(), ex.getMessage());
            cro.setInvalidReason(CERT_ERROR_MSG_EXPIRED);
        } catch (CertificateNotYetValidException ex) {
            LOG.securityError(SEC_USER_CERT_INVALID, cro.getCertificateId(), ex.getMessage());
            cro.setInvalidReason(CERT_ERROR_MSG_NOT_YET_VALID);
        } catch (CertificateRevokedException ex) {
            LOG.securityError(SEC_USER_CERT_INVALID, cro.getCertificateId(), ex.getMessage());
            cro.setInvalidReason(CERT_ERROR_MSG_REVOKED);
        } catch (CertificateException e) {
            LOG.securityError(SEC_USER_CERT_INVALID, e, cro.getCertificateId(), e.getMessage());
            if (ExceptionUtils.getRootCause(e) instanceof CertPathValidatorException) {
                cro.setInvalidReason("Certificate is not trusted! Invalid certificate policy path!");
            } else {
                cro.setInvalidReason(e.getMessage());
            }
        }
    }
}
