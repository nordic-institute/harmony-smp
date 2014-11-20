package eu.domibus.ebms3.workers.impl;

import eu.domibus.ebms3.config.As4Reliability;
import eu.domibus.ebms3.module.AS4ReliabilityUtil;
import eu.domibus.ebms3.persistent.ReceiptTracking;
import eu.domibus.ebms3.persistent.ReceiptTrackingDAO;
import eu.domibus.ebms3.persistent.UserMsgToPushDAO;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Random;

/**
 * <p>
 * This worker is responsible for the retransmission of user messages that did
 * not receive an AS4 receipt as expected.
 * </p>
 * <p>
 * Whether a user message must be retransmitted is configured in the PMode
 * within the <code>As4Reliability</code> element. With the attributes
 * <code>maxRetries</code> and <code>interval</code> the number of
 * retransmissions and the interval between is specified.
 * </p>
 *
 * @author safi
 * @see As4Reliability
 * @see As4Reliability#getMaxRetries()
 * @see As4Reliability#getInterval()
 */
public class AS4RetransmissionWorker implements Runnable {
    //  private static final Log log = LogFactory.getLog(AS4RetransmissionWorker.class.getName());
    private static final Logger LOG = Logger.getLogger(AS4RetransmissionWorker.class);
    private final UserMsgToPushDAO umd = new UserMsgToPushDAO();
    private final ReceiptTrackingDAO rtd = new ReceiptTrackingDAO();
    private final Random random;

    public AS4RetransmissionWorker() {
        this.random = new Random();
    }

    @Override
    public void run() {
        // Get all the message id's for unacknowlegded messages
        //      log.debug("Get all messages waiting for a Receipt");
        final Collection<ReceiptTracking> waitingReceiptTrackings = this.rtd.getAllWaitingForReceipt();
        if (AS4RetransmissionWorker.LOG.isInfoEnabled()) {
            final int messages = waitingReceiptTrackings.size();
            switch (messages) {
                case 0:
                    AS4RetransmissionWorker.LOG.debug("No message is waiting for a receipt");
                    break;
                case 1:
                    AS4RetransmissionWorker.LOG.info("One message is waiting for a receipt");
                    break;
                default:
                    AS4RetransmissionWorker.LOG.info(messages + " messages are waiting for a receipt");
                    break;
            }
        }

        // For each message check if it should be retransmitted or not
        for (final ReceiptTracking receiptTracking : waitingReceiptTrackings) {
          /* To be able to check if a message must be retransmitted we need to
           * get the AS4 reliability configuration of the message. */
            final As4Reliability relConfig = AS4ReliabilityUtil.getReliabilityConfig(receiptTracking.getPmode());

            // If there is reliability info available, check whether retransmission is required
            final long now = System.currentTimeMillis();
            int intervalInMilliseconds = relConfig.getInterval() * 1000;
            if (relConfig.isExponentialBackoff()) {
                intervalInMilliseconds *= 1 << receiptTracking.getRetries();
            }
            if (relConfig.isRandomize()) {
                intervalInMilliseconds = this.random.nextInt(intervalInMilliseconds + 1);
            }
            if ((relConfig != null) &&
                (receiptTracking.getRetries() < relConfig.getMaxRetries()) &&
                ((now - receiptTracking.getLastTransmission().getTime()) >= intervalInMilliseconds)) {
                // The message should be retransmitted now.
                AS4RetransmissionWorker.LOG
                        .debug("Triggering message for retransmit, messageId=" + receiptTracking.getMessageId());
                this.umd.setRetransmit(receiptTracking.getMessageId());
            }
        }
    }

}
