package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.TempStore;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.util.UIDGenerator;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: kochc01
 * Date: 09.12.13
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */

@NamedQueries({@NamedQuery(name = "MessageToSend.findFilePathByMessageID",
                           query = "SELECT m.msgInfoSet.payloads.bodyPayload.fileName FROM MessageToSend m WHERE m.msgInfoSet.messageId = :MESSAGE_ID")})

@Entity
@Table(name = "TB_MESSAGE_TO_SEND")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MessageToSend extends EbMessage {

    private MsgInfoSet msgInfoSet;

    @Column(name = "SENT")
    protected boolean sent;

    public MessageToSend(final String tempGroup, final MsgInfoSet mis, final Collection<TempStore> attachmentData) {
        super(tempGroup, mis, attachmentData);
        this.setMsgInfoSet(mis);
    }

    public MessageToSend() {
        super();
    }


    public final MsgInfoSet getMsgInfoSet() {
        return this.msgInfoSet;
    }


    protected final void setMsgInfoSet(final MsgInfoSet mis) {
        if (mis.getMessageId() == null) {
            mis.setMessageId(EbUtil.generateMessageID());
        }
        if (mis.getConversationId() == null) {
            mis.setConversationId(UIDGenerator.generateURNString());
        }
        if (mis.getTimeInMillis() == 0) {
            mis.setCreateTimeInMillis(System.currentTimeMillis());
        }
        this.msgInfoSet = mis;
        super.setMsgInfoSet(mis);
    }

    public final String getToURL() {
        return Configuration.getAddress(this.getMsgInfoSet().getPmode(), this.getMsgInfoSet().getLegNumber());
    }

    public final String getMep() {
        return Configuration.getMep(this.getMsgInfoSet().getPmode());
    }

    public final String getPmode() {
        return this.getMsgInfoSet().getPmode();
    }

    public final String getCallbackClass() {
        return this.getMsgInfoSet().getCallbackClass();
    }

    public final long getTimeInMillis() {
        return this.getMsgInfoSet().getTimeInMillis();
    }

    public final int getLegNumber() {
        return this.getMsgInfoSet().getLegNumber();
    }

    public final String getMessageId() {
        return this.getMsgInfoSet().getMessageId();
    }
}
