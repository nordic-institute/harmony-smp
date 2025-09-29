/*-
 * #START_LICENSE#
 * smp-server-library
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

import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
import eu.europa.ec.edelivery.smp.data.ui.DomainDocumentTemplateRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainDocumentTemplateService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;


/**
 * Purpose of the DomainDocTmplEditController is to provide methods for managing the document templates on the domain.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@RestController
@RequestMapping(path = CONTEXT_PATH_EDIT_DOMAIN_DOC_TEMPLATE)
public class DomainDocTemplateEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainDocTemplateEditController.class);

    protected final UIDomainDocumentTemplateService domainDocTemplateEditService;

    public DomainDocTemplateEditController(UIDomainDocumentTemplateService domainDocTemplateEditService) {
        this.domainDocTemplateEditService = domainDocTemplateEditService;
    }

    /**
     * Method returns all domains where user is domain administrator.
     *
     * @param userEncId encrypted user identifier
     * @return Domain list where user has role domain administrator
     */
    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public List<DomainDocumentTemplateRO> getDomainsDocTemplates(
            @PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
            @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId
    ) {
        logEditAccess("GetDomainsDocTemplates");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return domainDocTemplateEditService.getAllDomainsDocumentTemplatesForDomain(domainId);
    }

    /**
     * Method creates new domain document template for the domain document type.
     *
     * @param userEncId                encrypted user identifier
     * @param domainEncId              encrypted domain identifier
     * @param domainDocumentTemplateRO domain document template data
     * @return created domain document template
     */
    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_DOC_TEMPLATE_CREATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public DomainDocumentTemplateRO createDomainTemplateResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                                 @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                                 @RequestBody DomainDocumentTemplateRO domainDocumentTemplateRO) {
        logEditAccess("Create domain document template");
        if (domainDocumentTemplateRO == null || StringUtils.isBlank(domainDocumentTemplateRO.getDomainCode())) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
        }
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        return domainDocTemplateEditService.createTemplateForDomainAndTemplateData(domainId, domainDocumentTemplateRO);
    }

    /**
     * Method updates domain document template for the domain document type.
     *
     * @param userEncId     encrypted user identifier
     * @param domainEncId   encrypted domain identifier
     * @param templateEncId encrypted template identifier
     * @param documentRO    document data
     * @return updated domain document template
     */
    @PutMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_DOC_TEMPLATE_UPDATE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public DocumentRO updateDomainTemplateDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                   @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                   @PathVariable(PATH_PARAM_ENC_TEMPLATE_ID) String templateEncId,
                                                   @RequestBody DocumentRO documentRO) {
        logEditAccess("update domain document template");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long templateId = SessionSecurityUtils.decryptEntityId(templateEncId);
        return domainDocTemplateEditService.updateTemplateForDomain(domainId, templateId, documentRO);
    }

    /**
     * Method updates domain document template for the domain document type.
     *
     * @param userEncId     encrypted user identifier
     * @param domainEncId   encrypted domain identifier
     * @param templateEncId encrypted template identifier
     * @param documentRO    document data
     * @return updated domain document template
     */
    @PostMapping(path = ResourceConstants.SUB_CONTEXT_PATH_EDIT_DOMAIN_DOC_TEMPLATE_PUBLISH, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public DocumentRO publishDomainTemplateDocument(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                   @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                   @PathVariable(PATH_PARAM_ENC_TEMPLATE_ID) String templateEncId,
                                                   @RequestBody DocumentRO documentRO) {
        logEditAccess("update domain document template");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long templateId = SessionSecurityUtils.decryptEntityId(templateEncId);
        return domainDocTemplateEditService.publishTemplateForDomain(domainId, templateId, documentRO.getPayloadVersion());
    }

    @GetMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_DOC_TEMPLATE_GET, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public DocumentRO getDocumentForTemplate(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                             @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                             @PathVariable(PATH_PARAM_ENC_TEMPLATE_ID) String templateEncId,
                                             @RequestParam(value = PARAM_NAME_VERSION, defaultValue = "-1") int version) {
        logEditAccess("GetDocumentForTemplate");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long templateId = SessionSecurityUtils.decryptEntityId(templateEncId);
        return domainDocTemplateEditService.getDocumentTemplate(domainId, templateId, version);
    }

    /**
     * This method removes a domain document template associated with a specific domain document type.
     *
     * @param userEncId     encrypted user identifier
     * @param domainEncId   encrypted domain identifier
     * @param templateEncId encrypted template identifier
     * @return deleted domain document template
     */
    @DeleteMapping(path = SUB_CONTEXT_PATH_EDIT_DOMAIN_DOC_TEMPLATE_DELETE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userEncId) and @smpAuthorizationService.isDomainAdministrator(#domainEncId)")
    public DomainDocumentTemplateRO deleteDomainTemplateResource(@PathVariable(PATH_PARAM_ENC_USER_ID) String userEncId,
                                                                 @PathVariable(PATH_PARAM_ENC_DOMAIN_ID) String domainEncId,
                                                                 @PathVariable(PATH_PARAM_ENC_TEMPLATE_ID) String templateEncId) {
        logEditAccess("Delete domain document template");
        Long domainId = SessionSecurityUtils.decryptEntityId(domainEncId);
        Long templateId = SessionSecurityUtils.decryptEntityId(templateEncId);
        return domainDocTemplateEditService.deleteTemplateForDomain(domainId, templateId);
    }

    protected void logEditAccess(String action) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Domain template action [{}] by user [{}], ", action, SessionSecurityUtils.getSessionUserDetails());
    }
}