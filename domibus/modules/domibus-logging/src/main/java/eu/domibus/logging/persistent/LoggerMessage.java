package eu.domibus.logging.persistent;


import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * This Class represents a database table for logging ebms messages
 *
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 */
@Entity
@Table(name = "TB_LOGGER_MESSAGE")
public class LoggerMessage extends AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = 1200796957717630663L;

    public final static String MESSAGE_SENT_INIT_STATUS = "SENT_INIT";
    public final static String MESSAGE_SENT_OK_STATUS = "SENT_OK";
    public final static String MESSAGE_SENT_KO_STATUS = "SENT_KO";
    public final static String MESSAGE_RECEIVED_STATUS = "MESSAGE_RECEIVED";
    public final static String MESSAGE_DOWNLOADED_STATUS = "MESSAGE_DOWNLOADED";

    @Column(name = "MESSAGE_ID")
    protected String messageId;

    @Column(name = "SENDER")
    protected String sender;

    @Column(name = "FROM_ROLE")
    protected String fromRole;

    @Column(name = "RECIPIENT")
    protected String recipient;

    @Column(name = "TO_ROLE")
    protected String toRole;

    @Column(name = "SERVICE")
    protected String service;

    @Column(name = "ACTION")
    protected String action;

    @Column(name = "CONVERSATION_ID")
    protected String conversationId;

    @Column(name = "PMODE")
    protected String pmode;

    @Column(name = "TIMESTAMP")
    protected Date timestamp;

    @Column(name = "STATUS")
    protected String status;


    public LoggerMessage() {
        this.timestamp = new Date();
    }


    public LoggerMessage(final String messageId, final String sender, final String fromRole, final String recipient,
                         final String toRole, final String service, final String action, final String conversationId,
                         final String pmode, final String status) {
        super();
        this.messageId = messageId;
        this.sender = sender;
        this.fromRole = fromRole;
        this.recipient = recipient;
        this.toRole = toRole;
        this.service = service;
        this.action = action;
        this.conversationId = conversationId;
        this.pmode = pmode;
        this.status = status;

        this.timestamp = new Date();
    }

    public LoggerMessage(final MessageInfo mi) {
        this.messageId = mi.getMessageId();
        this.sender = mi.getSender();
        this.fromRole = mi.getFromRole();
        this.recipient = mi.getRecipient();
        this.toRole = mi.getToRole();
        this.service = mi.getService();
        this.action = mi.getAction();
        this.conversationId = mi.getConversationId();
        this.pmode = mi.getPmode();
        this.status = mi.getStatus();


        this.timestamp = new Date();
    }

    /*
     * Setter and Getter
     */
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPmode() {
        return this.pmode;
    }

    public void setPmode(final String pmode) {
        this.pmode = pmode;
    }


    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getFromRole() {
        return this.fromRole;
    }

    public void setFromRole(final String fromRole) {
        this.fromRole = fromRole;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public String getToRole() {
        return this.toRole;
    }

    public void setToRole(final String toRole) {
        this.toRole = toRole;
    }

    public String getService() {
        return this.service;
    }

    public void setService(final String service) {
        this.service = service;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getConversationId() {
        return this.conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}