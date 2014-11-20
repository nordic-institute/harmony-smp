package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * DAO for MsgIdCallback
 */
public class MsgIdCallbackDAO extends AbstractDAO<MsgIdCallback> {
    /**
     * {@inheritDoc}
     */

    @Override
    public MsgIdCallback findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final MsgIdCallback res = em.find(MsgIdCallback.class, id);
        em.close();
        return res;
    }

    public MsgIdCallback findByMessageId(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("MsgIdCallback.findByMsgId");
        q.setParameter("MESSAGEID", messageId);

        try {
            return (MsgIdCallback) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } finally {
            em.close();
        }
    }
}
