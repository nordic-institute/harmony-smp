package eu.domibus.ebms3.submit;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import eu.domibus.common.persistent.Attachment;
import eu.domibus.common.util.FileUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.PartInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.ReceivedUserMsgDAO;

import javax.activation.DataHandler;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

/**
 * This is a Callback class that simply saves the received user message
 * into database (the attachments of the received message are also
 * persisted in the "Received_Messages" folder). This class is simply
 * a convenience that can be used whenever a Callback class is needed.
 *
 * @author Hamid Ben Malek
 */
public class SaveReceivedMsg implements AxisCallback {
    private static final Logger log = Logger.getLogger(SaveReceivedMsg.class);
    private final ReceivedUserMsgDAO rumd = new ReceivedUserMsgDAO();

    public void onComplete() {
    }

    public void onError(final Exception ex) {
        log.error("Error occured in SaveReceivedMsg", ex);
    }

    public void onFault(final MessageContext ctx) {
        log.debug("SoapFault occured. For details see Envelope: " + ctx.getEnvelope().toString());
    }

    public void onMessage(final MessageContext msgCtx) {
        saveReceivedMessage(msgCtx);
    }

    public void saveReceivedMessage(final MessageContext msgCtx) {
        log.debug("onMessage(msgCtx): receiving a pulled user message...");
        MsgInfo msgInfo = (MsgInfo) msgCtx.getProperty(Constants.IN_MSG_INFO);
        if (msgInfo == null) {
            msgInfo = EbUtil.getMsgInfo();
        }
        if (msgInfo == null) {
            return;
        }
        final String dir = getSaveLocation(msgInfo.getMpc());
        final ReceivedUserMsg receivedUM = new ReceivedUserMsg(msgCtx, msgInfo);
        final Collection<PartInfo> parts = msgInfo.getParts();
        if ((parts == null) || parts.isEmpty()) {
            log.debug("There are no attachments in received pulled message");
            return;
        }

        final Options options = msgCtx.getOptions();
        if (options != null) {
            final String attachDir = (String) msgCtx.getConfigurationContext().getAxisConfiguration().getParameter(
                    org.apache.axis2.Constants.Configuration.ATTACHMENT_TEMP_DIR).getValue();
            if (attachDir != null) {
                options.setProperty(org.apache.axis2.Constants.Configuration.ATTACHMENT_TEMP_DIR, attachDir);
            }
        } else {
            log.warn("no options found in messageContext");
        }
        final Attachments atts = msgCtx.getAttachmentMap();
        for (final PartInfo part : parts) {
            final String cid = part.getCid();
            final DataHandler dh = atts.getDataHandler(cid);
            String name = cid;
            if (cid.indexOf(":") >= 0) {
                name = name.replaceAll(":", "-");
            }
            final String extension = FileUtil.getFileExtension(dh.getContentType());
            final String path = dir + File.separator + name + "." + extension;
            final File att = new File(path);
            log.debug("about to write attachment " + name + "." + extension);
            FileUtil.writeDataHandlerToFile(dh, att);
            final Attachment attach = new Attachment(path);
            attach.setContentID(cid);
            attach.setContentType(dh.getContentType());
            receivedUM.getAttachments().add(attach);
            writeEnvelope(msgCtx, dir);
            writeSoapHeader(msgCtx, dir);
        }

        rumd.persist(receivedUM);
        log.debug("Received message was saved");
    }

    private String getSaveLocation(String mpc) {
        if ((mpc == null) || mpc.trim().equals("")) {
            mpc = "default";
        }
        if (mpc.startsWith("mpc://")) {
            mpc = mpc.substring(6);
        } else if (mpc.startsWith("mpc//:")) {
            mpc = mpc.substring(6);
        } else if (mpc.startsWith("http://")) {
            mpc = mpc.substring(7);
        }
        mpc = mpc.replaceAll(":", "-");
        mpc = mpc.replaceAll("/", "_");
        final String dir = "Messages_" + mpc + File.separator + "Msg_" + getDate();
        final File location = new File(dir);
        if (location.exists() && location.isDirectory()) {
            return dir;
        }
        final String receivedMsgsFolder = Constants.getReceivedFolder();
        final String path = receivedMsgsFolder + File.separator + dir;
        final boolean b = new File(path).mkdirs();
        if (!b) {
            log.warn("Unable to create direcoty " + path);
        }
        return path;
    }

    private String getDate() {
        final Calendar now = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MMM.dd'@'HH.mm.ss");
        return sdf.format(now.getTime());
    }

    private void writeEnvelope(final MessageContext msgCtx, final String location) {
        try {
            final String file = location + File.separator + "envelope.xml";
            XMLUtil.prettyPrint(msgCtx.getEnvelope(), file);
        } catch (Exception ex) {
            log.error("Error while writing Envelope (prettyPrint)", ex);
        }
    }

    private void writeSoapHeader(final MessageContext msgCtx, final String location) {
        final OMElement header = (OMElement) msgCtx.getProperty(Constants.IN_SOAP_HEADER);
        if (header == null) {
            return;
        }
        try {
            final String file = location + "/SOAP-Header.xml";
            XMLUtil.prettyPrint(header, file);
        } catch (Exception ex) {
            log.error("Error while writing SOAPHeader (prettyPrint)", ex);
        }
    }
}