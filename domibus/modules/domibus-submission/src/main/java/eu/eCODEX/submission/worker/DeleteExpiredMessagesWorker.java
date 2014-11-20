package eu.eCODEX.submission.worker;

import eu.domibus.common.util.JNDIUtil;
import eu.eCODEX.submission.Constants;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatusDAO;
import org.apache.log4j.Logger;

/**
 * This worker is responsible for the removal of message attachments in case of expired (downloaded and older than amount of days) messages.
 *
 * @author muell16
 */
public class DeleteExpiredMessagesWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(DeleteExpiredMessagesWorker.class);

    private final ReceivedUserMsgStatusDAO rumsd = new ReceivedUserMsgStatusDAO();
    private final int ttl = (Integer) JNDIUtil.getEnvironmentParameter(Constants.MESSAGES_TIME_TO_LIVE_PROPERTY_KEY);
    private final int downloadedTtl =
            (Integer) JNDIUtil.getEnvironmentParameter(Constants.DOMIBUS_SUBMISSION_DOWNLOADED_MESSAGES_TIME_TO_LIVE);

    @Override
    public void run() {

        int deletedRows = this.rumsd.deletePayloadsFromMessagesOlderThan(this.ttl);
        if (deletedRows > 0) {
            LOG.warn("Attachments of undownloaded messages deleted: " + deletedRows);
        }

        deletedRows = this.rumsd.deletePayloadsFromDownloadedMessages(this.downloadedTtl);
        if (deletedRows > 0) {
            LOG.debug("Attachments of downloaded messages deleted: " + deletedRows);
        }
    }

}