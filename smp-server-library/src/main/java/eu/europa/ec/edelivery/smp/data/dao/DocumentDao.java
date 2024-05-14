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


import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
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
        TypedQuery<DBDocumentVersion> query = memEManager.createNamedQuery(QUERY_DOCUMENT_VERSION_LIST_FOR_SUBRESOURCE, DBDocumentVersion.class);
        query.setParameter(PARAM_SUBRESOURCE_ID, subresource.getId());
        return query.getResultList();
    }
}
