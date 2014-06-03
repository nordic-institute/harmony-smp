package eu.domibus.ebms3.persistent;

import org.apache.axiom.util.UIDGenerator;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;

import javax.persistence.*;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kochc01
 * Date: 09.12.13
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "TB_MESSAGE_TO_SEND")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MessageToSend extends EbMessage {

    private MsgInfoSet msgInfoSet;

    @Column(name = "SENT")
    protected boolean sent;

    public MessageToSend(final File folder, final MsgInfoSet mis) {
        super(folder, mis);
        this.setMsgInfoSet(mis);
    }

    public MessageToSend() {
        super();
    }

    public final MsgInfoSet getMsgInfoSet() {
        return this.msgInfoSet;
    }


    protected final void setMsgInfoSet(MsgInfoSet mis) {
        if (mis.getMessageId() == null) {
            mis.setMessageId(UIDGenerator.generateURNString());
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
        return Configuration.getAddress(getMsgInfoSet().getPmode(), getMsgInfoSet().getLegNumber());
    }

    public final String getMep() {
        return Configuration.getMep(getMsgInfoSet().getPmode());
    }

    public final String getPmode() {
        return getMsgInfoSet().getPmode();
    }

    public final String getCallbackClass() {
        return getMsgInfoSet().getCallbackClass();
    }

    public final long getTimeInMillis() {
        return getMsgInfoSet().getTimeInMillis();
    }

    public final int getLegNumber() {
        return getMsgInfoSet().getLegNumber();
    }

    public final String getMessageId() {
        return getMsgInfoSet().getMessageId();
    }
}
