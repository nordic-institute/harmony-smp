package eu.domibus.common.persistent;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * @author Christian Koch
 *         <p/>
 *         This class serves as a base class for data access objects, handling the acquisition of an
 *         EntityManager and basic database operations
 */
public abstract class AbstractDAO<T extends AbstractBaseEntity> {


    /**
     * Saves an Entity to the database
     *
     * @param entity the entity to persist
     */
    public void persist(final T entity) {
        final EntityManager em = JpaUtil.getEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(entity);
        tx.commit();
        em.close();
    }


    /**
     * finds the emtity with the corresponding primary key
     *
     * @param id primary key
     * @return the entity or null if it does not exist
     */
    public abstract T findById(String id);

    /**
     * updates the entity
     *
     * @param entity entity to update
     * @return the managed istance of the entity
     */
    public T update(final T entity) {
        final EntityManager em = JpaUtil.getEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        final T res = em.merge(entity);
        tx.commit();
        em.close();
        return res;
    }

    /**
     * deletes the entity
     *
     * @param entity the entity to delete
     * @throws IllegalArgumentException - if the instance is not an entity
     */
    public void delete(final T entity) {
        final EntityManager em = JpaUtil.getEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(em.merge(entity));
        tx.commit();
        em.close();
    }
}
