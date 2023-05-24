/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class SubresourceDao extends BaseDao<DBSubresource> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SubresourceDao.class);

    /**
     * Method returns DBSubresource for the resource object with given subresource identifier resource type.
     * If more than one result exist, it returns IllegalStateException caused by the database data inconsistency. Only one combination of
     * resource identifier must be registered in database for subresource type.
     *
     * @param resource         for the subresource type
     * @param subresourceId the subresource Identifier Object
     * @return Optional DBSubresource - empty if no metadata found else with DBSubresource objecdt
     */

    public Optional<DBSubresource> getSubResource(Identifier subresourceId, DBResource resource, String subresourceUrlCtx) {
        LOG.info("GetSubresource for subresource identifier [{}], resource: [{}], and service url [{}]", subresourceId, resource, subresourceUrlCtx);
        try {
            TypedQuery<DBSubresource> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_BY_IDENTIFIER_RESOURCE_SUBRESDEF, DBSubresource.class);
            query.setParameter(IDENTIFIER_VALUE, subresourceId.getValue());
            query.setParameter(IDENTIFIER_SCHEME, subresourceId.getScheme());
            query.setParameter(PARAM_URL_SEGMENT, subresourceUrlCtx);
            query.setParameter(PARAM_RESOURCE_ID, resource.getId());
            DBSubresource res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(subresourceId.getValue(), subresourceId.getScheme(), resource.getIdentifierValue(), resource.getIdentifierScheme()));
        }
    }

    /**
     * Method returns list of DBSubresources of the resource for specific subresources definition
     *
     * @param identifier the resource Identifier Object
     * @param subresourceDefIdentifier the resource schema
     * @return List of DBSubresources
     */
    public List<DBSubresource> getSubResourcesForResource(Identifier identifier, String subresourceDefIdentifier) {

        TypedQuery<DBSubresource> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_BY_RESOURCE_SUBRESDEF, DBSubresource.class);
        query.setParameter(PARAM_SUBRESOURCE_DEF_IDENTIFIER, subresourceDefIdentifier);
        query.setParameter(PARAM_RESOURCE_IDENTIFIER, identifier.getValue());
        query.setParameter(PARAM_RESOURCE_SCHEME, identifier.getScheme());
        return query.getResultList();
    }

    public Optional<DBSubresource> getSubResourcesForResource(Identifier subresourceId, DBResource resource) {

        try {
            TypedQuery<DBSubresource> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_BY_IDENTIFIER_RESOURCE_ID, DBSubresource.class);
            query.setParameter(PARAM_RESOURCE_ID, resource.getId());
            query.setParameter(PARAM_SUBRESOURCE_IDENTIFIER, subresourceId.getValue());
            query.setParameter(PARAM_SUBRESOURCE_SCHEME, subresourceId.getScheme());
            DBSubresource res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(subresourceId.getValue(), subresourceId.getScheme(), resource.getIdentifierValue(), resource.getIdentifierScheme()));
        }

    }


    public List<DBSubresource> getSubResourcesForResourceId(Long resourceId) {

        TypedQuery<DBSubresource> query = memEManager.createNamedQuery(QUERY_SUBRESOURCE_BY_RESOURCE_ID, DBSubresource.class);
        query.setParameter(PARAM_RESOURCE_ID, resourceId);
        return query.getResultList();
    }

    @Transactional
    public void remove(DBSubresource subresource) {
        removeById(subresource.getId());
    }
}
