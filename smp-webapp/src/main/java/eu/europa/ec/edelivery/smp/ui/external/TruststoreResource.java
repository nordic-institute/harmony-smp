package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.X509CertificateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
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
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_TRUSTSTORE)
public class TruststoreResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(TruststoreResource.class);

    @Autowired
    private UITruststoreService uiTruststoreService;


    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PostMapping(path = "/{user-id}/validate-certificate", consumes = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public CertificateRO validateCertificate(@PathVariable("user-id") String userId, @RequestBody byte[] data) {
        LOG.info("Got certificate data size: {}", data.length);
        return uiTruststoreService.getCertificateData(data, true);
    }

}
