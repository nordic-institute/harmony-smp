package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "UserService", strict = false)
public class UserService implements Serializable {
    private static final long serialVersionUID = -5593316570192830737L;

    @Attribute(required = false)
    protected String name;

    @Attribute(required = false)
    protected int maxPayloadsSize;

    @Element(name = "ToPartyInfo")
    protected ToParty toParty;

    @Element(name = "CollaborationInfo")
    protected CollaborationInfo collaborationInfo;

    @Element(name = "MessageProperties", required = false)
    protected MessageProperties messageProperties;

    @Element(name = "PayloadInfo", required = false)
    protected PayloadInfo payloadInfo;

    public UserService() {
    }

    public UserService(final ToParty toParty, final CollaborationInfo ci, final MessageProperties mp,
                       final PayloadInfo pi) {
        this.toParty = toParty;
        this.collaborationInfo = ci;
        this.messageProperties = mp;
        this.payloadInfo = pi;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ToParty getToParty() {
        return this.toParty;
    }

    public void setToParty(final ToParty toParty) {
        this.toParty = toParty;
    }

    public CollaborationInfo getCollaborationInfo() {
        return this.collaborationInfo;
    }

    public void setCollaborationInfo(final CollaborationInfo collaborationInfo) {
        this.collaborationInfo = collaborationInfo;
    }

    public MessageProperties getMessageProperties() {
        return this.messageProperties;
    }

    public void setMessageProperties(final MessageProperties messageProperties) {
        this.messageProperties = messageProperties;
    }

    public PayloadInfo getPayloadInfo() {
        return this.payloadInfo;
    }

    public void setPayloadInfo(final PayloadInfo payloadInfo) {
        this.payloadInfo = payloadInfo;
    }

    public void addToParty(final Party party) {
        if (party == null) {
            return;
        }
        if (this.toParty == null) {
            this.toParty = new ToParty();
        }
        this.toParty.addParty(party);
    }

    public int getPayloadsSize() {
        return this.maxPayloadsSize;
    }

    public void setPayloadsSize(final int maxPayloadsSize) {
        this.maxPayloadsSize = maxPayloadsSize;
    }
}