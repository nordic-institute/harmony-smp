package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * DAO for UserMsgToPull
 */

public class UserMsgToPullDAO extends AbstractDAO<UserMsgToPull> {

    /**
     * {@inheritDoc}
     */
    @Override

    public UserMsgToPull findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final UserMsgToPull res = em.find(UserMsgToPull.class, id);
        em.close();
        return res;
    }

    public UserMsgToPull getNextUserMsgToPull(final String mpc) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("UserMsgToPull.getNextUserMsgToPull");
        q.setParameter("mpc", mpc);
        q.setMaxResults(1);
        final UserMsgToPull msg = (UserMsgToPull) q.getSingleResult();
        if (msg != null) {
            msg.setPulled(true);
        }
        em.close();
        return msg;
    }


}
