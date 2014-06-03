/*
 * 
 */
package eu.domibus.backend.service._1_1.helper;

import backend.ecodex.org._1_1.Code;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.log4j.Logger;
import eu.domibus.backend.service._1_1.exception.SendMessageServiceException;
import eu.domibus.common.util.ClassUtil;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.persistent.UserMsgToPushDAO;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.domibus.ebms3.submit.SubmitUtil;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * The Class SendMessageHelper.
 */
@Service("SendMessageHelper_1_1")
public class SendMessageHelper {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(SendMessageHelper.class);
    private final UserMsgToPushDAO umd = new UserMsgToPushDAO();

    /**
     * Send.
     *
     * @param message the message
     * @throws SendMessageServiceException the send message service exception
     */
    public String send(final UserMsgToPush message) throws SendMessageServiceException {
        AxisCallback cb = null;
        if (message.getCallbackClass() != null && !message.getCallbackClass().trim().equals("")) {
            try {
                cb = (AxisCallback) ClassUtil.createInstance(message.getCallbackClass());
            } catch (Exception e) {
                final SendMessageServiceException sendMessageServiceException = new SendMessageServiceException("Error while creating AxisCallback", e, Code.ERROR_SEND_002);
                throw sendMessageServiceException;
            }
        }
        final MsgInfoSet metadata = message.getMsgInfoSet();
        log.debug("[ SendMessageHelper ]: about to send to " + message.getToURL());

        try {
            message.send(metadata, cb);

            final String messageId = eu.domibus.ebms3.packaging.PackagingFactory.getCurrentMessageID();

            log.debug("[ SendMessageHelper ]: message[" + messageId + "] was sent succcessfully");

            return messageId;
        } catch (Exception e) {
            //			SendMessageServiceException sendMessageServiceException = new SendMessageServiceException(
            //					"Error while sending message", e, Code.ERROR_SEND_003);
            //			log.warn("Could not send message. Cause:\n", e);
            //			throw sendMessageServiceException;

            final String messageId = eu.domibus.ebms3.packaging.PackagingFactory.getCurrentMessageID();

            log.warn("Could not send message[" + messageId + "] at first attempt. Cause:\n", e);

            return messageId;
        }
    }

    /**
     * Submit from folder.
     *
     * @param folder the folder
     * @return the user msg to push
     * @throws SendMessageServiceException the send message service exception
     */
    public UserMsgToPush submitFromFolder(final File folder) throws SendMessageServiceException {
        if (folder == null || !folder.exists()) {
            final SendMessageServiceException sendMessageServiceException = new SendMessageServiceException("Temporal directory not found", Code.ERROR_SEND_002);
            throw sendMessageServiceException;
        }

        MsgInfoSet mis = null;
        try {
            mis = readMeta(folder);
        } catch (Exception e) {
            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Error reading metadata.xml from temporal directory", e, Code.ERROR_SEND_002);
            throw sendMessageServiceException;
        }
        if (mis == null) {
            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Error reading metadata.xml from temporal directory", Code.ERROR_SEND_002);
            throw sendMessageServiceException;
        }
        final String bodyPayload = mis.getBodyPayload();
        log.debug("[ SendMessageHelper ] is scanning message folder " + folder.getName());
        log.debug("body payload is " + bodyPayload);
        final File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            final SendMessageServiceException sendMessageServiceException = new SendMessageServiceException("No files found in the temporal directory", Code.ERROR_SEND_002);
            throw sendMessageServiceException;
        }
        final UserMsgToPush userMsgToPush = new UserMsgToPush(folder, mis);

        // set pushed true to prevent sending the message twice
        userMsgToPush.setPushed(true);

        umd.persist(userMsgToPush);
        log.debug("UserMsgToPush was submitted to database");

        return userMsgToPush;
    }

    /**
     * Rename metadata.
     *
     * @param folder the folder
     * @return true, if successful
     */
    private static boolean renameMetadata(final File folder) {
        if (folder == null) {
            return false;
        }
        final File meta = new File(folder.getAbsolutePath() + File.separator + "metadata.xml");
        final File metaRenamed = new File(folder.getAbsolutePath() + File.separator + "metadata.xml.processed");
        return meta.renameTo(metaRenamed);
    }

    /**
     * Read meta.
     *
     * @param folder the folder
     * @return the msg info set
     */
    private static synchronized MsgInfoSet readMeta(final File folder) {
        if (folder == null || !folder.exists()) {
            return null;
        }
        File meta = new File(folder.getAbsolutePath() + File.separator + "metadata.xml");
        if (!meta.exists()) {
            return null;
        }
        renameMetadata(folder);
        meta = new File(folder.getAbsolutePath() + File.separator + "metadata.xml.processed");
        final MsgInfoSet mis = MsgInfoSet.read(meta);
        mis.setLegNumber(SubmitUtil.getLegNumber(mis));
        return mis;
    }
}
