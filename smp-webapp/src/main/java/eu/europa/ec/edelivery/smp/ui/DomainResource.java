package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.DomainService;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @PostConstruct
    protected void init() {

    }

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
        return uiDomainService.getTableList(page,pageSize, orderBy, orderType, null );
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.PUT)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public void updateDomainList(@RequestBody(required = true) DomainRO [] updateEntities ){
        LOG.info("GOT LIST OF DomainRO to UPDATE: " + updateEntities.length);
        uiDomainService.updateDomainList(Arrays.asList(updateEntities));
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(path = "validateDelete", method = RequestMethod.POST)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public DeleteEntityValidation validateDeleteUsers(@RequestBody List<Long> query) {

        DeleteEntityValidation dres = new DeleteEntityValidation();
        dres.getListIds().addAll(query);
        return uiDomainService.validateDeleteRequest(dres);
    }

    @PostMapping(value = "/{id}/smlregister/{domaincode}")
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public void registerDomain(@PathVariable("id") Long id,
                                               @PathVariable("domaincode") String domaincode
                                               ) {
        LOG.info("SML register domain code: {}, user id {}", domaincode, id);
        // try to open keystore
        DBDomain dbDomain =  domainService.getDomain(domaincode);
        domainService.registerDomainAndParticipants(dbDomain);
    }


    @PostMapping(value = "/{id}/smlunregister/{domaincode}")
    @PreAuthorize("@smpAuthorizationService.systemAdministrator || @smpAuthorizationService.isCurrentlyLoggedIn(#id)")
    public void unregisterDomainAndParticiants(@PathVariable("id") Long id,
                                               @PathVariable("domaincode") String domaincode
    ) {
        LOG.info("SML unregister domain code: {}, user id {}", domaincode, id);
        // try to open keystore
        DBDomain dbDomain =  domainService.getDomain(domaincode);
        domainService.unregisterDomainAndParticipantsFromSml(dbDomain);
    }
}
