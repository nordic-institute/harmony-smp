package eu.domibus.ebms3.handlers;

import eu.domibus.common.soap.Element;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.packaging.Messaging;
import eu.domibus.ebms3.persistent.ReceiptData;
import eu.domibus.ebms3.persistent.ReceiptDataDAO;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

/**
 * This handler runs only on the server side during the OUT_FLOW, and its
 * job is to append a receipt to the outgoing message on the back channel
 * when the initial request expects a receipt to be sent as a "Response".
 * If no receipt is found in the message context of the previous incoming
 * request message, then no receipt will be appended in the outgoing response
 *
 * @author Hamid Ben Malek
 */
public class ReceiptAppender extends AbstractHandler {

    private final ReceiptDataDAO rdd = new ReceiptDataDAO();
    private static final Logger LOG = Logger.getLogger(ReceiptAppender.class.getName());
    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.OUT_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        if (ReceiptAppender.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }

        final ReceiptData receipt = this.getReceiptData(msgCtx);
        if (receipt == null) {
            ReceiptAppender.LOG.info(this.logPrefix + " No ReceiptData found");
            return InvocationResponse.CONTINUE;
        }
        ReceiptAppender.LOG.info(this.logPrefix + " ReceiptData found");

        // Now add the Receipt Signal to the message
        OMElement messaging = EbUtil.getMessaging(msgCtx);
        if (messaging == null) {
            new Messaging(msgCtx.getEnvelope());
            messaging = EbUtil.getMessaging(msgCtx);
        }
        final OMElement signalMsg = EbUtil.getSignalMessage(msgCtx);
        if (signalMsg == null) {
            messaging.addChild(receipt.getSignalMessage().getElement());
        } else {
        /*
         * There's already another Signal in the message. Check whether it is for
         * a specific UserMessage by checking the RefToMessageId. When empty the
         * Receipt can be safely piggybacked to this Signal.
         */
            final OMElement refToMsgId =
                    XMLUtil.getGrandChildNameNS(signalMsg, Constants.REF_TO_MESSAGE_ID, Constants.NS);
            if (refToMsgId == null) {
                // Add the Receipt element
                signalMsg.addChild(receipt.getReceipt().getElement());
                // and the RefToMessageId element
                final Element refToMessageIdElement =
                        new Element(Constants.REF_TO_MESSAGE_ID, Constants.NS, Constants.PREFIX);
                refToMessageIdElement.setText(receipt.getRefToMessageId());
                signalMsg.getFirstElement().addChild(refToMessageIdElement.getElement());
            }

        }
        //signalMsg.addChild( receiptData.getReceipt() );
        ReceiptAppender.LOG.debug(this.logPrefix + " receipt appended to outgoing message on the back channel: ");
        XMLUtil.debug(ReceiptAppender.LOG, this.logPrefix, msgCtx.getEnvelope().getHeader());

        return InvocationResponse.CONTINUE;
    }

    /**
     * Fetch any receipt data from the message context (push leg) or any receipt
     * data that shall be piggybacked (pull leg).
     *
     * @param msgCtx the context of the message that may containts receipt data
     * @return the data for the receipt to send.
     * @see ReceiptData
     */
    private ReceiptData getReceiptData(final MessageContext msgCtx) {
        if (msgCtx.isServerSide()) {
            final ReceiptData receipt = (ReceiptData) WSUtil.getPropertyFromInMsgCtx(msgCtx, Constants.RECEIPT);
            return ((receipt != null) && receipt.isReplyPatternResponse()) ? receipt : null;
        } else {
            final MsgInfoSet mis = (MsgInfoSet) msgCtx.getProperty(Constants.MESSAGE_INFO_SET);
            return this.rdd.getNextReceiptForPMode(mis.getPmode());
        }
    }


    /**
     * Set the status of the receipt to <code>sent</code> if no error occurred
     * on sending the receipt.
     *
     * @param msgCtx message context with or without a receipt
     */
    @Override
    public void flowComplete(final MessageContext msgCtx) {
        if (msgCtx.getFLOW() != MessageContext.OUT_FLOW) {
            return;
        }
        if (msgCtx.getFailureReason() == null) {
            final ReceiptData receipt = this.getReceiptData(msgCtx);
            if (receipt == null) {
                return;
            }

            final ReceiptData storedReceipt = this.rdd.findByMessageId(receipt.getRefToMessageId());
            if (storedReceipt == null) {
                return;
            }

            receipt.setSent(true);
            storedReceipt.setSent(true);
            this.rdd.update(storedReceipt);

            ReceiptAppender.LOG.debug("Completed sending " + receipt.getReplyPattern() +
                                      " receipt for messageId=" + receipt.getRefToMessageId());
        }
    }

}