package eu.domibus.ebms3.submit;

import org.apache.log4j.Logger;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.persistent.EbmsPayload;
import eu.domibus.ebms3.persistent.PartProperties;
import eu.domibus.ebms3.persistent.Payloads;
import eu.domibus.ebms3.persistent.Properties;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

import javax.persistence.*;
import java.io.File;
import java.util.*;


/**
 * @author Hamid Ben Malek
 */
@Root(name = "Metadata")
@Embeddable
public class MsgInfoSet {

    private static final Logger log = Logger.getLogger(MsgInfoSet.class);


    @Element
    @Column(name = "PMODE")
    private String pmode = null;

    @Element(name = "AgreementRef", required = false)
    @Column(name = "AGREEMENT_REF")
    private String agreementRef = null;

    @Element(name = "ConversationId", required = false)
    @Column(name = "CONVERSATION_ID")
    private String conversationId = null;

    @Element(name = "RefToMessageId", required = false)
    @Column(name = "REF_TO_MESSAGE_ID")
    private String refToMessageId = null;

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
    private String callbackClass = null;

    @Element(name = "Payloads", required = false)
    @JoinColumn(name = "PAYLOADS_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Payloads payloads;

    @Column(name = "MESSAGE_ID")
    private String messageId;

    @Column(name = "TIME_IN_MILLIS")
    private long timeInMillis = 0;

    public MsgInfoSet() {
    }

    public MsgInfoSet(final String agreementRef, final String pmode, final String conversationId, final String refToMessageId) {
        this.agreementRef = agreementRef;
        this.pmode = pmode;
        this.conversationId = conversationId;
        this.refToMessageId = refToMessageId;
    }

    public MsgInfoSet(final String fromParty, final String pmodeName) {
        if ((fromParty != null) && !fromParty.trim().equals("")) {
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

    public String getPmode() {
        return this.pmode;
    }

    public void setPmode(final String pmodeId) {
        this.pmode = pmodeId;
    }

    public String getRefToMessageId() {
        return this.refToMessageId;
    }

    public void setRefToMessageId(final String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getFromRole() {
        if (this.producer != null) {
            return this.producer.getRole();
        }
        return null;

    }

    public void setFromRole(final String fromRole) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.setRole(fromRole);
    }

    public String getAgreementRef() {
        return this.agreementRef;
    }

    public void setAgreementRef(final String agreementRef) {
        this.agreementRef = agreementRef;
    }

    public String getConversationId() {
        return this.conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public Map<String, String> getPropertiesMap() {
        if (this.properties != null) {
            return this.properties.getProperties();
        }
        return null;

    }

    public void setPropertiesMap(final Map<String, String> props) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.setProperties(props);
    }

    public void addProperty(final String name, final String value) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.addProperty(name, value);
    }

    public String getProperty(final String propertyName) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        return this.properties.getProperty(propertyName);
    }

    public int getLegNumber() {
        return this.legNumber;
    }

    public void setLegNumber(final int legNumber) {
        this.legNumber = legNumber;
    }

    public String getCallbackClass() {
        return this.callbackClass;
    }

    public void setCallbackClass(final String callbackClass) {
        this.callbackClass = callbackClass;
    }

    public Collection<Party> getFromParties() {
        if (this.producer != null) {
            return this.producer.getParties();
        }
        return null;

    }

    public void setFromParties(final Set<Party> fromParties) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.setParties(fromParties);
    }

    public void addFromParty(final Party fromParty) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.addParty(fromParty);
    }

    public void addFromParties(final Party[] p) {
        if ((p == null) || (p.length == 0)) {
            return;
        }
        for (final Party aP : p) {
            this.addFromParty(aP);
        }
    }

    public void addFromParty(final String type, final String partyId) {
        if (this.producer == null) {
            this.producer = new Producer();
        }
        this.producer.addParty(type, partyId);
    }

    public void setProducer(final Producer producer) {
        this.producer = producer;
    }

    public Producer getProducer() {
        return this.producer;
    }

    public String getBodyPayload() {
        if (this.payloads != null) {
            return this.payloads.getBodyPayload();
        }
        return null;

    }

    public void setBodyPayload(final String bodyPayload) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayload(bodyPayload);
    }

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

    public boolean isBodyPayloadCompressed() {
        if (this.payloads == null) {
            return false;
        }
        return this.payloads.isBodyPayloadCompressed();
    }

    public void setBodyPayloadCompressed(final boolean compressed) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadCompressed(compressed);
    }

    public String getBodyPayloadCID() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadCID();
    }

    public void setBodyPayloadCID(final String cid) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadCID(cid);
    }

    public String getBodyPayloadDescription() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadDescription();
    }

    public void setBodyPayloadDescription(final String description) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setBodyPayloadDescription(description);
    }

    public PartProperties getBodyPayloadPartProperties() {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getBodyPayloadPartProperties();
    }

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

    public Payloads getPayloads() {
        return this.payloads;
    }

    public void setPayloads(final Payloads payloads) {
        this.payloads = payloads;
    }

    public void addPayload(final String cid, final String payloadURI) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.addPayload(cid, payloadURI);
    }

    public String getPayload(final String cid) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        return this.payloads.getPayload(cid);
    }

    public String getCID(final String payload) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        return this.payloads.getCID(payload);
    }

    public static MsgInfoSet read(final String file) {
        return MsgInfoSet.read(new File(file));
    }

    public static MsgInfoSet read(final File file) {
        if ((file == null) || !file.exists()) {
            return null;
        }
        MsgInfoSet instance = null;
        try {
            final Persister serializer = new Persister();
            instance = serializer.read(MsgInfoSet.class, file);
        } catch (Exception ex) {
            MsgInfoSet.log.error("Exception during deserialization", ex);
        }
        return instance;
    }

    public void writeToFile(final String fileName) {
        try {
            final Persister serializer = new Persister();
            final File result = new File(fileName);
            serializer.write(this, result);
        } catch (Exception ex) {
            MsgInfoSet.log.error("Exception while writing MsgInfoSet to file", ex);
        }
    }

    public boolean hasBodyPayload() {
        return ((this.payloads != null) && (this.payloads.getBodyPayload() != null) &&
                !this.payloads.getBodyPayload().trim().equals(""));
    }

    public boolean isCompressed(final String fileName) {
        if ((fileName == null) || fileName.trim().equals("")) {
            return false;
        }
        if (this.payloads == null) {
            return false;
        }
        return this.payloads.isCompressed(fileName);
    }

    public void setCID(final String cid, final String payloadFile) {
        if (this.payloads == null) {
            this.payloads = new Payloads();
        }
        this.payloads.setCID(cid, payloadFile);
    }

    public String getDescription(final String payloadFile) {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getDescription(payloadFile);
    }

    public PartProperties getPartProperties(final String payloadFile) {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getPartProperties(payloadFile);
    }

    public String getSchemaLocation(final String payloadFile) {
        if (this.payloads == null) {
            return null;
        }
        return this.payloads.getSchemaLocation(payloadFile);
    }

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