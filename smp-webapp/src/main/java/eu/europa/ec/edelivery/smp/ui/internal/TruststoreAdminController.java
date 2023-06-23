package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.PayloadValidatorService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_INTERNAL_TRUSTSTORE)
public class TruststoreAdminController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(TruststoreAdminController.class);


    private final UITruststoreService uiTruststoreService;
    private final PayloadValidatorService payloadValidatorService;

    public TruststoreAdminController(UITruststoreService uiTruststoreService, PayloadValidatorService payloadValidatorService) {
        this.uiTruststoreService = uiTruststoreService;
        this.payloadValidatorService = payloadValidatorService;
    }

    @GetMapping(path = "/{user-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId) and @smpAuthorizationService.isSystemAdministrator")
    public List<CertificateRO> getSystemTruststoreCertificates(@PathVariable("user-id") String userId) {
        logAdminAccess("getSystemTruststoreCertificates");

        List<CertificateRO> truststoreEntriesList = uiTruststoreService.getCertificateROEntriesList();
        // clear encoded value to reduce http traffic
        truststoreEntriesList.stream().forEach(certificateRO -> {
            certificateRO.setEncodedValue(null);
            certificateRO.setStatus(EntityROStatus.PERSISTED.getStatusNumber());
        });
        return truststoreEntriesList;
    }

    @PostMapping(value = "/{user-id}/upload-certificate", consumes = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId) and @smpAuthorizationService.isSystemAdministrator")
    public CertificateRO uploadCertificate(@PathVariable("user-id") String userId,
                                           @RequestBody byte[] fileBytes) {
        LOG.info("Got certificate cert size: {}", fileBytes.length);

        // validate content
        payloadValidatorService.validateUploadedContent(new ByteArrayInputStream(fileBytes), MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);

        X509Certificate x509Certificate;
        CertificateRO certificateRO;
        try {
            x509Certificate = X509CertificateUtils.getX509Certificate(fileBytes);
        } catch (SMPRuntimeException | CertificateException e) {
            String message = "Error occurred while parsing certificate. Is certificate valid!";
            LOG.error(message, e);
            return creatEmptyResponse(null, EntityROStatus.ERROR, message);
        }
        try {
            String alias = uiTruststoreService.addCertificate(null, x509Certificate);
            certificateRO = uiTruststoreService.convertToRo(x509Certificate);
            uiTruststoreService.basicCertificateValidation(x509Certificate, certificateRO);
            certificateRO.setAlias(alias);
            certificateRO.setStatus(EntityROStatus.NEW.getStatusNumber());
            return certificateRO;
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
            String message = "Error occurred while storing the certificate!";
            LOG.error(message, e);
            creatEmptyResponse(null, EntityROStatus.ERROR, message);
        }
        return null;
    }


    @DeleteMapping(value = "/{id}/delete/{alias}", produces = {"application/json"})
    @PreAuthorize("@smpAuthorizationService.systemAdministrator && @smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    public CertificateRO deleteCertificate(@PathVariable("id") String userId,
                                           @PathVariable("alias") String alias) {
        logAdminAccess("deleteCertificate: " + alias);
        LOG.info("Remove alias by user id {}, alias {}.", userId, alias);
        CertificateRO response;
        try {
            X509Certificate x509Certificate = uiTruststoreService.deleteCertificate(alias);
            if (x509Certificate == null) {
                String msg = "Certificate not removed because alias [" + alias + "] does not exist in truststore!";
                LOG.error(msg);
                response = creatEmptyResponse(alias, EntityROStatus.REMOVE, msg);
            } else {
                response = uiTruststoreService.convertToRo(x509Certificate);
                response.setAlias(alias);
                response.setStatus(EntityROStatus.REMOVE.getStatusNumber());
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            String msg = e.getClass().getName() + " occurred while reading the truststore: " + e.getMessage();
            LOG.error(msg, e);
            response = creatEmptyResponse(alias, EntityROStatus.ERROR, msg);
        }
        return response;
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Truststore action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }

    public CertificateRO creatEmptyResponse(String alias, EntityROStatus status, String message) {
        CertificateRO certificateRO = new CertificateRO();
        certificateRO.setAlias(alias);
        certificateRO.setActionMessage(message);
        certificateRO.setStatus(status.getStatusNumber());
        return certificateRO;
    }
}
