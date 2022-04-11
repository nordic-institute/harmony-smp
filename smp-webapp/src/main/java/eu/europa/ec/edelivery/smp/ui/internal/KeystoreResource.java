package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
public class KeystoreResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(KeystoreResource.class);

    @Autowired
    private UIKeystoreService uiKeystoreService;

    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<CertificateRO> getKeyCertificateList() {
        List<CertificateRO> lst = uiKeystoreService.getKeystoreEntriesList();
        // clear encoded value to reduce http traffic
        lst.stream().forEach(certificateRO -> {
            certificateRO.setEncodedValue(null);
        });
        ServiceResult<CertificateRO> sg = new ServiceResult<>();
        sg.getServiceEntities().addAll(lst);
        sg.setCount((long) lst.size());
        return sg;
    }

    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#userEncId)")
    @PostMapping(path = "/{user-enc-id}/upload/{keystoreType}/{password}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_OCTET_STREAM_VALUE)
    public KeystoreImportResult uploadKeystore(@PathVariable("user-enc-id") String userEncId,
                                               @PathVariable("keystoreType") String keystoreType,
                                               @PathVariable("password") String password,
                                               @RequestBody byte[] fileBytes) {
        LOG.info("Got keystore data size: {}, type {}, password length {}", fileBytes.length, keystoreType, password.length());
        // try to open keystore
        KeystoreImportResult keystoreImportResult = new KeystoreImportResult();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new ByteArrayInputStream(fileBytes), password.toCharArray());
            LOG.debug(keyStore.aliases().nextElement());
            uiKeystoreService.importKeys(keyStore, password);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
            String msg = e.getClass().getName() + " occurred while reading the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        }

        return keystoreImportResult;
    }

    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#userEncId)")
    @DeleteMapping(value = "/{user-enc-id}/delete/{alias}", produces = APPLICATION_JSON_VALUE)
    public KeystoreImportResult deleteCertificate(@PathVariable("user-enc-id") String userEncId,
                                                  @PathVariable("alias") String alias) {
        LOG.info("Remove alias by user id {}, alias {}.", userEncId, alias);
        KeystoreImportResult keystoreImportResult = new KeystoreImportResult();
        try {
            uiKeystoreService.deleteKey(alias);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            String msg = e.getClass().getName() + " occurred while reading the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        }
        return keystoreImportResult;
    }
}
