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
package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.doc.*;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * The purpose of the DocumentDao is to manage the resource content in the database
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class DocumentDao extends BaseDao<DBDocument> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DocumentDao.class);
    /**
     * Method returns the document for the resource
     *
     * @param dbResource resource
     * @return document for the resource or empty if not found
     */
    public Optional<DBDocument> getDocumentForResource(DBResource dbResource) {
        try {
            // expected is only one domain,
            TypedQuery<DBDocument> query = memEManager.createNamedQuery(QUERY_DOCUMENT_FOR_RESOURCE, DBDocument.class);
            query.setParameter(PARAM_RESOURCE_ID, dbResource.getId());
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(ErrorCode.RESOURCE_DOCUMENT_ERROR, dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(), "Multiple documents");
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Method returns the document for the  (detached) subresource
     *
     * @param dbSubresource resource
     * @return document for the resource or empty if not found
     */
    public Optional<DBDocument> getDocumentForSubresource(DBSubresource dbSubresource) {
        try {
            // expected is only one domain,
            TypedQuery<DBDocument> query = memEManager.createNamedQuery(QUERY_DOCUMENT_FOR_SUBRESOURCE, DBDocument.class);
            query.setParameter(PARAM_SUBRESOURCE_ID, dbSubresource.getId());
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(ErrorCode.RESOURCE_DOCUMENT_ERROR, dbSubresource.getIdentifierValue(), dbSubresource.getIdentifierScheme(), "Multiple documents");
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<DBDocumentVersion> getCurrentDocumentVersionForResource(DBResource dbResource) {

        try {
            // expected is only one domain,
            TypedQuery<DBDocumentVersion> query = memEManager.createNamedQuery(QUERY_DOCUMENT_VERSION_CURRENT_FOR_RESOURCE, DBDocumentVersion.class);
            query.setParameter(PARAM_RESOURCE_ID, dbResource.getId());
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(ErrorCode.RESOURCE_DOCUMENT_ERROR, dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(), "Multiple documents");
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<DBDocumentVersion> getCurrentDocumentVersionForSubresource(DBSubresource subresource) {

        try {
            // expected is only one domain,
            TypedQuery<DBDocumentVersion> query = memEManager.createNamedQuery(QUERY_DOCUMENT_VERSION_CURRENT_FOR_SUBRESOURCE, DBDocumentVersion.class);
            query.setParameter(PARAM_SUBRESOURCE_ID, subresource.getId());
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            DBResource resource = subresource.getResource();
            throw new SMPRuntimeException(ErrorCode.SUBRESOURCE_DOCUMENT_ERROR,
                    subresource.getIdentifierValue(), subresource.getIdentifierScheme(),
                    resource.getIdentifierValue(), resource.getIdentifierScheme(),
                    "Multiple documents for subresource");
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Method returns list of document versions for the resource
     *
     * @param dbResource which owns the document versions
     * @return document version list
     */
    public List<DBDocumentVersion> getDocumentVersionsForResource(DBResource dbResource) {
        TypedQuery<DBDocumentVersion> query = memEManager.createNamedQuery(QUERY_DOCUMENT_VERSION_LIST_FOR_RESOURCE, DBDocumentVersion.class);
        query.setParameter(PARAM_RESOURCE_ID, dbResource.getId());
        return query.getResultList();
    }

    /**
     * Method returns list of document versions for the subresource
     *
     * @param subresource which owns the document versions
     * @return document version list
     */
    public List<DBDocumentVersion> getDocumentVersionsForSubresource(DBSubresource subresource) {
        TypedQuery<DBDocumentVersion> query = memEManager.createNamedQuery(QUERY_DOCUMENT_VERSION_LIST_FOR_SUBRESOURCE,
                DBDocumentVersion.class);
        query.setParameter(PARAM_SUBRESOURCE_ID, subresource.getId());
        return query.getResultList();
    }

    /**
     * Method creates query for searching users review tasks
     * @param resultClass class of the result, can be DBResource or Long
     * @param dbUserId target user id
     * @return the typed query
     * @param <T> type of the result
     */
    public  <T> TypedQuery<T> createDocumentReviewListForUserQuery(Class<T> resultClass, Long dbUserId) {

        String queryName = resultClass == Long.class? QUERY_DOCUMENT_VERSION_UNDER_REVIEW_FOR_USER_COUNT: QUERY_DOCUMENT_VERSION_UNDER_REVIEW_FOR_USER;
        LOG.debug("Creating query [{}] for class [{}] and user id [{}]", queryName, resultClass,dbUserId);

        TypedQuery<T> query = memEManager.createNamedQuery(queryName, resultClass);
        query.setParameter(PARAM_USER_ID, dbUserId);
        query.setParameter(PARAM_PERMISSION_CAN_REVIEW, true);
        query.setParameter(PARAM_REVIEW_ENABLED, true);
        query.setParameter(PARAM_STATUS, DocumentVersionStatusType.UNDER_REVIEW.name());
        return query;
    }

    public List<DBReviewDocumentVersion> getDocumentReviewListForUser(Long dbUserId, int iPage, int iPageSize) {
        TypedQuery<DBReviewDocumentVersion> query = createDocumentReviewListForUserQuery(DBReviewDocumentVersion.class, dbUserId);
        setPaginationParametersToQuery(query, iPage, iPageSize);
        return query.getResultList();

    }

    /**
     * Method returns count of document versions under review for the user
     *
     * @param dbUserId
     * @return
     */
    public long getDocumentReviewListForUserCount(Long dbUserId) {
        TypedQuery<Long> query =createDocumentReviewListForUserQuery(Long.class, dbUserId);

        return query.getSingleResult().longValue();
    }

    /**
     * Method creates query for searching reference document resources
     * @param resultClass class of the result, can be DBResource or Long
     * @param dbTargetResource target resource
     * @param searchResourceIdentifier if of the search resource
     * @param searchResourceScheme scheme of the search resource
     * @return the typed query
     * @param <T> type of the result
     */
    public  <T> TypedQuery<T> createSearchReferenceDocumentResourcesQuery(Class<T> resultClass, DBResource dbTargetResource, String searchResourceIdentifier, String searchResourceScheme) {

        String queryName = resultClass == Long.class? QUERY_SEARCH_DOCUMENT_REFERENCES_COUNT: QUERY_SEARCH_DOCUMENT_REFERENCES;
        LOG.debug("Creating query [{}] for class [{}] with identifier [{}] and scheme [{}]", queryName, resultClass,
                searchResourceIdentifier, searchResourceScheme);

        TypedQuery<T> query = memEManager.createNamedQuery(queryName, resultClass);

        query.setParameter(PARAM_RESOURCE_DEF_IDENTIFIER, dbTargetResource.getDomainResourceDef().getResourceDef().getIdentifier());
        query.setParameter(PARAM_RESOURCE_ID, dbTargetResource.getId());
        query.setParameter(PARAM_SHARING_ENABLED, true);
        query.setParameter(PARAM_RESOURCE_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_RESOURCE_SCHEME, StringUtils.trimToNull(searchResourceScheme));
        query.setParameter(PARAM_RESOURCE_IDENTIFIER, StringUtils.trimToNull(searchResourceIdentifier));
        query.setParameter(PARAM_GROUP_ID, dbTargetResource.getId());
        query.setParameter(PARAM_GROUP_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_DOMAIN_ID, dbTargetResource.getDomainResourceDef().getDomain().getId());
        query.setParameter(PARAM_DOMAIN_VISIBILITY, VisibilityType.PUBLIC);

        return query;
    }

    public List<DBResource> getSearchReferenceDocumentResources(DBResource dbTargetResource,
                                                                String searchResourceIdentifier,
                                                                String searchResourceScheme, int iPageSize, int iPage) {
        TypedQuery<DBResource> query = createSearchReferenceDocumentResourcesQuery(DBResource.class, dbTargetResource, searchResourceIdentifier, searchResourceScheme);
        setPaginationParametersToQuery(query, iPage, iPageSize);
        return query.getResultList();
    }

    public long getSearchReferenceDocumentResourcesCount(DBResource dbTargetResource, String searchResourceIdentifier, String searchResourceScheme) {
        TypedQuery<Long> query = createSearchReferenceDocumentResourcesQuery(Long.class, dbTargetResource, searchResourceIdentifier, searchResourceScheme);
        return query.getSingleResult();
    }

    /**
     * Method sets pagination to the query
     * @param query query to set pagination
     * @param iPage page number
     * @param iPageSize page size
     */
    private void setPaginationParametersToQuery(TypedQuery<?> query, int iPage, int iPageSize) {
        if (iPageSize > -1 && iPage > -1) {
            query.setFirstResult(iPage * iPageSize);
        }
        if (iPageSize > 0) {
            query.setMaxResults(iPageSize);
        }
    }
}
