/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.SMLIntegrationResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.DomainService;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

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
public class DomainAdminController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainAdminController.class);
    final UIDomainService uiDomainService;
    final DomainService domainService;

    public DomainAdminController(UIDomainService uiDomainService, DomainService domainService) {
        this.uiDomainService = uiDomainService;
        this.domainService = domainService;

    }

    @GetMapping(path = "/{user-id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public List<DomainRO> getAllDomainList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId) {
        logAdminAccess("getAllDomainList");
        return uiDomainService.getAllDomains();
    }

    @DeleteMapping(path = "/{user-id}/{domain-enc-id}/delete", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public DomainRO deleteDomain(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                 @PathVariable("domain-enc-id") String domainEncId) {
        logAdminAccess("deleteDomain:" + domainEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        LOG.info("Delete domain with id [{}]", domainId);
        DomainRO domainRO = uiDomainService.deleteDomain(domainId);
        domainRO.setDomainId(domainEncId);
        return domainRO;
    }

    @PutMapping(path = "/{user-id}/create", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public DomainRO createBasicDomainData(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                          @RequestBody DomainRO domainData) {
        logAdminAccess("createBasicDomainData" );

        uiDomainService.createDomainData(domainData);

        DomainRO domainRO = uiDomainService.getDomainDataByDomainCode(domainData.getDomainCode());
        domainRO.setStatus(EntityROStatus.NEW.getStatusNumber());
        return domainRO;
    }
    @PostMapping(path = "/{user-id}/{domain-enc-id}/update", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public DomainRO updateBasicDomainData(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                     @PathVariable("domain-enc-id") String domainEncId,
                                     @RequestBody DomainRO domainData) {
        logAdminAccess("updateBasicDomainData:" + domainEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        LOG.info("Update basic domain with id [{}]", domainId);
        uiDomainService.updateBasicDomainData(domainId, domainData);

        DomainRO domainRO = uiDomainService.getDomainData(domainId);
        domainRO.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        domainRO.setDomainId(domainEncId);
        return domainRO;
    }

    @PostMapping(path = "/{user-id}/{domain-enc-id}/update-resource-types", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public DomainRO updateResourceDefDomainList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                          @PathVariable("domain-enc-id") String domainEncId,
                                          @RequestBody List<String> resourceDefs) {
        logAdminAccess("updateResourceDefDomainList:" + domainEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        LOG.info("Update basic domain with id [{}]", domainId);
        uiDomainService.updateResourceDefDomainList(domainId, resourceDefs);
        DomainRO domainRO = uiDomainService.getDomainData(domainId);
        domainRO.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        domainRO.setDomainId(domainEncId);
        return domainRO;
    }

    @PostMapping(path = "/{user-id}/{domain-enc-id}/update-sml-integration-data", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isSystemAdministrator")
    public DomainRO updateSmlIntegrationData(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                @PathVariable("domain-enc-id") String domainEncId,
                                                @RequestBody DomainRO domainData) {
        logAdminAccess("updateSmlIntegrationData:" + domainEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        LOG.info("Update domain integration data for id [{}]", domainId);
        uiDomainService.updateDomainSmlIntegrationData(domainId, domainData);
        DomainRO domainRO = uiDomainService.getDomainData(domainId);
        domainRO.setStatus(EntityROStatus.UPDATED.getStatusNumber());
        domainRO.setDomainId(domainEncId);
        return domainRO;
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId) and @smpAuthorizationService.systemAdministrator")
    @PutMapping(value = "/{user-id}/sml-register/{domain-code}")
    public SMLIntegrationResult registerDomainAndParticipants(@PathVariable(PATH_PARAM_ENC_USER_ID) String userId,
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


    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId) and @smpAuthorizationService.systemAdministrator")
    @PutMapping(value = "/{user-id}/sml-unregister/{domain-code}")
    public SMLIntegrationResult unregisterDomainAndParticipants(@PathVariable(PATH_PARAM_ENC_USER_ID) String userId,
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
