package eu.domibus.ebms3.handlers;

import eu.domibus.common.util.WSUtil;
import eu.domibus.ebms3.module.Constants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

/**
 * This handler runs only at the server side when a message is received and its purpose is just to take a picture of the
 * complete SOAP Header and store that picture in the MessageContext, so that at later time, a consumer may have access
 * to that original SOAP Header.
 *
 * @author Hamid Ben Malek
 */
public class SoapHeaderShot extends AbstractHandler {
    //  private static final Log log = LogFactory.getLog(SoapHeaderShot.class.getName());
    private static final Logger log = Logger.getLogger(SoapHeaderShot.class.getName());
    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        //    if ( msgCtx.getFLOW() != MessageContext.IN_FLOW  ||
        //         !msgCtx.isServerSide() )
        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        if (SoapHeaderShot.log.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }

        final SOAPHeader header = msgCtx.getEnvelope().getHeader();

        if (header != null) {
            final OMElement clone = header.cloneOMElement();
            msgCtx.setProperty(Constants.IN_SOAP_HEADER, clone);
            SoapHeaderShot.log.debug(this.logPrefix + " Saved original SOAP Header as a property in MessageContext");
        }

        return InvocationResponse.CONTINUE;
    }
}