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
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.*;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
        if (dbResource == null || dbResource.getId() == null) {
            LOG.debug("Can not get document for resource, because resource is not persisted to the database");
            return Optional.empty();
        }
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
        if (dbSubresource == null|| dbSubresource.getId() == null) {
            LOG.debug("Can not get document for subresource, because resource is not persisted to the database");
            return Optional.empty();
        }
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

    public Optional<DBDocumentVersion> getCurrentDocumentVersionForDocument(DBDocument document) {

        try {
            // expected is only one domain,
            TypedQuery<DBDocumentVersion> query = memEManager.createNamedQuery(QUERY_DOCUMENT_VERSION_CURRENT_FOR_DOCUMENT, DBDocumentVersion.class);
            query.setParameter(PARAM_DOCUMENT_ID, document.getId());
            return Optional.of(query.getSingleResult());
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
     *
     * @param resultClass class of the result, can be DBResource or Long
     * @param dbUserId    target user id
     * @param <T>         type of the result
     * @return the typed query
     */
    private <T> TypedQuery<T> createDocumentReviewListForUserQuery(Class<T> resultClass, Long dbUserId) {

        String queryName = resultClass == Long.class ? QUERY_DOCUMENT_VERSION_UNDER_REVIEW_FOR_USER_COUNT : QUERY_DOCUMENT_VERSION_UNDER_REVIEW_FOR_USER;
        LOG.debug("Creating query [{}] for class [{}] and user id [{}]", queryName, resultClass, dbUserId);
        TypedQuery<T> query = memEManager.createNamedQuery(queryName, resultClass);
        query.setParameter(PARAM_USER_ID, dbUserId);
        query.setParameter(PARAM_PERMISSION_CAN_REVIEW, true);
        query.setParameter(PARAM_REVIEW_ENABLED, true);
        query.setParameter(PARAM_STATUS, DocumentVersionStatusType.UNDER_REVIEW.name());
        return query;
    }

    public List<DBReviewDocumentVersionMapping> getDocumentReviewListForUser(Long dbUserId, int iPage, int iPageSize) {
        TypedQuery<DBReviewDocumentVersionMapping> query = createDocumentReviewListForUserQuery(DBReviewDocumentVersionMapping.class, dbUserId);
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
        TypedQuery<Long> query = createDocumentReviewListForUserQuery(Long.class, dbUserId);

        return query.getSingleResult();
    }

    /**
     * Method retrieved all the documents where given document is set as target and un-link the documents
     *
     * @param document the target document
     */
    public void unlinkDocument(DBDocument document){
        if (document == null || document.getId() == null) {
            LOG.debug("Can not unlink document, because document is not persisted to the database");
            return;
        }
        TypedQuery<DBDocument> query =  memEManager.createNamedQuery(QUERY_DOCUMENT_LIST_FOR_TARGET_DOCUMENT, DBDocument.class);
        query.setParameter(PARAM_DOCUMENT_ID, document.getId());
        // user stream ulink to capture audit record
        try (Stream<DBDocument> streamDocument = query.getResultStream()) {
            streamDocument.forEach(linkedDoc -> {
                linkedDoc.setReferenceDocument(null);
            });
        }
   }

    /**
     * Method creates query for searching reference document resources
     *
     * @param resultClass              class of the result, can be DBResource or Long
     * @param dbTargetResource         target resource
     * @param searchResourceIdentifier if of the search resource
     * @param searchResourceScheme     scheme of the search resource
     * @param <T>                      type of the result
     * @return the typed query
     */
    private <T> TypedQuery<T> createSearchReferenceDocumentResourcesQuery(Class<T> resultClass, DBResource dbTargetResource, String searchResourceIdentifier, String searchResourceScheme) {

        String queryName = resultClass == Long.class ? QUERY_SEARCH_DOCUMENT_REFERENCES_COUNT : QUERY_SEARCH_DOCUMENT_REFERENCES;
        LOG.debug("Create search query [{}] for resource references class [{}] with resource [{}] - [{}]", queryName, resultClass,
                searchResourceIdentifier, searchResourceScheme);

        DBDomainResourceDef dbDomainResourceDef = dbTargetResource.getDomainResourceDef();
        TypedQuery<T> query = memEManager.createNamedQuery(queryName, resultClass);
        query.setParameter(PARAM_RESOURCE_DEF_ID, dbDomainResourceDef.getResourceDef().getId());
        query.setParameter(PARAM_RESOURCE_ID, dbTargetResource.getId());
        query.setParameter(PARAM_SHARING_ENABLED, true);
        query.setParameter(PARAM_RESOURCE_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_RESOURCE_SCHEME, likeParam(searchResourceScheme));
        query.setParameter(PARAM_RESOURCE_IDENTIFIER, likeParam(searchResourceIdentifier));
        query.setParameter(PARAM_GROUP_ID, dbTargetResource.getGroup().getId());
        query.setParameter(PARAM_GROUP_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_DOMAIN_ID, dbDomainResourceDef.getDomain().getId());
        query.setParameter(PARAM_DOMAIN_VISIBILITY, VisibilityType.PUBLIC);

        return query;
    }


    private <T> TypedQuery<T> createSearchReferenceDocumentSubResourcesQuery(Class<T> resultClass,
                                                                             DBSubresource dbTargetSubresource,
                                                                             String searchResourceIdentifier,
                                                                             String searchResourceScheme,
                                                                             String searchSubresourceIdentifier,
                                                                             String searchSubresourceScheme) {

        String queryName = resultClass == Long.class ? QUERY_SEARCH_DOCUMENT_REFERENCES_FOR_SUBRESOURCES_COUNT :
                QUERY_SEARCH_DOCUMENT_REFERENCES_FOR_SUBRESOURCES;
        LOG.debug("Create search query [{}] for subresource references with class [{}] with resource [{}] - [{}], subresource [{}] - [{}]",
                queryName, resultClass,
                searchResourceIdentifier,
                searchResourceScheme,
                searchResourceIdentifier,
                searchResourceScheme);

        DBResource dbTargetResource = dbTargetSubresource.getResource();
        DBDomainResourceDef dbDomainResourceDef = dbTargetResource.getDomainResourceDef();

        TypedQuery<T> query = memEManager.createNamedQuery(queryName, resultClass);
        query.setParameter(PARAM_SUBRESOURCE_DEF_ID, dbTargetSubresource.getSubresourceDef().getId());
        query.setParameter(PARAM_SUBRESOURCE_ID, dbTargetSubresource.getId());
        query.setParameter(PARAM_SHARING_ENABLED, true);
        query.setParameter(PARAM_RESOURCE_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_GROUP_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_GROUP_ID, dbTargetResource.getGroup().getId());
        query.setParameter(PARAM_DOMAIN_VISIBILITY, VisibilityType.PUBLIC);
        query.setParameter(PARAM_DOMAIN_ID, dbDomainResourceDef.getDomain().getId());
        query.setParameter(PARAM_RESOURCE_SCHEME, likeParam(searchResourceScheme));
        query.setParameter(PARAM_RESOURCE_IDENTIFIER, likeParam(searchResourceIdentifier));
        query.setParameter(PARAM_SUBRESOURCE_SCHEME, likeParam(searchSubresourceScheme));
        query.setParameter(PARAM_SUBRESOURCE_IDENTIFIER, likeParam(searchSubresourceIdentifier));

        return query;
    }


    /**
     * Method returns list of all possible reference document resources for the target resource with pagination.
     * The target resource defines the domain and the resource definition type of the reference document.
     *
     * @param dbTargetResource         target resource
     * @param searchResourceIdentifier identifier of the search resource
     * @param searchResourceScheme     scheme of the search resource
     * @param iPage                    page number
     * @param iPageSize                page size
     * @return list of reference document resources
     */
    public List<DBSearchReferenceDocumentMapping> getSearchReferenceDocumentResources(DBResource dbTargetResource,
                                                                                      String searchResourceIdentifier,
                                                                                      String searchResourceScheme, int iPage, int iPageSize) {
        TypedQuery<DBSearchReferenceDocumentMapping> query = createSearchReferenceDocumentResourcesQuery(DBSearchReferenceDocumentMapping.class,
                dbTargetResource,
                searchResourceIdentifier,
                searchResourceScheme);
        setPaginationParametersToQuery(query, iPage, iPageSize);
        return query.getResultList();
    }

    public long getSearchReferenceDocumentResourcesCount(DBResource dbTargetResource, String searchResourceIdentifier, String searchResourceScheme) {
        TypedQuery<Long> query = createSearchReferenceDocumentResourcesQuery(Long.class, dbTargetResource, searchResourceIdentifier, searchResourceScheme);
        return query.getSingleResult();
    }

    public List<DBSearchReferenceDocumentMapping> getSearchReferenceDocumentSubresource(DBSubresource dbTargetSubresource,
                                                                                        String searchResourceIdentifier,
                                                                                        String searchResourceScheme,
                                                                                        String searchSubresourceIdentifier,
                                                                                        String searchSubresourceScheme,
                                                                                        int iPage, int iPageSize) {
        TypedQuery<DBSearchReferenceDocumentMapping> query = createSearchReferenceDocumentSubResourcesQuery(DBSearchReferenceDocumentMapping.class,
                dbTargetSubresource,
                searchResourceIdentifier,
                searchResourceScheme,
                searchSubresourceIdentifier,
                searchSubresourceScheme);
        setPaginationParametersToQuery(query, iPage, iPageSize);
        return query.getResultList();
    }

    public long getSearchReferenceDocumentSubresourceCount(DBSubresource dbTargetSubresource,
                                                           String searchResourceIdentifier,
                                                           String searchResourceScheme,
                                                           String searchSubresourceIdentifier,
                                                           String searchSubresourceScheme) {
        TypedQuery<Long> query = createSearchReferenceDocumentSubResourcesQuery(Long.class,
                dbTargetSubresource,
                searchResourceIdentifier,
                searchResourceScheme,
                searchSubresourceIdentifier,
                searchSubresourceScheme);

        return query.getSingleResult();
    }
}
