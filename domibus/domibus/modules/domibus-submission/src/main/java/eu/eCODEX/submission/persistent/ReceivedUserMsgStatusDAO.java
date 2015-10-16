package eu.eCODEX.submission.persistent;


import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;
import org.apache.commons.lang.time.DateUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;


/**
 * Data access object to access {@link ReceivedUserMsgStatus} objects
 */
public class ReceivedUserMsgStatusDAO extends AbstractDAO<ReceivedUserMsgStatus> {
    @Override
    public ReceivedUserMsgStatus findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final ReceivedUserMsgStatus res = em.find(ReceivedUserMsgStatus.class, id);
        em.close();
        return res;
    }

    /**
     * Provides a list of the messages that have not been downloaded yet.
     *
     * @param limit the maximum size of the list
     * @return a list of the messages that have not been downloaded yet, up to the specified limit
     */
    public List<String> listPendingMessageIds(final int limit) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceivedMessageStatus.listPendingMessages");
        q.setParameter("CONSUMED_BY", eu.domibus.common.Constants.CONSUMER_NAME);
        q.setMaxResults(limit);
        final List<String> res = q.getResultList();
        em.close();
        return res;
    }

    /**
     * Provides a ReceivedUserMsgStatus {@link eu.eCODEX.submission.persistent.ReceivedUserMsgStatus} which is linked
     * to a ReceivedUserMsg {@link eu.domibus.ebms3.persistent.ReceivedUserMsg} via receivedUserMsg.id (primary key).
     *
     * @param receivedUserMsgId the id of the ReceivedUserMsg the requested ReceivedUserMsgStatus is linked to
     * @return the requested ReceivedUserMsgStatus
     */
    public ReceivedUserMsgStatus findByReceivedUserMsgId(final String receivedUserMsgId) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("ReceivedMessageStatus.findByReceivedUserMessageId");
        q.setParameter("RECEIVED_USER_MSG_ID", receivedUserMsgId);
        q.setMaxResults(1);
        final ReceivedUserMsgStatus res = (ReceivedUserMsgStatus) q.getSingleResult();
        em.close();
        return res;
    }

    /**
     * Removes attachments (blobs) of all messages
     * not downloaded and older than the given amount of days.
     *
     * @param days the amount of days
     * @return amount of affected messages
     */
    public int deletePayloadsFromMessagesOlderThan(int days) {
        if (days > 0) {
            days *= -1;
        }
        Date deleteDate = DateUtils.addDays(new Date(), days);

        final EntityManager em = JpaUtil.getEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Query q = em.createNamedQuery("ReceivedMessageStatus.findPayloadsFromMessagesOlderThan");
        q.setParameter("DELETE_DATE", deleteDate);
        final List<String> res = q.getResultList();

        int deleted = 0;

        if (!res.isEmpty()) {

            final Query qs = em.createNamedQuery("ReceivedMessageStatus.deletePayloadsWithIds");
            qs.setParameter("IDS", res);
            deleted = qs.executeUpdate();

            final Query r = em.createNamedQuery("ReceivedMessageStatus.markAsDeleted");
            r.setParameter("DELETED", new Date());
            r.setParameter("DELETE_DATE", deleteDate);
            r.executeUpdate();

        }

        tx.commit();
        em.close();

        return deleted;
    }

    /**
     * Removes attachments (blobs) of all already downloaded messages
     *
     * @param delayInSeconds the delay in seconds until the messages will be deleted
     * @return amount of affected messages
     */
    public int deletePayloadsFromDownloadedMessages(int delayInSeconds) {
        if (delayInSeconds > 0) {
            delayInSeconds *= -1;
        }
        Date deleteDate = DateUtils.addSeconds(new Date(), delayInSeconds);

        final EntityManager em = JpaUtil.getEntityManager();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Query q = em.createNamedQuery("ReceivedMessageStatus.findPayloadsFromDownloadedMessages");
        q.setParameter("DELETE_DATE", deleteDate);
        final List<String> res = q.getResultList();

        int deleted = 0;

        if(!res.isEmpty()) {

            final Query qs = em.createNamedQuery("ReceivedMessageStatus.deletePayloadsWithIds");
            qs.setParameter("IDS", res);
            deleted = qs.executeUpdate();

            final Query r = em.createNamedQuery("ReceivedMessageStatus.markDownloadedAsDeleted");
            r.setParameter("DELETED", new Date());
            r.executeUpdate();
        }

        tx.commit();
        em.close();
        return deleted;
    }


}
