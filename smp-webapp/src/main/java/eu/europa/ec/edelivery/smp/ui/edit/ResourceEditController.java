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


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.filter.Filter;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIResourceService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the ResourceEditController is to provide edut methods to retrieve
 * update  resources in the DomiSMP.
 * base path for the resource includes two variables user who is editing and domain for the group
 * /ui/edit/rest/[user-id]/domain/[domain-id]/group/[group-id]/resource\[resource-id]
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_EDIT_RESOURCE)
public class ResourceEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceEditController.class);
    private final UIResourceService uiResourceService;

    public ResourceEditController(UIResourceService uiResourceService) {
        this.uiResourceService = uiResourceService;
    }

    /**
     * Return all Resources  for the group. If parameter forRole is
     * resource-admin it returns all Resources for the group where user is Resources admin;
     * resource-viewer it returns all Resources for the group where user is Resources viewer;
     * all-roles it returns all groups for the domain for user
     *
     * @param userEncId logged user identifier
     * @param domainEncId domain identifier
     * @param groupEncId group identifier
     * @param forRole
     * @return
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or @smpAuthorizationService.isAnyGroupResourceAdministrator(#groupEncId))")
    public ServiceResult<ResourceRO> getResourcesForGroup(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                          @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                          @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                                          @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
                                                          @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
                                                          @RequestParam(value = PARAM_NAME_TYPE, defaultValue = "", required = false) String forRole,
                                                          @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) @Filter String filter) {
        logAdminAccess("getResourcesForGroup and type: " + forRole);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long userId = SessionSecurityUtils.decryptEntityId(userEncId);

        if (StringUtils.isBlank(forRole)) {
            return uiResourceService.getGroupResources(groupId, page, pageSize, filter);
        }

        if (StringUtils.equalsIgnoreCase("group-admin", forRole)) {
            return uiResourceService.getGroupResources(groupId, page, pageSize, filter);
        }

        if (StringUtils.equalsIgnoreCase("resource-admin", forRole)) {
            return uiResourceService.getResourcesForUserAndGroup(userId, MembershipRoleType.ADMIN, groupId, page, pageSize, filter);
        }

        throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "ResourcesForGroups", "Unknown parameter type [" + forRole + "]!");
    }

    /**
     * Methods enables to group admin to delete resource from the group
     * @param userEncId logged user identifier
     * @param domainEncId domain identifier
     * @param groupEncId group identifier
     * @param resourceEncId resource identifier
     * @return the deleted ResourceRO
     */
    @DeleteMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_DELETE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public ResourceRO deleteResourceFromGroup(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                              @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                              @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                              @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId) {
        logAdminAccess("deleteResourceFromGroup");
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiResourceService.deleteResourceFromGroup(resourceId, groupId, domainId);
    }

    /**
     * Methods enables to group admin to create resource on the group
     * @param userEncId logged user identifier
     * @param domainEncId domain identifier
     * @param groupEncId group identifier
     * @return the created ResourceRO data
     */
    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_CREATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public ResourceRO createResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                     @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                     @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                     @RequestBody ResourceRO resourceRO) {
        logAdminAccess("createResource");
        Long userId = SessionSecurityUtils.decryptEntityId(userEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiResourceService.createResourceForGroup(resourceRO, groupId, domainId, userId);
    }

    /**
     * Method allows Group admin and Resource admin to change
     *   resource visibility and enable/disable review flow.
     * @param userEncId logged user identifier
     * @param domainEncId domain identifier
     * @param groupEncId group identifier
     * @param resourceEncId resource identifier
     * @param resourceRO updated resource data
     * @return the updated ResourceRO data
     */
    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_UPDATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or @smpAuthorizationService.isResourceAdministrator(#resourceEncId))")
    public ResourceRO updateResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                     @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                     @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                     @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                     @RequestBody ResourceRO resourceRO) {
        logAdminAccess("updateResource");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiResourceService.updateResourceForGroup(resourceRO, resourceId, groupId, domainId);
    }

    /**
     * Method returns list of members for the resource for group and resource
     * @param userEncId
     * @param domainEncId
     * @param groupEncId
     * @param resourceEncId
     * @param page
     * @param pageSize
     * @param filter
     * @return
     */

    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            " (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or @smpAuthorizationService.isResourceAdministrator(#resourceEncId))")
    public ServiceResult<MemberRO> getResourceMemberList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                      @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                      @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                                      @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                                      @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
                                                      @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
                                                      @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) @Filter String filter) {

        LOG.info("Search for group members with filter  [{}], paging: [{}/{}], user: {}", filter, page, pageSize, userEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiResourceService.getResourceMembers(resourceId, groupId, page, pageSize, filter);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_PUT, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            " (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or @smpAuthorizationService.isResourceAdministrator(#resourceEncId))")
    public MemberRO addUpdateMemberToResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                   @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                   @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                   @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                   @RequestBody MemberRO memberRO) {

        LOG.debug("Add/Update resource member");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long memberId = memberRO.getMemberId() == null ? null : SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiResourceService.addUpdateMemberToResource(resourceId, groupId, memberRO, memberId);
    }

    @DeleteMapping(value = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_DELETE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            " (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or @smpAuthorizationService.isResourceAdministrator(#resourceEncId))")
    public MemberRO deleteMemberFromResource(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
            @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
            @PathVariable(PATH_PARAM_ENC_MEMBER_ID) String memberEncId
    ) {
        LOG.info("Delete member from resource");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long memberId = SessionSecurityUtils.decryptEntityId(memberEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);

        // is user domain admin or system admin
        return uiResourceService.deleteMemberFromResource(resourceId, groupId, memberId);
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Group/Resource admin action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}

