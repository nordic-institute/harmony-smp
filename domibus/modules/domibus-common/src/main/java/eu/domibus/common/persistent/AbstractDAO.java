package eu.domibus.common.persistent;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;


/**
 * @author Christian Koch
 *         <p/>
 *         This class serves as a base class for data access objects, handling the acquisition of an
 *         EntityManager and basic database operations
 */
public abstract class AbstractDAO<T extends AbstractBaseEntity> {
	 
	private static final Logger LOG = Logger.getLogger(AbstractDAO.class);

    /**
     * Saves an Entity to the database
     *
     * @param entity the entity to persist
     */
    public void persist(final T entity) {
        EntityManager em = null;
        EntityTransaction tx = null;
        
        try {
        	em = JpaUtil.getEntityManager();
        	tx = em.getTransaction();
			tx.begin();
			em.persist(entity);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally{
			if (tx != null && tx.isActive()){
				tx.commit();
			}
			if (em != null){
				em.close();
			}
		}
		
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
    	EntityManager em =null;
    	EntityTransaction tx =null;
        try {
        	 em = JpaUtil.getEntityManager();
        	 tx = em.getTransaction();

			tx.begin();
			final T res = em.merge(entity);
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			if (tx != null && tx.isActive()) {
				tx.commit();
			}
			if (em != null){
				em.close();
			}
		}
    }

    /**
     * deletes the entity
     *
     * @param entity the entity to delete
     * @throws IllegalArgumentException - if the instance is not an entity
     */
    public void delete(final T entity) {
		EntityManager em =null;
		EntityTransaction tx = null;
		try {
			em = JpaUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			em.remove(em.merge(entity));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (tx != null && tx.isActive()) {
				tx.commit();
			}
			if (em != null) {
				em.close();
			}
		}
    }
}
