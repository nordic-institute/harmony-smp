package eu.domibus.backend.consumer.helper;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import eu.domibus.backend.db.dao.MessageDAO;
import eu.domibus.backend.db.model.Message;
import eu.domibus.backend.db.model.Payload;
import eu.domibus.backend.util.IOUtils;
import eu.domibus.common.util.FileUtil;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.PartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BackendConsumerHelper {
    /**
     * The log.
     */
    private static final Logger log = Logger.getLogger(BackendConsumerHelper.class);

    /**
     * The message dao.
     */
    @Autowired
    private MessageDAO messageDAO;

    @Transactional
    public void push(final Map<String, String> parameters) {
        final MsgInfo msgInfo = EbUtil.getMsgInfo();
        final MessageContext msgCtx = MessageContext.getCurrentMessageContext();

        final File directory = getSaveLocation(parameters, msgInfo.getMpc());

        final Message message = saveMessage(msgInfo, directory);

        writeMessaging(msgCtx, directory);

        final File messageFile = new File(message.getDirectory(), eu.domibus.backend.module.Constants.MESSAGING_FILE_NAME);

        final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging = eu.domibus.backend.util.Converter_1_1.convertFileToMessagingE(messageFile);

        writeAttachments(msgCtx, directory, msgInfo.getParts(), message, messaging);

        log.log(eu.domibus.logging.level.Message.MESSAGE, eu.domibus.backend.util.Converter_1_1.convertUserMessageToMessageInfo(messaging.getMessaging().getUserMessage()[0],
                                                                                                                                    messaging.getMessaging().getUserMessage()[0]
                                                                                                                                            .getMessageInfo().getMessageId()
                                                                                                                                            .getNonEmptyString(), "BackendConsumer",
                                                                                                                                    "push",
                                                                                                                                    eu.domibus.logging.persistent.LoggerMessage.MESSAGE_RECEIVED_STATUS));
    }

    /**
     * Save message.
     *
     * @param msgInfo   the msg info
     * @param directory the directory
     */
    private Message saveMessage(final MsgInfo msgInfo, final File directory) {
        final Message message = new Message();
        message.setMessageDate(new Date());
        message.setMessageUID(msgInfo.getMessageId());
        message.setPmode(msgInfo.getPmode());
        message.setDirectory(directory.getAbsolutePath());

        messageDAO.save(message);

        return message;
    }

    /**
     * Gets the save location.
     *
     * @param mpc the mpc
     * @return the save location
     */
    private File getSaveLocation(final Map<String, String> parameters, String mpc) {
        String dir = parameters.get("directory");
        if (mpc == null || mpc.trim().equals("")) {
            mpc = "default";
        }
        if (mpc.startsWith("mpc://")) {
            mpc = mpc.substring(6);
        }
        if (mpc.startsWith("http://")) {
            mpc = mpc.substring(7);
        }
        mpc = mpc.replaceAll(":", "-");
        mpc = mpc.replaceAll("/", "_");
        if (dir == null || dir.trim().equals("")) {
            dir = "Messages_" + mpc + File.separator + "Msg_" + getDate() + "_" + UUID.randomUUID();
        } else {
            dir = dir + "_" + mpc + File.separator + "Msg_" + getDate() + "_" + UUID.randomUUID();
        }
        final File location = new File(dir);
        if (location.exists() && location.isDirectory()) {
            return location;
        }

        final String receivedMsgsFolder = eu.domibus.backend.module.Constants.getMessagesFolder();

        final String path = receivedMsgsFolder + File.separator + dir;
        final File saveFolder = new File(path);
        if (!saveFolder.mkdirs()) {
            log.error("Unable to create direcoty " + path);
        }
        return saveFolder;
    }

    /**
     * Write messaging.
     *
     * @param msgCtx    the msg ctx
     * @param directory the directory
     */
    private void writeMessaging(final MessageContext msgCtx, final File directory) {
        if (msgCtx == null) {
            return;
        }

        final Attachments atts = msgCtx.getAttachmentMap();

        if (atts == null) {
            return;
        }

        final java.util.Iterator<OMElement> iterator = msgCtx.getEnvelope().getHeader().getChildren();

        org.apache.axiom.om.OMElement messaging = null;

        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (obj instanceof OMElement) {
                final OMElement omElement = (OMElement) obj;

                if (omElement.getLocalName().equalsIgnoreCase(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.MY_QNAME.getLocalPart())) {
                    messaging = omElement;
                }
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(directory, eu.domibus.backend.module.Constants.MESSAGING_FILE_NAME));

            IOUtils.write(messaging.toString(), fileOutputStream);
        } catch (Exception e) {
            log.error("Unable to write messaging file", e);
        } finally {
            eu.domibus.backend.util.IOUtils.closeQuietly(fileOutputStream);
        }
    }

    /**
     * Write attachments.
     *
     * @param msgCtx    the msg ctx
     * @param parts     the parts
     * @param directory the directory
     */
    private void writeAttachments(final MessageContext msgCtx, final File directory, final Collection<PartInfo> parts, final Message message,
                                  final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging) {
        if (msgCtx == null) {
            return;
        }

        final Attachments atts = msgCtx.getAttachmentMap();

        final List<Payload> payloads = new ArrayList<Payload>();

        final SOAPBody soapBody = msgCtx.getEnvelope().getBody();

        final boolean _1_1_Version = soapBody.getFirstElement() != null;


        if (_1_1_Version) {
            final String payloadFileName = eu.domibus.backend.module.Constants.BODYLOAD_FILE_NAME_FORMAT;

            final File att = new File(directory, payloadFileName);
            FileUtil.writeToFile(att, new ByteArrayInputStream(soapBody.getFirstElement().toString().getBytes()));

            final Payload payload = new Payload();

            payload.setFileName(payloadFileName);
            payload.setMessage(message);

            if (messaging != null && messaging.getMessaging() != null &&
                messaging.getMessaging().getUserMessage() != null &&
                messaging.getMessaging().getUserMessage().length > 0 &&
                messaging.getMessaging().getUserMessage()[0].getPayloadInfo() != null &&
                messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo() != null &&
                messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length > 0) {
                for (final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo partInfo : messaging.getMessaging().getUserMessage()[0].getPayloadInfo()
                                                                                                                                                   .getPartInfo()) {
                    if (partInfo.getHref() != null && (partInfo.getHref().toString().startsWith("#") || !partInfo.getHref().toString().toLowerCase().startsWith("cid:"))) {
                        payload.setPayloadId(partInfo.getHref().toString());
                        break;
                    }
                }
            }

            payload.setContentType(eu.domibus.backend.module.Constants.XML_MYMETYPE);
            payload.setBodyload(true);

            payloads.add(payload);
        }


        int counter = 0;

        for (final String contentID : atts.getAllContentIDs()) {
            final DataHandler dh = atts.getDataHandler(contentID);

            if (dh == null || counter == 0) {
                counter++;

                continue;
            }

            final String payloadFileName = MessageFormat.format(eu.domibus.backend.module.Constants.PAYLOAD_FILE_NAME_FORMAT, counter);

            final File att = new File(directory, payloadFileName);
            FileUtil.writeDataHandlerToFile(dh, att);
            final Payload payload = new Payload();

            payload.setFileName(payloadFileName);
            payload.setMessage(message);

            payload.setContentType(dh.getContentType());

            payload.setPayloadId(contentID);

            payload.setBodyload(false);

            payloads.add(payload);

            counter++;
        }

        message.setPayloads(payloads);

        messageDAO.update(message);
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    private String getDate() {
        final Calendar now = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat(eu.domibus.backend.module.Constants.PATTERN_DATE_FORMAT);
        return sdf.format(now.getTime());
    }

}
