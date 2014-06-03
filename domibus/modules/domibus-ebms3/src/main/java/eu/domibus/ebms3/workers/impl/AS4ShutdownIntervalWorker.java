package eu.domibus.ebms3.workers.impl;

import org.apache.log4j.Logger;
import eu.domibus.ebms3.config.As4Reliability;
import eu.domibus.ebms3.module.AS4ReliabilityUtil;
import eu.domibus.ebms3.persistent.ReceiptTracking;
import eu.domibus.ebms3.persistent.ReceiptTrackingDAO;

import java.util.Collection;

/**
 * <p>This worker is responsible for detecting finally missing receipts.</p>
 * <p>It sets the <code>status<code> of every <code>ReceiptTracking</code> from
 * <code>STATUS_IN_PROCESS</code> to <code>STATUS_NO_RECEIPT</code>
 * if no receipt has been received past the final shutdown interval.</p>
 *
 * @author Thorsten Niedzwetzki
 * @see ReceiptTracking
 * @see ReceiptTracking#getStatus()
 * @see ReceiptTracking#STATUS_IN_PROCESS
 * @see ReceiptTracking#STATUS_NO_RECEIPT
 */
public class AS4ShutdownIntervalWorker implements Runnable {
    private static final Logger log = Logger.getLogger(AS4ShutdownIntervalWorker.class);
    private final ReceiptTrackingDAO rtd = new ReceiptTrackingDAO();

    @Override
    public void run() {
        final Collection<ReceiptTracking> waitingReceiptTrackings = rtd.getAllWaitingForReceipt();
        if (log.isInfoEnabled()) {
            final int messages = waitingReceiptTrackings.size();
            switch (messages) {
                case 0:
                    log.debug("No message is waiting for a receipt");
                    break;
                case 1:
                    log.info("One message is waiting for a receipt");
                    break;
                default:
                    log.info(messages + " messages are waiting for a receipt");
                    break;
            }
        }

        final long now = System.currentTimeMillis();

        // Check for finally past-due receipts.
        for (final ReceiptTracking receiptTracking : waitingReceiptTrackings) {
            final String pModeName = receiptTracking.getPmode();
            final String messageID = receiptTracking.getMessageId();

            final As4Reliability as4Reliability = AS4ReliabilityUtil.getReliabilityConfig(pModeName);
            if (as4Reliability == null) {
                log.error("No As4Reliability configuration found. " +
                          "PMode=" + pModeName + "; messageID=" + messageID);
                continue;
            }

            // Do not finalize receipt trackings if there are retransmissions pending.
            if (receiptTracking.getRetries() < as4Reliability.getMaxRetries()) {
                continue;
            }

            // Finalize receipt tracking if the @shutdown interval is passed.
            int shutdownInMilliseconds = as4Reliability.getShutdown() * 1000;
            // Take the final @interval value as a default if no @shutdown interval is given.
            if (shutdownInMilliseconds <= 0) {
                shutdownInMilliseconds = as4Reliability.getInterval() * 1000;
                if (as4Reliability.isExponentialBackoff()) {
                    shutdownInMilliseconds *= 1 << (receiptTracking.getRetries() - 1);
                }
            }
            if (now - receiptTracking.getLastTransmission().getTime() >= shutdownInMilliseconds) {
                log.info("No receipt received after final (re)transmission. " +
                         "PMode=" + pModeName + "; shutdown=" + shutdownInMilliseconds + " seconds; messageID=" +
                         messageID);
                rtd.updateTrackingStatus(ReceiptTracking.STATUS_NO_RECEIPT, messageID);
                AS4ReliabilityUtil.removeAttachedFiles(messageID);
            }
        }
    }

}
