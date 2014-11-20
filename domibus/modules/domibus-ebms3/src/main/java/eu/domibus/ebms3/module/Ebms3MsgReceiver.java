package eu.domibus.ebms3.module;

import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.consumers.EbConsumer;
import eu.domibus.ebms3.persistent.MsgInfo;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.receivers.AbstractMessageReceiver;
import org.apache.axis2.util.MessageContextBuilder;
import org.apache.log4j.Logger;


/**
 * @author Hamid Ben Malek
 */
public class Ebms3MsgReceiver extends AbstractMessageReceiver {
    // private static final Log log =
    // LogFactory.getLog(Ebms3MsgReceiver.class.getName());
    private static final Logger log = Logger.getLogger(Ebms3MsgReceiver.class.getName());

    @Override
    public final void receive(final MessageContext msgContext) throws AxisFault {
        final MessageContext outMsgContext = MessageContextBuilder.createOutMessageContext(msgContext);
        final SOAPFactory factory = (SOAPFactory) msgContext.getEnvelope().getOMFactory();
        outMsgContext.setEnvelope(factory.getDefaultEnvelope());
        outMsgContext.getOperationContext().addMessageContext(outMsgContext);

        final OMElement ebMess = (OMElement) msgContext.getProperty(Constants.IN_MESSAGING);
        if (ebMess != null) {
            msgContext.getEnvelope().getHeader().addChild(ebMess);
        }

        final ThreadContextDescriptor tc = this.setThreadContext(msgContext);
        boolean sendOut = false;
        try {
            final PMode pmode = (PMode) msgContext.getProperty(Constants.IN_PMODE);
            if (pmode == null) {
                Ebms3MsgReceiver.log.debug("Received message does not have a corresponding PMode");
                return;
            }
            final Boolean ignoreThisMessage = (Boolean) msgContext.getProperty(Constants.DO_NOT_DELIVER);
            final boolean doInvokeBusiness = (ignoreThisMessage == null) || !ignoreThisMessage.booleanValue();
            final String mep = pmode.getMep(); // getMep(msgContext);
            sendOut = this.handle(msgContext, outMsgContext, mep, doInvokeBusiness);
        } finally {
            this.restoreThreadContext(tc);
        }

        if (sendOut) {
            // AxisEngine engine = new
            // AxisEngine(msgContext.getConfigurationContext());
            // engine.send(outMsgContext);
            this.replicateState(msgContext);
            AxisEngine.send(outMsgContext);
        }
    }

    /*
     * private String getMep(MessageContext msgCtx) { return
     * EbUtil.getMep(msgCtx); }
     */
    private boolean handle(final MessageContext msgCtx, final MessageContext outMsgContext, final String mep,
                           final boolean doInvokeBusiness) throws AxisFault {
        Ebms3MsgReceiver.log.debug("Ebms3MsgReceiver::handle() is called");

        final Object outObject = this.getTheImplementationObject(outMsgContext);
        if (!(outObject instanceof EbConsumer)) {
            throw new AxisFault(
                    "The service class " + outObject + " does not implement the " + EbConsumer.class.getName() +
                    " interface");
        }
        final EbConsumer consumer = (EbConsumer) outObject;

        MsgInfo msgInfo = // EbUtil.createMsgInfo(msgCtx);
                (MsgInfo) msgCtx.getProperty(Constants.IN_MSG_INFO);
        if (msgInfo == null) {
            msgInfo = EbUtil.getMsgInfo();
        }
        Ebms3MsgReceiver.log.debug("Ebms3MsgReceiver::handle(): mep is " + mep);

        // check if need to send receipt or ack on back channel
        final boolean expectReceipt = Constants.getProperty(msgCtx, eu.domibus.common.Constants.EXPECT_RECEIPT, false);
        final boolean expectAck = Constants.getProperty(msgCtx, eu.domibus.common.Constants.EXPECT_ACK, false);

        if (mep.equals(Constants.ONE_WAY_PULL)) {
            if (doInvokeBusiness) {
                consumer.pull();
            }
            return true;
        }
        if (mep.equals(Constants.ONE_WAY_PUSH)) {
            if (doInvokeBusiness) {
                consumer.push();
            }
            return expectReceipt || expectAck;
        } else if (mep.equals(Constants.TWO_WAY_SYNC)) {
            if (doInvokeBusiness) {
                consumer.push();
            }
            return true;
        } else if (mep.equals(Constants.TWO_WAY_PUSH_AND_PUSH)) {
            if (doInvokeBusiness) {
                consumer.push();
            }
            // need to store the outMsgContext in the database
            // to be pushed out later by a background thread...

            return expectReceipt || expectAck;
        } else if (mep.equals(Constants.TWO_WAY_PUSH_AND_PULL)) {
            if (msgInfo.getService() != null) {
                if (doInvokeBusiness) {
                    consumer.push();
                }
                // need to store the outMsgContext in the database
                // to be pulled out later...
                return expectReceipt || expectAck;
            } else {
                // normally we should not be here, as the pulled message
                // was already store previously in the database and ready
                // for pulling
                if (doInvokeBusiness) {
                    consumer.pull();
                }
                return true;
            }
        } else if (mep.equals(Constants.TWO_WAY_PULL_AND_PUSH)) {
            if (msgInfo.getService() != null) {
                if (doInvokeBusiness) {
                    consumer.push();
                }
                return expectReceipt || expectAck;
            } else {
                if (doInvokeBusiness) {
                    consumer.pull();
                }
                return true;
            }
        } else if (mep.equals(Constants.TWO_WAY_PULL_AND_Pull)) {
            if (doInvokeBusiness) {
                consumer.pull();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void invokeBusinessLogic(final MessageContext msgContext) throws AxisFault {
    }

}