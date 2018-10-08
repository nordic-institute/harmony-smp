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

import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Created by gutowpa on 24/11/2017.
 */
public abstract class BaseDao<E extends BaseEntity> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<E> entityClass;

    public BaseDao() {
        entityClass = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseDao.class);
    }

    public E find(Object primaryKey) {
        return em.find(entityClass, primaryKey);
    }


    @Transactional
    public void persistFlushDetach(E entity) {
        em.persist(entity);
        em.flush();
        em.detach(entity);
    }

    @Transactional
    public void update(E entity) {
        em.merge(entity);
        em.flush();
        em.detach(entity);
    }


    /**
     * Removes Entity by given primary key
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    @Transactional
    public boolean removeById(Object primaryKey) {
        int removedRecords = em.createQuery("delete from " + entityClass.getName() + " e where e.id = :primaryKey")
                .setParameter("primaryKey", primaryKey)
                .executeUpdate();
        return removedRecords > 0;
    }


    /**
     * Clear the persistence context, causing all managed entities to become detached.
     * Changes made to entities that have not been flushed to the database will not be persisted.
     *
     * Main purpose is to clear cache for unit testing
     *
     */
    public void clearPersistenceContext(){
        em.clear();
    }



}
