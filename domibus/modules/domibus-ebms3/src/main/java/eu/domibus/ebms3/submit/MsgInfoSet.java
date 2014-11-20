package eu.domibus.ebms3.submit;

import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.persistent.*;
import eu.domibus.ebms3.persistent.Properties;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.persistence.*;
import java.util.*;


/**
 * This class contains ebMS3 specific fields and is used for sending (push and pull) messages in domibus ({@link MessageToSend})
 *
 * @author Hamid Ben Malek
 * @see MsgInfo
 */
@Root(name = "Metadata")
@Embeddable
public class MsgInfoSet {

    private static final Logger log = Logger.getLogger(MsgInfoSet.class);


    @Element
    @Column(name = "PMODE")
    private String pmode;

    @Element(name = "AgreementRef", required = false)
    @Column(name = "AGREEMENT_REF")
    private String agreementRef;

    @Element(name = "ConversationId", required = false)
    @Column(name = "CONVERSATION_ID")
    private String conversationId;

    @Element(name = "RefToMessageId", required = false)
    @Column(name = "REF_TO_MESSAGE_ID")
    private String refToMessageId;

    // embedded
    @Element(name = "From", required = false)
    private Producer producer;

    @Element(name = "Properties", required = false)
    @JoinColumn(name = "PROPERTIES_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Properties properties;

    @Element(required = false)
    @Column(name = "LEG_NUMBER")
    private int legNumber = -1;

    @Element(required = false)
    @Column(name = "CALLBACK_CLASS")
    private String callbackClass;

    @Element(name = "Payloads", required = false)
    @JoinColumn(name = "PAYLOADS_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Payloads payloads;

    @Column(name = "MESSAGE_ID")
    private String messageId;

    @Column(name = "TIME_IN_MILLIS")
    private long timeInMillis;

    public MsgInfoSet() {

    }

    public MsgInfoSet(final String agreementRef, final String pmode, final String conversationId,
                      final String refToMessageId) {
        this.agreementRef = agreementRef;
        this.pmode = pmode;
        this.conversationId = conversationId;
        this.refToMessageId = refToMessageId;
    }

    public MsgInfoSet(final String fromParty, final String pmodeName) {
        if ((fromParty != null) && !"".equals(fromParty.trim())) {
            this.addFromParty(null, fromParty);
        }
        this.pmode = pmodeName;
    }

    public MsgInfoSet(final String[] fromParty, final String fromRole, final String pmodeName) {
        if ((fromParty != null) && (fromParty.length > 0)) {
            for (final String p : fromParty) {
                this.addFromParty(null, p);
            }
        }
        this.setFromRole(fromRole);
        this.pmode = pmodeName;
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
     * @param pmodeId name of the PMode as {@link String}
     */
    public void setPmode(final String pmodeId) {
        this.pmode = pmodeId;
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
     * Returns the Role of the sender of this message
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PartyInfo/From/Role</a>)
     *
     * @return the Role of the sender of this message as {@link String}
     */
    public String getFromRole() {
        if (this.producer != null) {
            return this.producer.getRole();
        }
        return null;

    }

    /**
     * Sets the Role of the sender of this message
     * If the producer is null a new instance of a producer (@link Producer} will be instantiated.
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PartyInfo/From/Role</a>)
     *
     * @param fromRole the Role of the sender of this message as {@link String}
     */
    public void setFromRole(final String fromRole) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.setRole(fromRole);
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
     * Returns the message properties of this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @return the message properties of this message as {@link Properties}
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Sets the message properties of this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @param properties the message properties of this message as {@link Properties}
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns the message properties of this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @return the message properties of this message as a {@link Map}
     */
    public Map<String, String> getPropertiesMap() {
        if (this.properties != null) {
            return this.properties.getProperties();
        }
        return null;

    }

    /**
     * Sets the message properties of this message
     * For example in e-CODEX two properties were used to route the message to the final recipient and back to the original sender:
     * <ol>
     * <li>finalRecipient</li>
     * <li>originalSender</li>
     * </ol>
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/MessageProperties</a>)
     *
     * @param props the message properties of this message as a {@link Map}
     */
    public void setPropertiesMap(final Map<String, String> props) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.setProperties(props);
    }

    /**
     * Adds a message property to this message
     *
     * @param name  key of the property as {@link String}
     * @param value value of the property as {@link String}
     */
    public void addProperty(final String name, final String value) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.addProperty(name, value);
    }

    /**
     * Returns message property by property name
     *
     * @param propertyName name (key) of the property to return
     * @return value of the requested property as {@link String}
     */
    public String getProperty(final String propertyName) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        return this.properties.getProperty(propertyName);
    }

    /**
     * Return the number of legs
     *
     * @return the number of legs as {@link String}
     */
    public int getLegNumber() {
        return this.legNumber;
    }

    /**
     * Sets the number of legs
     *
     * @param legNumber the number of legs as {@link String}
     */
    public void setLegNumber(final int legNumber) {
        this.legNumber = legNumber;
    }

    /**
     * Return the name of callback class
     *
     * @return the name of callback class as {@link String}
     */
    public String getCallbackClass() {
        return this.callbackClass;
    }

    /**
     * Sets the name of callback class
     *
     * @param callbackClass the name of callback class as {@link String}
     */
    public void setCallbackClass(final String callbackClass) {
        this.callbackClass = callbackClass;
    }

    /**
     * Returns all senders of this message
     *
     * @return all senders of this message as {@link Collection<Party>}
     */
    public Collection<Party> getFromParties() {
        if (this.producer != null) {
            return this.producer.getParties();
        }
        return null;

    }

    /**
     * Sets senders of this message
     *
     * @param fromParties all senders of this message as {@link Set<Party>}
     */
    public void setFromParties(final Set<Party> fromParties) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.setParties(fromParties);
    }

    /**
     * Adds party to collection of senders
     *
     * @param fromParty the party to add as {@link Party}
     */
    public void addFromParty(final Party fromParty) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.addParty(fromParty);
    }

    /**
     * Adds array of party to collection of senders
     *
     * @param p the array of party to add as {@link Party[]}
     */
    public void addFromParties(final Party[] p) {
        if ((p == null) || (p.length == 0)) {
            return;
        }
        for (final Party aP : p) {
            this.addFromParty(aP);
        }
    }

    /**
     * Adds party to collection of senders
     *
     * @param type    the type of the sender party to add as {@link String}
     * @param partyId the party id of the sender to add as {@link String}
     */
    public void addFromParty(final String type, final String partyId) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.addParty(type, partyId);
    }

    /**
     * Sets the producer of this message
     *
     * @param producer the producer of this message as {@link Producer}
     */
    public void setProducer(final Producer producer) {
        this.producer = producer;
    }

    /**
     * Return the producer of this message
     *
     * @return the producer of this message as {@link Producer}
     */
    public Producer getProducer() {
        return this.producer;
    }

    /**
     * Returns the filename of the content of the soap body
     * In case of e-CODEX the xml business document will be transported in the body of the message
     *
     * @return the filename of the content of the soap body as {@link String}
     */
    public String getBodyPayload() {
        if (this.payloads != null) {
            return this.payloads.getBodyPayload();
        }
        return null;

    }

    /**
     * Sets the filename of the content of the soap body
     * In case of e-CODEX the xml business document will be transported in the body of the message
     *
     * @param bodyPayload the filename of the content of the soap body as {@link String}
     */
    public void setBodyPayload(final String bodyPayload) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayload(bodyPayload);
    }

    /**
     * Sets the filename of the content of the soap body
     * In case of e-CODEX the xml business document will be transported in the body of the message
     *
     * @param bodyPayload the filename of the content of the soap body as {@link EbmsPayload}
     */
    public void setBodyPayload(final EbmsPayload bodyPayload) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayload(bodyPayload);
    }


    public void setBodyPayload(final String cid, final String payloadURI) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayload(cid, payloadURI);
    }

    // FIX: compression of body payload is not allowed
    public boolean isBodyPayloadCompressed() {
        if (this.payloads == null) {
            return false;
        }
        return this.payloads.isBodyPayloadCompressed();
    }

    // FIX: compression of body payload is not allowed
    public void setBodyPayloadCompressed(final boolean compressed) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadCompressed(compressed);
    }

    /**
     * Returns CID of the soap body content
     *
     * @return the CID of the soap body content as {@link String}
     */
    public String getBodyPayloadCID() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadCID();
    }

    /**
     * Sets CID of the soap body content
     *
     * @param cid the CID of the soap body content as {@link String}
     */
    public void setBodyPayloadCID(final String cid) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadCID(cid);
    }

    /**
     * Returns corresponding description of the soap body content
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo</a>)
     *
     * @return corresponding description of the soap body content as {@link String}
     */
    public String getBodyPayloadDescription() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadDescription();
    }

    /**
     * Returns corresponding description of the soap body content
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo</a>)
     *
     * @param description corresponding description of the soap body content as {@link String}
     */
    public void setBodyPayloadDescription(final String description) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadDescription(description);
    }

    /**
     * Returns corresponding description of the soap body content
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo/PartInfo</a>)
     *
     * @return corresponding description of the soap body content as {@link String}
     */
    public PartProperties getBodyPayloadPartProperties() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadPartProperties();
    }

    /**
     * Sets corresponding description of the soap body content
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo/PartInfo</a>)
     *
     * @param partProperties corresponding description of the soap body content as {@link String}
     */
    public void setBodyPayloadPartProperties(final PartProperties partProperties) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadPartProperties(partProperties);
    }

    public String getBodyPayloadSchemaLocation() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadSchemaLocation();
    }

    public void setBodyPayloadSchemaLocation(final String schemaLocation) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadSchemaLocation(schemaLocation);
    }

    /**
     * Returns Payloads of this message
     *
     * @return Payloads as {@link Payloads}
     */
    public Payloads getPayloads() {
        return this.payloads;
    }

    /**
     * Sets Payloads of this message
     *
     * @param payloads Payloads as {@link Payloads}
     */
    public void setPayloads(final Payloads payloads) {
        this.payloads = payloads;
    }

    public void addPayload(final String cid, final String payloadURI) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.addPayload(cid, payloadURI);
    }

    /**
     * Returns payload by cid
     *
     * @param cid cid of the requested payload as {@link String}
     * @return filename of the payload with requested cid as {@link String}
     */
    public String getPayload(final String cid) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        return this.payloads.getPayload(cid);
    }

    /**
     * Returns cid of payload by filenam
     *
     * @param payload filename of payload as {@link String}
     * @return cid of requested payload as {@link String}
     */
    public String getCID(final String payload) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        return this.payloads.getCID(payload);
    }


    /**
     * Returns <code>true</code> if the message contains a payload inside the body
     *
     * @return <code>true</code> if the message contains a payload inside the body;
     * <code>false</code> otherwise
     */
    public boolean hasBodyPayload() {
        return ((this.payloads != null) && (this.payloads.getBodyPayload() != null) &&
                !"".equals(this.payloads.getBodyPayload().trim()));
    }

    /**
     * Returns <code>true</code> if the given file exists and is marked as compressed
     *
     * @param fileName the filename as {@link String}
     * @return <code>true</code> if the given file exists and is marked as compressed;
     * <code>false</code> otherwise
     */
    public boolean isCompressed(final String fileName) {
        if ((fileName == null) || "".equals(fileName.trim())) {
            return false;
        }
        if (this.payloads == null) {
            return false;
        }
        return this.payloads.isCompressed(fileName);
    }

    /**
     * Sets cid of a given payloadFile
     *
     * @param cid         the cid to set as {@link String}
     * @param payloadFile the payloadFile as {@link String}
     */
    public void setCID(final String cid, final String payloadFile) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setCID(cid, payloadFile);
    }

    /**
     * Returns the content of the description element of the given payloadFile
     *
     * @param payloadFile the payloadFile as {@link String}
     * @return the description element of the payloadFile as {@link String}
     */
    public String getDescription(final String payloadFile) {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getDescription(payloadFile);
    }

    /**
     * Returns the PartProperties Element ({@link PartProperties}) corresponding to the given payloadFile
     * (Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo/PartInfo/PartProperties</a>)
     *
     * @param payloadFile the payloadFile as {@link String}
     * @return the PartProperties Element as {@link PartProperties}
     */
    public PartProperties getPartProperties(final String payloadFile) {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getPartProperties(payloadFile);
    }

    /**
     * Returns the corresponding schema for the given payloadFile
     * Link to specification: <a href="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/os/ebms_core-3.0-spec-os.html#5.2.2.eb:Messaging/eb:UserMessage|outline">ebMS3 UserMessage/PayloadInfo/PartInfo/Schema
     * </a>)
     *
     * @param payloadFile the payloadFile as {@link String}
     * @return the schemalocation of the payloadFile as {@link String}
     */
    public String getSchemaLocation(final String payloadFile) {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getSchemaLocation(payloadFile);
    }

    /**
     * Returns CIDs of all payloads of this message
     *
     * @return the payloads as {@link String[]}
     */
    public String[] getCids() {
        if (this.payloads == null) {
            return null;
        }
        final List<String> cids = new ArrayList<String>();
        for (final EbmsPayload p : this.payloads.getPayloads()) {
            cids.add(p.getCid());
        }
        final String[] ids = new String[cids.size()];
        cids.toArray(ids);
        return ids;
    }

    /**
     * @return the timeInMillis
     */
    public long getTimeInMillis() {
        return this.timeInMillis;
    }

    /**
     * @param timeInMillis the timeInMillis to set
     */
    public void setCreateTimeInMillis(final long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}