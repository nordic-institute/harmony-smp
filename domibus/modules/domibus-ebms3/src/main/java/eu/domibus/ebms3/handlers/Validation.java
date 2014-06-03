package eu.domibus.ebms3.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;

/**
 * @author Hamid Ben Malek
 */
public class Validation extends AbstractHandler {

    private static final Logger log = Logger.getLogger(Validation.class);

    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (!msgCtx.isServerSide() || msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        if (log.isDebugEnabled()) {
            logPrefix = WSUtil.logPrefix(msgCtx);
        }
        log.debug(logPrefix + msgCtx.getEnvelope().getHeader());

        return InvocationResponse.CONTINUE;
    }
}