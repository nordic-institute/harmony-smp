package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.TempStore;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;

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

    public SyncResponse(final String tempGroup, final MsgInfoSet mis, final Collection<TempStore> attachmentData) {
        super(tempGroup, mis, attachmentData);
        this.setStorageFolder(Constants.getSubmitFolder());
    }

    public String getMpc() {
        return Configuration.getMpc(this.getMsgInfoSet().getPmode(), this.getMsgInfoSet().getLegNumber());
    }

    public boolean isSent() {
        return this.sent;
    }

    public void setSent(final boolean sent) {
        this.sent = sent;
    }
}