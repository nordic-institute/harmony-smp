package eu.domibus.security.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import org.apache.neethi.Policy;
import org.apache.rampart.RampartMessageData;
import eu.domibus.security.module.Configuration;
import eu.domibus.security.module.SecurityUtil;

/**
 * This handler (which is part of the Domibus-Security Module) runs before
 * rampart module on the server side, and its purpose is to on the fly attach a
 * policy to the service the message is going (the security policy depends on
 * which PMode it is being used for that particular received request)
 *
 * @author Hamid Ben Malek
 */
public class InFlowHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(InFlowHandler.class.getName());

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        final String security = (String) msgCtx.getProperty(SecurityUtil.SECURITY);
        if (security == null || security.trim().equals("")) {
            log.warn("no security defined, doing nothing");
            return InvocationResponse.CONTINUE;
        }
        final Policy policy = Configuration.getRemoteSecurity(security).getPolicy();
        if (policy == null) {
            log.warn("no security defined, doing nothing");
            return InvocationResponse.CONTINUE;
        }
        //        final AxisService srv = msgCtx.getConfigurationContext().getAxisConfiguration().getService("msh");
        //        srv.getPolicySubject().attachPolicy(policy);

        msgCtx.setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);

        log.debug("Recipient policy " + policy.getId() + " attached to the request message");
        return InvocationResponse.CONTINUE;
    }
}