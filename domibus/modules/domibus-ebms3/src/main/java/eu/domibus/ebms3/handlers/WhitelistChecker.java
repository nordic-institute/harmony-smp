package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.SenderWhitelist;
import eu.domibus.ebms3.persistent.SenderWhitelistDAO;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;


/**
 * First handler running during the <b>InFlow</b> and its job is to if the producer GW of the
 * incoming UserMessage is allowed. Check runs against database. See senderWhitelist
 * Created by nowos01 on 09.05.14.
 */
public class WhitelistChecker extends AbstractHandler {

    private static final Logger log = Logger.getLogger(WhitelistChecker.class);
    @Override
    public InvocationResponse invoke(MessageContext msgCtx) throws AxisFault {

        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        final SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            return InvocationResponse.CONTINUE;
        }

        if(!JNDIUtil.getBooleanEnvironmentParameter(Constants.ENABLE_WHITE_LIST)) {
            log.debug("WhitelistChecker is not activated");
            return InvocationResponse.CONTINUE;
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
        SenderWhitelistDAO senderWhitelistDAO = new SenderWhitelistDAO();
        SenderWhitelist senderWhitelist = new SenderWhitelist();
        String service = msgMetaData.getService();
        String action = msgMetaData.getAction();
        for(Party party:msgMetaData.getFromParties()){
            switch((int)senderWhitelistDAO.findWhitelistEntry(party.getPartyId(), party.getType(), service,
                                                                     action)){
                case 0:
                    log.error("GW is not allowed to receive messages from FromParty");
                    return null;
                case 1:
                    log.debug("FromParty is on Whitelist");
                    return InvocationResponse.CONTINUE;
                default:
                    log.debug("FromParty is on Whitelist");
                    log.warn("Please check your Whitelist configuration. Too many results for receiving message.");
                    return InvocationResponse.CONTINUE;
            }
        }
        return null;
    }
}
