package eu.domibus.ebms3.handlers;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;

/**
 * This handler runs during the <b>InFlow</b> and its job is to determine the P-Mode
 * that can handle the incoming UserMessage. The retrieved information is stored as
 * property {@link Constants.IN_PMODE} in the MessageContext.
 * <p/>
 * <p>Note that this handler only gets the P-Mode for UserMessage, for ebMS
 * SignalMessage the specific handlers that will handle the signal has to determine
 * the P-Mode (if necessary).
 *
 * @author Sander Fieten
 * @author Hamid Ben Malek
 */
public class PModeFinder extends AbstractHandler {

    private static final Logger log = Logger.getLogger(PModeFinder.class);
    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        final SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            return InvocationResponse.CONTINUE;
        }
        if (log.isDebugEnabled()) {
            logPrefix = WSUtil.logPrefix(msgCtx);
        }

        if (!EbUtil.isUserMessage(msgCtx)) {
            // This is not an UserMessage, set indicator in MessageContext
            msgCtx.setProperty(Constants.IS_USERMESSAGE, Boolean.FALSE);
            // and continue with the next handler
            return InvocationResponse.CONTINUE;
        }

        // indicate message contains an ebMS UserMessage
        msgCtx.setProperty(Constants.IS_USERMESSAGE, Boolean.TRUE);
        // and get the P-Mode for this UserMessage based on the meta data
        final MsgInfo msgMetaData = EbUtil.getMsgInfo(msgCtx);
        final Leg leg = Configuration.getLeg(msgMetaData);

        if (leg == null) {
            log.error("No P-Mode found for received UserMessage [" + msgMetaData.getMessageId() + "]");
        }

        msgCtx.setProperty(Constants.IN_PMODE, leg.getPmode());
        msgCtx.setProperty(Constants.IN_LEG, new Integer(leg.getNumber()));
        msgCtx.setProperty(Constants.MESSAGE_INFO, msgMetaData);

        if (leg != null && leg.getReceiptReply() != null &&
            leg.getReceiptReply().equalsIgnoreCase("Response")) {
            msgCtx.setProperty(eu.domibus.common.Constants.EXPECT_RECEIPT, true);
            log.info(logPrefix + "This incoming request message expects an AS4 Receipt on the back-channel");
        } else {
            log.info(logPrefix + "This incoming request message does not expect an AS4 Receipt on the back-channel");
            msgCtx.setProperty(eu.domibus.common.Constants.EXPECT_RECEIPT, false);
        }

        return InvocationResponse.CONTINUE;
    }

    /**
     * @deprecated
     */
    private static PMode getPModeFromServerSideReq(final MessageContext requestMsgCtx) {
        if (requestMsgCtx == null) {
            return null;
        }
        final SOAPHeader header = requestMsgCtx.getEnvelope().getHeader();
        if (header == null) {
            return null;
        }
        final PMode pmode;
        final String address = requestMsgCtx.getTo().getAddress();
        requestMsgCtx.setProperty(Constants.TO_ADDRESS, address);

        final OMElement pullReq = XMLUtil.getGrandChildNameNS(header, Constants.PULL_REQUEST, Constants.NS);
        if (pullReq != null) {
            final String mpc = XMLUtil.getAttributeValue(pullReq, "mpc");
            pmode = Configuration.matchPMode(mpc, address);
            if (pmode != null) {
                return pmode;
            }
        } else {
            final OMElement userMessage = XMLUtil.getGrandChildNameNS(header, Constants.USER_MESSAGE, Constants.NS);
            if (userMessage == null) {
                return null;
            }
            final String pm = XMLUtil.getGrandChildAttributeValue(userMessage, Constants.AGREEMENT_REF, "pmode");
            if (pm != null) {
                pmode = Configuration.getPMode(pm);
                return pmode;
            }

            MsgInfo mi = (MsgInfo) requestMsgCtx.getProperty(Constants.IN_MSG_INFO);
            if (mi == null) {
                mi = EbUtil.getMsgInfo();
                requestMsgCtx.setProperty(Constants.IN_MSG_INFO, mi);
            }
            pmode = Configuration.match(mi, address);
            if (pmode != null) {
                return pmode;
            }
        }
        return pmode;
    }
}