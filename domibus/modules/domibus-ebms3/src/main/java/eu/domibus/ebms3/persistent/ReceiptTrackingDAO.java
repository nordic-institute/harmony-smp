package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * DAO for ReceiptTracking entities.
 *
 * @author Christian Koch
 * @since 1.4
 */
public class ReceiptTrackingDAO extends AbstractDAO<ReceiptTracking> {
    private static final Logger LOG = Logger.getLogger(ReceiptTrackingDAO.class);

    @Override
    /**
     * {@inheritDoc}
     */

    public ReceiptTracking findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final ReceiptTracking res = em.find(ReceiptTracking.class, id);
        em.close();
        return res;
    }


    /**
     * Get all ReceiptTracking object for which no Receipt has been received.
     *
     * @return All ReceiptTracking objects still waiting for a receipt
     */
    public List<ReceiptTracking> getAllWaitingForReceipt() {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptTracking.getAllWaitingForReceipt");
        final List<ReceiptTracking> res = q.getResultList();
        em.close();
        return res;
    }


    /**
     * Updates the status of a {@link ReceiptTracking}. This only applies to ReceiptTrackings in the {@link ReceiptTracking.STATUS_IN_PROCESS} status.
     *
     * @param newStatus the new status of the receipt. Must be one defined in {@link ReceiptTracking}
     * @param messageId the messageId of the tracked message
     * @return number of updated {@link ReceiptTracking}
     */

    public int updateTrackingStatus(final String newStatus, final String messageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptTracking.updateTrackingStatus");
        q.setParameter("NEW_STATUS", newStatus);
        q.setParameter("MESSAGE_ID", messageId);
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        final int updated = q.executeUpdate();
        tx.commit();
        em.close();
        return updated;
    }

    /**
     * Save the receipt signal message to the database.
     * The datebase will only be updated for the first received receipt.
     * Later receipts for the same message ID will be ignored.
     *
     * @param refToMessageId    ID of the referenced user message
     * @param receipt           text dump of the message containing the XML receipt signal message
     * @param receivedTimestamp
     * @return
     */
    public synchronized int setReceipt(final String refToMessageId, final String receipt,
                                       final Date receivedTimestamp) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptTracking.setReceipt");
        q.setParameter("RECEIPT", receipt);
        q.setParameter("FIRST_RECEPTION", receivedTimestamp);
        q.setParameter("MESSAGE_ID", refToMessageId);
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        final int res = q.executeUpdate();
        tx.commit();
        em.close();
        return res;

    }

    /**
     * Get the ReceiptTracking object for the User Message with the given message id
     *
     * @param mesageId The message id of the User Message the ReceiptTracking object should be retrieved for
     * @return The object tracking the Receipt for the User Message or null if no tracking information is found
     */
    public ReceiptTracking getReceiptTrackerForUserMsg(final String mesageId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceiptTracking.getReceiptTrackerForUserMsg");
        q.setParameter("MESSAGE_ID", mesageId);
        try {
            return (ReceiptTracking) q.getSingleResult();
        } catch (NoResultException e) {
            ReceiptTrackingDAO.LOG.debug("no ReceiptTracker found for message with id: " + mesageId);
            return null;
        } finally {
            em.close();
        }
    }


}
