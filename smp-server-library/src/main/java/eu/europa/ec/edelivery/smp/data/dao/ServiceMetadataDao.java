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

import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceMetadataDao extends BaseDao<DBServiceMetadata> {

    /**
     * Method returns DBServiceGroup by domain, and participant. If there is no service group it returns empty Option.
     * If more than one result returns IllegalStateException caused by database data inconsistency.
     *
     * @param participantId participant id
     * @param participantSchema        participant identifier schema
     * @param documentId  document id
     * @param documentSchema document identifier schema
     *
     * @return Optional DBServiceMetadata - empty if no metadata found else with DBServiceMetadata objecdt
     */

    public Optional<DBServiceMetadata> findServiceMetadata(String participantId, String participantSchema, String documentId, String documentSchema){

        try {
            TypedQuery<DBServiceMetadata> query = memEManager.createNamedQuery("DBServiceMetadata.getBySGIdentifierAndSMDdentifier", DBServiceMetadata.class);
            query.setParameter("partcId", participantId);
            query.setParameter("partcSch", participantSchema);
            query.setParameter("docId", documentId);
            query.setParameter("docSch", documentSchema);
            DBServiceMetadata res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(documentId,documentSchema,participantId, participantSchema));
        }
    }

    public List<DBServiceMetadata> getAllMetadataForServiceGroup(String participantId,
                                                         String participantSchema) {
        TypedQuery<DBServiceMetadata> query = memEManager.createNamedQuery("DBServiceMetadata.getBySGIdentifier", DBServiceMetadata.class);
        query.setParameter("partcId", participantId);
        query.setParameter("partcSch", participantSchema);
        return query.getResultList();
   }
}
