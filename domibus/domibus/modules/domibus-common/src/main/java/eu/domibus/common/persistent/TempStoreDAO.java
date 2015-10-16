package eu.domibus.common.persistent;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * TODO: Insert Description here
 *
 * @author muell16
 */
public class TempStoreDAO extends AbstractDAO<TempStore> {
	private static final Logger LOG = Logger.getLogger(TempStoreDAO.class);
    @Override
    public TempStore findById(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        
        TempStore res= null;
		try {
			res = em.find(TempStore.class, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			em.close();
		}
        return res;
    }

    public List<TempStore> findByGroup(String group) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("TempStore.findByGroup");
        q.setParameter("GROUP", group);
        final List<TempStore> res = q.getResultList();
        em.close();

        return res;
    }

    public TempStore findByGroupAndArtifact(String group, String artifact) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("TempStore.findByGroupAndArtifact");
        q.setParameter("GROUP", group);
        q.setParameter("ARTIFACT", artifact);
        final TempStore res = (TempStore) q.getSingleResult();
        em.close();

        return res;
    }

    public void persistAll(Collection<TempStore> data) {
        for (TempStore ts : data) {
            this.persist(ts);
        }
    }

    public int deleteAttachments(String messageId) {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = JpaUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			final Query q = em
					.createNamedQuery("MessageToSend.findFilePathByMessageID");
			q.setParameter("MESSAGE_ID", messageId);
			String groupIDAndArtifact = (String) q.getSingleResult();

			final Query r = em.createNamedQuery("TempStore.deleteAttachments");
			r.setParameter("GROUP", groupIDAndArtifact.split("/")[0]);
			int numberOfDeletedAttachments = r.executeUpdate();
			return numberOfDeletedAttachments;
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
