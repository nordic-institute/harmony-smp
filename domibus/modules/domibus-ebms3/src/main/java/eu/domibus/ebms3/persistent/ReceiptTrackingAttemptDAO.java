package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;

/**
 * DAO for ReceiptTrackingAttempt
 */
public class ReceiptTrackingAttemptDAO extends AbstractDAO<ReceiptTrackingAttempt> {
    /**
     * {@inheritDoc}
     */
    @Override
    public ReceiptTrackingAttempt findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final ReceiptTrackingAttempt res = em.find(ReceiptTrackingAttempt.class, id);
        em.close();
        return res;
    }
}
