package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.WSUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.*;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * This handler runs only on the client side during the OUT_FLOW, and its
 * main job is to save to the database information that a given messageId
 * is being sent out (pushed) so that to keep track of which messages did not
 * get any receipts. A background thread will notify the producer if finds that
 * some these outgoing messaging did not get any receipts after some timeout.
 * <p/>
 * [2012-09-19/safi] Added correct processing of retransmissions
 *
 * @author Sander Fieten
 * @author Hamid Ben Malek
 */
public class TrackReceipt extends AbstractHandler {
    private static final Logger LOG = Logger.getLogger(TrackReceipt.class.getName());
    private final ReceiptTrackingDAO rtd = new ReceiptTrackingDAO();
    private final UserMsgToPushDAO umd = new UserMsgToPushDAO();
    private final ReceiptTrackingAttemptDAO rtad = new ReceiptTrackingAttemptDAO();

    /**
     * Name suffix for the keep-attachments folder.
     */
    private final static String NEW_FOLDER_SUFFIX = "-KeptForRetransmissions";

    /**
     * Compiled information needed to process this message.
     */
    private final class TrackReceiptInfo {
        public String messageId;
        public String pmodeName;
    }

    /**
     * <p>Tracks the attempt to send the message if successful.</p>
     * <p/>
     * <p>Disengages the domibus-reliability module from the message context
     * and keeps all attachments from being deleted.</p>
     * <p/>
     * <p>The domibus-reliability module that implements WS-Reliability
     * violates database constraints if the domibus-ebms3 module retransmits
     * messages.</p>
     */
    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        final String logPrefix = TrackReceipt.LOG.isDebugEnabled() ? WSUtil.logPrefix(msgCtx) : "";
        final TrackReceiptInfo trackReceiptInfo = this.compileTrackReceiptInfo(msgCtx);
        if (trackReceiptInfo == null) {
            TrackReceipt.LOG.debug(logPrefix +
                                   "The outgoing message does not require receipts," +
                                   " therefore not tracking will be saved to database");
            return InvocationResponse.CONTINUE;
        }


        this.trackReceipt(trackReceiptInfo.messageId, msgCtx.getTo().getAddress(), trackReceiptInfo.pmodeName);
        TrackReceipt.LOG.debug(logPrefix + " saved tracking info for outgoing request");

        return InvocationResponse.CONTINUE;
    }


    /**
     * Compile the information needed to process the message.
     *
     * @param msgCtx context of the message
     * @return TrackReceiptInfo for the message
     * or {@code null} if this message shall not be processed by this handler.
     * @see TrackReceiptInfo
     */
    private TrackReceiptInfo compileTrackReceiptInfo(final MessageContext msgCtx) {
        if ((msgCtx.getFLOW() != MessageContext.OUT_FLOW) || msgCtx.isServerSide()) {
            return null;
        }

        final TrackReceiptInfo trackReceiptInfo = new TrackReceiptInfo();

        trackReceiptInfo.messageId = EbUtil.getUserMessageId(msgCtx);
        if (trackReceiptInfo.messageId == null) {
            return null;
        }

        final MsgInfoSet mis = (MsgInfoSet) msgCtx.getProperty(Constants.MESSAGE_INFO_SET);
        if (mis == null) {
            return null;
        }
        final Leg thisRequestLeg = Configuration.getLeg(mis);
        if (thisRequestLeg == null) {
            return null;
        }
        trackReceiptInfo.pmodeName = thisRequestLeg.getPmode().getName();
        return (thisRequestLeg.getAs4Receipt() == null) ? null : trackReceiptInfo;
    }

    /**
     * Updates the receipt tracking of the message or creates a new receipt tracking
     * if this is the initial attempt to send the message
     *
     * @param messageId        the ID of the message, identical for every (re)transmission
     * @param receiptToAddress address where to send receipt to (callback reply pattern)
     * @param pmodeName        name of the associated PMode
     */
    private void trackReceipt(final String messageId, final String receiptToAddress, final String pmodeName) {

        // Account for this attempt to send the message.
        final Date lastTransmission = new Date();
        final ReceiptTrackingAttempt receiptTrackingAttempt = new ReceiptTrackingAttempt();
        receiptTrackingAttempt.setTransmission(lastTransmission);

        // Check if there is already a ReceiptTracking object for this UserMessage as this might
        // be a retransmission
        ReceiptTracking tracker = this.rtd.getReceiptTrackerForUserMsg(messageId);
        if (tracker == null) {
            // No tracking info, so this is first transmission of message => create tracker
            tracker = new ReceiptTracking();
            tracker.setMessageId(messageId);
            tracker.setStatus(ReceiptTracking.STATUS_IN_PROCESS);
            tracker.setToURL(receiptToAddress);
            tracker.setPmode(pmodeName);
            tracker.setRetries(0);
            this.rtd.persist(tracker);
        } else {
            tracker.setRetries(tracker.getRetries() + 1);
        }
        tracker.setLastTransmission(lastTransmission);
        tracker.addAttempt(receiptTrackingAttempt);
        this.rtd.update(tracker);
    }


}