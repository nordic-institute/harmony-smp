package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.As4Receipt;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.ReceiptData;
import eu.domibus.ebms3.persistent.ReceiptDataDAO;
import eu.domibus.ebms3.workers.impl.ReceiptSender;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This handler runs only during the <b>InFlow</b> (client and server side), and
 * its job is to generate an AS4 Receipt when the incoming message has
 * a P-Mode that asks for AS4 receipts.
 * <p/>
 * <p>If the receipt is expected to be sent out on the back channel (as a response),
 * the receipt is not saved to database, but it is only placed on the context.
 * <br>If, however the receipt is expected to be sent out as a callback, then the
 * receipt is saved to database.
 * <p/>
 * The content of the Receipt element is specified in section 5.1.8 of the AS4
 * profile:
 * If incoming message is signed, a NRR will be created using the ds:Reference
 * elements of the received messages.
 * Otherwise the MessageId of the message to acknowledge is used in a
 * MessagePartIdentifier element.
 *
 * @author Hamid Ben Malek
 * @author Sander Fieten
 */
public class ReceiptGen extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(ReceiptGen.class.getName());
    private final ReceiptDataDAO rdd = new ReceiptDataDAO();

    private static ReceiptData generateReceiptData(final String messageId, final OMElement header,
                                                   final boolean nrrMode) {
        // The receipt contains a list of references to the received message (see class doc)

        final List<OMElement> signatures = XMLUtil.getGrandChildrenNameNS(header, "Signature", Constants.dsigNS);

        final boolean prepareNRinformation = nrrMode && (signatures != null);

        if (prepareNRinformation) {
            // Prepare a receipt of type "non-repudiation of receipt":
            // The receipt must contain a copy of all ds:Reference elements of all ds:Signature elements
            // from the user message to acknowledge.
            final List<OMElement> allSignatureReferenceElements = new ArrayList<OMElement>();
            for (final OMElement signatureElement : signatures) {
                final List<OMElement> signatureReferenceElements =
                        XMLUtil.getGrandChildrenNameNS(signatureElement, "Reference", Constants.dsigNS);
                if (signatureReferenceElements != null) {
                    for (final OMElement signatureReferenceElement : signatureReferenceElements) {
                        allSignatureReferenceElements.add(signatureReferenceElement);
                    }
                }
            }
            if (ReceiptGen.LOG.isDebugEnabled()) {
                ReceiptGen.LOG.debug("Generated NRR receipt for signed message; MessageId=" + messageId +
                                     " containing " + allSignatureReferenceElements.size() +
                                     " Signature/Reference elements");
            }
            return new ReceiptData(messageId, allSignatureReferenceElements);
        } else {
            // Prepare a receipt of type "reception awareness":
            // The receipt must contain a copy of the eb:UserMessage element
            // from the message to acknowledge (see section 5.1.8 of the AS4 profile).
            final OMElement userMessage = XMLUtil.getGrandChildNameNS(header, Constants.USER_MESSAGE, Constants.NS);

            if (!signatures.isEmpty()) {
                ReceiptGen.LOG.debug("Generated RA receipt for signed message; MessageId=" + messageId);
            } else {
                ReceiptGen.LOG.debug("Generated RA receipt for unsigned message; MessageId=" + messageId);

            }
            return new ReceiptData(messageId, userMessage);
        }
    }

    /**
     * Create receipt data and prepare to send a receipt depending on the reply pattern.
     * Do not prepare any receipt data if AS4 receipts are switched off.
     * <p/>
     * Every ReceiptData object will be saved to the database table "Receipts".
     * <dl>
     * <dt>ReplyPattern = Response</dt>
     * <dd>The ReceiptAppender adds receipts to the back-channel of the message.</dd>
     * <dt>ReplyPattern = Callback</dt>
     * <dd>The ReceiptSender worker transmits receipts by means of distinct messages.</dd>
     * </dl>
     *
     * @see ReceiptData
     * @see ReceiptSender
     * @see ReceiptAppender
     */
    @Override
    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        // Receipt will only be generated for ebMS UserMessages
        final Boolean isUserMessage = (Boolean) msgCtx.getProperty(Constants.IS_USERMESSAGE);
        if ((isUserMessage == null) || !isUserMessage.booleanValue()) {
            return InvocationResponse.CONTINUE;
        }

        // Existence of the "As4Receipt" element inside the "Leg" element enables receipts.
        final PMode pmode = (PMode) msgCtx.getProperty(Constants.IN_PMODE);
        final Leg leg = pmode.getLeg((Integer) msgCtx.getProperty(Constants.IN_LEG));
        final As4Receipt as4Receipt = leg.getAs4Receipt();
        if (as4Receipt == null) {
            return InvocationResponse.CONTINUE;
        }

        // Save and send a receipt for each incoming user message, no matter if it is a duplicate.
        final String refToMessageId = ((MsgInfo) msgCtx.getProperty(Constants.MESSAGE_INFO)).getMessageId();
        final ReceiptData receiptData = ReceiptGen
                .generateReceiptData(refToMessageId, msgCtx.getEnvelope().getHeader(), as4Receipt.isNonRepudiation());
        receiptData.setPmode(pmode.getName());
        this.saveReceiptData(msgCtx, refToMessageId, receiptData, as4Receipt.getReceiptTo());

        return InvocationResponse.CONTINUE;
    }

    /**
     * Save the receipt data to the database.
     * Additionally, save the receipt data to the message context
     * if the the response reply pattern is configured.
     *
     * @param msgCtx         context of the message
     * @param refToMessageId ID of the incoming user message
     * @param receiptData    receipt data to save
     */
    private void saveReceiptData(final MessageContext msgCtx, final String refToMessageId,
                                 final ReceiptData receiptData, final String sendReceiptToURL) {
        final String logPrefix = LOG.isDebugEnabled() ? WSUtil.logPrefix(msgCtx) : "";
        final Boolean responseReplyPattern = (Boolean) msgCtx.getProperty(eu.domibus.common.Constants.EXPECT_RECEIPT);
        if (responseReplyPattern == null || responseReplyPattern.booleanValue()) {

            // Prepare a response reply for the ReceiptAppender handler.
            ReceiptGen.LOG.debug("Generating Response receipt data for " + refToMessageId);
            receiptData.setReplyPatternResponse();
            msgCtx.setProperty(Constants.RECEIPT, receiptData);
            ReceiptGen.LOG.debug(logPrefix +
                                 "A response receipt was created and placed in the message context" +
                                 " so that it will be included on the back channel");
        } else {

            // Prepare a callback reply for the ReceiptSender worker.
            receiptData.setReplyPatternCallback();
            receiptData.setToURL(sendReceiptToURL);
        }
        this.rdd.persist(receiptData);
        ReceiptGen.LOG.debug(logPrefix +
                             "A " + receiptData.getReplyPattern() + " receipt was generated and stored in database");
    }

}