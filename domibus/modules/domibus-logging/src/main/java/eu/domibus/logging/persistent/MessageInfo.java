package eu.domibus.logging.persistent;


/**
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 */

public class MessageInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1200796957717630663L;

    protected String messageId;
    protected String sender;
    protected String fromRole;
    protected String recipient;
    protected String toRole;
    protected String service;
    protected String action;
    protected String conversationId;
    protected String pmode;
    protected String status;

    public MessageInfo() {
    }

    public MessageInfo(final String messageId, final String sender, final String fromRole, final String recipient,
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
    }


    public String getPmode() {
        return pmode;
    }


    public void setPmode(final String pmode) {
        this.pmode = pmode;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getFromRole() {
        return fromRole;
    }

    public void setFromRole(final String fromRole) {
        this.fromRole = fromRole;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public String getToRole() {
        return toRole;
    }

    public void setToRole(final String toRole) {
        this.toRole = toRole;
    }

    public String getService() {
        return service;
    }

    public void setService(final String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

}