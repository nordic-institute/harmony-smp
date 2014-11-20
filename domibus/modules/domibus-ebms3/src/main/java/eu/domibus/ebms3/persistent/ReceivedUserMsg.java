package eu.domibus.ebms3.persistent;

import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.EbMessage;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_RECEIVED_USER_MSG")
//TODO: Maybe duplicate namedquery findUndownloaded
@NamedQueries({@NamedQuery(name = "ReceivedUserMsg.findByMessageId",
                           query = "SELECT m from ReceivedUserMsg m WHERE m.msgInfo.messageId = :MESSAGE_ID"),
               @NamedQuery(name = "ReceivedUserMsg.findUndownloaded",
                           query = "SELECT r from ReceivedUserMsg r, ReceivedUserMsgStatus s WHERE r.id = s.msg.id and s.downloaded is null and s.deleted is null and s.consumed_by = :CONSUMED_BY"),
               @NamedQuery(name = "ReceivedUserMsg.countForMessageId",   //This is needed for duplicate elimination
                           query = "SELECT COUNT(m) from ReceivedUserMsg m WHERE m.msgInfo.messageId = :MESSAGE_ID ")})

public class ReceivedUserMsg extends EbMessage implements Serializable {
    private static final long serialVersionUID = -1765117661137572845L;
    private static final Logger LOG = Logger.getLogger(ReceivedUserMsg.class);
    // embedded
    private MsgInfo msgInfo;

    @Column(name = "RAW_XML", length = 2147483647)
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String rawXMLMessage;

    public ReceivedUserMsg() {
        this.setStorageFolder(Constants.getReceivedFolder());
    }

   /* public ReceivedUserMsg(final ConfigurationContext confgCtx) {
        super(confgCtx);
        setStorageFolder(Constants.getReceivedFolder());
    }*/


    public ReceivedUserMsg(final MessageContext context, final MsgInfo mi) {
        EbMessage.configContext = context.getConfigurationContext();
        this.setStorageFolder(Constants.getReceivedFolder());
        this.setMessageContext(context);
        this.msgInfo = mi;

        final Iterator<OMElement> iterator = context.getEnvelope().getHeader().getChildren();

        OMElement messaging = null;

        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (obj instanceof OMElement) {
                final OMElement omElement = (OMElement) obj;

                if ("Messaging".equals(omElement.getLocalName())) {
                    messaging = omElement;
                    break;
                }
            }
        }

        this.setRawXMLMessage(messaging.toString());

        this.processPayloads(context, mi.getParts());
    }

    public MsgInfo getMsgInfo() {
        return this.msgInfo;
    }

    public String getMessageId() {
        return this.msgInfo.getMessageId();

    }

    public String getRefToMessageId() {
        return this.msgInfo.getRefToMessageId();
    }

    public String getMpc() {
        return this.msgInfo.getMpc();
    }

    public String getFromParty() {
        return this.msgInfo.getFromParties().iterator().next().getPartyId();
    }

    public String getToParty() {
        return this.msgInfo.getToParties().iterator().next().getPartyId();
    }

    public String getService() {
        return this.msgInfo.getService();
    }

    public String getAction() {
        return this.msgInfo.getAction();
    }

    public String getRawXMLMessage() {
        return this.rawXMLMessage;
    }

    public void setRawXMLMessage(final String rawXMLMessage) {
        this.rawXMLMessage = rawXMLMessage;
    }

    private void processPayloads(final MessageContext msgCtx, final Collection<PartInfo> partInfos) {
        final Attachments atts = msgCtx.getAttachmentMap();
        final Set<String> keys = atts.getMap().keySet();
        for (final PartInfo p : partInfos) {
            final String cid = p.getCid();
            if (keys.contains(cid)) {
                try {
                    p.setPayloadData(IOUtils.toByteArray(atts.getDataHandler(cid).getInputStream()));
                } catch (IOException e) {
                    ReceivedUserMsg.LOG.error(e);
                    throw new RuntimeException(e);
                }
            } else {
                if ((cid == null) || cid.isEmpty() || cid.startsWith("#")) {
                    //we have found the "bodyload"
                    p.setPayloadData(msgCtx.getEnvelope().getBody().getFirstElement().toString().getBytes());
                    p.setBody(true);
                }
            }
        }
    }

}