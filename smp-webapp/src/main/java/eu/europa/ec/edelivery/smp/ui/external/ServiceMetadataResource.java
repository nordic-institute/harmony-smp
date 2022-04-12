package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceMetadataService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_SERVICE_METADATA)
public class ServiceMetadataResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceMetadataResource.class);

    @Autowired
    private UIServiceMetadataService uiServiceMetadataService;

    @GetMapping(path = "{serviceMetadataId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceMetadataRO getServiceGroupMetadataById(@PathVariable Long serviceMetadataId) {
        LOG.info("Get service group metadata [{}]", serviceMetadataId);
        return uiServiceMetadataService.getServiceMetadataXMLById(serviceMetadataId);
    }

    @PostMapping(path = "validate", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceMetadataValidationRO validateServiceMetadata(@RequestBody ServiceMetadataValidationRO serviceMetadataValidationRO) {
        LOG.info("Validate service group metadata");
        return uiServiceMetadataService.validateServiceMetadata(serviceMetadataValidationRO);
    }
}

