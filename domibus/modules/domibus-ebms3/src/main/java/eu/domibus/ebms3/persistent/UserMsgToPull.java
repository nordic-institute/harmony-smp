package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.TempStore;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axis2.context.MessageContext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Collection;


/**
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_USER_MSG_TO_PULL")
@NamedQuery(name = "UserMsgToPull.getNextUserMsgToPull",
            query = "SELECT c FROM UserMsgToPull c WHERE c.mpc = ':mpc' AND c.sent = FALSE ORDER BY c.msgInfoSet.timeInMillis ASC")
public class UserMsgToPull extends MessageToSend {

    @Column(name = "MPC")
    private String mpc;


    public UserMsgToPull() {
        this.setStorageFolder(Constants.getSubmitFolder());
    }


    public UserMsgToPull(final MessageContext context, final MsgInfoSet mis) {
        this.setStorageFolder(Constants.getSubmitFolder());
        this.setMessageContext(context);
        this.mpc = Configuration.getMpc(mis.getPmode(), mis.getLegNumber());
        super.setMsgInfoSet(mis);
        this.getMsgInfoSet().setCreateTimeInMillis(System.currentTimeMillis());
    }

    public UserMsgToPull(final String tempGroup, final MsgInfoSet mis, final Collection<TempStore> attachmentData) {
        super(tempGroup, mis, attachmentData);

        this.setMsgInfoSet(mis);
        this.getMsgInfoSet().setCreateTimeInMillis(System.currentTimeMillis());
        this.setStorageFolder(Constants.getSubmitFolder());
    }


    public String getMpc() {
        return this.mpc;
    }

    public boolean isPulled() {
        return this.sent;
    }

    public void setPulled(final boolean pulled) {
        this.sent = pulled;
    }
}