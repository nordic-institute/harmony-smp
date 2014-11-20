package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * DAO for ReceivedUserMsg
 */
public class ReceivedUserMsgDAO extends AbstractDAO<ReceivedUserMsg> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ReceivedUserMsg findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final ReceivedUserMsg res = em.find(ReceivedUserMsg.class, id);
        em.close();
        return res;
    }

    public List<ReceivedUserMsg> findByMessageId(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceivedUserMsg.findByMessageId");
        q.setParameter("MESSAGE_ID", messageId);
        final List<ReceivedUserMsg> res = q.getResultList();
        em.close();
        return res;
    }

    public ReceivedUserMsg findNextUndownloaded() {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceivedUserMsg.findUndownloaded");
        q.setParameter("CONSUMED_BY", eu.domibus.common.Constants.CONSUMER_NAME);
        q.setMaxResults(1);
        final ReceivedUserMsg res = (ReceivedUserMsg) q.getSingleResult();
        em.close();
        return res;
    }

    public long countMessagesByMessageId(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceivedUserMsg.countForMessageId");
        q.setParameter("MESSAGE_ID", messageId);
        final long res = (Long) q.getSingleResult();
        em.close();
        return res;
    }
}
