package eu.domibus.ebms3.handlers;

import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.ReceivedUserMsgDAO;

import java.util.Collection;
import java.util.List;

/**
 * This handler stores a received UserMessage in the
 * "Received_UserMsg" table, so that the message can be consumed
 * later by the concerned parties, or be dispatched to a different
 * service by some background worker that listen on arrived UserMessages.
 * These received UserMessages could be received either by a push or a pull
 *
 * @author Hamid Ben Malek
 */
public class ReceivedUserMsgHandler extends AbstractHandler {

    private static final Logger log = Logger.getLogger(ReceivedUserMsgHandler.class.getName());
    private String logPrefix = "";
    private final ReceivedUserMsgDAO rdd = new ReceivedUserMsgDAO();

    /**
     * Default setting for duplicate detection and elimination
     */
    private static final boolean DUPLICATE_ELIMINATION_DEFAULT = false;

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        final SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            return InvocationResponse.CONTINUE;
        }

        if (!EbUtil.isUserMessage(msgCtx)) {
            return InvocationResponse.CONTINUE;
        }

        if (log.isDebugEnabled()) {
            logPrefix = WSUtil.logPrefix(msgCtx);
        }
        XMLUtil.debug(log, logPrefix, msgCtx.getEnvelope().getHeader());

        // Duplicate detection and elimination
        if (isDuplicateEliminationEnabled(msgCtx)) {
            final String messageId = getMessageId(msgCtx);
            if (messageId != null) {
                final List<ReceivedUserMsg> receivedUserMsg = rdd.findByMessageId(messageId);
                if (!receivedUserMsg.isEmpty()) {
                    log.debug(logPrefix + "Eliminating duplicate message with messageId=" + messageId);
                    msgCtx.setProperty(Constants.DO_NOT_DELIVER, true);
                    return InvocationResponse.CONTINUE;
                }
            }
        }

        MsgInfo msgInfo = (MsgInfo) msgCtx.getProperty(Constants.IN_MSG_INFO);
        if (msgInfo == null) {
            msgInfo = EbUtil.getMsgInfo();
            msgCtx.setProperty(Constants.IN_MSG_INFO, msgInfo);
        }
        final ReceivedUserMsg receivedUM = new ReceivedUserMsg(msgCtx, msgInfo);
        //@todo [2012-09-19/safi] This might raise an exception when the message is received twice because the msgId already exists (do something with Dup.Elim)
        rdd.persist(receivedUM);
        log.info(logPrefix + "Stored received UserMessage into Database");

        return InvocationResponse.CONTINUE;
    }

    /**
     * Extract the message ID from the message.
     *
     * @param msgCtx context containing the message
     * @return the concatenated direct text() children of the message ID element or {@code null} if not available
     */
    private String getMessageId(final MessageContext msgCtx) {
        final MsgInfo msgInfo = (MsgInfo) msgCtx.getProperty(Constants.MESSAGE_INFO);
        return (msgInfo == null) ? null : msgInfo.getMessageId();
    }

    /**
     * Decide whether duplicate elimination is enabled for this message context.
     *
     * @param msgCtx message context of the message
     * @return whether to detect and eliminate duplicate messages
     * @see #DUPLICATE_ELIMINATION_DEFAULT
     */
    private boolean isDuplicateEliminationEnabled(final MessageContext msgCtx) {
        final PMode pmode = (PMode) msgCtx.getProperty(Constants.IN_PMODE);
        if (pmode == null) {
            return DUPLICATE_ELIMINATION_DEFAULT;
        }
        final Binding binding = pmode.getBinding();
        if (binding == null) {
            return DUPLICATE_ELIMINATION_DEFAULT;
        }
        final MEP mep = binding.getMep();
        if (mep == null) {
            return DUPLICATE_ELIMINATION_DEFAULT;
        }
        final Collection<Leg> legs = mep.getLegs();
        if (legs == null) {
            return DUPLICATE_ELIMINATION_DEFAULT;
        }
        for (final Leg leg : legs) {
            if (leg == null) {
                continue;
            }
            final As4Receipt as4Receipt = leg.getAs4Receipt();
            if (as4Receipt == null) {
                return DUPLICATE_ELIMINATION_DEFAULT;
            }
            final As4Reliability as4Reliability = as4Receipt.getAs4Reliability();
            if (as4Reliability == null) {
                return DUPLICATE_ELIMINATION_DEFAULT;
            }
            return as4Reliability.isDuplicateElimination();
        }
        return DUPLICATE_ELIMINATION_DEFAULT;
    }
}