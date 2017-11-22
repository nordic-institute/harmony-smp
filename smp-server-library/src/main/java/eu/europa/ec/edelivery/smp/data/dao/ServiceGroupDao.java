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

import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupID;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceGroupDao {

    @PersistenceContext
    EntityManager entityManager;

    public DBServiceGroup find(ParticipantIdentifierType serviceGroupId) {
        DBServiceGroupID dbServiceGroupId = new DBServiceGroupID(serviceGroupId);
        return entityManager.find(DBServiceGroup.class, dbServiceGroupId);
    }

    public void update(DBServiceGroup dbServiceGroup) {
        //TODO Try to use one method for both create and update
        entityManager.merge(dbServiceGroup);
    }

    public void save(DBServiceGroup dbServiceGroup) {
        //TODO Try to use one method for both create and update
        entityManager.persist(dbServiceGroup);
        /*entityManager.flush();
        System.out.print("====DUPA");*/
    }

    public void remove(DBServiceGroup serviceGroup) {
        entityManager.remove(serviceGroup);
    }
}
