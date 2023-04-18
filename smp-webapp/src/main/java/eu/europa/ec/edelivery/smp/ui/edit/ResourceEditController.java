package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
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
 * Purpose of the DomainResource is to provide search method to retrieve configured domains in SMP.
 * base path for the resource includes two variables user who is editing and domain for the group
 * /ui/edit/rest/[user-id]/domain/[domain-id]/group/[group-id]/resource
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
     * @param userEncId
     * @param groupEncId
     * @param forRole
     * @return
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or  @smpAuthorizationService.isAnyGroupResourceAdministrator(#groupEncId))")
    public ServiceResult<ResourceRO> getResourcesForGroup(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                          @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                          @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                                          @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
                                                          @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
                                                          @RequestParam(value = PARAM_NAME_TYPE, defaultValue = "", required = false) String forRole,
                                                          @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {
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

    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_UPDATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public ResourceRO updateResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                     @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                     @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                     @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                     @RequestBody ResourceRO resourceRO) {
        logAdminAccess("createResource");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiResourceService.updateResourceForGroup(resourceRO, resourceId, groupId, domainId);
    }


    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and" +
            " (@smpAuthorizationService.isGroupAdministrator(#groupEncId) or @smpAuthorizationService.isResourceAdministrator(#resourceEncId))")
    public ServiceResult<MemberRO> getGroupMemberList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                      @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                      @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                                      @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                                      @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
                                                      @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
                                                      @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {

        LOG.info("Search for group members with filter  [{}], paging: [{}/{}], user: {}", filter, page, pageSize, userEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        return uiResourceService.getResourceMembers(resourceId, page, pageSize, filter);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_PUT, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public MemberRO putGroupMember(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                   @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                   @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                   @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                   @RequestBody MemberRO memberRO) {

        LOG.info("add member to group");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);
        Long memberId = memberRO.getMemberId() == null ? null : SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiResourceService.addMemberToResource(resourceId, memberRO, memberId);
    }

    @DeleteMapping(value = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_DELETE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public MemberRO deleteDomainMember(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
            @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
            @PathVariable(PATH_PARAM_ENC_MEMBER_ID) String memberEncId
    ) {
        LOG.info("Delete member from group");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long memberId = SessionSecurityUtils.decryptEntityId(memberEncId);
        Long resourceId = SessionSecurityUtils.decryptEntityId(resourceEncId);

        // is user domain admin or system admin
        return uiResourceService.deleteMemberFromResource(resourceId, memberId);
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}

