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
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;
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
public class SubresourceDefDao extends BaseDao<DBSubresourceDef> {


    /**
     * Returns DBSubresourceDef records from the database.
     *
     * @return the list of DBSubresourceDef records from smp_extension table
     */
    public List<DBSubresourceDef> getAllSubresourceDef() {
        TypedQuery<DBSubresourceDef> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_DEF_ALL, DBSubresourceDef.class);
        return query.getResultList();
    }


    /**
     * Returns the DBSubresourceDef by identifier.
     * Returns the DBSubresourceDef or Optional.empty() if there is no SubresourceDef.
     *
     * @return the optional record for DBSubresourceDef
     * @throws IllegalStateException if more than one DBSubresourceDef is returned
     */
    public Optional<DBSubresourceDef> getSubresourceDefByIdentifier(String resourceDeftIdentifier) {
        try {
            TypedQuery<DBSubresourceDef> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_DEF_BY_IDENTIFIER, DBSubresourceDef.class);
            query.setParameter(PARAM_IDENTIFIER, resourceDeftIdentifier);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(INTERNAL_ERROR.getMessage("More than one result for SubresourceDef with identifier:" + resourceDeftIdentifier));
        }
    }

    /**
     * Returns the DBSubresourceDef by url path segment.
     * Returns the DBSubresourceDef or Optional.empty() if there is no SubresourceDef.
     *
     * @return the only single record for DBSubresourceDef url segment or empty value
     * @throws IllegalStateException if more than one DBSubresourceDef is returned
     */
    public Optional<DBSubresourceDef> getSubresourceDefByURLSegment(String resourceDeftUrlSegment) {
        try {
            TypedQuery<DBSubresourceDef> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_DEF_URL_SEGMENT, DBSubresourceDef.class);
            query.setParameter(PARAM_URL_SEGMENT, resourceDeftUrlSegment);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(INTERNAL_ERROR.getMessage("More than one result for SubresourceDef with url context:" + resourceDeftUrlSegment));
        }
    }


    /**
     * Removes Entity
     *
     * @param identifier is unique id of the subresource definition
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    @Transactional
    public boolean removeRegisteredResourceDefByDomainIdAndIdentifier(String identifier) {
        Optional<DBSubresourceDef> optd = getSubresourceDefByIdentifier(identifier);
        if (optd.isPresent()) {
            memEManager.remove(optd.get());
            return true;
        }
        return false;
    }
}
