package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.TempStore;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.MsgInfoSet;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_USER_MSG_TO_PUSH")
@NamedQueries({@NamedQuery(name = "UserMsgToPush.findMessagesToPush",
                           query = "SELECT c FROM UserMsgToPush c WHERE (c.mep = 'One-Way/Push' OR (c.mep = 'Two-Way/Sync'  AND c.msgInfoSet.legNumber = 1) OR (c.mep = 'Two-Way/Push-And-Push' AND c.msgInfoSet.legNumber = 1) OR (c.mep = 'Two-Way/Push-And-Pull' AND c.msgInfoSet.legNumber = 1)) AND c.sent = FALSE ORDER BY c.msgInfoSet.timeInMillis ASC"),
               @NamedQuery(name = "UserMsgToPush.setRetransmit",
                           query = "UPDATE UserMsgToPush u set u.sent = FALSE where u.msgInfoSet.messageId = :MESSAGE_ID"),
               @NamedQuery(name = "UserMsgToPush.findByMessageId",
                           query = "SELECT m from UserMsgToPush m WHERE m.msgInfoSet.messageId = :MESSAGE_ID")})

public class UserMsgToPush extends MessageToSend {

    //read by query UserMsgToPush.findMessagesToPush
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    @Column(name = "MEP")
    private String mep;

    public UserMsgToPush() {
        super();
        this.setStorageFolder(Constants.getSubmitFolder());
    }

    public UserMsgToPush(final String tempGroup, final MsgInfoSet mis, final Collection<TempStore> attachmentData) {
        super(tempGroup, mis, attachmentData);
        this.setStorageFolder(Constants.getSubmitFolder());
        this.setMsgInfoSet(mis);
        this.mep = Configuration.getMep(this.getMsgInfoSet().getPmode());
    }


    public boolean isPushed() {
        return this.sent;
    }

    public void setPushed(final boolean pushed) {
        this.sent = pushed;
    }


    /**
     * @return the messageId
     */

}