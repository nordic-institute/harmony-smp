package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.ui.DocumentRo;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDocumentService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainResource is to provide search method to retrieve configured domains in SMP.
 * base path for the resource includes two variables user who is editing and domain for the group
 * /ui/edit/rest/[user-id]/domain/[domain-id]/group/[group-id]/resource
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_EDIT_DOCUMENT)
public class DocumentEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DocumentEditController.class);
    private final UIDocumentService uiDocumentService;

    public DocumentEditController(UIDocumentService uiDocumentService) {
        this.uiDocumentService = uiDocumentService;
    }


    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRo getDocumentForResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                             @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                             @RequestParam(value = PARAM_NAME_VERSION, defaultValue = "-1") int version) {
        logAdminAccess("getDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiDocumentService.getDocumentForResource(resourceId, version);
    }

    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRo getDocumentForSubResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                                @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId,
                                                @RequestParam(value = PARAM_NAME_VERSION, defaultValue = "-1") int version) {
        logAdminAccess("getDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        return uiDocumentService.getDocumentForSubResource(subresourceId, resourceId, version);
    }

    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_VALIDATE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public void validateDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                 @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                 @RequestBody DocumentRo document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        uiDocumentService.validateDocumentForResource(resourceId, document);
    }

    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_VALIDATE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public void validateSubresourceDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                            @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                            @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId,
                                            @RequestBody DocumentRo document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        uiDocumentService.validateDocumentForSubresource(subresourceId, resourceId, document);
    }


    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GENERATE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRo generateDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                       @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                       @RequestBody(required = false) DocumentRo document) {
        logAdminAccess("generateDocument");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiDocumentService.generateDocumentForResource(resourceId, document);
    }

    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_GENERATE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRo generateSubresourceDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                  @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                                  @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId,
                                                  @RequestBody(required = false) DocumentRo document) {
        logAdminAccess("generateDocument");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        return uiDocumentService.generateDocumentForSubresource(subresourceId, resourceId, document);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET,
            consumes = MimeTypeUtils.APPLICATION_JSON_VALUE,
            produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRo saveDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                   @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                   @RequestBody DocumentRo document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiDocumentService.saveDocumentForResource(resourceId, document);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE,
            consumes = MimeTypeUtils.APPLICATION_JSON_VALUE,
            produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRo saveSubresourceDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                              @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                              @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId,
                                              @RequestBody DocumentRo document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        return uiDocumentService.saveSubresourceDocumentForResource(subresourceId, resourceId, document);
    }


    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}

