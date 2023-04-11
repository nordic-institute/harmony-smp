package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.DomainPublicRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainPublicService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainResource is to provide search method to retrieve configured domains in SMP.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_DOMAIN)
public class DomainResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainResource.class);

    private final UIDomainPublicService uiDomainService;


    public DomainResource(UIDomainPublicService uiDomainService) {
        this.uiDomainService = uiDomainService;

    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<DomainPublicRO> getDomainList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = PARAM_QUERY_USER, required = false) String user) {

        LOG.info("Search for page: {}, page size: {}, user: {}", page, pageSize, user);
        ServiceResult<DomainPublicRO> result = uiDomainService.getTableList(page, pageSize, orderBy, orderType, null);
        return result;
    }


    @GetMapping(path = "/{user-enc-id}/{domain-enc-id}/members", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId)")
    public ServiceResult<MemberRO> getDomainMemberList(
            @PathVariable("user-enc-id") String userEncId,
            @PathVariable("domain-enc-id") String domainEncId,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) String filter) {

        LOG.info("Search for domain members with filter  [{}], paging: [{}/{}], user: {}",filter,  page, pageSize, userEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiDomainService.getDomainMembers(domainId, page, pageSize,  filter);
    }

    @PutMapping(path = "/{user-enc-id}/{domain-enc-id}/member", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public MemberRO  putDomainMember(
            @PathVariable("user-enc-id") String userEncId,
            @PathVariable("domain-enc-id") String domainEncId,
            @RequestBody MemberRO memberRO) {

        LOG.info("add member to domain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long memberId = memberRO.getMemberId() == null?null: SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiDomainService.addMemberToDomain(domainId, memberRO, memberId);
    }

    @DeleteMapping(value = "/{user-enc-id}/{domain-enc-id}/member/{member-enc-id}/delete")
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public MemberRO  deleteDomainMember(
            @PathVariable("user-enc-id") String userEncId,
            @PathVariable("domain-enc-id") String domainEncId,
            @PathVariable("member-enc-id") String memberEncId
            ) {
        LOG.info("Delete member from domain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long memberId= SessionSecurityUtils.decryptEntityId(memberEncId);

        // is user domain admin or system admin
        return uiDomainService.deleteMemberFromDomain(domainId, memberId);
    }
}
