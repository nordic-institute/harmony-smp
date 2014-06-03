package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>This table keeps track of the attempts to send out a message.</p>
 * <p>It contains at least one row for each initial attempt to send out
 * the message and up to <em>n</em> rows for each retry.</p>
 *
 * @author Thorsten Niedzwetzki
 */
@Entity
@Table(name = "TB_RECEIPT_TRACKING_ATTEMPT")
public class ReceiptTrackingAttempt extends AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = 4884203371666072375L;


    /**
     * The timestamp of this attempt to send out the message
     */
    @Column(name = "TRANSMISSION")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date transmission;

    public Date getTransmission() {
        return transmission;
    }

    public void setTransmission(final Date transmission) {
        this.transmission = transmission;
    }
}