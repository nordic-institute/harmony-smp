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
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadataId;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceMetadataDao extends BaseDao<DBServiceMetadata> {

    public List<DBServiceMetadataId> findIdsByServiceGroup(String participantIdScheme,
                                                           String participantIdValue) {

        return em.createQuery("SELECT p.id FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value", DBServiceMetadataId.class)
                .setParameter("scheme", participantIdScheme)
                .setParameter("value", participantIdValue)
                .getResultList();
    }
}
