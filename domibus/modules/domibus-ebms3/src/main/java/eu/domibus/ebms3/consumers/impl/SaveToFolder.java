package eu.domibus.ebms3.consumers.impl;

import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.consumers.EbConsumer;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.PartInfo;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hamid Ben Malek
 */
public class SaveToFolder implements EbConsumer {
    static Logger log = Logger.getLogger(SaveToFolder.class);
    protected Map<String, String> parameters;

    public void setParameters(final Map<String, String> properties) {
        this.parameters = properties;
    }

    public void push() {
        final MsgInfo msgInfo = EbUtil.getMsgInfo();
        final MessageContext msgCtx = MessageContext.getCurrentMessageContext();
        final String directory = this.getSaveLocation(msgInfo.getMpc());
        this.writeAttachments(msgCtx, msgInfo.getParts(), directory);
        this.writeEnvelope(msgCtx, directory);
        this.writeSoapHeader(msgCtx, directory);

    }

    public void pull() {
        throw new UnsupportedOperationException();
    }

    private String getSaveLocation(String mpc) {
        String dir = this.parameters.get("directory");
        if ((mpc == null) || "".equals(mpc.trim())) {
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
        if ((dir == null) || "".equals(dir.trim())) {
            dir = "Messages_" + mpc + File.separator + "Msg_" + this.getDate();
        } else {
            dir = dir + "_" + mpc + File.separator + "Msg_" + this.getDate();
        }
        final File location = new File(dir);
        if (location.exists() && location.isDirectory()) {
            return dir;
        }
        final String receivedMsgsFolder = eu.domibus.ebms3.module.Constants.getReceivedFolder();
        final String path = receivedMsgsFolder + File.separator + dir;
        final boolean b = new File(path).mkdirs();
        if (!b) {
            SaveToFolder.log.error("Unable to create directory " + path);
        }
        return path;
    }

    private void writeEnvelope(final MessageContext msgCtx, final String location) {
        try {
            final String file = location + File.separator + "envelope.xml";
            XMLUtil.prettyPrint(msgCtx.getEnvelope(), file);
        } catch (Exception ex) {
            SaveToFolder.log.error("Error while writing Envelope (prettyPrint)", ex);
        }
    }

    private void writeAttachments(final MessageContext msgCtx, final Collection<PartInfo> parts,
                                  final String location) {
        if ((msgCtx == null) || (parts == null) || (parts.size() <= 0)) {
            return;
        }
        final List<String> cids = new ArrayList<String>();
        final List<String> cts = new ArrayList<String>();
        for (final PartInfo part : parts) {
            cids.add(part.getCid());
            cts.add(part.getMimeType());
        }
        WSUtil.writeAttachments(msgCtx, cids, cts, new File(location));
    }

    private void writeSoapHeader(final MessageContext msgCtx, final String location) {
        final OMElement header = (OMElement) msgCtx.getProperty(Constants.IN_SOAP_HEADER);
        if (header == null) {
            return;
        }
        try {

            final String file = location + File.separator + "SOAP-Header.xml";
            XMLUtil.prettyPrint(header, file);
        } catch (Exception ex) {
            SaveToFolder.log.error("Error while writing SOAPHeader (prettyPrint)", ex);
        }
    }

    private String getDate() {
        final Calendar now = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MMM.dd'@'HH.mm.ss");
        return sdf.format(now.getTime());
    }
}