/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
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
    public DocumentRO getDocumentForResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                             @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                             @RequestParam(value = PARAM_NAME_VERSION, defaultValue = "-1") int version) {
        logAdminAccess("getDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiDocumentService.getDocumentForResource(resourceId, version);
    }

    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRO getDocumentForSubResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
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
                                 @RequestBody DocumentRO document) {
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
                                            @RequestBody DocumentRO document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        uiDocumentService.validateDocumentForSubresource(subresourceId, resourceId, document);
    }


    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GENERATE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRO generateDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                       @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                       @RequestBody(required = false) DocumentRO document) {
        logAdminAccess("generateDocument");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiDocumentService.generateDocumentForResource(resourceId, document);
    }

    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_GENERATE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRO generateSubresourceDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                  @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                                  @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId,
                                                  @RequestBody(required = false) DocumentRO document) {
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
    public DocumentRO saveDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                   @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                   @RequestBody DocumentRO document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiDocumentService.saveDocumentForResource(resourceId, document);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE,
            consumes = MimeTypeUtils.APPLICATION_JSON_VALUE,
            produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and @smpAuthorizationService.isResourceAdministrator(#resourceEncId)")
    public DocumentRO saveSubresourceDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                              @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                              @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId,
                                              @RequestBody DocumentRO document) {
        logAdminAccess("validateDocumentForResource");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        return uiDocumentService.saveSubresourceDocumentForResource(subresourceId, resourceId, document);
    }


    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}

