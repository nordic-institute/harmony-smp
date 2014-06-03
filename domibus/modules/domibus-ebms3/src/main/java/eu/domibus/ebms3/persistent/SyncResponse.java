package eu.domibus.ebms3.persistent;

import org.apache.log4j.Logger;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.MsgInfoSet;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.File;

/**
 * This class represents messages that should be sent in the back channel
 * as a response to a request in the "Two-Way/Sync" MEP.
 *
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_SYNC_RESPONSE")
public class SyncResponse extends MessageToSend {

    private static final Logger log = Logger.getLogger(SyncResponse.class);

    public SyncResponse() {
        this.setStorageFolder(Constants.getSubmitFolder());
    }

    public SyncResponse(final File folder, final MsgInfoSet mis) {
        super(folder, mis);
        setStorageFolder(Constants.getSubmitFolder());
    }

    public String getMpc() {
        return Configuration.getMpc(getMsgInfoSet().getPmode(), getMsgInfoSet().getLegNumber());
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(final boolean sent) {
        this.sent = sent;
    }
}