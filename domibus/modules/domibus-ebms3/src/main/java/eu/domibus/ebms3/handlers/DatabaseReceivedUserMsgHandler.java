package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.FileUtil;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.*;
import eu.domibus.ebms3.persistent.Property;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * This handler stores a received UserMessage in the
 * "Received_UserMsg" table, so that the message can be consumed
 * later by the concerned parties, or be dispatched to a different
 * service by some background worker that listen on arrived UserMessages.
 * These received UserMessages could be received either by a push or a pull
 *
 * @author Hamid Ben Malek
 */
public class DatabaseReceivedUserMsgHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(DatabaseReceivedUserMsgHandler.class.getName());
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

        if (DatabaseReceivedUserMsgHandler.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }
        XMLUtil.debug(DatabaseReceivedUserMsgHandler.LOG, this.logPrefix, msgCtx.getEnvelope().getHeader());

        // Duplicate detection and elimination
        if (this.isDuplicateEliminationEnabled(msgCtx)) {
            final String messageId = this.getMessageId(msgCtx);
            if (messageId != null) {
                final long receivedUserMsgCount = this.rdd.countMessagesByMessageId(messageId);
                if (receivedUserMsgCount > 0) {
                    DatabaseReceivedUserMsgHandler.LOG
                            .debug(this.logPrefix + "Eliminating duplicate message with messageId=" +
                                   messageId);
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


        for(PartInfo p : receivedUM.getMsgInfo().getParts()) {
            //if PartInfo belongs to bodyload, skip
            if (p.isBody()) {
                continue;
            }

            if(p.getProperties() == null) {
                continue;
            }

            for(Property prop : p.getProperties()) {
                if(Constants.COMPRESSION_PROPERTY_NAME.equals(prop.getName()) && Constants.COMPRESSION_GZIP_MIMETYPE.equals(prop.getValue())) {
                    p.setPayloadData(FileUtil.doDecompress(p.getPayloadData()));
                }
            }
        }

        this.rdd.persist(receivedUM);
        DatabaseReceivedUserMsgHandler.LOG.debug(this.logPrefix + "Stored received UserMessage into Database");

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
            return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
        }
        final Binding binding = pmode.getBinding();
        if (binding == null) {
            return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
        }
        final MEP mep = binding.getMep();
        if (mep == null) {
            return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
        }
        final Collection<Leg> legs = mep.getLegs();
        if (legs == null) {
            return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
        }
        for (final Leg leg : legs) {
            if (leg == null) {
                continue;
            }
            final As4Receipt as4Receipt = leg.getAs4Receipt();
            if (as4Receipt == null) {
                return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
            }
            final As4Reliability as4Reliability = as4Receipt.getAs4Reliability();
            if (as4Reliability == null) {
                return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
            }
            return as4Reliability.isDuplicateElimination();
        }
        return DatabaseReceivedUserMsgHandler.DUPLICATE_ELIMINATION_DEFAULT;
    }
}