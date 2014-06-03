package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

/**
 * DAO for UserMessageToPush
 *
 * @author Christian Koch
 * @since 1.4
 */
public class UserMsgToPushDAO extends AbstractDAO<UserMsgToPush> {
    /**
     * {@inheritDoc}
     */
    @Override
    public UserMsgToPush findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final UserMsgToPush res = em.find(UserMsgToPush.class, id);
        em.close();
        return res;
    }


    /**
     * finds all messages that need to be pushed
     *
     * @return messages that need to be pushed
     */
    public List<UserMsgToPush> findMessagesToPush() {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("UserMsgToPush.findMessagesToPush");
        final List<UserMsgToPush> res = q.getResultList();
        em.close();
        return res;
    }


    /**
     * Set the <i>pushed</i> flag of a message to false so it will be retransmitted
     *
     * @param messageId The message-id of the message to retransmit
     */
    public void setRetransmit(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("UserMsgToPush.setRetransmit");
        q.setParameter("MESSAGE_ID", messageId);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        q.executeUpdate();
        tx.commit();
        em.close();
    }

    public List<UserMsgToPush> findByMessageId(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("UserMsgToPush.findByMessageId");
        q.setParameter("MESSAGE_ID", messageId);
        return q.getResultList();
    }
}
