package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.SMLIntegrationResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.DomainService;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * DomainAdminResource provides admin services for managing the domains configured in SMP. The services defined in path
 * ResourceConstants.CONTEXT_PATH_INTERNAL should not be exposed to internet.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_DOMAIN)
public class DomainAdminResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainAdminResource.class);

    final UIDomainService uiDomainService;
    final DomainService domainService;

    public DomainAdminResource(UIDomainService uiDomainService, DomainService domainService) {
        this.uiDomainService = uiDomainService;
        this.domainService = domainService;

    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<DomainRO> geDomainList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = PARAM_QUERY_USER, required = false) String user) {

        LOG.info("Search for page: {}, page size: {}, user: {}", page, pageSize, user);
        return uiDomainService.getTableList(page, pageSize, orderBy, orderType, null);
    }

    @GetMapping(path = "/{user-enc-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public List<DomainRO> getAllDomainList(@PathVariable("user-enc-id") String userEncId) {
        logAdminAccess("getAllDomainList");
        return uiDomainService.getAllDomains();
    }

    /**
     * List of domains to be added or updated
     *
     * @param updateEntities
     */
    @PutMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public void updateDomainList(@RequestBody DomainRO[] updateEntities) {
        uiDomainService.updateDomainList(Arrays.asList(updateEntities));
    }

    /**
     * Validated if domains with provided IDs can be deleted and returns the result in DeleteEntityValidation.
     *
     * @param listOfDomainIds
     * @return
     */

    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    @PutMapping(value = "validate-delete", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public DeleteEntityValidation validateDeleteDomain(@RequestBody List<String> listOfDomainIds) {

        DeleteEntityValidation dres = new DeleteEntityValidation();
        dres.getListIds().addAll(listOfDomainIds);
        return uiDomainService.validateDeleteRequest(dres);
    }

    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PutMapping(value = "/{user-id}/sml-register/{domain-code}")
    public SMLIntegrationResult registerDomainAndParticipants(@PathVariable("user-id") String userId,
                                                              @PathVariable("domain-code") String domainCode
    ) {
        LOG.info("SML register domain code: {}, user user-id {}", domainCode, userId);
        SMLIntegrationResult result = new SMLIntegrationResult();
        try {
            DBDomain dbDomain = domainService.getDomain(domainCode);
            domainService.registerDomainAndParticipants(dbDomain);
            result.setSuccess(true);
        } catch (SMPRuntimeException e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }


    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PutMapping(value = "/{user-id}/sml-unregister/{domain-code}")
    public SMLIntegrationResult unregisterDomainAndParticipants(@PathVariable("user-id") String userId,
                                                                @PathVariable("domain-code") String domainCode) {
        LOG.info("SML unregister domain code: {}, user id {}", domainCode, userId);
        // try to open keystore
        SMLIntegrationResult result = new SMLIntegrationResult();
        try {
            DBDomain dbDomain = domainService.getDomain(domainCode);
            domainService.unregisterDomainAndParticipantsFromSml(dbDomain);
            result.setSuccess(true);
        } catch (SMPRuntimeException e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }

}
