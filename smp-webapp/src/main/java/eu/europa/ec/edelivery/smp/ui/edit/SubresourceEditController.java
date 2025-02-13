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


import eu.europa.ec.edelivery.smp.data.ui.SubresourceRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UISubresourceService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_EDIT_SUBRESOURCE)
public class SubresourceEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SubresourceEditController.class);
    private final UISubresourceService uiSubresourceService;

    public SubresourceEditController(UISubresourceService uiSubresourceService) {
        this.uiSubresourceService = uiSubresourceService;
    }

    /**
     * Return all SubResources  for the resource
     *
     * @param userEncId
     * @param resourceEncId
     * @return
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            " and @smpAuthorizationService.isResourceMember(#resourceEncId) ")
    public List<SubresourceRO> getSubResourcesForResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                          @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId) {

        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        logAdminAccess("getSubResourcesForResource: " + resourceId);
        return uiSubresourceService.getSubResourcesForResource(resourceId);
    }

    @DeleteMapping(path = SUB_CONTEXT_PATH_EDIT_SUBRESOURCE_DELETE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            " and @smpAuthorizationService.isResourceMember(#resourceEncId) ")
    public SubresourceRO deleteSubresourceFromGroup(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                    @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                                    @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId) {
        logAdminAccess("deleteSubresourceFromGroup");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long subresourceId = SessionSecurityUtils.decryptEntityId(subresourceEncId);
        return uiSubresourceService.deleteSubresourceFromResource(subresourceId, resourceId);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_CREATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            " and @smpAuthorizationService.isResourceMember(#resourceEncId) ")
    public SubresourceRO createSubresource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                           @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                           @RequestBody SubresourceRO subresourceRO) {
        logAdminAccess("createSubresource");
        Long subresourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiSubresourceService.createSubresourceForResource(subresourceRO, subresourceId);
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}

