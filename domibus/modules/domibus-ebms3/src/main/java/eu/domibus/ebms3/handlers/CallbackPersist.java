package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgIdCallback;
import eu.domibus.ebms3.persistent.MsgIdCallbackDAO;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

/**
 * This handler writes a MsgIdCallback object to the database when the outgoing
 * UserMessage is the first leg of a Two-Way/Push-And-Push or a
 * Two-Way/Push-And-Pull MEP and the callbackClass property of MsgInfoSet is not
 * null.
 *
 * @author Hamid Ben Malek
 */
public class CallbackPersist extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(CallbackPersist.class);
    private final MsgIdCallbackDAO mid = new MsgIdCallbackDAO();

    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.OUT_FLOW) {
            return InvocationResponse.CONTINUE;
        }
        if (msgCtx.isServerSide()) {
            return InvocationResponse.CONTINUE;
        }

        final MsgInfoSet mis = (MsgInfoSet) msgCtx.getProperty(Constants.MESSAGE_INFO_SET);
        if (mis == null) {
            return InvocationResponse.CONTINUE;
        }
        final String callbackClass = mis.getCallbackClass();
        if ((callbackClass == null) || "".equals(callbackClass.trim())) {
            CallbackPersist.LOG.debug(this.logPrefix +
                                      "Outgoing message has no callback, therefore MsgIdCallback will not be saved to database");
            return InvocationResponse.CONTINUE;
        }
        final String pmode = mis.getPmode();
        final int legNumber = mis.getLegNumber();
        // get the pmode mep, and proceed only if the mep is either
        // two-way/push-and-push with leg = 1 or mep is
        // two-way/push-and-pull with leg = 1

        final String mep = Configuration.getMep(mis.getPmode());

        if ((!mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PUSH) || (legNumber != 1)) &&
            (!mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL) || (legNumber != 1))) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement userMessage = EbUtil.getUserMessage(msgCtx);
        if (userMessage == null) {
            return InvocationResponse.CONTINUE;
        }

        if (CallbackPersist.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }

        final String messageId = XMLUtil.getGrandChildValue(userMessage, Constants.MESSAGE_ID);
        if ((messageId == null) || "".equals(messageId.trim())) {
            return InvocationResponse.CONTINUE;
        }

        final MsgIdCallback micb = new MsgIdCallback(messageId, pmode, legNumber, callbackClass);
        this.mid.persist(micb);
        CallbackPersist.LOG.debug(this.logPrefix + " saved MsgIdCallback in database");
        return InvocationResponse.CONTINUE;
    }
}