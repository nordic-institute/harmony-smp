package eu.eCODEX.submission.consumer;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.ReceivedUserMsgDAO;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatus;
import eu.eCODEX.submission.persistent.ReceivedUserMsgStatusDAO;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static eu.domibus.common.Constants.*;

public class EtrustexSubmissionConsumer extends SubmissionConsumer {
    private static final Logger LOG = Logger
            .getLogger(EtrustexSubmissionConsumer.class);
    private final ReceivedUserMsgStatusDAO rumsd = new ReceivedUserMsgStatusDAO();
    private final ReceivedUserMsgDAO rumd = new ReceivedUserMsgDAO();
    private Map<String, String> parameterMap;

    public void push() {
        String consumerName = CONSUMER_NAME;
        if (this.parameterMap != null) {
            if ((this.parameterMap.containsKey("name"))
                    && ((this.parameterMap.get("name") != null) || (((String) this.parameterMap
                    .get("name")).isEmpty()))) {
                consumerName = (String) this.parameterMap.get("name");
                LOG.debug("Nameparameter for consumer found. Overriding standard name with: "
                        + consumerName);
            }
            LOG.debug("No name for this consumer specified. Fallback to standard name: SUBMISSION_CONSUMER");
        }
        MessageContext msgCtx = MessageContext.getCurrentMessageContext();
        if (msgCtx == null) {
            throw new NullPointerException(
                    "No MessageContext could be retrieved");
        }
        MsgInfo msgInfo = EbUtil.getMsgInfo(msgCtx);

        String msgId = msgInfo.getMessageId();
        if (msgId == null) {
            throw new NullPointerException("No messageID in incoming message");
        }
        List<ReceivedUserMsg> msgs = this.rumd.findByMessageId(msgId);
        if ((msgs == null) || (msgs.size() != 1)) {
            throw new RuntimeException(
                    "No or more than one message found for messageId: " + msgId);
        }
        ReceivedUserMsgStatus receivedUserMsgStatus = new ReceivedUserMsgStatus(
                (ReceivedUserMsg) msgs.get(0));
        receivedUserMsgStatus.setConsumed_by(consumerName);

        this.rumsd.persist(receivedUserMsgStatus);
        try {
            sendMessage(receivedUserMsgStatus.getMsg().getMessageId());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String messageID) throws JMSException {
        LOG.info("------------------>RECEIVED MESSAGE:: " + messageID);

        Connection qcon = null;
        Session qsession = null;
        MessageProducer qsender = null;
        try {
            Hashtable env = new Hashtable();

            String context, factory, queue, url, principal, password = null;
            context = JNDIUtil.getStringEnvironmentParameter(ETX_SUBMISSION_CONTEXT);
            factory = JNDIUtil.getStringEnvironmentParameter(ETX_SUBMISSION_FACTORY);
            queue = JNDIUtil.getStringEnvironmentParameter(ETX_SUBMISSION_QUEUE);
            url = JNDIUtil.getStringEnvironmentParameter(ETX_SUBMISSION_URL);
            principal = JNDIUtil.getStringEnvironmentParameter(ETX_SUBMISSION_PRINCIPAL);
            password = JNDIUtil.getStringEnvironmentParameter(ETX_SUBMISSION_PASSWORD);


            if (StringUtils.isNotEmpty(context)) {
                env.put(Context.INITIAL_CONTEXT_FACTORY, context);
            }
            if (StringUtils.isNotEmpty(url)) {
                env.put(Context.PROVIDER_URL, url);
            }

            boolean isAuthenticationrequired = false;
            try {
                if (StringUtils.isNotEmpty(principal)) {
                    env.put(Context.SECURITY_PRINCIPAL, principal);
                    isAuthenticationrequired = true;
                }
                if (StringUtils.isNotEmpty(password)) {
                    env.put(Context.SECURITY_CREDENTIALS, password);
                }
                env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

            } catch (ConfigurationException exc) {
                // the property is null
            }

            InitialContext ctx = new InitialContext(env);

            ConnectionFactory qconFactory = (ConnectionFactory) ctx.lookup(factory);

            if (isAuthenticationrequired) {
                qcon = qconFactory.createConnection(principal, password);
            } else {
                qcon = qconFactory.createConnection();
            }
            qsession = qcon.createSession(true, 1);
            Destination destQueue = (Destination) ctx.lookup(queue);
            qsender = qsession.createProducer(destQueue);
            TextMessage msg = qsession.createTextMessage();

            LOG.info("------------------>Before sending the message");

            msg.setText(messageID);
            qsender.send(msg);
            qsession.commit();

            LOG.info("------------------>After sending the message");
        } catch (NamingException e) {
            LOG.error("------------------>ERROR:: NamingException", e);
        } catch (JMSException e) {
            LOG.error("------------------>ERROR:: JMSException", e);
        } finally {
            if (qsender != null) {
                qsender.close();
            }
            if (qsession != null) {
                qsession.close();
            }
            if (qcon != null) {
                qcon.close();
            }
        }
    }
}
