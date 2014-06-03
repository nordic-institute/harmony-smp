package eu.domibus.ebms3.handlers;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.config.UserService;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.packaging.*;
import eu.domibus.ebms3.persistent.UserMsgToPull;
import eu.domibus.ebms3.persistent.UserMsgToPullDAO;
import eu.domibus.ebms3.submit.MsgInfoSet;

import java.util.Iterator;

//import eu.domibus.ebms3.pmodes.*;

/**
 * This handler runs only on the server side, and it processes an incoming
 * PullRequest message
 *
 * @author Hamid Ben Malek
 */
public class PullProcessor extends AbstractHandler {
    private static final Logger log = Logger.getLogger(PullProcessor.class.getName());
    private String logPrefix = "";
    private final UserMsgToPullDAO umd = new UserMsgToPullDAO();

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (!msgCtx.isServerSide()) {
            return InvocationResponse.CONTINUE;
        }
        final SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            return InvocationResponse.CONTINUE;
        }

        if (log.isDebugEnabled()) {
            logPrefix = WSUtil.logPrefix(msgCtx);
        }
        //log.debug(logPrefix + msgCtx.getEnvelope().getHeader());
        XMLUtil.debug(log, logPrefix, msgCtx.getEnvelope().getHeader());

        if (msgCtx.getFLOW() == MessageContext.IN_FLOW) {
            final OMElement ebms3Pull = XMLUtil.getGrandChildNameNS(header, Constants.PULL_REQUEST, Constants.NS);
            final OMElement ebMess = XMLUtil.getGrandChildNameNS(header, Constants.MESSAGING, Constants.NS);
            if (ebms3Pull == null) {
                return InvocationResponse.CONTINUE;
            } else {
                final String mpc = XMLUtil.getAttributeValue(ebms3Pull, "mpc");
                final String msgId = XMLUtil.getGrandChildValue(ebMess, Constants.MESSAGE_ID);
                final String address = msgCtx.getTo().getAddress();
                final PMode pmode = Configuration.getPMode(Constants.ONE_WAY_PULL, mpc, address);
                log.info(logPrefix + "Received a PullRequest message on mpc: " + mpc);
                if (pmode == null) {
                    // generate an ebms3 mismatch pmode error and send it:
                    final eu.domibus.ebms3.packaging.Error pmodeMismatch =
                            eu.domibus.ebms3.packaging.Error.getProcessingModeMismatchError(msgId);
                    final SignalMessage modeMismatch = new SignalMessage(new MessageInfo(null, msgId), pmodeMismatch);
                    final SOAPFactory factory = (SOAPFactory) msgCtx.getEnvelope().getOMFactory();
                    final SOAPEnvelope env = factory.getDefaultEnvelope();
                    new Messaging(env, modeMismatch, null);
                    WSUtil.sendResponse(env, msgCtx);
                    log.info(logPrefix + "Mode Mismatch Error is being sent as response to the pull");
                    return InvocationResponse.ABORT;
                }

                // ToDo: need to verify if the pull request is authorized.
                // Only when it is authorized, then proceed ...

                // Retrieve the user message from the mpc queue and send it as response.
                // If the queue is empty, then let the request propagate to the service
                // who may send back a user message as a response to the pull request:

                final UserMsgToPull message = umd.getNextUserMsgToPull(mpc);
                if (message != null) {
                    //MessageContext resp =
                    //    message.getMessageContext(msgCtx.getConfigurationContext());
                    message.setConfigurationContext(msgCtx.getConfigurationContext());
                    final MessageContext resp = message.getMessageContext();

                    log.info(logPrefix + "Found UserMessage to pull from DB for mpc " + mpc);
                    final MsgInfoSet mis = message.getMsgInfoSet();
                    resp.setProperty(Constants.MESSAGE_INFO_SET, mis);
                    resp.setProperty("attachments", message.getAttachments());
                    final Messaging ebM = PackagingFactory.createMessagingElement(resp);
                    ebM.addToHeader(resp.getEnvelope());
                    log.info(logPrefix + "ebms headers have been added to pulled user message");
                    WSUtil.sendResponse(resp, msgCtx);
                    log.info(logPrefix + "A UserMessage is being sent as response to the pull request");
                    XMLUtil.debug(log, logPrefix, resp.getEnvelope().getHeader());
                    return InvocationResponse.ABORT;
                } else {
                    log.info(logPrefix + "No UserMessage found in DB to pull, " +
                             "so continuing until the final partyId decides what message to be pulled");
                }

                msgCtx.setProperty(Constants.IN_PULL_REQUEST, ebms3Pull);
                msgCtx.setProperty(Constants.IN_MESSAGING, ebMess);
                msgCtx.setProperty(Constants.TO_ADDRESS, msgCtx.getTo().getAddress());
                // ToDo: try to read the wss:Security actor="ebms" element and store it
                // as well in the message context in case the MPC requires
                // authorization ..

                return InvocationResponse.CONTINUE;
            }
        }

        if (msgCtx.getFLOW() != MessageContext.OUT_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement pullReq = (OMElement) WSUtil.getPropertyFromInMsgCtx(msgCtx, Constants.IN_PULL_REQUEST);
        if (pullReq == null) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement ebMess = (OMElement) WSUtil.getPropertyFromInMsgCtx(msgCtx, Constants.IN_MESSAGING);

        // Check if the SOAP body is empty. In this case, we should send
        // an ebms3 warning of type "Empty MPC":
        final SOAPBody body = msgCtx.getEnvelope().getBody();
        final Iterator it = body.getChildElements();
        if (it == null || !it.hasNext()) {
            final String ref = XMLUtil.getGrandChildValue(ebMess, Constants.MESSAGE_ID);
            final eu.domibus.ebms3.packaging.Error emptyMPC =
                    eu.domibus.ebms3.packaging.Error.getEmptyPartitionError(ref);
            final SignalMessage ebWarn = new SignalMessage(new MessageInfo(null, ref), emptyMPC);
            new Messaging(msgCtx.getEnvelope(), ebWarn, null);
            return InvocationResponse.CONTINUE;
        }

        final String mpc = XMLUtil.getAttributeValue(pullReq, "mpc");
        //log.debug(logPrefix + "mpc is " + mpc);
        final String msgId = XMLUtil.getGrandChildValue(ebMess, Constants.MESSAGE_ID);
        final String address = (String) WSUtil.getPropertyFromInMsgCtx(msgCtx, Constants.TO_ADDRESS);

        final PMode pm = Configuration.getPMode(Constants.ONE_WAY_PULL, mpc, address);
        final Producer producer = Configuration.getLeg(pm.getName(), 2).getProducer();
        final UserService us = Configuration.getLeg(pm.getName(), 2).getUserService();
        final String pmode = pm.getName();

        final UserMessage userMsg =
                PackagingFactory.createUserMessage(mpc, msgId, producer, pmode, us, msgCtx.getAttachmentMap());
        //log.debug(logPrefix + userMsg.toString());
        new Messaging(msgCtx.getEnvelope(), null, userMsg);

        return InvocationResponse.CONTINUE;
    }
}