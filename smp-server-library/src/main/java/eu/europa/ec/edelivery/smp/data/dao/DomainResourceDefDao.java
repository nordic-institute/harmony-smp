/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.PARAM_DOMAIN_CODE;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class DomainResourceDefDao extends BaseDao<DBDomainResourceDef> {



    /**
     * Returns the ResourceDef configuration for domain
     *
     * @param domain
     * @return the List of records for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is returned
     */
    public List<DBDomainResourceDef> getResourceDefConfigurationsForDomain(DBDomain domain) {
        TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery(QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_ALL, DBDomainResourceDef.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }


    /**
     * Returns the DBDomainResourceDef configuration for domain or Optional.empty() if there is no DBDomainResourceDef configured for domain.
     *
     * @param domainCode             domain cod
     * @param resourceDeftUrlSegment resourceDeftUrlSegment
     * @return the only single record for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is returned
     */
    public Optional<DBDomainResourceDef> getResourceDefConfigurationForDomainAndResourceDef(String domainCode, String resourceDeftUrlSegment) {
        try {
            TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery(QUERY_DOMAIN_RESOURCE_DEF_DOMAIN_CODE_SEGMENT_URL, DBDomainResourceDef.class);
            query.setParameter(PARAM_DOMAIN_CODE, domainCode);
            query.setParameter(PARAM_URL_SEGMENT, resourceDeftUrlSegment);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(INTERNAL_ERROR.getMessage("More than one result for ResourceDef with url context [" + resourceDeftUrlSegment + "] and domain code [" + domainCode + "]"));
        }
    }

    /**
     * Returns the DBDomainResourceDef configuration for domain or Optional.empty() if there is no DBDomainResourceDef configured for domain.
     *
     * @param domain the DBDomain
     * @param resourceDef resourceDeftUrlSegment
     * @return the only single record for DBDomainResourceDef
     * @throws IllegalStateException if more than one ResourceDef is returned
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
            throw new IllegalStateException(INTERNAL_ERROR.getMessage("More than one result for ResourceDef with url context [" + resourceDef + "] and domain code [" + domain + "]"));
        }
    }

    public DBDomainResourceDef create(DBDomain domain, DBResourceDef resourceDef) {
        DBDomainResourceDef domainResourceDef = new DBDomainResourceDef();
        domainResourceDef.setDomain(domain);
        domainResourceDef.setResourceDef(resourceDef);
        return merge(domainResourceDef);
    }


}
