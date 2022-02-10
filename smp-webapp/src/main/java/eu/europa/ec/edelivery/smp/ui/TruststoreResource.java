package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.utils.X509CertificateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(value = "/ui/rest/truststore")
public class TruststoreResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(TruststoreResource.class);

    @Autowired
    private UITruststoreService uiTruststoreService;

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public ServiceResult<CertificateRO> getCertificateList() {
        List<CertificateRO> lst = uiTruststoreService.getCertificateROEntriesList();
        // clear encoded value to reduce http traffic
        lst.stream().forEach(certificateRO -> {
            certificateRO.setEncodedValue(null);
        });

        ServiceResult<CertificateRO> sg = new ServiceResult<>();
        sg.getServiceEntities().addAll(lst);
        sg.setCount((long) lst.size());
        return sg;
    }

    @PostMapping(value = "/{id}/certdata", produces = {"application/json"}, consumes = {"application/octet-stream"})
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public CertificateRO uploadCertificate(@PathVariable("id") Long id,
                                               @RequestBody byte[] fileBytes) {
        LOG.info("Got truststore cert size: {}", fileBytes.length);

        X509Certificate x509Certificate;
        CertificateRO certificateRO=null;
        try {
            x509Certificate = X509CertificateUtils.getX509Certificate(fileBytes);
        } catch (SMPRuntimeException e) {
            LOG.error("Error occurred while parsing certificate.", e);
            return certificateRO;
        }
        try {
            String alias = uiTruststoreService.addCertificate(null, x509Certificate);
            certificateRO = uiTruststoreService.convertToRo(x509Certificate);
            certificateRO.setAlias(alias);
        } catch (NoSuchAlgorithmException |  KeyStoreException | IOException |CertificateException e) {
            LOG.error("Error occurred while parsing certificate.", e);
            return certificateRO;
        }
        return certificateRO;
    }


    @DeleteMapping(value = "/{id}/delete/{alias}", produces = {"application/json"})
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public KeystoreImportResult deleteCertificate(@PathVariable("id") Long id,
                                               @PathVariable("alias") String alias) {
        LOG.info("Remove alias by user id {}, alias {}.", id, alias);
        KeystoreImportResult keystoreImportResult = new KeystoreImportResult();

        try {
            uiTruststoreService.deleteCertificate(alias);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            String msg = e.getClass().getName() +" occurred while reading the truststore: " + e.getMessage();
            LOG.error(msg, e);
            keystoreImportResult.setErrorMessage(msg);
        }

        return keystoreImportResult;
    }
}
