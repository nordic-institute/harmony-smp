package eu.domibus.ebms3.handlers;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgIdCallback;
import eu.domibus.ebms3.persistent.MsgIdCallbackDAO;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.async.AsyncResult;
import org.apache.axis2.client.async.Callback;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;

/**
 * This handler, when it receives an incoming UserMessage
 * that has an eb:RefToMessageId in its ebMS headers, looks up the database
 * table MsgIdCallback to see if there is a callback class registered to
 * handle such a response message.
 *
 * @author Hamid Ben Malek
 */
public class InvokeCallback extends AbstractHandler {
    private static final Logger LOG = Logger.getLogger(InvokeCallback.class);
    private final MsgIdCallbackDAO mid = new MsgIdCallbackDAO();
    private String logPrefix = "";

    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        if (msgCtx.getFLOW() != MessageContext.IN_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement userMessage = EbUtil.getUserMessage(msgCtx);
        if (userMessage == null) {
            return InvocationResponse.CONTINUE;
        }
        final String refToMessageId = XMLUtil.getGrandChildValue(userMessage, Constants.REF_TO_MESSAGE_ID);
        if ((refToMessageId == null) || "".equals(refToMessageId.trim())) {
            return InvocationResponse.CONTINUE;
        }

        if (InvokeCallback.LOG.isDebugEnabled()) {
            this.logPrefix = WSUtil.logPrefix(msgCtx);
        }
        InvokeCallback.LOG
                .trace(this.logPrefix + "looking the database table MsgIdCallback for a registered callback class");
        final MsgIdCallback micb = this.mid.findByMessageId(refToMessageId);
        if (micb == null) {
            InvokeCallback.LOG.warn(this.logPrefix + " no registered callback class was found");
            return InvocationResponse.CONTINUE;
        }
        final String callbackClass = micb.getCallbackClass();
        if ((callbackClass == null) || "".equals(callbackClass.trim())) {
            return InvocationResponse.CONTINUE;
        }

        final Callback cb = this.createCallback(callbackClass);
        if (cb != null) {
            InvokeCallback.LOG.debug("Invoking the callback class " + callbackClass + " ...");
            final AsyncResult result = new AsyncResult(msgCtx);
            cb.onComplete(result);
        }
        return InvocationResponse.CONTINUE;
    }

    private Callback createCallback(final String callbackClassName) {
        Callback callbackInstance = null;
        try {
            final int dollarPos = callbackClassName.indexOf("$");
            if (dollarPos < 0) {
                //instantiate it as a normal class
                final Class callbackClass = Class.forName(callbackClassName);
                callbackInstance = (Callback) callbackClass.newInstance();
            } else {
                //instantiate it as a normal class
                final String containerClassName = callbackClassName.substring(0, dollarPos);
                final Class containerClass = Class.forName(containerClassName);
                final Class innerClass = Class.forName(callbackClassName);
                final Object containerInstance = containerClass.newInstance();
                final Constructor innerConstructor = innerClass.getDeclaredConstructor(new Class[]{containerClass});
                callbackInstance = (Callback) innerConstructor.newInstance(containerInstance);
            }
        } catch (Exception e) {
            throw new ConfigurationException("Cannot instantiate the Callback class " + callbackClassName +
                                             ". Make sure that it has a default constructor", e);
        }
        return callbackInstance;
    }
}