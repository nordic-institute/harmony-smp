package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.config.UserService;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.packaging.Messaging;
import eu.domibus.ebms3.packaging.PackagingFactory;
import eu.domibus.ebms3.packaging.UserMessage;
import eu.domibus.ebms3.persistent.MsgInfo;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

/**
 * This handler runs only on the server side, and its main job is to populate
 * the ebms3 header of a sync response that contains a UserMessage.
 *
 * @author Hamid Ben Malek
 */
public class ResponsePackager extends AbstractHandler {
    private static final Logger LOG = Logger.getLogger(ResponsePackager.class);

    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (!msgCtx.isServerSide()) {
            return InvocationResponse.CONTINUE;
        }
        if (ResponsePackager.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }

        if (msgCtx.getFLOW() == MessageContext.IN_FLOW) {
            final SOAPHeader header = msgCtx.getEnvelope().getHeader();
            if (header == null) {
                return InvocationResponse.CONTINUE;
            }

            final OMElement ebMess = XMLUtil.getGrandChildNameNS(header, Constants.MESSAGING, Constants.NS);
            if (ebMess == null) {
                return InvocationResponse.CONTINUE;
            }

            //log.debug(logPrefix + msgCtx.getEnvelope().getHeader());
            XMLUtil.debug(ResponsePackager.LOG, this.logPrefix, msgCtx.getEnvelope().getHeader());

            MsgInfo msgInfo = (MsgInfo) msgCtx.getProperty(Constants.IN_MSG_INFO);
            if (msgInfo == null) {
                msgInfo = EbUtil.getMsgInfo(msgCtx);
            }
            msgCtx.setProperty(Constants.IN_MSG_INFO, msgInfo);
            return InvocationResponse.CONTINUE;
        }

        if (msgCtx.getFLOW() == MessageContext.OUT_FLOW) {
            ResponsePackager.LOG.trace(this.logPrefix + "processing out_flow");
            final MsgInfo msgInfo = (MsgInfo) WSUtil.getPropertyFromInMsgCtx(msgCtx, Constants.IN_MSG_INFO);
            if (msgInfo == null) {
                ResponsePackager.LOG.info(this.logPrefix + "could not find msgInfo from previous in request");
                return InvocationResponse.CONTINUE;
            }
            if (msgInfo.getService() != null) {
                ResponsePackager.LOG.debug(this.logPrefix + "msgInfo and its service are not null");
                // construct a UserMessage header and add it to the SOAP header...
                final Leg leg = Configuration.getLeg(msgInfo, 2, Constants.TWO_WAY_SYNC);
                if (leg == null) {
                    ResponsePackager.LOG.debug("this is not a two-way/sync mep");
                    // check if previous request is expecting a receipt or an ack:
                    final boolean expectReceipt = (Boolean) WSUtil
                            .getPropertyFromInMsgCtx(msgCtx, eu.domibus.common.Constants.EXPECT_RECEIPT);
                    // insert an empty eb:Messsaging header here...
                    if (expectReceipt) {
                        new Messaging(msgCtx.getEnvelope());
                    }
                    return InvocationResponse.CONTINUE;
                }
                final Producer producer = leg.getProducer();
                final UserService us = leg.getUserService();
                final Attachments att = msgCtx.getAttachmentMap();
                final UserMessage userMessage = PackagingFactory.createRespUserMessage(msgInfo, producer, us, att);
                new Messaging(msgCtx.getEnvelope(), null, userMessage);
                XMLUtil.debug(ResponsePackager.LOG, this.logPrefix, msgCtx.getEnvelope());
            } else {
                ResponsePackager.LOG.debug(this.logPrefix + "found msgInfo but its service is null");
            }
        }

        return InvocationResponse.CONTINUE;
    }
}