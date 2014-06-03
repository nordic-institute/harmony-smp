package eu.domibus.ebms3.persistent;

import org.apache.axis2.context.MessageContext;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.MsgInfoSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.File;


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
        setStorageFolder(Constants.getSubmitFolder());
    }


    public UserMsgToPull(final MessageContext context, final MsgInfoSet mis) {
        setStorageFolder(Constants.getSubmitFolder());
        setMessageContext(context);
        this.mpc = Configuration.getMpc(mis.getPmode(), mis.getLegNumber());
        super.setMsgInfoSet(mis);
        getMsgInfoSet().setCreateTimeInMillis(System.currentTimeMillis());
    }

    public UserMsgToPull(final File folder, final MsgInfoSet mis) {
        super(folder, mis);

        setMsgInfoSet(mis);
        getMsgInfoSet().setCreateTimeInMillis(System.currentTimeMillis());
        setStorageFolder(Constants.getSubmitFolder());
    }


    public String getMpc() {
        return mpc;
    }

    public boolean isPulled() {
        return sent;
    }

    public void setPulled(final boolean pulled) {
        this.sent = pulled;
    }
}