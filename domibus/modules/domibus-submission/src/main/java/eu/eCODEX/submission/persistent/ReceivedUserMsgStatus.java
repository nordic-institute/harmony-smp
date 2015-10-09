package eu.eCODEX.submission.persistent;


import eu.domibus.common.persistent.AbstractBaseEntity;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "TB_RECEIVED_MESSAGE_STATUS")
//TODO: CHECK: This query is fine for low amounts of messages in the db but maybe it needs to be refactored for high message volumes. LOADTEST THIS!
@NamedQueries({@NamedQuery(name = "ReceivedMessageStatus.listPendingMessages",
                           query = "SELECT r.msgInfo.messageId from ReceivedUserMsg r, ReceivedUserMsgStatus s WHERE r.id = s.msg.id and s.downloaded is null and s.deleted is null and s.consumed_by = :CONSUMED_BY"),
               @NamedQuery(name = "ReceivedMessageStatus.findByReceivedUserMessageId",
                           query = "SELECT s from ReceivedUserMsgStatus s WHERE s.msg.id = :RECEIVED_USER_MSG_ID"),
               @NamedQuery(name = "ReceivedMessageStatus.findPayloadsFromMessagesOlderThan",
                           query = "SELECT joined.id from ReceivedUserMsg r JOIN r.msgInfo.parts joined, ReceivedUserMsgStatus s WHERE r.id = s.msg.id and s.downloaded is null and s.received < :DELETE_DATE and s.deleted is null"),
               @NamedQuery(name = "ReceivedMessageStatus.deletePayloadsWithIds",
                           query = "UPDATE PartInfo p SET p.payloadData=null WHERE p.id IN :IDS"),
               @NamedQuery(name = "ReceivedMessageStatus.markAsDeleted",
                           query = "UPDATE ReceivedUserMsgStatus s SET s.deleted=:DELETED WHERE s.downloaded is null and s.received < :DELETE_DATE"),
               @NamedQuery(name = "ReceivedMessageStatus.markDownloadedAsDeleted",
                           query = "UPDATE ReceivedUserMsgStatus s SET s.deleted=:DELETED WHERE s.downloaded is not null and s.received is not null"),
               @NamedQuery(name = "ReceivedMessageStatus.findPayloadsFromDownloadedMessages",
                           query = "SELECT joined.id from ReceivedUserMsg r JOIN r.msgInfo.parts joined, ReceivedUserMsgStatus s WHERE r.id = s.msg.id and s.downloaded < :DELETE_DATE and s.deleted is null")})

//


/**
 * @author Christian Koch
 * This Entity is used to track the status of received messages
 *
 */
public class ReceivedUserMsgStatus extends AbstractBaseEntity {

    public ReceivedUserMsgStatus() {
        super();
    }

    public ReceivedUserMsgStatus(final ReceivedUserMsg msg) {
        super();
        this.msg = msg;
    }

    @OneToOne()
    @JoinColumn(name = "received_user_primary_key")
    // It would be nicer if the relation would be the other way around. As this would touch the original
    // BackendInterface as of now we refrain from this.
    private ReceivedUserMsg msg;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DOWNLOADED")
    private Date downloaded;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED")
    private Date deleted;

    @Column(name = "CONSUMED_BY")
    private String consumed_by;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RECEIVED")
    private final Date received = new Date();

    /**
     * Provides the corresponding {@link ReceivedUserMsg}
     *
     * @return the {@link ReceivedUserMsg} this status is corresponding to
     */
    public ReceivedUserMsg getMsg() {
        return this.msg;
    }


    /**
     * Sets the corresponding {@link ReceivedUserMsg}
     *
     * @param msg the {@link ReceivedUserMsg} this status is corresponding to
     */
    public void setMsg(final ReceivedUserMsg msg) {
        this.msg = msg;
    }

    /**
     * Provides the {@link Date} when the corresponding message was downloaded, or {@code null} if the message has not been downloaded yet
     *
     * @return the {@link Date} when the corresponding message was downloaded, or {@code null} if the message has not been downloaded yet
     */
    public Date getDownloaded() {
        return this.downloaded;
    }

    /**
     * Sets the {@link Date} when the corresponding message was downloaded.
     *
     * @param downloaded the {@link Date} when the corresponding message was downloaded. If set to {@code null} the
     *                   corresponding message will be considered as not downloaded yet
     */
    //TODO: Should this be set when a message is redownloaded?
    public void setDownloaded(final Date downloaded) {
        this.downloaded = downloaded;
    }


    /**
     * Provides the {@link Date} when the corresponding message was deleted, or {@code null} if the message has not been deleted yet
     *
     * @return the {@link Date} when the corresponding message was deleted, or {@code null} if the message has not been deleted yet
     */
    public Date getDeleted() {
        return this.deleted;
    }

    /**
     * Sets the {@link Date} when the corresponding message was deleted.
     *
     * @param deleted the {@link Date} when the corresponding message was deleted.
     */
    //TODO: Should this be settable to null?
    public void setDeleted(final Date deleted) {
        this.deleted = deleted;
    }

    /**
     * Provides the name of the consumer as {@link java.lang.String} this message was consumed by or {@code null} if the message was not consumed yet
     *
     * @return the name of the consumer as {@link java.lang.String} this message was consumed by or {@code null} if the message was not consumed yet
     */
    public String getConsumed_by() {
        return this.consumed_by;
    }

    /**
     * Sets the name of the consumer as {@link java.lang.String} this message was consumed by or {@code null} if the message was not consumed yet
     *
     * @param consumed_by the name of the consumer that matched first
     */
    public void setConsumed_by(String consumed_by) {
        this.consumed_by = consumed_by;
    }

    public Date getReceived() {
        return this.received;
    }
}
