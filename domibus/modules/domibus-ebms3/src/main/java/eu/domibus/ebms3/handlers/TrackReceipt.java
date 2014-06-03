package eu.domibus.ebms3.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.persistent.Attachment;
import eu.domibus.common.util.FileUtil;
import eu.domibus.common.util.WSUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.*;
import eu.domibus.ebms3.submit.MsgInfoSet;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.util.Date;
import java.util.List;

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
    private static final Logger log = Logger.getLogger(TrackReceipt.class.getName());
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
        final String logPrefix = log.isDebugEnabled() ? WSUtil.logPrefix(msgCtx) : "";
        final TrackReceiptInfo trackReceiptInfo = compileTrackReceiptInfo(msgCtx);
        if (trackReceiptInfo == null) {
            log.info(logPrefix +
                     "The outgoing message does not require receipts," +
                     " therefore not tracking will be saved to database");
            return InvocationResponse.CONTINUE;
        }


        trackReceipt(trackReceiptInfo.messageId, msgCtx.getTo().getAddress(), trackReceiptInfo.pmodeName);
        log.info(logPrefix + " saved tracking info for outgoing request");

        return InvocationResponse.CONTINUE;
    }


    /**
     * Keep the attachments for retransmissions.
     */
    @Override
    public void flowComplete(final MessageContext msgCtx) {
        final TrackReceiptInfo trackReceiptInfo = compileTrackReceiptInfo(msgCtx);
        if (trackReceiptInfo == null) {
            return;
        }

        keepAttachments(msgCtx, trackReceiptInfo.messageId);
    }


    /**
     * Compile the information needed to process the message.
     *
     * @param msgCtx context of the message
     * @return TrackReceiptInfo for the message
     *         or {@code null} if this message shall not be processed by this handler.
     * @see TrackReceiptInfo
     */
    private TrackReceiptInfo compileTrackReceiptInfo(final MessageContext msgCtx) {
        if (msgCtx.getFLOW() != MessageContext.OUT_FLOW || msgCtx.isServerSide()) {
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
        return thisRequestLeg.getAs4Receipt() == null ? null : trackReceiptInfo;
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
        ReceiptTracking tracker = rtd.getReceiptTrackerForUserMsg(messageId);
        if (tracker == null) {
            // No tracking info, so this is first transmission of message => create tracker
            tracker = new ReceiptTracking();
            tracker.setMessageId(messageId);
            tracker.setStatus(ReceiptTracking.STATUS_IN_PROCESS);
            tracker.setToURL(receiptToAddress);
            tracker.setPmode(pmodeName);
            tracker.setRetries(0);
            rtd.persist(tracker);
        } else {
            tracker.setRetries(tracker.getRetries() + 1);
        }
        tracker.setLastTransmission(lastTransmission);
        tracker.addAttempt(receiptTrackingAttempt);
        rtd.update(tracker);
    }


    /**
     * Move message payloads to a new folder and update database.
     * From the new folder, the payloads can be re-read for retransmissions.
     *
     * @param msgCtx    message context with references to all the attachments of the message
     * @param messageID the ID of the message
     */
    private void keepAttachments(final MessageContext msgCtx, final String messageID) {
        @SuppressWarnings("unchecked")
        final List<UserMsgToPush> messages = umd.findByMessageId(messageID);
        for (final UserMsgToPush message : messages) {
            int keptPayloadFiles = 0;
            for (final Attachment attachment : message.getAttachments()) {
                final String filePath = attachment.getFilePath();
                final File keptPayloadFile = keepAttachedFile(filePath);
                if (keptPayloadFile == null) {
                    // The payload file has already been moved.
                    // There is no need to update the database table.
                    continue;
                }
                // Update the pointer to the payload file in the database.
                attachment.setFilePath(keptPayloadFile.getPath());
                // Replace the file handler to access the moved file.
                final String contentID = attachment.getContentID();
                final FileDataSource fileDataSource = new FileDataSource(keptPayloadFile);
                final DataHandler newDataHandler = new DataHandler(fileDataSource);
                fileDataSource.setFileTypeMap(FileUtil.getMimeTypes());
                msgCtx.addAttachment(contentID, newDataHandler);
                ++keptPayloadFiles;
            }
            if (keptPayloadFiles > 0) {
                // Save all modified attachments of the message
                umd.update(message);
            }
        }
    }


    /**
     * Move a payload file to a new folder.
     * The file can be re-read for retransmissions from the new folder.
     *
     * @param filePath name of the file to move
     * @return a File if it has been moved or {@code null} if the file is already there
     */
    private static File keepAttachedFile(final String filePath) {
        final int index = filePath.lastIndexOf(File.separator);
        final String oldFolderName = filePath.substring(0, index);
        final String fileName = filePath.substring(index);

        // Only move files that reside in the temporary folder.
        // The backend interface stores payload files there.
        final String tempFolder = System.getProperty("java.io.tmpdir");
        if (!filePath.startsWith(tempFolder)) {
            return null;
        }

        // Do not move the file if it has already been moved.
        if (oldFolderName.endsWith(NEW_FOLDER_SUFFIX)) {
            return null;
        }

        final String newFolderName = oldFolderName + NEW_FOLDER_SUFFIX;
        new File(newFolderName).mkdirs();
        final String newFilePath = newFolderName + fileName;
        final File newFile = new File(newFilePath);
        return new File(filePath).renameTo(newFile) ? newFile : null;
    }
}