package eu.domibus.ebms3.workers.impl;

import eu.domibus.common.persistent.Attachment;
import eu.domibus.common.persistent.TempStoreDAO;
import eu.domibus.common.util.ClassUtil;
import eu.domibus.ebms3.config.As4Receipt;
import eu.domibus.ebms3.module.AS4ReliabilityUtil;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.persistent.UserMsgToPushDAO;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
public class DatabaseSenderWorker implements Runnable {
    private static final Logger log = Logger.getLogger(DatabaseSenderWorker.class.getName());
    private static boolean running;
    private final UserMsgToPushDAO umd = new UserMsgToPushDAO();
    private final TempStoreDAO tsd = new TempStoreDAO();

    public void run() {

        synchronized (DatabaseSenderWorker.class) {
            if (DatabaseSenderWorker.running) {
                return;
            }
            DatabaseSenderWorker.running = true;
        }
        try {
            final List<UserMsgToPush> messages = this.umd.findMessagesToPush();
            if ((messages == null) || messages.isEmpty()) {
                return;
            }

            for (final UserMsgToPush message : messages) {
                try {
                    // Assume that the message has successfully been sent even if
                    // the send method fails.  Use AS4 Reliability to handle
                    // recoverable errors, i. e. networking errors.
                    message.setPushed(true);
                    this.umd.update(message);

                    for (final Attachment attachment : message.getAttachments()) {
                        //                        final FileDataSource ds = new FileDataSource(attachment.getFilePath());
                        String[] splittedPath = attachment.getFilePath().split("/");
                        byte[] payload = this.tsd.findByGroupAndArtifact(splittedPath[0], splittedPath[1]).getBytes();
                        final DataSource ds = new ByteArrayDataSource(payload, attachment.getContentType());
                        final DataHandler dh = new DataHandler(ds);
                        message.getMessageContext().addAttachment(attachment.getContentID(), dh);
                    }

                    message.addToBody(message.getMsgInfoSet().getPayloads().getBodyPayload());
                    this.send(message);

                    As4Receipt as4Config = AS4ReliabilityUtil.getReceiptConfig(message.getPmode());
                    //if message does not use as4 reliability
                    if (as4Config == null) {
                        //delete attachments from temporary store
                        this.tsd.deleteAttachments(message.getMessageId());
                    }

                } catch (Exception e) {
                    DatabaseSenderWorker.log.error("An error occured when sending the message.\n" +
                                                   "Details:" + e.toString(), e);
                }
            }
        } finally {
            DatabaseSenderWorker.running = false;
        }
    }

    private void send(final UserMsgToPush message) {
        AxisCallback cb = null;
        if ((message.getCallbackClass() != null) && !"".equals(message.getCallbackClass().trim())) {
            try {
                cb = (AxisCallback) ClassUtil.createInstance(message.getCallbackClass());
            } catch (Exception ex) {
                DatabaseSenderWorker.log.debug(ex.getMessage());
            }
        }
        final MsgInfoSet metadata = message.getMsgInfoSet();

        DatabaseSenderWorker.log.debug("DatabaseSenderWorker: about to send to " + message.getToURL());

        message.send(metadata, cb);
    }
}
