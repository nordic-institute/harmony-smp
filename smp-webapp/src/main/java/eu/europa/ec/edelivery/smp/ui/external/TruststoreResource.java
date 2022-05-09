package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.PayloadValidatorService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_TRUSTSTORE)
public class TruststoreResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(TruststoreResource.class);

    private final UITruststoreService uiTruststoreService;
    private final PayloadValidatorService payloadValidatorService;

    public TruststoreResource(UITruststoreService uiTruststoreService, PayloadValidatorService payloadValidatorService) {
        this.uiTruststoreService = uiTruststoreService;
        this.payloadValidatorService = payloadValidatorService;
    }

    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PostMapping(path = "/{user-id}/validate-certificate", consumes = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public CertificateRO validateCertificate(@PathVariable("user-id") String userId, @RequestBody byte[] data) {
        LOG.info("Got certificate data size: {}", data.length);
        // validate uploaded content
        payloadValidatorService.validateUploadedContent(new ByteArrayInputStream(data), MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        return uiTruststoreService.getCertificateData(data, true);
    }

}
