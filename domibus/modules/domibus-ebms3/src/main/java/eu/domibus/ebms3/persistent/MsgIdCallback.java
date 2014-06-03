package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_MSG_ID_CALLBACK")
@NamedQuery(name = "MsgIdCallback.findByMsgId",
            query = "SELECT c FROM MsgIdCallback c WHERE c.messageId = :MESSAGEID")
public class MsgIdCallback extends AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = 1352148385354239611L;

    @Column(name = "MESSAGE_ID")
    protected String messageId = null;

    @Column(name = "PMODE")
    protected String pmode = null;

    @Column(name = "LEG_NUMBER")
    protected int legNumber = 1;

    @Column(name = "CALLBACK_CLASS")
    protected String callbackClass;

    public MsgIdCallback() {
    }

    public MsgIdCallback(final String messageId, final String callbackClass) {
        this.messageId = messageId;
        this.callbackClass = callbackClass;
    }

    public MsgIdCallback(final String messageId, final String pmode, final int leg, final String callbackClass) {
        this.messageId = messageId;
        this.pmode = pmode;
        this.legNumber = leg;
        this.callbackClass = callbackClass;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getPmode() {
        return pmode;
    }

    public void setPmode(final String pmode) {
        this.pmode = pmode;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public void setLegNumber(final int legNumber) {
        this.legNumber = legNumber;
    }

    public String getCallbackClass() {
        return callbackClass;
    }

    public void setCallbackClass(final String callbackClass) {
        this.callbackClass = callbackClass;
    }
}