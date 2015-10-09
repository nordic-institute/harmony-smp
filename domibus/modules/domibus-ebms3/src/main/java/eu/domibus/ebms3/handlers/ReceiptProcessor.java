package eu.domibus.ebms3.handlers;

import eu.domibus.common.persistent.TempStoreDAO;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.As4Receipt;
import eu.domibus.ebms3.module.AS4ReliabilityUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.ReceiptTracking;
import eu.domibus.ebms3.persistent.ReceiptTrackingDAO;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * This handler runs both on the client and server side but only during the
 * IN_FLOW, and its job is to detect if there is any AS4 receipt present in the
 * headers and if so, update the database to mark a message as having received
 * a receipt
 *
 * @author Hamid Ben Malek
 */
public class ReceiptProcessor extends AbstractHandler {
    private static final Logger LOG = Logger.getLogger(ReceiptProcessor.class.getName());
    private final ReceiptTrackingDAO rtd = new ReceiptTrackingDAO();
    private final TempStoreDAO tsd = new TempStoreDAO();

    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        if (ReceiptProcessor.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }

        // Step 1) Find Receipt

        final OMElement signalMessage = EbUtil.getSignalMessage(msgCtx);
        if (signalMessage == null) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement receipt = XMLUtil.getFirstChildWithNameNS(signalMessage, Constants.RECEIPT, Constants.NS);
        if (receipt == null) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement messageInfo =
                XMLUtil.getFirstChildWithNameNS(signalMessage, Constants.MESSAGE_INFO, Constants.NS);
        if (messageInfo == null) {
            return InvocationResponse.CONTINUE;
        }

        final String refToMessageId =
                XMLUtil.getFirstChildWithNameNS(messageInfo, Constants.REF_TO_MESSAGE_ID, Constants.NS).getText();
        ReceiptProcessor.LOG.debug(this.logPrefix + " Received a receipt for messageId " + refToMessageId);

        // Step 2) Process receipt

        if (this.isAcceptableReceipt(receipt, refToMessageId)) {
            this.setReceiptTrackingStatus(refToMessageId, ReceiptTracking.STATUS_RECEIPT_RECEIVED, signalMessage);
        } else {
            this.setReceiptTrackingStatus(refToMessageId, ReceiptTracking.STATUS_UNRECOVERABLE_ERROR, signalMessage);
        }
        //TODO: remove attachments
        this.tsd.deleteAttachments(refToMessageId);

        return InvocationResponse.CONTINUE;
    }


    /**
     * Check if the receipt matches the stipulated type of receipt.
     *
     * @param receipt        the receipt to check
     * @param refToMessageId the ID of the user message
     * @return {@code true} if the receipt matches the stipulated type of receipt
     */
    private boolean isAcceptableReceipt(final OMElement receipt, final String refToMessageId) {
        final ReceiptTracking receiptTracking = this.rtd.getReceiptTrackerForUserMsg(refToMessageId);

        final String pmodeName = receiptTracking.getPmode();
        if ((pmodeName == null) || pmodeName.isEmpty()) {
            ReceiptProcessor.LOG.error("Unrecoverable Error: Empty PMode name in ReceiptTracking" +
                                       " for messageId=" + refToMessageId);
            return false;
        }

        final As4Receipt as4Receipt = AS4ReliabilityUtil.getReceiptConfig(pmodeName);
        if (as4Receipt == null) {
            ReceiptProcessor.LOG.error("Unrecoverable Error: No As4Receipt configuration" +
                                       " in PMode=" + pmodeName + " for messageId=" + refToMessageId);
            return false;
        }

        if (as4Receipt.isNonRepudiation()) {
            final OMElement nonRepudiationInformation =
                    XMLUtil.getFirstChildWithNameNS(receipt, Constants.NON_REPUDIATION_INFORMATION, Constants.ebbpNS);
            if (nonRepudiationInformation == null) {
                ReceiptProcessor.LOG.error("Unrecoverable Error: Cannot find " +
                                           Constants.NON_REPUDIATION_INFORMATION +
                                           " in Receipt for messageId=" + refToMessageId);
                return false;
            }
            // Found the NON_REPUDIATION_INFORMATION.
            return true;
        } else {
            final OMElement copyOfUserMessage =
                    XMLUtil.getFirstChildWithNameNS(receipt, Constants.USER_MESSAGE, Constants.NS);
            if (copyOfUserMessage == null) {
                ReceiptProcessor.LOG.error("Unrecoverable Error: Cannot find " +
                                           Constants.USER_MESSAGE +
                                           " in Receipt for messageId=" + refToMessageId);
                return false;
            }
            // Found the USER_MESSAGE.
            return true;
        }
    }


    /**
     * Modify the status of an in-process receipt.
     * The status of the receipt will not be modified if it is not in-process.
     *
     * @param signalMessage  the signal message to be saved (optional)
     * @param refToMessageId the user message ID
     * @param newStatus      the new status to take
     * @see ReceiptTracking#STATUS_IN_PROCESS
     */
    private void setReceiptTrackingStatus(final String refToMessageId, final String newStatus,
                                          final OMElement signalMessage) {
        final int updatedRows = this.rtd.updateTrackingStatus(newStatus, refToMessageId);
        if (updatedRows == 0) {
            ReceiptProcessor.LOG.info("Received out-of-order receipt" +
                                      " for messageId=" + refToMessageId);
        } else {
            ReceiptProcessor.LOG.debug("Received receipt" +
                                       " for messageId=" + refToMessageId);
            if (signalMessage != null) {
                this.rtd.setReceipt(refToMessageId, XMLUtil.toString(signalMessage), new Date());
            }
        }
    }

}
