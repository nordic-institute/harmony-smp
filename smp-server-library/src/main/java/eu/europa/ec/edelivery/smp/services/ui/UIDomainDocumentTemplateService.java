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
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDocumentTemplateDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainResourceDefDao;
import eu.europa.ec.edelivery.smp.data.enums.DocumentLevelType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDocumentTemplate;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
import eu.europa.ec.edelivery.smp.data.ui.DomainDocumentTemplateRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.2
 */

@Service
public class UIDomainDocumentTemplateService {
    private static final Logger LOG = LoggerFactory.getLogger(UIDomainDocumentTemplateService.class);

    protected final DomainDocumentTemplateDao domainDocumentTemplateDao;
    protected final DomainDao domainDao;
    protected final ConversionService conversionService;
    protected final DomainResourceDefDao domainResourceDefDao;
    protected final UIDocumentService uiDocumentService;


    public UIDomainDocumentTemplateService(DomainDocumentTemplateDao domainDocumentTemplateDao,
                                           DomainDao domainDao,
                                           ConversionService conversionService,
                                           DomainResourceDefDao domainResourceDefDao,
                                           UIDocumentService uiDocumentService) {
        this.domainDocumentTemplateDao = domainDocumentTemplateDao;
        this.domainDao = domainDao;
        this.conversionService = conversionService;
        this.domainResourceDefDao = domainResourceDefDao;
        this.uiDocumentService = uiDocumentService;
    }

    @Transactional
    public List<DomainDocumentTemplateRO> getAllDomainsDocumentTemplatesForDomain(long domainId) {
        DBDomain domain = domainDao.find(domainId);
        if (domain == null) {
            LOG.warn("Domain with id [{}] does not exist", domainId);
            throw new SMPRuntimeException(ErrorMessageType.DOMAIN_NOT_EXISTS_ID);
        }
        List<DBDomainDocumentTemplate> templates = domainDocumentTemplateDao.getAllDomainsDocumentTemplatesForDomain(domain);
        return templates.stream().map(tmpl -> conversionService.convert(tmpl, DomainDocumentTemplateRO.class))
                .toList();
    }


    @Transactional
    public DomainDocumentTemplateRO createTemplateForDomainAndTemplateData(long domainId, DomainDocumentTemplateRO templateRO) {
        validateDomainDocumentTemplate(templateRO);
        DBDomain domain = domainDao.find(domainId);
        String resourceDefIdentifier = templateRO.getResourceDefIdentifier();
        DBSubresourceDef dbSubresourceDef = null;
        if (domain == null) {
            LOG.warn("Domain with id [{}] does not exist", domainId);
            throw new SMPRuntimeException(ErrorMessageType.DOMAIN_NOT_EXISTS_ID);
        }
        DBDomainResourceDef domainResourceDef = domainResourceDefDao.getResourceDefConfigurationForDomainAndResourceDefIdentifier(domain, resourceDefIdentifier)
                .orElseThrow(() -> {
                    LOG.warn("Resource definition with id [{}] does not exist for domain with id [{}]", resourceDefIdentifier, domainId);
                    return new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCEDEF_LOOKUP_FOR_DOMAIN_BY_IDENTIFIER)
                            .addParam(ErrorMessageArgument.IDENTIFIER, resourceDefIdentifier)
                            .addParam(ErrorMessageArgument.DOMAIN_CODE, domain.getDomainCode());
                });

        if (templateRO.getDocumentLevel() == DocumentLevelType.SUBRESOURCE) {
            String subresourceDefIdentifier = templateRO.getSubresourceDefIdentifier();
            dbSubresourceDef = domainResourceDef.getResourceDef().getSubresources().stream().filter(
                    srd -> Strings.CI.equals(srd.getIdentifier(), subresourceDefIdentifier)
            ).findFirst().orElseThrow(() -> {
                        LOG.warn("Subresource definition with id [{}] does not exist for resource [{}] and domain, domain with id [{}]", resourceDefIdentifier, subresourceDefIdentifier, domainId);
                        return new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
                    }
            );
        }

        if (!domainDocumentTemplateDao.getDomainDocumentTemplate(domainResourceDef, dbSubresourceDef, templateRO.getDocumentLevel()).isEmpty()) {
            LOG.warn("Resource definition with id [{}] already has document template for domain with id [{}]", resourceDefIdentifier, domainId);
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_DOC_TEMPLATE_ALREADY_EXISTS_FOR_RESOURCEDEF_AND_DOMAIN)
                    .addParam(ErrorMessageArgument.IDENTIFIER, resourceDefIdentifier)
                    .addParam(ErrorMessageArgument.DOMAIN_CODE, domain.getDomainCode());
        }

        DBDomainDocumentTemplate template = createDBDomainDocumentTemplate(templateRO, domainResourceDef, dbSubresourceDef);
        DBDomainDocumentTemplate response = domainDocumentTemplateDao.merge(template);
        DocumentRO payload = uiDocumentService.generateTemplateDocument(domainResourceDef, dbSubresourceDef);
        uiDocumentService.saveDocumentForTemplate(response.getId(), payload);
        return conversionService.convert(response, DomainDocumentTemplateRO.class);
    }

    /**
     * Creates the DBDomainDocumentTemplate entity from the DomainDocumentTemplateRO
     *
     * @param templateRO        the DomainDocumentTemplateRO
     * @param domainResourceDef the DBDomainResourceDef
     * @param dbSubresourceDef  the DBSubresourceDef, can be null
     * @return the DBDomainDocumentTemplate entity
     */
    private static DBDomainDocumentTemplate createDBDomainDocumentTemplate(DomainDocumentTemplateRO templateRO, DBDomainResourceDef domainResourceDef, DBSubresourceDef dbSubresourceDef) {
        DBDomainDocumentTemplate template = new DBDomainDocumentTemplate();
        template.setDomainResourceDef(domainResourceDef);
        template.setDocumentLevelType(templateRO.getDocumentLevel());
        DBResourceDef resourceDef = domainResourceDef.getResourceDef();
        template.setSubresourceDef(dbSubresourceDef);
        DBDocument document = new DBDocument();
        document.setMimeType(resourceDef.getMimeType());
        document.setName(resourceDef.getName());
        template.setDocument(document);
        return template;
    }

    @Transactional
    public DocumentRO updateTemplateForDomainVersion(long domainId, long templateId, DocumentRO payload) {
        DBDomainDocumentTemplate template = getDomainDocumentTemplate(domainId, templateId);
        return uiDocumentService.saveDocumentForTemplate(template.getId(), payload);
    }


    @Transactional
    public DocumentRO publishTemplateForDomainVersion(long domainId, long templateId, int version) {
        LOG.info("Publish Document For Domain [{}], template [{}], version [{}]", domainId, templateId, version);
        DBDomainDocumentTemplate template = getDomainDocumentTemplate(domainId, templateId);

        return uiDocumentService.publishDocumentVersion(template.getDocument(), version, false, new ArrayList<>());
    }

    @Transactional
    public DocumentRO deleteTemplateForDomainVersion(long domainId, long templateId, int version) {
        LOG.info("Delete Document version For Domain [{}], template [{}], version [{}]", domainId, templateId, version);
        DBDomainDocumentTemplate template = getDomainDocumentTemplate(domainId, templateId);

        return uiDocumentService.deleteDocumentVersion(template.getDocument(), version, new ArrayList<>());
    }

    private DBDomainDocumentTemplate getDomainDocumentTemplate(long domainId, long templateId) {
        DBDomain domain = domainDao.find(domainId);
        if (domain == null) {
            LOG.warn("Domain with id [{}] does not exist", domainId);
            throw new SMPRuntimeException(ErrorMessageType.DOMAIN_NOT_EXISTS_ID);
        }

        DBDomainDocumentTemplate template = domainDocumentTemplateDao.find(templateId);
        if (template == null || !template.getDomainResourceDef().getDomain().getId().equals(domainId)) {
            LOG.warn("Domain document template with id [{}] does not exist for domain with id [{}]", templateId, domainId);
            throw new SMPRuntimeException(ErrorMessageType.DOMAIN_DOC_TEMPLATE_NOT_EXISTS_ID);
        }
        return template;
    }


    @Transactional
    public DomainDocumentTemplateRO deleteTemplateForDomain(long domainId, long templateId) {
        DBDomainDocumentTemplate template = getDomainDocumentTemplate(domainId, templateId);
        domainDocumentTemplateDao.remove(template);
        return conversionService.convert(template, DomainDocumentTemplateRO.class);
    }

    @Transactional
    public DocumentRO getDocumentTemplate(long domainId, long templateId, int version) {
        DBDomainDocumentTemplate template = getDomainDocumentTemplate(domainId, templateId);
        DBDocument doc = template.getDocument();
        return uiDocumentService.convertWithVersion(doc, version, new ArrayList<>());
    }

    private void validateDomainDocumentTemplate(DomainDocumentTemplateRO template) {
        if (template == null) {
            LOG.error("Invalid DomainDocumentTemplateRO: Domain document template is null");
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
        }
        if (StringUtils.isBlank(template.getDomainCode())) {
            LOG.error("Invalid DomainDocumentTemplateRO: Domain code is blank");
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
        }
        if (StringUtils.isBlank(template.getResourceDefIdentifier())) {
            LOG.error("Invalid DomainDocumentTemplateRO: Resource definition identifier is blank");
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
        }
        if (template.getDocumentLevel() == null) {
            LOG.error("Invalid DomainDocumentTemplateRO: Document level is null");
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
        }
        if (template.getDocumentLevel() == DocumentLevelType.SUBRESOURCE && StringUtils.isBlank(template.getSubresourceDefIdentifier())) {
            LOG.error("Invalid DomainDocumentTemplateRO: Subresource definition identifier is blank");
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_GENERIC);
        }
        if (template.getDocumentLevel() == DocumentLevelType.RESOURCE && StringUtils.isNotBlank(template.getSubresourceDefIdentifier())) {
            LOG.warn("Invalid DomainDocumentTemplateRO: Subresource definition identifier is set for resource level document");
        }
    }
}
