package eu.europa.ec.digit.domibus.domain.domibus;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.domain.AbstractBaseBO;

public class MessageHeaderBO extends AbstractBaseBO {

	/* ---- Constants ---- */
	private static final long serialVersionUID = 201511042303L;

	/* ---- Instance Variables ---- */

    private PartyBO fromParty = null;
    private PartyBO toParty = null;
    private Set<PropertyBO> messageProperties = new HashSet<PropertyBO>();
    private String action = null;
    private String service = null;
    private String serviceType = null;
    private String conversationId = null;
    private String refToMessageId = null;
    private String agreementRef = null;
    private String agreementRefType = null;

    /* ---- Constructors ---- */

	/* ---- Business Methods ---- */

    @Override
	public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof MessageIdBO)) return false;

         MessageHeaderBO that = (MessageHeaderBO) o;

         if (fromParty != null ? !fromParty.equals(that.fromParty) : that.fromParty != null) return false;
         if (toParty != null ? !toParty.equals(that.toParty) : that.toParty != null) return false;
         if (messageProperties != null ? !messageProperties.equals(that.messageProperties) : that.messageProperties != null) return false;
         if (action != null ? !action.equals(that.action) : that.action != null) return false;
         if (service != null ? !service.equals(that.service) : that.service != null) return false;
         if (serviceType != null ? !serviceType.equals(that.serviceType) : that.serviceType != null) return false;
         if (conversationId != null ? !conversationId.equals(that.conversationId) : that.conversationId != null) return false;
         if (refToMessageId != null ? !refToMessageId.equals(that.refToMessageId) : that.refToMessageId != null) return false;
         if (agreementRef != null ? !agreementRef.equals(that.agreementRef) : that.agreementRef != null) return false;
         return !(agreementRefType != null ? !agreementRefType.equals(that.agreementRefType) : that.agreementRefType != null);

	}

	@Override
	public int hashCode() {
         int result = fromParty != null ? fromParty.hashCode() : 0;
         result = 31 * result + (toParty != null ? toParty.hashCode() : 0);
         return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("fromParty", fromParty.toString())
			.append("toParty", toParty.toString())
			.toString();
	}

	/* ---- Getters and Setters ---- */

	public PartyBO getFromParty() {
		return fromParty;
	}

	public void setFromParty(PartyBO fromParty) {
		this.fromParty = fromParty;
	}

	public PartyBO getToParty() {
		return toParty;
	}

	public void setToParty(PartyBO toParty) {
		this.toParty = toParty;
	}

	public Set<PropertyBO> getMessageProperties() {
		return messageProperties;
	}

	public void setMessageProperties(Set<PropertyBO> messageProperties) {
		this.messageProperties = messageProperties;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getRefToMessageId() {
		return refToMessageId;
	}

	public void setRefToMessageId(String refToMessageId) {
		this.refToMessageId = refToMessageId;
	}

	public String getAgreementRef() {
		return agreementRef;
	}

	public void setAgreementRef(String agreementRef) {
		this.agreementRef = agreementRef;
	}

	public String getAgreementRefType() {
		return agreementRefType;
	}

	public void setAgreementRefType(String agreementRefType) {
		this.agreementRefType = agreementRefType;
	}
}
