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
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.CONFIGURATION_ERROR;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class ResourceDefDao extends BaseDao<DBResourceDef> {


    /**
     * Returns DBResourceDef records from the database.
     *
     * @return the list of DBResourceDef records from smp_extension table
     */
    public List<DBResourceDef> getAllResourceDef() {
        TypedQuery<DBResourceDef> query = memEManager.createNamedQuery(QUERY_RESOURCE_DEF_ALL, DBResourceDef.class);
        return query.getResultList();
    }

    /**
     * Returns DBResourceDef records from the database.
     *
     * @return the list of DBResourceDef records from smp_extension table
     */
    public List<DBResourceDef> getAllResourceDefForDomain(DBDomain domain) {
        TypedQuery<DBResourceDef> query = memEManager.createNamedQuery(QUERY_RESOURCE_DEF_BY_DOMAIN, DBResourceDef.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }


    /**
     * Returns the ResourceDef by url path segment.
     * Returns the ResourceDef or Optional.empty() if there is no ResourceDef.
     *
     * @return the only single record for ResourceDef url segment or empty value
     * @throws IllegalStateException if more than one ResourceDef is returned
     */
    public Optional<DBResourceDef> getResourceDefByURLSegment(String resourceDeftUrlSegment) {
        try {
            TypedQuery<DBResourceDef> query = memEManager.createNamedQuery(QUERY_RESOURCE_DEF_URL_SEGMENT, DBResourceDef.class);
            query.setParameter(PARAM_URL_SEGMENT, resourceDeftUrlSegment);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(INTERNAL_ERROR.getMessage("More than one result for ResourceDef with url context:" + resourceDeftUrlSegment));
        }
    }

    /**
     * Returns the ResourceDef by url path segment.
     * Returns the ResourceDef or Optional.empty() if there is no ResourceDef.
     *
     * @return the only single record for ResourceDef url segment or empty value
     * @throws IllegalStateException if more than one ResourceDef is returned
     */
    public Optional<DBResourceDef> getResourceDefByIdentifier(String resourceIdentifier) {
        try {
            TypedQuery<DBResourceDef> query = memEManager.createNamedQuery(QUERY_RESOURCE_DEF_BY_IDENTIFIER, DBResourceDef.class);
            query.setParameter(PARAM_IDENTIFIER, resourceIdentifier);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(INTERNAL_ERROR.getMessage("More than one result for ResourceDef with identifier:" + resourceIdentifier));
        }
    }

    /**
     * Returns the DBResourceDef by the resource definition code.
     *
     * @param extension with the resource definitions
     * @param code      is unique code for extension of the resourceDef
     * @return the record for ResourceDef code empty Optional
     */
    public Optional<DBResourceDef> getResourceDefByIdentifierAndExtension(String code, DBExtension extension) {
        return getResourceDefByIdentifierAndExtension(code, extension.getId());
    }

    /**
     * Returns the DBResourceDef by the resource definition code.
     *
     * @param extensionId with the resource definitions
     * @param code        is unique code for extension of the resourceDef
     * @return the record for ResourceDef code empty Optional
     */
    public Optional<DBResourceDef> getResourceDefByIdentifierAndExtension(String code, Long extensionId) {
        try {
            TypedQuery<DBResourceDef> query = memEManager.createNamedQuery(QUERY_RESOURCE_DEF_BY_IDENTIFIER_EXTENSION, DBResourceDef.class);
            query.setParameter(PARAM_EXTENSION_ID, extensionId);
            query.setParameter(PARAM_IDENTIFIER, code);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, "More than one resource type is registered for the name!");
        }
    }
}
