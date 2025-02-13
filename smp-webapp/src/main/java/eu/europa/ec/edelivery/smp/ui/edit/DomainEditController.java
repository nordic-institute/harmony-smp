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
package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.filter.Filter;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainEditService;
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

    private final UIDomainEditService uiDomainEditService;


    public DomainEditController(UIDomainEditService uiDomainService) {
        this.uiDomainEditService = uiDomainService;

    }

    /**
     * Method returns all domains where user is domain administrator.
     *
     * @param userEncId encrypted user identifier
     * @return Domain list where user has role domain administrator
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId)")
    public List<DomainRO> getDomainsForUserType(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @RequestParam(value = PARAM_NAME_TYPE, defaultValue = "domain-admin", required = false) String forRole) {
        logAdminAccess("getDomainsForUserType [" + forRole + "]");
        Long userId = SessionSecurityUtils.decryptEntityId(userEncId);

        if (StringUtils.equals(forRole, "group-admin")) {
            return uiDomainEditService.getAllDomainsForGroupAdminUser(userId);
        }
        if (StringUtils.equals(forRole, "resource-admin")) {
            return uiDomainEditService.getAllDomainsForResourceAdminUser(userId);
        }
        if (StringUtils.isBlank(forRole) || StringUtils.equals(forRole, "domain-admin")) {
            return uiDomainEditService.getAllDomainsForDomainAdminUser(userId);
        }
        throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "GetDomains", "Unknown parameter type [" + forRole + "]!");
    }


    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            "(@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public ServiceResult<MemberRO> getDomainMemberList(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_FILTER, defaultValue = "", required = false) @Filter String filter) {
        logAdminAccess("getDomainMemberList");
        LOG.info("Search for domain members with filter  [{}], paging: [{}/{}], user: {}", filter, page, pageSize, userEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return uiDomainEditService.getDomainMembers(domainId, page, pageSize, filter);
    }

    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_PUT, produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public MemberRO putDomainMember(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @RequestBody MemberRO memberRO) {

        logAdminAccess("putDomainMember");
        LOG.info("add or update domain member");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long memberId = memberRO.getMemberId() == null ? null : SessionSecurityUtils.decryptEntityId(memberRO.getMemberId());
        if (memberRO.getRoleType() == null) {
            memberRO.setRoleType(MembershipRoleType.VIEWER);
        }
        // is user domain admin or system admin
        return uiDomainEditService.addMemberToDomain(domainId, memberRO, memberId);
    }

    @DeleteMapping(value = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_DELETE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) " +
            "and (@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId))")
    public MemberRO deleteDomainMember(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
            @PathVariable(PATH_PARAM_ENC_MEMBER_ID) String memberEncId
    ) {
        logAdminAccess("deleteDomainMember");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long memberId = SessionSecurityUtils.decryptEntityId(memberEncId);

        // is user domain admin or system admin
        return uiDomainEditService.deleteMemberFromDomain(domainId, memberId);
    }


    @GetMapping(value = SUB_CONTEXT_PATH_EDIT_DOMAIN_RESOURCE_DEF)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            "(@smpAuthorizationService.systemAdministrator or @smpAuthorizationService.isDomainAdministrator(#domainEncId) " +
            "or @smpAuthorizationService.isAnyDomainGroupAdministrator(#domainEncId)" +
            "or @smpAuthorizationService.isAnyResourceAdministrator)")
    public List<ResourceDefinitionRO> getDomainResourceDefinitions(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId
    ) {
        logAdminAccess("DomainResourceDefinitions");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);

        // is user domain admin or system admin
        return uiDomainEditService.getResourceDefDomainList(domainId);
    }


    /**
     * Method returns domain properties with access rights for domain administrators
     * and group administrators to be able to configure new resources according to the domain settings.
     *
     * @param userEncId   encrypted user identifier
     * @param domainEncId the  encrypted domain identifier
     * @return list of domain properties
     */
    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_PROPERTIES, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and " +
            "(@smpAuthorizationService.isDomainAdministrator(#domainEncId) " +
            " or @smpAuthorizationService.isAnyDomainGroupAdministrator(#domainEncId))")
    public List<DomainPropertyRO> getEditDomainPropertyList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId) {
        logAdminAccess("getDomainPropertyList:" + domainEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        LOG.debug("Get domain properties for domain with id [{}]", domainId);
        return uiDomainEditService.getDomainEditProperties(domainId);
    }

    /**
     * Method validates authorization for the users and updates all given properties.
     * As the result it returns ALL (updated) domain properties.
     *
     * @param userEncId   encrypted user identifier
     * @param domainEncId the  encrypted domain identifier
     * @return list of domain properties to be updated.
     */
    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_PROPERTIES, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public List<DomainPropertyRO> updateEditDomainPropertyList(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                               @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                               @RequestBody List<DomainPropertyRO> domainProperties) {
        logAdminAccess("updateEditDomainPropertyList:" + domainEncId);
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        LOG.debug("Update domain properties for domain with id [{}]", domainId);
        return uiDomainEditService.updateDomainEditProperties(domainId, domainProperties);
    }


    @PostMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_PROPERTIES_VALIDATE,
            consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public PropertyValidationRO validateProperty(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                 @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                 @RequestBody PropertyRO propertyRO) {
        LOG.info("Validate Domain property: [{}]", propertyRO);

        if (propertyRO == null || StringUtils.isBlank(propertyRO.getProperty())) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "ValidateProperty", "Property name is empty!");
        }
        return uiDomainEditService.validateDomainProperty(propertyRO);
    }


    protected void logAdminAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Admin Domain action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}
