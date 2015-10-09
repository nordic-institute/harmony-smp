package eu.domibus.security.handlers;

import eu.domibus.security.config.model.RemoteSecurityConfig;
import eu.domibus.security.module.Configuration;
import eu.domibus.security.module.SecurityUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import org.apache.neethi.Policy;
import org.apache.rampart.RampartMessageData;

/**
 * @author Hamid Ben Malek
 */
public class OutFlowHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(OutFlowHandler.class.getName());

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {

        final String securityName = (String) msgCtx.getProperty(SecurityUtil.SECURITY);
        final RemoteSecurityConfig remoteSecurityConfig = Configuration.getRemoteSecurity(securityName);
        final Policy policy = remoteSecurityConfig.getPolicy();
        OutFlowHandler.log.debug("Properties for Security: " + securityName);
        msgCtx.setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);
        OutFlowHandler.log.debug("Message going out using policy " + policy.getId());
        return InvocationResponse.CONTINUE;
    }
}