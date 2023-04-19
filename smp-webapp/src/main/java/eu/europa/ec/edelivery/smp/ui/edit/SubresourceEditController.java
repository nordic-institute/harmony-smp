package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.SubresourceRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIResourceService;
import eu.europa.ec.edelivery.smp.services.ui.UISubresourceService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 *
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
    public SubresourceRO deleteResourceFromGroup(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                              @PathVariable(PATH_PARAM_ENC_RESOURCE_ID) String resourceEncId,
                                              @PathVariable(PATH_PARAM_ENC_SUBRESOURCE_ID) String subresourceEncId) {
        logAdminAccess("deleteResourceFromGroup");
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
        return uiSubresourceService.createResourceForGroup(subresourceRO, subresourceId);
    }
/*
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
    }*/

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}

