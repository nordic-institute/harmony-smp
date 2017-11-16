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
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceMetadataDao {

    @PersistenceContext
    EntityManager entityManager;

    public DBServiceMetadata find(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {
        DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(serviceGroupId, documentId);
        return entityManager.find(DBServiceMetadata.class, aDBServiceMetadataID);
    }

    public boolean remove(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {
        DBServiceMetadata serviceMetadata = find(serviceGroupId, documentId);
        if (serviceMetadata == null) {
            return false;
        }
        entityManager.remove(serviceMetadata);
        return true;
    }

    public void save(DBServiceMetadata serviceMetadata) {
        entityManager.persist(serviceMetadata);
    }

    public List<DBServiceMetadataID> findIdsByServiceGroup(ParticipantIdentifierType serviceGroupId) {
        //TODO Check if you can retrieve IDs directly
        List<DBServiceMetadata> aServices = entityManager.createQuery("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value", DBServiceMetadata.class)
                .setParameter("scheme", serviceGroupId.getScheme())
                .setParameter("value", serviceGroupId.getValue())
                .getResultList();

        final List<DBServiceMetadataID> serviceMetadataIds = new ArrayList<>();
        for (final DBServiceMetadata aService : aServices) {
            serviceMetadataIds.add(aService.getId());
        }
        return serviceMetadataIds;
    }
}
