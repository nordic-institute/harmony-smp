package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIGroupPublicService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainResource is to provide search method to retrieve configured domains in SMP.
 * base path for the resource includes two variables user who is editing and domain for the group
 * /ui/edit/rest/[user-id]/domain/[domain-id]/group
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_EDIT_GROUP)
public class GroupEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(GroupEditController.class);

    private final UIGroupPublicService uiGroupPublicService;

    public GroupEditController(UIGroupPublicService uiGroupPublicService) {
        this.uiGroupPublicService = uiGroupPublicService;
    }


    /**
     * Return all Groups for the domain. If parameter forRole is
     * group-admin it returns all groups for the domain where user is group admin;
     * group-viewer it returns all groups for the domain where user is group viewer;
     * all-roles it returns all groups for the domain for user
     *
     * @param userEncId
     * @param domainEncId
     * @param forRole
     * @return
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.isDomainAdministrator(#domainEncId) or @smpAuthorizationService.isAnyDomainGroupAdministrator(#domainEncId))")
    public List<GroupRO> getGroupsForDomain(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @RequestParam(value = PARAM_NAME_TYPE, defaultValue = "", required = false) String forRole) {
        logAdminAccess("getGroupsForDomain and type: " + forRole);
        Long userId = SessionSecurityUtils.decryptEntityId(userEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        if (StringUtils.isBlank(forRole)) {
            return uiGroupPublicService.getAllGroupsForDomain(domainId);
        }
        if (StringUtils.equalsIgnoreCase("group-admin", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndGroupRole(domainId, userId, MembershipRoleType.ADMIN);
        }

        if (StringUtils.equalsIgnoreCase("resource-admin", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndResourceRole(domainId, userId, MembershipRoleType.ADMIN);
        }

        if (StringUtils.equalsIgnoreCase("group-viewer", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndGroupRole(domainId, userId, MembershipRoleType.VIEWER);
        }
        if (StringUtils.equalsIgnoreCase("all-roles", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndGroupRole(domainId, userId, null);
        }
        throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "getGroupsForDomain", "Unknown parameter type [" + forRole + "]!");
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_GROUP_CREATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public GroupRO putGroupForDomain(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @RequestBody GroupRO group) {
        logAdminAccess("putGroupForDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiGroupPublicService.createGroupForDomain(domainId, group);
    }

    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_GROUP_UPDATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public GroupRO submitGroupForDomain(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                        @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                        @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                        @RequestBody GroupRO group) {
        logAdminAccess("updateGroupForDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiGroupPublicService.saveGroupForDomain(domainId, groupId, group);
    }

    @DeleteMapping(path = SUB_CONTEXT_PATH_EDIT_GROUP_DELETE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public GroupRO deleteGroupFromDomain(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                         @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                         @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId) {
        logAdminAccess("deleteGroupFromDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiGroupPublicService.deleteGroupFromDomain(domainId, groupId);
    }

    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and  @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public ServiceResult<MemberRO> getGroupMemberList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                      @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                      @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                                      @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
                                                      @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
                                                      @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {

        LOG.info("Search for group members with filter  [{}], paging: [{}/{}], user: {}", filter, page, pageSize, userEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiGroupPublicService.getGroupMembers(groupId, page, pageSize, filter);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_PUT, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public MemberRO putGroupMember(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                   @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                   @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
                                   @RequestBody MemberRO memberRO) {

        LOG.info("add member to group");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long memberId = memberRO.getMemberId() == null ? null : SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiGroupPublicService.addMemberToGroup(groupId, memberRO, memberId);
    }

    @DeleteMapping(value = SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_DELETE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public MemberRO deleteDomainMember(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @PathVariable(PATH_PARAM_ENC_GROUP_ID) String groupEncId,
            @PathVariable(PATH_PARAM_ENC_MEMBER_ID) String memberEncId
    ) {
        LOG.info("Delete member from group");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long memberId = SessionSecurityUtils.decryptEntityId(memberEncId);

        // is user domain admin or system admin
        return uiGroupPublicService.deleteMemberFromGroup(groupId, memberId);
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}
