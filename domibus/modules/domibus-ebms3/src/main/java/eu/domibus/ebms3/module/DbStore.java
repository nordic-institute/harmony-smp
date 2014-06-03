package eu.domibus.ebms3.module;

import org.apache.log4j.Logger;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.Query;

/**
 * Simple database manager class.
 *
 * @author Sander Fieten
 * @author Hamid Ben Malek
 */
public class DbStore extends JpaUtil {

    private static final Logger log = Logger.getLogger(DbStore.class);

    /**
     * Checks whether a message has already been downloaded.
     *
     * @param refToMessageId ID of the message to check
     * @return {@code true} if the message has already been downloaded
     */
    public boolean checkForDownload(final String refToMessageId) {
        // This is a native query to avoid unnecessary dependencies on the backend interface module because Message is a table of the BackendInterface
        // It is a plain SQL-92 query compatible to virtually any RDBMS.
        final Query query = em.createNativeQuery("SELECT COUNT(1) FROM TB_MESSAGE WHERE MESSAGE_UID = :refToMessageId AND DOWNLOADED = 1");
        query.setParameter("refToMessageId", refToMessageId);
        try {
            final Number count = (Number) query.getSingleResult();
            return count.intValue() > 0;
        } catch (Exception ex) {
            // Could not count downloaded messages
            log.error("Could not count downloaded messages", ex);
            return false;
        }
    }

}