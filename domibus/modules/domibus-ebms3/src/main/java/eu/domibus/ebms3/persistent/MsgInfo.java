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

    /**
     * Adds party to collection of senders
     *
     * @param type    the type of the sender party to add as {@link String}
     * @param partyId the party id of the sender to add as {@link String}
     */
    public void addFromParty(final String type, final String partyId) {
        final Party p = new Party(type, partyId);
        this.fromParties.add(p);
    }

    /**
     * Adds party to collection of receivers
     *
     * @param type    the type of the receiver party to add as {@link String}
     * @param partyId the party id of the receiver to add as {@link String}
     */
    public void addToParty(final String type, final String partyId) {
        final Party p = new Party(type, partyId);
        this.toParties.add(p);
    }

    /**
     * Adds message properties to this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @param name  key of the property as {@link String}
     * @param value value of the property as {@link String}
     */
    public void addMessageProperty(final String name, final String value) {
        final Property p = new Property(name, value);
        this.messageProperties.add(p);
    }

    /**
     * Adds PartInfo to the message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo/PartInfo</a>)
     *
     * @param href           the id of a referenced payload object as {@link String}
     * @param schemaLocation the schemalocation of the payload file as {@link String}
     * @param desc           the description of the partinfo as {@link String}
     * @return the previously added PartInfo object as {@link PartInfo}
     */
    public PartInfo addPartInfo(final String href, final String schemaLocation, final String desc) {
        final PartInfo part = new PartInfo(href, schemaLocation, desc);
        this.parts.add(part);
        return part;
    }

    /**
     * Returns the corresponding MPC (Message Partition Channel) of this message
     * Link to specification:
     * <ol>
     * <li><a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#3.4.Message%20Partition%20Channels|outline">ebMS3 Message Partition Channels</a></li>
     * <li><a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outlinee">ebMS3 UserMessage/@mpc</a></li>
     * </ol>
     *
     * @return the mpc of this message as {@link String}
     */
    public String getMpc() {
        return this.mpc;
    }

    /**
     * Sets the MPC (Message Partition Channel) of this message
     * Link to specification:
     * <ol>
     * <li><a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#3.4.Message%20Partition%20Channels|outline">ebMS3 Message Partition Channels</a></li>
     * <li><a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outlinee">ebMS3 UserMessage/@mpc</a></li>
     * </ol>
     *
     * @param mpc the mpc to set, as {@link String}
     */
    public void setMpc(final String mpc) {
        this.mpc = mpc;
    }

    /**
     * Returns the MessageID of this message conforming to MessageId [RFC2822]
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageInfo/MessageId</a>)
     *
     * @return the MessageID of this message as {@link String}
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * Sets the MessageID of this message conforming to MessageId [RFC2822]
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageInfo/MessageId</a>)
     *
     * @param messageId the MessageID of this message as {@link String}
     */
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    /**
     * Returns the RefToMessageID of this message conforming to MessageId [RFC2822]
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageInfo/RefToMessageId</a>)
     *
     * @return the RefToMessageID of this message as {@link String}
     */
    public String getRefToMessageId() {
        return this.refToMessageId;
    }

    /**
     * Sets the RefToMessageID of this message conforming to MessageId [RFC2822]
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageInfo/RefToMessageId</a>)
     *
     * @param refToMessageId the RefToMessageID of this message as {@link String}
     */
    public void setRefToMessageId(final String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    /**
     * Returns all senders of this message
     *
     * @return all senders of this message as {@link Collection<Party>}
     */
    public Collection<Party> getFromParties() {
        return this.fromParties;
    }

    /**
     * Sets senders of this message
     *
     * @param fromParties all senders of this message as {@link Set<Party>}
     */
    public void setFromParties(final Set<Party> fromParties) {
        this.fromParties = fromParties;
    }

    /**
     * Returns the Role of the sender of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PartyInfo/From/Role</a>)
     *
     * @return the Role of the sender of this message as {@link String}
     */
    public String getFromRole() {
        return this.fromRole;
    }

    /**
     * Sets the Role of the sender of this message
     * If the producer is null a new instance of a producer (@link Producer} will be instantiated.
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PartyInfo/From/Role</a>)
     *
     * @param fromRole the Role of the sender of this message as {@link String}
     */
    public void setFromRole(final String fromRole) {
        this.fromRole = fromRole;
    }

    /**
     * Returns all receivers of this message
     *
     * @return all receivers of this message as {@link Collection<Party>}
     */
    public Collection<Party> getToParties() {
        return this.toParties;
    }

    /**
     * Sets receivers of this message
     *
     * @param toParties all receivers of this message as {@link Set<Party>}
     */
    public void setToParties(final Set<Party> toParties) {
        this.toParties = toParties;
    }

    /**
     * Returns the Role of the receiver of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PartyInfo/From/Role</a>)
     *
     * @return toRole the Role of the receiver of this message as {@link String}
     */
    public String getToRole() {
        return this.toRole;
    }

    /**
     * Returns the Role of the receiver of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PartyInfo/From/Role</a>)
     *
     * @param toRole the Role of the receiver of this message as {@link String}
     */
    public void setToRole(final String toRole) {
        this.toRole = toRole;
    }

    /**
     * Returns the AgreementRef of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/AgreementRef</a>)
     *
     * @return the AgreementRef of this message as {@link String}
     */
    public String getAgreementRef() {
        return this.agreementRef;
    }

    /**
     * Sets the AgreementRef of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/AgreementRef</a>)
     *
     * @param agreementRef the AgreementRef of this message as {@link String}
     */
    public void setAgreementRef(final String agreementRef) {
        this.agreementRef = agreementRef;
    }

    /**
     * Returns the name of the ProcessingMode (PMode) for this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#4.Processing%20Modes|outline">ebMS3 Processing Modes</a>)
     *
     * @return the name of the PMode as {@link String}
     */
    public String getPmode() {
        return this.pmode;
    }

    /**
     * Sets the name of the ProcessingMode (PMode) for this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#4.Processing%20Modes|outline">ebMS3 Processing Modes</a>)
     *
     * @param pmode name of the PMode as {@link String}
     */
    public void setPmode(final String pmode) {
        this.pmode = pmode;
    }

    /**
     * Returns the corresponding service of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/Service</a>)
     *
     * @return the corresponding service as {@link String}
     */
    public String getService() {
        return this.service;
    }

    /**
     * Sets the corresponding service of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/Service</a>)
     *
     * @param service the corresponding service as {@link String}
     */
    public void setService(final String service) {
        this.service = service;
    }

    /**
     * Sets the corresponding action of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/Action</a>)
     *
     * @return the corresponding service as {@link String}
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Sets the corresponding service of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/Action</a>)
     *
     * @param action the corresponding service as {@link String}
     */
    public void setAction(final String action) {
        this.action = action;
    }

    /**
     * Returns the ConversationId of the message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/ConversationId</a>)
     *
     * @return the ConversationId of this message as {@link String}
     */
    public String getConversationId() {
        return this.conversationId;
    }

    /**
     * Sets the ConversationId of the message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/CollaborationInfo/ConversationId</a>)
     *
     * @param conversationId the ConversationId of this message as {@link String}
     */
    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Returns message properties to this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @return the collection of Property {@link Property}
     */
    public Collection<Property> getMessageProperties() {
        return this.messageProperties;
    }

    /**
     * Returns message properties to this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @param messageProperties the collection of Property {@link Property}
     */
    public void setMessageProperties(final Set<Property> messageProperties) {
        this.messageProperties = messageProperties;
    }

    /**
     * Returns collection of PartInfo object
     *
     * @return the collection of PartInfo object as {@link Collection<PartInfo>}
     */
    public Collection<PartInfo> getParts() {
        return this.parts;
    }

    /**
     * Sets set of PartInfo as {@link Set<PartInfo>}
     *
     * @param parts the set of PartInfo objects
     */
    public void setParts(final Set<PartInfo> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "MsgInfo [mpc=" + this.mpc + ", messageId=" + this.messageId + ", refToMessageId=" +
               this.refToMessageId +
               ", fromParties=" + this.fromParties + ", fromRole=" + this.fromRole + ", toParties=" + this.toParties +
               ", toRole=" +
               this.toRole + ", agreementRef=" + this.agreementRef + ", pmode=" + this.pmode + ", service=" +
               this.service + ", action=" +
               this.action + ", conversationId=" + this.conversationId + ", messageProperties=" +
               this.messageProperties + ", parts=" +
               this.parts + "]";
    }


}