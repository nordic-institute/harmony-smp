package eu.domibus.logging.handler;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

public class MessageLoggingHandler extends AbstractHandler {

    private final static Logger LOG = Logger.getLogger(MessageLoggingHandler.class);

    @Override
    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {

        MessageLoggingHandler.LOG.info(msgCtx.getEnvelope().toString());

        return InvocationResponse.CONTINUE;
    }

}
