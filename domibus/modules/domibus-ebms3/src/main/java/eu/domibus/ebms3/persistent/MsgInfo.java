package eu.domibus.ebms3.persistent;

import eu.domibus.ebms3.config.Party;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//import eu.domibus.ebms3.pmodes.Party;

/**
 * @author Hamid Ben Malek
 */
@Root
@Embeddable
public class MsgInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1200796957717630663L;

    @Element(required = false)
    @Column(name = "MPC")
    protected String mpc;

    @Element(required = false)
    @Column(name = "MESSAGE_ID")
    protected String messageId;

    @Column(name = "REF_TO_MESSAGE_ID")
    @Element(required = false)
    protected String refToMessageId;

    @JoinColumn(name = "FROM_PARTIES")
    @ElementList(required = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    protected Set<Party> fromParties = new HashSet<Party>();

    @Column(name = "FROM_ROLE")
    @Element(required = false)
    protected String fromRole;

    @JoinColumn(name = "TO_PARTIES")
    @ElementList(required = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    protected Set<Party> toParties = new HashSet<Party>();

    @Column(name = "TO_ROLE")
    @Element(required = false)
    protected String toRole;

    @Column(name = "AGREEMENT_REF")
    @Element(required = false)
    protected String agreementRef;

    @Column(name = "PMODE")
    @Element(required = false)
    protected String pmode;

    @Column(name = "SERVICE")
    @Element(required = false)
    protected String service;

    @Column(name = "ACTION")
    @Element(required = false)
    protected String action;

    @Column(name = "CONVERSATION_ID")
    @Element(required = false)
    protected String conversationId;

    @ElementList(required = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "MSG_INFO_ID")
    protected Set<Property> messageProperties = new HashSet<Property>();

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "MSG_INFO_ID")
    protected Set<PartInfo> parts = new HashSet<PartInfo>();

    public MsgInfo() {
    }

    public MsgInfo(final String mpc, final String messageId, final String refToMessageId, final String agreementRef,
                   final String pmode, final String service, final String action, final String conversationId) {
        this.mpc = mpc;
        this.messageId = messageId;
        this.refToMessageId = refToMessageId;
        this.agreementRef = agreementRef;
        this.pmode = pmode;
        this.service = service;
        this.action = action;
        this.conversationId = conversationId;
    }

    public void addFromParty(final String type, final String partyId) {
        final Party p = new Party(type, partyId);
        fromParties.add(p);
    }

    public void addToParty(final String type, final String partyId) {
        final Party p = new Party(type, partyId);
        toParties.add(p);
    }

    public void addMessageProperty(final String name, final String value) {
        final Property p = new Property(name, value);
        messageProperties.add(p);
    }

    public PartInfo addPartInfo(final String href, final String schemaLocation, final String desc) {
        final PartInfo part = new PartInfo(href, schemaLocation, desc);
        parts.add(part);
        return part;
    }

    public String getMpc() {
        return mpc;
    }

    public void setMpc(final String mpc) {
        this.mpc = mpc;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getRefToMessageId() {
        return refToMessageId;
    }

    public void setRefToMessageId(final String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    public Collection<Party> getFromParties() {
        return fromParties;
    }

    public void setFromParties(final Set<Party> fromParties) {
        this.fromParties = fromParties;
    }

    public String getFromRole() {
        return fromRole;
    }

    public void setFromRole(final String fromRole) {
        this.fromRole = fromRole;
    }

    public Collection<Party> getToParties() {
        return toParties;
    }

    public void setToParties(final Set<Party> toParties) {
        this.toParties = toParties;
    }

    public String getToRole() {
        return toRole;
    }

    public void setToRole(final String toRole) {
        this.toRole = toRole;
    }

    public String getAgreementRef() {
        return agreementRef;
    }

    public void setAgreementRef(final String agreementRef) {
        this.agreementRef = agreementRef;
    }

    public String getPmode() {
        return pmode;
    }

    public void setPmode(final String pmode) {
        this.pmode = pmode;
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

    public Collection<Property> getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(final Set<Property> messageProperties) {
        this.messageProperties = messageProperties;
    }

    public Collection<PartInfo> getParts() {
        return parts;
    }

    public void setParts(final Set<PartInfo> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "MsgInfo [mpc=" + mpc + ", messageId=" + messageId + ", refToMessageId=" + refToMessageId +
               ", fromParties=" + fromParties + ", fromRole=" + fromRole + ", toParties=" + toParties + ", toRole=" +
               toRole + ", agreementRef=" + agreementRef + ", pmode=" + pmode + ", service=" + service + ", action=" +
               action + ", conversationId=" + conversationId + ", messageProperties=" + messageProperties + ", parts=" +
               parts + "]";
    }


}