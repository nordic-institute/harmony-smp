package eu.domibus.ebms3.persistent;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.EbMessage;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_RECEIVED_USER_MSG")
@NamedQueries({@NamedQuery(name = "ReceivedUserMsg.findByMessageId",
                           query = "SELECT m from ReceivedUserMsg m WHERE m.msgInfo.messageId = :MESSAGE_ID")})
public class ReceivedUserMsg extends EbMessage implements Serializable {
    private static final long serialVersionUID = -1765117661137572845L;

    // embedded
    private MsgInfo msgInfo;

    public ReceivedUserMsg() {
        setStorageFolder(Constants.getReceivedFolder());
    }

    public ReceivedUserMsg(final ConfigurationContext confgCtx) {
        super(confgCtx);
        setStorageFolder(Constants.getReceivedFolder());
    }

    public ReceivedUserMsg(final MessageContext context, final MsgInfo mi) {
        configContext = context.getConfigurationContext();
        setStorageFolder(Constants.getReceivedFolder());
        setMessageContext(context);
        this.msgInfo = mi;
    }

    public MsgInfo getMsgInfo() {
        return msgInfo;
    }

    public String getMessageId() {
        return msgInfo.getMessageId();

    }

    public String getRefToMessageId() {
        return msgInfo.getRefToMessageId();
    }

    public String getMpc() {
        return msgInfo.getMpc();
    }


    public String getFromParty() {
        return msgInfo.getFromParties().iterator().next().getPartyId();
    }

    public String getToParty() {
        return msgInfo.getToParties().iterator().next().getPartyId();
    }


    public String getService() {
        return msgInfo.getService();
    }


    public String getAction() {
        return msgInfo.getAction();
    }

}