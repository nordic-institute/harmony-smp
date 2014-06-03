package eu.domibus.ebms3.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.log4j.Logger;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.persistent.MsgInfo;

/**
 * This handler runs only at the server side when a message is received and its
 * purpose is just to examine the SOAP Header in order to determine the PMode
 * being used, and then which leg of the PMode, and finally what is the security
 * quality used on that leg (This security quality will then be store in the
 * MessageContext, so that at later time, the Domibus-Security Module would
 * know what WS-Security Policy to attach to the service the message is going
 * to).
 *
 * @author Hamid Ben Malek
 */
public class SecurityShot extends AbstractHandler {
    private static final Logger log = Logger.getLogger(SecurityShot.class);

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        String security = null;
        if (msgCtx.getFLOW() == MessageContext.OUT_FLOW || msgCtx.getFLOW() == MessageContext.OUT_FAULT_FLOW) {

            final MessageContext inMessageContext = msgCtx.getOperationContext().getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            if (inMessageContext != null) {
                final MsgInfo inMessageInfo = (MsgInfo) inMessageContext.getProperty(Constants.MESSAGE_INFO);

                if (inMessageInfo != null) {
                    final String service = inMessageInfo.getService();
                    final String action = inMessageInfo.getAction();
                    PMode pmode = null;
                    for (final Party fromParty : inMessageInfo.getFromParties()) {
                        for (final Party toParty : inMessageInfo.getToParties()) {
                            pmode = Configuration.getPModeO(action, service, toParty.getPartyId(), toParty.getType(), fromParty.getPartyId(), fromParty.getType());
                            if (pmode != null) {
                                break;
                            }
                        }
                    }
                    security = pmode.getBinding().getMep().getLegByNumber(1).getSecurity();
                }
            }

        } else {
            final Leg requestLeg = Configuration.getLegFromServerSideReq(msgCtx);
            if (requestLeg != null) {
                security = requestLeg.getSecurity();
            }
        }
        if (security != null && msgCtx.getProperty("SECURITY") == null) {
            msgCtx.setProperty("SECURITY", security);
            log.debug("Received Request Message is using security " + security);

        }
        return InvocationResponse.CONTINUE;
    }
}