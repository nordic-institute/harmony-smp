package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.smp.auth.SMPAuthorizationService;
import eu.europa.ec.edelivery.smp.data.ui.ExtensionRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIExtensionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_EXTENSION;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_EXTENSION)
public class ExtensionAdminController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ExtensionAdminController.class);

    protected UIExtensionService uiExtensionService;

    protected SMPAuthorizationService authorizationService;

    public ExtensionAdminController(UIExtensionService uiExtensionService, SMPAuthorizationService authorizationService) {
        this.uiExtensionService = uiExtensionService;
        this.authorizationService = authorizationService;
    }

    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public List<ExtensionRO> getExtensionList() {
        LOG.info("getExtensionList count: ");
        return uiExtensionService.getExtensions();
    }


}
