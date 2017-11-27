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

import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Created by gutowpa on 24/11/2017.
 */
public abstract class BaseDao<E extends Serializable> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<E> entityClass;

    public BaseDao() {
        entityClass = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseDao.class);
    }

    public E find(Object primaryKey) {
        return em.find(entityClass, primaryKey);
    }

    public void save(E entity) {
        try {
            em.persist(entity);
        } catch(EntityExistsException e){
            em.merge(entity);
        }
    }

    public void remove(E entity) {
        em.remove(entity);
    }

    /**
     * Removes Entity by given primary key
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    public boolean removeById(Object primaryKey) {
        int removedRecords = em.createQuery("delete from " + entityClass.getName() + " e where e.id = :primaryKey")
                .setParameter("primaryKey", primaryKey)
                .executeUpdate();
        return removedRecords > 0;
    }
}