package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.SMLIntegrationResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.DomainService;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/domain")
public class DomainResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainResource.class);

    @Autowired
    private UIDomainService uiDomainService;

    @Autowired
    private DomainService domainService;


    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ServiceResult<DomainRO> geDomainList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "user", required = false) String user
    ) {
        return uiDomainService.getTableList(page, pageSize, orderBy, orderType, null);
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.PUT)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public void updateDomainList(@RequestBody(required = true) DomainRO[] updateEntities) {
        LOG.info("GOT LIST OF DomainRO to UPDATE: " + updateEntities.length);
        uiDomainService.updateDomainList(Arrays.asList(updateEntities));
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(path = "validateDelete", method = RequestMethod.POST)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public DeleteEntityValidation validateDeleteDomain(@RequestBody List<Long> query) {

        DeleteEntityValidation dres = new DeleteEntityValidation();
        dres.getListIds().addAll(query);
        return uiDomainService.validateDeleteRequest(dres);
    }

    @PostMapping(value = "/{id}/smlregister/{domaincode}")
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public SMLIntegrationResult registerDomainAndParticipants(@PathVariable("id") Long id,
                                                              @PathVariable("domaincode") String domaincode
    ) {
        LOG.info("SML register domain code: {}, user id {}", domaincode, id);
        SMLIntegrationResult result = new SMLIntegrationResult();
        try {
            DBDomain dbDomain = domainService.getDomain(domaincode);
            domainService.registerDomainAndParticipants(dbDomain);
            result.setSuccess(true);
        } catch (SMPRuntimeException e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }


    @PostMapping(value = "/{id}/smlunregister/{domaincode}")
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public SMLIntegrationResult unregisterDomainAndParticipants(@PathVariable("id") Long id,
                                                                @PathVariable("domaincode") String domaincode
    ) {
        LOG.info("SML unregister domain code: {}, user id {}", domaincode, id);
        // try to open keystore
        SMLIntegrationResult result = new SMLIntegrationResult();
        try {
            DBDomain dbDomain = domainService.getDomain(domaincode);
            domainService.unregisterDomainAndParticipantsFromSml(dbDomain);
            result.setSuccess(true);
        } catch (SMPRuntimeException e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }
}
