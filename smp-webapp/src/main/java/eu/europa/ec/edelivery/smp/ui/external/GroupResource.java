package eu.europa.ec.edelivery.smp.ui.external;


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
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_GROUP)
public class GroupResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(GroupResource.class);

    private final UIGroupPublicService uiGroupPublicService;

    public GroupResource(UIGroupPublicService uiGroupPublicService) {
        this.uiGroupPublicService = uiGroupPublicService;
    }


    /**
     * Return all Groups for the domain. If parameter forRole is
     * group-admin it returns all groups for the domain where user it group admin;
     * group-viewer it returns all groups for the domain where user it group viewer;
     * user it returns all groups for the domain for user
     *
     * @param userEncId
     * @param domainEncId
     * @param forRole
     * @return
     */
    @GetMapping(path = "/{user-enc-id}/domain/{domain-enc-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.isDomainAdministrator(#domainEncId) or @smpAuthorizationService.isAnyDomainGroupAdministrator(#domainEncId))")
    public List<GroupRO> getGroupsForDomain(@PathVariable("user-enc-id") String userEncId,
                                               @PathVariable("domain-enc-id") String domainEncId,
                                               @RequestParam(value = PARAM_NAME_TYPE, defaultValue = "", required = false) String forRole) {
        logAdminAccess("getGroupsForDomain and type: " +forRole );
        Long userId = SessionSecurityUtils.decryptEntityId(userEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        if (StringUtils.isBlank(forRole)) {
            return uiGroupPublicService.getAllGroupsForDomain(domainId);
        }
        if (StringUtils.equalsIgnoreCase("group-admin", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndRole(domainId, userId, MembershipRoleType.ADMIN);
        }
        if (StringUtils.equalsIgnoreCase("group-viewer", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndRole(domainId, userId, MembershipRoleType.VIEWER);
        }
        if (StringUtils.equalsIgnoreCase("all-roles", forRole)) {
            return uiGroupPublicService.getAllGroupsForDomainAndUserAndRole(domainId, userId, null);
        }
        throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "getGroupsForDomain", "Unknown parameter type ["+forRole+"]!");
    }

    @PutMapping(path = "/{user-enc-id}/domain/{domain-enc-id}/create", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public GroupRO putGroupForDomain(@PathVariable("user-enc-id") String userEncId,
                                     @PathVariable("domain-enc-id") String domainEncId,
                                     @RequestBody GroupRO group) {
        logAdminAccess("putGroupForDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiGroupPublicService.createGroupForDomain(domainId, group);
    }

    @PostMapping(path = "/{user-enc-id}/{group-enc-id}/domain/{domain-enc-id}/update", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public GroupRO submitGroupForDomain(@PathVariable("user-enc-id") String userEncId,
                                        @PathVariable("domain-enc-id") String domainEncId,
                                        @PathVariable("group-enc-id") String groupEncId,
                                        @RequestBody GroupRO group) {
        logAdminAccess("updateGroupForDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiGroupPublicService.saveGroupForDomain(domainId, groupId, group);
    }

    @DeleteMapping(path = "/{user-enc-id}/{group-enc-id}/domain/{domain-enc-id}/delete", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public GroupRO deleteGroupFromDomain(@PathVariable("user-enc-id") String userEncId,
                                         @PathVariable("domain-enc-id") String domainEncId,
                                         @PathVariable("group-enc-id") String groupEncId) {
        logAdminAccess("deleteGroupFromDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiGroupPublicService.deleteGroupFromDomain(domainId, groupId);
    }

    @GetMapping(path = "/{user-enc-id}/{group-enc-id}/members", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and  @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public ServiceResult<MemberRO> getGroupMemberList(
            @PathVariable("user-enc-id") String userEncId,
            @PathVariable("group-enc-id") String groupEncId,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {

        LOG.info("Search for group members with filter  [{}], paging: [{}/{}], user: {}",filter,  page, pageSize, userEncId);
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        return uiGroupPublicService.getGroupMembers(groupId, page, pageSize,  filter);
    }

    @PutMapping(path = "/{user-enc-id}/{group-enc-id}/member", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public MemberRO  putGroupMember(
            @PathVariable("user-enc-id") String userEncId,
            @PathVariable("group-enc-id") String groupEncId,
            @RequestBody MemberRO memberRO) {

        LOG.info("add member to group");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long memberId = memberRO.getMemberId() == null?null: SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiGroupPublicService.addMemberToGroup(groupId, memberRO, memberId);
    }

    @DeleteMapping(value = "/{user-enc-id}/{group-enc-id}/member/{member-enc-id}/delete")
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isGroupAdministrator(#groupEncId)")
    public MemberRO  deleteDomainMember(
            @PathVariable("user-enc-id") String userEncId,
            @PathVariable("group-enc-id") String groupEncId,
            @PathVariable("member-enc-id") String memberEncId
    ) {
        LOG.info("Delete member from group");
        Long groupId = SessionSecurityUtils.decryptEntityId(groupEncId);
        Long memberId= SessionSecurityUtils.decryptEntityId(memberEncId);

        // is user domain admin or system admin
        return uiGroupPublicService.deleteMemberFromGroup(groupId, memberId);
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}
