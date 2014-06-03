package eu.domibus.ebms3.handlers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.AbstractDispatcher;
import org.apache.log4j.Logger;
import eu.domibus.common.util.WSUtil;

import javax.xml.namespace.QName;

/**
 * @author Hamid Ben Malek
 */
public class Ebms3Dispatcher extends AbstractDispatcher {
    private static final Logger log = Logger.getLogger(Ebms3Dispatcher.class);

    private String logPrefix = "";

    public AxisOperation findOperation(final AxisService service, final MessageContext msgCtx) throws AxisFault {
        if (!isDestinedForMSH(msgCtx)) {
            return null;
        }
        return service.getOperation(new QName("push"));
    }

    public AxisService findService(final MessageContext msgCtx) throws AxisFault {
        if (!isDestinedForMSH(msgCtx)) {
            return null;
        }

        if (log.isDebugEnabled()) {
            logPrefix = WSUtil.logPrefix(msgCtx);
        }
        log.debug(logPrefix + msgCtx.getEnvelope().getHeader());
        return msgCtx.getConfigurationContext().getAxisConfiguration().getService("msh");
    }

    public void initDispatcher() {
    }

    private boolean isDestinedForMSH(final MessageContext msgCtx) {
        final EndpointReference epr = msgCtx.getTo();
        if (epr == null) {
            return false;
        }
        final String to = epr.getAddress();
        return to != null && (to.endsWith("/msh") || to.endsWith("/msh/"));
    }
}