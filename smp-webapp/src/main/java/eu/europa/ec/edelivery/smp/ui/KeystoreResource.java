package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = "/ui/rest/keystore")
public class KeystoreResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(KeystoreResource.class);

    @Autowired
    private UIKeystoreService uiKeystoreService;

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
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

    @PostMapping(value = "/{id}/upload/{keystoreType}/{password}", produces = {"application/json"}, consumes = {"application/octet-stream"})
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public KeystoreImportResult uploadKeystore(@PathVariable("id") Long id,
                                               @PathVariable("keystoreType") String keystoreType,
                                               @PathVariable("password") String password,
                                               @RequestBody byte[] fileBytes) {
        LOG.info("Got keystore data size: {}, type {}, password length {}", fileBytes.length, keystoreType, password.length());
        // try to open keystore
        KeystoreImportResult keystoreImportResult = new KeystoreImportResult();


        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new ByteArrayInputStream(fileBytes), password.toCharArray());

            LOG.info(keyStore.aliases().nextElement());
            uiKeystoreService.importKeys(keyStore,password );
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            String msg = "CertificateException occurred while reading the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        } catch (NoSuchAlgorithmException e) {
            String msg = "NoSuchAlgorithmException occurred while reading the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        } catch (IOException e) {
            String msg = "IOException occurred while reading the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        } catch (UnrecoverableKeyException e) {
            String msg = "UnrecoverableKeyException occurred while importing new keys the keystore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        }

        return keystoreImportResult;
    }
}
