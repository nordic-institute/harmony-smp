package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceDefinitionRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainPublicService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainEditController is to provide domain edit methods.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_EDIT_DOMAIN)
public class DomainEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainEditController.class);

    private final UIDomainPublicService uiDomainService;


    public DomainEditController(UIDomainPublicService uiDomainService) {
        this.uiDomainService = uiDomainService;

    }

    /**
     * Method returns all domains where user is domain administrator
     * @param userEncId encrypted user identifier
     * @return Domain list where user has role domain administrator
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isAnyGroupAdministrator")
    public List<DomainRO> getDomainsForUserType(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @RequestParam(value = PARAM_NAME_TYPE, defaultValue = "domain-admin", required = false) String forRole) {
        logAdminAccess("getDomainsForUserType ["+forRole+"]");
        Long userId = SessionSecurityUtils.decryptEntityId(userEncId);

        if (StringUtils.equals(forRole, "group-admin")) {
            return uiDomainService.getAllDomainsForGroupAdminUser(userId);
        }
        if (StringUtils.equals(forRole, "resource-admin")) {
            return uiDomainService.getAllDomainsForResourceAdminUser(userId);
        }
        if (StringUtils.isBlank(forRole) || StringUtils.equals(forRole, "domain-admin")) {
            return uiDomainService.getAllDomainsForDomainAdminUser(userId);
        }
        throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "GetDomains", "Unknown parameter type ["+forRole+"]!");
    }


    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public ServiceResult<MemberRO> getDomainMemberList(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {
        logAdminAccess("getDomainMemberList");
        LOG.info("Search for domain members with filter  [{}], paging: [{}/{}], user: {}",filter,  page, pageSize, userEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiDomainService.getDomainMembers(domainId, page, pageSize,  filter);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_PUT, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public MemberRO  putDomainMember(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @RequestBody MemberRO memberRO) {

        logAdminAccess("putDomainMember");
        LOG.info("add or update domain member");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long memberId = memberRO.getMemberId() == null?null: SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiDomainService.addMemberToDomain(domainId, memberRO, memberId);
    }

    @DeleteMapping(value = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_DELETE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public MemberRO  deleteDomainMember(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @PathVariable(PATH_PARAM_ENC_MEMBER_ID) String memberEncId
            ) {
        logAdminAccess("deleteDomainMember");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long memberId= SessionSecurityUtils.decryptEntityId(memberEncId);

        // is user domain admin or system admin
        return uiDomainService.deleteMemberFromDomain(domainId, memberId);
    }


    @GetMapping(value = SUB_CONTEXT_PATH_EDIT_DOMAIN_RESOURCE_DEF)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            "(@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId) or  @smpAuthorizationService.isAnyDomainGroupAdministrator(#domainEncId))")
    public List<ResourceDefinitionRO>  getDomainResourceDefinitions(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId
    ) {
        logAdminAccess("DomainResourceDefinitions");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);

        // is user domain admin or system admin
        return uiDomainService.getResourceDefDomainList(domainId);
    }



    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}
