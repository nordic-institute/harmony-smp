/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadataID;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceMetadataDao {

    @PersistenceContext
    EntityManager entityManager;

    public DBServiceMetadata find(String participantIdScheme,
                                  String participantIdValue,
                                  String documentIdScheme,
                                  String documentIdValue) {

        DBServiceMetadataID serviceMetadataId = new DBServiceMetadataID(participantIdScheme,
                participantIdValue,
                documentIdScheme,
                documentIdValue);

        return entityManager.find(DBServiceMetadata.class, serviceMetadataId);
    }

    /**
     * Removes ServiceMetadata
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    public boolean remove(String participantIdScheme,
                          String participantIdValue,
                          String documentIdScheme,
                          String documentIdValue) {

        DBServiceMetadata serviceMetadata = find(participantIdScheme,
                participantIdValue,
                documentIdScheme,
                documentIdValue);

        if (serviceMetadata == null) {
            return false;
        }
        entityManager.remove(serviceMetadata);
        return true;
    }

    public void save(DBServiceMetadata serviceMetadata) {
        entityManager.persist(serviceMetadata);
    }

    public List<DBServiceMetadataID> findIdsByServiceGroup(String participantIdScheme,
                                                           String participantIdValue) {

        return entityManager.createQuery("SELECT p.id FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value", DBServiceMetadataID.class)
                .setParameter("scheme", participantIdScheme)
                .setParameter("value", participantIdValue)
                .getResultList();
    }
}
