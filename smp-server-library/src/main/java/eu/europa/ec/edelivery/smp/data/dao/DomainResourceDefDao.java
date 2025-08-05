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

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class DomainResourceDefDao extends BaseDao<DBDomainResourceDef> {

    private final SMPExceptionLanguageService smpExceptionLanguageService;

    public DomainResourceDefDao(SMPExceptionLanguageService smpExceptionLanguageService) {
        this.smpExceptionLanguageService = smpExceptionLanguageService;
    }

    /**
     * Returns the ResourceDef configuration for domain
     *e
     * @param domain the DBDomain
     * @return the List of records for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is found
     */
    public List<DBDomainResourceDef> getResourceDefConfigurationsForDomain(DBDomain domain) {
        TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery(QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_ALL, DBDomainResourceDef.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }

    /**
     * Returns the DBDomainResourceDef configuration for domain or Optional.empty() if there is no DBDomainResourceDef configured for domain.
     *
     * @param domainId             domain ide
     * @param resourceIdentifier resource definition identifier
     * @return the only single record for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is found
     */
    public Optional<DBDomainResourceDef> getResourceDefConfigurationForDomainIdAndResourceDefIdentifier(Long domainId, String resourceIdentifier) {
        try {
            TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery(QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_ID_RESDEF_IDENTIFIER, DBDomainResourceDef.class);
            query.setParameter(PARAM_DOMAIN_ID, domainId);
            query.setParameter(PARAM_RESOURCE_DEF_IDENTIFIER, resourceIdentifier);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(smpExceptionLanguageService.getMessageTranslation("error.internal.resource.lookup.by.url.and.domain.id.illegal.state.multiple.entries",
                    Map.of("identifier", resourceIdentifier, "domainId", domainId)));
        }
    }

    /**
     * Returns the DBDomainResourceDef configuration for domain or Optional.empty() if there is no DBDomainResourceDef configured for domain.
     *
     * @param domainCode             domain code
     * @param resourceDefUrlSegment resourceDefUrlSegment
     * @return the only single record for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is found
     */
    public Optional<DBDomainResourceDef> getResourceDefConfigurationForDomainCodeAndResourceDefCtx(String domainCode, String resourceDefUrlSegment) {
        try {
            TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery(QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_CODE_SEGMENT_URL, DBDomainResourceDef.class);
            query.setParameter(PARAM_DOMAIN_CODE, domainCode);
            query.setParameter(PARAM_URL_SEGMENT, resourceDefUrlSegment);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(smpExceptionLanguageService.getMessageTranslation("error.internal.resource.lookup.by.url.and.domain.code.illegal.state.multiple.entries",
                    Map.of("urlSegment", resourceDefUrlSegment, "domainCode", domainCode)));
        }
    }

    /**
     * Returns the DBDomainResourceDef configuration for domain or Optional.empty() if there is no DBDomainResourceDef configured for domain.
     *
     * @param domain the DBDomain
     * @param resourceDef the DBResourceDef
     * @return the only single record for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is found
     */
    public Optional<DBDomainResourceDef> getResourceDefConfigurationForDomainAndResourceDef(DBDomain domain, DBResourceDef resourceDef) {
        try {
            TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery(QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_RES_DEF, DBDomainResourceDef.class);
            query.setParameter(PARAM_DOMAIN_ID, domain.getId());
            query.setParameter(PARAM_RESOURCE_DEF_ID, resourceDef.getId());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(smpExceptionLanguageService.getMessageTranslation("error.internal.resource.lookup.by.url.and.domain.code.illegal.state.multiple.entries",
                    Map.of("urlSegment", resourceDef, "domainCode", domain)));
        }
    }

    public DBDomainResourceDef create(DBDomain domain, DBResourceDef resourceDef) {
        DBDomainResourceDef domainResourceDef = new DBDomainResourceDef();
        domainResourceDef.setDomain(domain);
        domainResourceDef.setResourceDef(resourceDef);
        return merge(domainResourceDef);
    }


}
