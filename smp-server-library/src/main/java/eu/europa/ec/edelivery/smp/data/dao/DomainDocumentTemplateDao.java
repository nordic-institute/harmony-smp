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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.DocumentLevelType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDocumentTemplate;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.PARAM_DOMAIN_ID;
import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.QUERY_DOMAIN_DOC_TEMPLATES_BY_DOMAIN;

/**
 * The purpose of the DomainDocumentTemplateDao is to manage the Domain document templates  content in the database
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Repository
public class DomainDocumentTemplateDao extends BaseDao<DBDomainDocumentTemplate> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DomainDocumentTemplateDao.class);
    final DocumentDao documentDao;

    public DomainDocumentTemplateDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }


    /**
     * Returns all domain document templates for domain
     *
     * @param domain the DBDomain
     * @return the List of records for DBDomainDocumentTemplate
     */
    public List<DBDomainDocumentTemplate> getAllDomainsDocumentTemplatesForDomain(DBDomain domain) {
        LOG.debug("Get all domain document templates for domain code: [{}]", domain.getDomainCode());
        TypedQuery<DBDomainDocumentTemplate> query = memEManager.createNamedQuery(QUERY_DOMAIN_DOC_TEMPLATES_BY_DOMAIN, DBDomainDocumentTemplate.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }

    /**
     * Returns the DBDomainDocumentTemplate for domain and resourceDefIdentifier or Optional.empty() if there is no DBDomainDocumentTemplate configured for domain and resourceDefIdentifier.
     *
     * @param domainResourceDef        domain resource definition
     * @param subresourceDefIdentifier option subresource definition, can be null
     * @param documentLevelType        the document level type
     * @return the List of DBDomainDocumentTemplate
     */
    public List<DBDomainDocumentTemplate> getDomainDocumentTemplate(DBDomainResourceDef domainResourceDef,
                                                                    DBSubresourceDef subresourceDefIdentifier,
                                                                    DocumentLevelType documentLevelType) {
        LOG.debug("Get domain document templates for domain code: [{}] and resource definition identifier: [{}]", domainResourceDef.getDomain().getDomainCode(),
                domainResourceDef.getResourceDef().getIdentifier());
        TypedQuery<DBDomainDocumentTemplate> query = memEManager.createNamedQuery(QueryNames.QUERY_DOMAIN_DOC_TEMPLATES_BY_DOMAIN_RESDEF_SUBRESDEF, DBDomainDocumentTemplate.class);
        query.setParameter(QueryNames.PARAM_DOMAIN_RESDEF__ID, domainResourceDef.getId());
        query.setParameter(QueryNames.PARAM_SUBRESOURCE_DEF_ID, subresourceDefIdentifier == null ? null : subresourceDefIdentifier.getId());
        query.setParameter(QueryNames.PARAM_DOCUMENT_LEVEL_TYPE, documentLevelType);
        return query.getResultList();
    }
}