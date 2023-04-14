package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIGroupPublicService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_GROUP;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.PARAM_ROLE;

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
     * Return all Groups for the domain
     * @param userEncId
     * @param domainEncId
     * @return
     */
    @GetMapping(path = "/{user-enc-id}/domain/{domain-enc-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public List<GroupRO> getAllGroupsForDomain(@PathVariable("user-enc-id") String userEncId,
                                               @PathVariable("domain-enc-id") String domainEncId) {
        logAdminAccess("getAllGroupsForDomain");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiGroupPublicService.getAllGroupsForDomain(domainId);
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
        return uiGroupPublicService.saveGroupForDomain(domainId,groupId, group);
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


    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}
