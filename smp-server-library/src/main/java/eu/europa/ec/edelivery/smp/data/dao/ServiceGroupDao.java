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
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceGroupDao extends BaseDao<DBServiceGroup> {


    /**
     * Method returns ServiceGroup by participant identifier. If there is no service group it returns empty Option.
     * If more than one result returns IllegalStateException caused by database data inconsistency. Only one combination of
     * participant identifier must be in the database.
     *
     * @param participantId participant identifier
     * @param schema        participant identifier schema
     * @return DBServiceGroup
     */
    public Optional<DBServiceGroup> findServiceGroup(String participantId, String schema) {


        try {
            TypedQuery<DBServiceGroup> query = memEManager.createNamedQuery("DBServiceGroup.getServiceGroup", DBServiceGroup.class);
            query.setParameter("participantIdentifier", participantId);
            query.setParameter("participantScheme", schema);
            DBServiceGroup res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(participantId, schema));
        }
    }

    /**
     * Method removes service group from DB. Related entities:Extension, ownerships,
     * metadata clobs, metadata are also deleted.
     *
     * @param dbServiceGroup
     */
    @Transactional
    public void removeServiceGroup(DBServiceGroup dbServiceGroup){
     // Because of one to many relationships
         // remove with JPA/JPQL (or native sql) querieas are much more efficient in this case.
         // but it does not capture audit with envers: Hibernate Envers captures the Audit information only when the updates
         // happen through Persistence Context.
 /*       em.createNamedQuery("DBServiceGroup.deleteAllOwnerships")
                .setParameter("serviceGroupId", dbServiceGroup.getId()).executeUpdate();

        em.createNamedQuery("DBServiceGroupExtension.deleteById")
                .setParameter("id", dbServiceGroup.getId()).executeUpdate();

        em.createNamedQuery("DBServiceMetadata.deleteOwnedByServiceGroup")
                .setParameter("serviceGroup", dbServiceGroup).executeUpdate();

        em.createNamedQuery("DBServiceGroup.deleteById")
                .setParameter("id", dbServiceGroup.getId()).executeUpdate()>0;
 */
        memEManager.remove(memEManager.contains(dbServiceGroup) ? dbServiceGroup : memEManager.merge(dbServiceGroup));
    }





}
