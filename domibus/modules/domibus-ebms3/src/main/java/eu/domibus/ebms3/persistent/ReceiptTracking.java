package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This table keeps track of the messages that were sent out and whether a
 * receipts have been received for them
 *
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_RECEIPT_TRACKING")
@NamedQueries({

                      @NamedQuery(name = "ReceiptTracking.getReceiptTrackerForUserMsg",
                                  query = "SELECT rt FROM ReceiptTracking rt WHERE rt.messageId = :MESSAGE_ID"), @NamedQuery(name = "ReceiptTracking.setReceipt",
                                                                                                                             query = "UPDATE ReceiptTracking set receiptSignal = :RECEIPT, firstReception = :FIRST_RECEPTION where messageId = :MESSAGE_ID and receiptSignal is null"),
                      @NamedQuery(name = "ReceiptTracking.updateTrackingStatus",
                                  query = "UPDATE ReceiptTracking set status = :NEW_STATUS where messageId = :MESSAGE_ID and status = 'IN_PROCESS'"),

                      @NamedQuery(name = "ReceiptTracking.getAllWaitingForReceipt",
                                  query = "SELECT rt FROM ReceiptTracking rt WHERE rt.status = '" +
                                          ReceiptTracking.STATUS_IN_PROCESS + "'")})
public class ReceiptTracking extends AbstractBaseEntity implements Serializable {
    /**
     * Initial status: Message (re)transmitted and waiting for a receipt
     */
    public static final String STATUS_IN_PROCESS = "IN_PROCESS";
    /**
     * Final status: Receipt received
     */
    public static final String STATUS_RECEIPT_RECEIVED = "RECEIVED";
    /**
     * Final status: No receipt received past the final shutdown interval
     */
    public static final String STATUS_NO_RECEIPT = "NO_RECEIPT";
    /**
     * Final status: Unrecoverable error
     */
    public static final String STATUS_UNRECOVERABLE_ERROR = "ERROR";
    private static final long serialVersionUID = 1061327109897654528L;
    @Column(name = "MESSAGE_ID", unique = true)
    protected String messageId;
    @Column(name = "TO_URL")
    protected String toURL;
    @Column(name = "PMODE")
    protected String pmode;
    @Column(name = "STATUS")
    protected String status;
    @Column(name = "FIRST_RECEPTION")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date firstReception;
    @Lob
    @Column(name = "RECEIPT", length = 9999999)
    @Basic(fetch = FetchType.EAGER)
    protected String receiptSignal;
    /**
     * Number of times the message has been retransmitted
     */
    @Column(name = "RETRANSMISSIONS")
    protected Integer retries = 0;
    /**
     * The timestamp of the last retransmission
     */
    @Column(name = "LAST_TRANSMISSION")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastTransmission;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "RECEIPT_TRACKING_PK")
    private Set<ReceiptTrackingAttempt> attempts = new HashSet<ReceiptTrackingAttempt>();


    public ReceiptTracking() {
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void addAttempt(ReceiptTrackingAttempt attempt) {
        attempts.add(attempt);
    }

    public Collection<ReceiptTrackingAttempt> getAttempts() {
        return attempts;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getToURL() {
        return this.toURL;
    }

    public void setToURL(final String toURL) {
        this.toURL = toURL;
    }

    public String getPmode() {
        return this.pmode;
    }

    public void setPmode(final String pmode) {
        this.pmode = pmode;
    }

    public Date getFirstReception() {
        return this.firstReception;
    }

    public void setFirstReception(final Date firstReception) {
        this.firstReception = firstReception;
    }

    public String getReceiptSignal() {
        return this.receiptSignal;
    }

    public void setReceiptSignal(final String receiptSignal) {
        this.receiptSignal = receiptSignal;
    }

    /**
     * @return
     * @see #STATUS_IN_PROCESS
     * @see #STATUS_RECEIPT_RECEIVED
     * @see #STATUS_NO_RECEIPT
     * @see #STATUS_UNRECOVERABLE_ERROR
     */
    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * @return the retries
     */
    public int getRetries() {
        return ((this.retries != null) ? this.retries.intValue() : 0);
    }


    /**
     * @param retries the retries to set
     */
    public void setRetries(final int retries) {
        this.retries = new Integer(retries);
    }

    /**
     * @return the lastRetransmission
     */
    public Date getLastTransmission() {
        return this.lastTransmission;
    }

    /**
     * @param lastTransmission the lastRetransmission to set
     */
    public void setLastTransmission(final Date lastTransmission) {
        this.lastTransmission = lastTransmission;
    }
}