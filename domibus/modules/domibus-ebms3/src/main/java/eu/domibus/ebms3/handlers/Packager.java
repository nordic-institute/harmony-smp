package eu.domibus.ebms3.handlers;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.packaging.Messaging;
import eu.domibus.ebms3.packaging.PackagingFactory;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;


/**
 * @author Hamid Ben Malek
 */
public class Packager extends AbstractHandler {
    private static final Logger log = Logger.getLogger(Packager.class);

    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.OUT_FLOW || msgCtx.isServerSide()) {
            return InvocationResponse.CONTINUE;
        }

        final MsgInfoSet mis = (MsgInfoSet) msgCtx.getProperty(Constants.MESSAGE_INFO_SET);
        if (mis == null) {
            log.info("No PMode was specified for the outgoing message");
            return InvocationResponse.CONTINUE;
        }

        if (msgCtx.getEnvelope() == null) {
            final Leg leg = Configuration.getLeg(mis);
            double soapVersion = 1.1;
            final String ver = leg.getSoapVersion();
            if (ver != null && !ver.trim().equals("")) {
                soapVersion = Double.parseDouble(ver);
            }
            msgCtx.setEnvelope(EbMessage.createEnvelope(soapVersion));
        }

        SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            final SOAPEnvelope env = msgCtx.getEnvelope();
            header = ((SOAPFactory) env.getOMFactory()).createSOAPHeader(env);
        }

        final OMElement ebMess = XMLUtil.getGrandChildNameNS(header, Constants.MESSAGING, Constants.NS);
        if (ebMess != null) {
            return InvocationResponse.CONTINUE;
        }

        if (log.isDebugEnabled()) {
            logPrefix = WSUtil.logPrefix(msgCtx);
        }
        //log.debug(logPrefix + msgCtx.getEnvelope().getHeader());
        XMLUtil.debug(log, logPrefix, msgCtx.getEnvelope().getHeader());

        final Messaging mess = PackagingFactory.createMessagingElement(msgCtx);
        //EbUtil.createMessagingElement(msgCtx);

        mess.addToHeader(msgCtx.getEnvelope());
        log.info(logPrefix + " ebms3 headers were added to outgoing message");
        XMLUtil.debug(log, logPrefix, msgCtx.getEnvelope().getHeader());

        return InvocationResponse.CONTINUE;
    }
}