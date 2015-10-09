package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * DAO for UserMessageToPush
 *
 * @author Christian Koch
 * @since 1.4
 */
public class UserMsgToPushDAO extends AbstractDAO<UserMsgToPush> {
	 private static final Logger LOG = Logger.getLogger(UserMsgToPushDAO.class);
    /**
     * {@inheritDoc}
     */
    @Override
    public UserMsgToPush findById(final String id) {
        
    	final EntityManager em = JpaUtil.getEntityManager();
    	UserMsgToPush res = null;
    	try {
    		 res = em.find(UserMsgToPush.class, id);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
			if (em != null) {
				em.close();
			}
		}
    	return res;
    }


    /**
     * finds all messages that need to be pushed
     *
     * @return messages that need to be pushed
     */
    public List<UserMsgToPush> findMessagesToPush() {
    	
    	final EntityManager em = JpaUtil.getEntityManager();
    	List<UserMsgToPush> res = null;
    	try {
			final Query q = em.createNamedQuery("UserMsgToPush.findMessagesToPush");
			res = q.getResultList();
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
			if (em != null) {
				em.close();
			}
		}
    	return res;
    }


    /**
     * Set the <i>pushed</i> flag of a message to false so it will be retransmitted
     *
     * @param messageId The message-id of the message to retransmit
     */
    public void setRetransmit(final String messageId) {
     
      
        EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = JpaUtil.getEntityManager();
			   final Query q = em.createNamedQuery("UserMsgToPush.setRetransmit");
		        q.setParameter("MESSAGE_ID", messageId);
			tx = em.getTransaction();
			tx.begin();
			q.executeUpdate();
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

    public List<UserMsgToPush> findByMessageId(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        List<UserMsgToPush> res = null;
        try {
        	 final Query q = em.createNamedQuery("UserMsgToPush.findByMessageId");
             q.setParameter("MESSAGE_ID", messageId);
            res = q.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}finally {
			if (em != null) {
				em.close();
			}
		}
        return res;
    }
}
