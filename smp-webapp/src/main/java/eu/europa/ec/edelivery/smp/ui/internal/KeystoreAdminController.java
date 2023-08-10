package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.PayloadValidatorService;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_KEYSTORE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_KEYSTORE)
public class KeystoreAdminController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(KeystoreAdminController.class);

    private final UIKeystoreService uiKeystoreService;
    private final PayloadValidatorService payloadValidatorService;

    public KeystoreAdminController(UIKeystoreService uiKeystoreService, PayloadValidatorService payloadValidatorService) {
        this.uiKeystoreService = uiKeystoreService;
        this.payloadValidatorService = payloadValidatorService;
    }

    @GetMapping(path = "/{user-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId) and @smpAuthorizationService.isSystemAdministrator")
    public List<CertificateRO> getSystemKeystoreCertificates(@PathVariable("user-id") String userId) {
        logAdminAccess("getSystemKeystoreCertificates");

        List<CertificateRO> keystoreEntriesList = uiKeystoreService.getKeystoreEntriesList();
        // clear encoded value to reduce http traffic
        keystoreEntriesList.stream().forEach(certificateRO -> {
            certificateRO.setEncodedValue(null);
            certificateRO.setStatus(EntityROStatus.PERSISTED.getStatusNumber());
        });
        return keystoreEntriesList;
    }

    @PreAuthorize("@smpAuthorizationService.systemAdministrator AND @smpAuthorizationService.isCurrentlyLoggedIn(#userEncId)")
    @PostMapping(path = "/{user-enc-id}/upload/{keystoreType}/{password}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_OCTET_STREAM_VALUE)
    public KeystoreImportResult uploadKeystore(@PathVariable("user-enc-id") String userEncId,
                                               @PathVariable("keystoreType") String keystoreType,
                                               @PathVariable("password") String password,
                                               @RequestBody byte[] fileBytes) {
        LOG.info("Got keystore data size: {}, type {}, password length {}", fileBytes.length, keystoreType, password.length());
        // validate uploaded content
        payloadValidatorService.validateUploadedContent(new ByteArrayInputStream(fileBytes), MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        // try to open keystore
        KeystoreImportResult keystoreImportResult = new KeystoreImportResult();
        try {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new ByteArrayInputStream(fileBytes), password.toCharArray());
            List<CertificateRO> certificateROList = uiKeystoreService.importKeys(keyStore, password);
            certificateROList.forEach(cert -> cert.setStatus(EntityROStatus.NEW.getStatusNumber()));
            keystoreImportResult.getAddedCertificates().addAll(certificateROList);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException |
                 UnrecoverableKeyException e) {
            String msg = e.getClass().getName() + " occurred while reading the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        }
        return keystoreImportResult;
    }

    @PreAuthorize("@smpAuthorizationService.systemAdministrator AND @smpAuthorizationService.isCurrentlyLoggedIn(#userEncId)")
    @DeleteMapping(value = "/{user-enc-id}/delete/{alias}", produces = APPLICATION_JSON_VALUE)
    public CertificateRO deleteCertificate(@PathVariable("user-enc-id") String userEncId,
                                           @PathVariable("alias") String alias) {
        LOG.info("Remove alias by user id {}, alias {}.", userEncId, alias);
        CertificateRO response;
        try {
            X509Certificate x509Certificate = uiKeystoreService.deleteKey(alias);
            if (x509Certificate == null) {
                String msg = "Certificate Key not removed because alias [" + alias + "] does not exist in keystore!";
                LOG.error(msg);
                response = creatEmptyResponse(alias, EntityROStatus.REMOVE, msg);
            } else {
                response = uiKeystoreService.convertToRo(x509Certificate);
                response.setAlias(alias);
                response.setStatus(EntityROStatus.REMOVE.getStatusNumber());
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            String msg = e.getClass().getName() + " occurred while reading the keystore: " + e.getMessage();
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
