package eu.domibus.ebms3.workers.impl;


import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import eu.domibus.common.util.ClassUtil;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.domibus.ebms3.workers.Task;

import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
public class PullWorker implements Task {
    private static final Logger log = Logger.getLogger(PullWorker.class);

    protected String pmode;
    protected String mpc;
    protected String callbackClass;

    public void run() {
        final MsgInfoSet metadata = new MsgInfoSet();
        metadata.setLegNumber(1);
        metadata.setPmode(pmode);
        final EbMessage msg = new EbMessage(metadata);
        log.debug("PullWorker: about to pull from mpc " + mpc);
        final MessageContext usrMsg = msg.inOut(metadata);
        if (usrMsg != null) {
            final AxisCallback handler = getCallback();
            if (handler != null) {
                handler.onMessage(usrMsg);
            }
        }
    }

    public void setParameters(final Map<String, String> parameters) {
        if (parameters == null) {
            return;
        }
        pmode = parameters.get("pmode");
        mpc = parameters.get("mpc");
        callbackClass = parameters.get("callbackClass");
    }

    protected AxisCallback getCallback() {
        if (callbackClass == null || callbackClass.trim().equals("")) {
            return null;
        }
        return (AxisCallback) ClassUtil.createInstance(callbackClass);
    }
}