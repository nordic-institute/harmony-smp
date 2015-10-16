package eu.eCODEX.submission.consumer;


import eu.domibus.common.Constants;
import eu.domibus.ebms3.consumers.EbConsumer;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.ReceivedUserMsgDAO;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatus;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatusDAO;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * This is consumer marks an incoming (received) message with its consumer name, in the database {@link eu.eCODEX.submission.persistent.ReceivedUserMsgStatus},
 * in order to give backends the chance to distinguish between messages by consumer name.
 * <p/>
 * Currently only Push is supported.
 *
 * @author muellers
 */
public class SubmissionConsumer implements EbConsumer {
    private static final Logger LOG = Logger.getLogger(SubmissionConsumer.class);

    private final ReceivedUserMsgStatusDAO rumsd = new ReceivedUserMsgStatusDAO();
    private final ReceivedUserMsgDAO rumd = new ReceivedUserMsgDAO();

    private Map<String, String> parameterMap;

    /**
     * This method is called when an incoming message (via msh webservice) was received after the whole InFlow (axis.xml) was processed.
     */
    @Override
    public void push() {
        String consumerName = Constants.CONSUMER_NAME;


        if (this.parameterMap != null) {
            if (this.parameterMap.containsKey("name") &&
                (this.parameterMap.get("name") != null || this.parameterMap.get("name").isEmpty())) {
                consumerName = this.parameterMap.get("name");
                LOG.debug("Nameparameter for consumer found. Overriding standard name with: " + consumerName);
            }
            LOG.debug("No name for this consumer specified. Fallback to standard name: " + Constants.CONSUMER_NAME);
        }

        final MessageContext msgCtx = MessageContext.getCurrentMessageContext();
        if (msgCtx == null) {
            throw new NullPointerException("No MessageContext could be retrieved");
        }
        final MsgInfo msgInfo = EbUtil.getMsgInfo(msgCtx);

        final String msgId = msgInfo.getMessageId();

        if (msgId == null) {
            // TODO: ebms exception? even though we cant deliver the error message back to the sender
            throw new NullPointerException("No messageID in incoming message");
        }

        List<ReceivedUserMsg> msgs = this.rumd.findByMessageId(msgId);

        if (msgs == null || msgs.size() != 1) {
            throw new RuntimeException("No or more than one message found for messageId: " + msgId);
        }

        ReceivedUserMsgStatus receivedUserMsgStatus = new ReceivedUserMsgStatus(msgs.get(0));
        receivedUserMsgStatus.setConsumed_by(consumerName);

        this.rumsd.persist(receivedUserMsgStatus);

    }

    @Override
    public void pull() {
        throw new UnsupportedOperationException("Pull is currently not supported");
    }

    /**
     * This method is called by the Gateway Servlet when a consumer is loaded (gateway.xml). The parameters defined in the xml
     * were published via this method. The property \"name\", if available, will be used as the consumer name. If no \"name\" property
     * is availble the standard name ({@link org.holodeck.common.Constants#CONSUMER_NAME}) is used.
     *
     * @param parameters a map of parameters published to the consumer
     */
    @Override
    public void setParameters(Map<String, String> parameters) {
        this.parameterMap = parameters;
    }
}
