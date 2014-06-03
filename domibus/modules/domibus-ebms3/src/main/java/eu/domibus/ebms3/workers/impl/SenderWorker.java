package eu.domibus.ebms3.workers.impl;

import org.apache.axis2.client.async.AxisCallback;
import org.apache.log4j.Logger;
import eu.domibus.common.util.ClassUtil;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.persistent.UserMsgToPushDAO;
import eu.domibus.ebms3.submit.MsgInfoSet;

import java.util.List;

/**
 * @author Hamid Ben Malek
 */
public class SenderWorker implements Runnable {
    private static final Logger log = Logger.getLogger(SenderWorker.class.getName());
    private final UserMsgToPushDAO umd = new UserMsgToPushDAO();

    public void run() {
        final List<UserMsgToPush> messages = umd.findMessagesToPush();
        if (messages == null || messages.size() == 0) {
            return;
        }
        for (final UserMsgToPush message : messages) {
            try {
                // Assume that the message has successfully been sent even if
                // the send method fails.  Use AS4 Reliability to handle
                // recoverable errors, i. e. networking errors.
                message.setPushed(true);
                umd.update(message);
                send(message);
            } catch (Exception e) {
                log.error("An error occured when sending the message.\n" +
                          "Details:" + e.toString(), e);
            }
        }
    }

    private void send(final UserMsgToPush message) {
        AxisCallback cb = null;
        if (message.getCallbackClass() != null && !message.getCallbackClass().trim().equals("")) {
            try {
                cb = (AxisCallback) ClassUtil.createInstance(message.getCallbackClass());
            } catch (Exception ex) {
                log.debug(ex.getMessage());
            }
        }
        final MsgInfoSet metadata = message.getMsgInfoSet();

        log.debug("SenderWorker: about to send to " + message.getToURL());

        message.send(metadata, cb);
    }
}
