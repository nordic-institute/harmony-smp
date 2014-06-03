package eu.domibus.ebms3.persistent;

import org.apache.log4j.Logger;
import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * DAO for ReceiptData
 */
public class ReceiptDataDAO extends AbstractDAO<ReceiptData> {
    private static final Logger LOG = Logger.getLogger(ReceiptDataDAO.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ReceiptData findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final ReceiptData res = em.find(ReceiptData.class, id);
        em.close();
        return res;
    }

    public List<ReceiptData> getUnsentCallbackReceiptData() {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptData.getUnsentCallbackReceiptData");
        final List<ReceiptData> res = q.getResultList();
        em.close();
        return res;
    }

    public ReceiptData findByMessageId(final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptData.findAllForMessageId");
        q.setParameter("MESSAGE_ID", messageId);
        q.setMaxResults(1);
        final ReceiptData res = (ReceiptData) q.getSingleResult();
        em.close();
        return res;
    }

    public ReceiptData getNextReceiptForPMode(final String pmode) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptData.getNextReceiptDataForPmode");
        q.setParameter("PMODE", pmode);
        q.setMaxResults(1);
        ReceiptData res = null;
        try {
            res = (ReceiptData) q.getSingleResult();
        } catch (NoResultException e) {
            LOG.debug("no receipt found for pmode " + pmode);
        } finally {
            em.close();
        }
        return res;
    }

}
