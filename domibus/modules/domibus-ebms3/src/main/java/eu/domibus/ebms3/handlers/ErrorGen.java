package eu.domibus.ebms3.handlers;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.util.MessageContextBuilder;
import eu.domibus.common.soap.Element;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.ErrorAtReceiver;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.packaging.Error;
import eu.domibus.ebms3.packaging.MessageInfo;
import eu.domibus.ebms3.packaging.Messaging;
import eu.domibus.ebms3.packaging.SignalMessage;

/**
 * Generate EBMS3 error messages if there is any error on the inflow of a user message.
 */
public class ErrorGen extends AbstractHandler {


    /**
     * Generate an EBMS3 error message and attach it to the back-channel of the connection.
     * <p/>
     * Generate an EBMS3 error message only if ErrorAtReceiver/@notifiyProducer is switched on.
     *
     * @see ErrorAtReceiver
     * @see ErrorAtReceiver#isNotifiyProducer()
     */
    @Override
    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.OUT_FAULT_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        // Generate the eb:Error element
        final String refToMessageId = msgCtx.getRelatesTo().getValue();

        if (!isNotifyProducer(refToMessageId)) {
            return InvocationResponse.CONTINUE;
        }

        // TODO: Find out what exactly kind of EBMS3 or security error occured
        final Error error = Error.getOtherError(refToMessageId);
        final Exception failureReason = msgCtx.getFailureReason();
        if (failureReason != null) {
            final Element errorDetailElement = error.addElement("ErrorDetail", Constants.PREFIX);
            errorDetailElement.setText(failureReason.getMessage());
        }
        addErrorToMessage(error, msgCtx);

        return InvocationResponse.CONTINUE;
    }


    /**
     * Add an error message to a message.
     * A signal message will be added to the message if there is no signal message yet.
     * An error message will only be added if the message contains no signal message
     * or if the signal message references the same user message or no user message.
     *
     * @param error  error to add to the message
     * @param msgCtx message context of the message to add the error message to
     */
    private void addErrorToMessage(final Error error, final MessageContext msgCtx) {

        // Message ID of the referenced user message or null.
        final String refToMessageId = error.getRefToMessageInError();

        // Create the eb:Messaging element if it does not exist.
        OMElement messaging = EbUtil.getMessaging(msgCtx);
        if (messaging == null) {
            new Messaging(msgCtx.getEnvelope());
            messaging = EbUtil.getMessaging(msgCtx);
        }

        // Create the eb:SignalMessage element if it does not exist
        // and append the eb:Error element to it.
        OMElement signalMessage = EbUtil.getSignalMessage(msgCtx);
        if (signalMessage == null) {
            signalMessage = new SignalMessage(new MessageInfo(null, refToMessageId), error).getElement();
            messaging.addChild(signalMessage);
        } else {
            // Check existing refToMessageId or add missing refToMessageId
            final OMElement existingRefToMessageId =
                    XMLUtil.getGrandChildNameNS(signalMessage, Constants.REF_TO_MESSAGE_ID, Constants.NS);
            if (existingRefToMessageId == null) {
                final Element refToMessageIdElement =
                        new Element(Constants.REF_TO_MESSAGE_ID, Constants.NS, Constants.PREFIX);
                refToMessageIdElement.setText(refToMessageId);
                signalMessage.getFirstElement().addChild(refToMessageIdElement.getElement());
                signalMessage.addChild(error.getElement());
            } else if (existingRefToMessageId.getText().equals(refToMessageId)) {
                signalMessage.addChild(error.getElement());
            }
        }

    }

    /**
     * Find out if the producer of the incoming message shall receive an EBMS3 error
     * message.  MSHs are bound to process EBMS3 error messages (mustUnderstand).
     * Therefore, generating EBMS3 error messages is off by default.
     *
     * @param messageId ID of the user message to find
     * @return notifyProducer setting for the message
     * @see ErrorAtReceiver#isNotifiyProducer()
     * @see MessageContextBuilder#createResponseMessageContext
     */
    private boolean isNotifyProducer(final String messageId) {

        // To get the PMode for a received message will not work
        // if there is an error on the inflow of the message.
        // TODO: Update to Axis2 1.6.2+ and read the MessageContext.IN_MESSAGE property
        final PMode pmode = EbUtil.getPModeForReceivedMessage(messageId);
        if (pmode == null) {
            return false;
        }

        // Use leg 1 for One-Way/Push messages.
        // TODO: Support other MEPs.
        final Leg leg = pmode.getLeg(1);
        if (leg == null) {
            return false;
        }

        final ErrorAtReceiver errorAtReceiver = leg.getErrorAtReceiver();
        if (errorAtReceiver == null) {
            return false;
        }

        return errorAtReceiver.isNotifiyProducer();
    }

}
