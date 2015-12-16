package eu.europa.ec.digit.domibus.domain.domibus;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.domain.AbstractBaseBO;

public class MessageBO extends AbstractBaseBO {

	/* ---- Constants ---- */

    private static final long serialVersionUID = 201511051149L;

    /* ---- Instance Variables ---- */

	private MessageIdBO messageId = null;
	private MessageHeaderBO header = null;
	private PayloadBO body = null;
    private Set<PayloadBO> payloads = new HashSet<PayloadBO>();

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

	public void add(PayloadBO payload) {
		this.payloads.add(payload);
	}

	public void add(Set<PayloadBO> payloads) {
		this.payloads.addAll(payloads);
	}

	@Override
    public boolean equals(Object o) {
    	// TODO
        return true;
    }

    @Override
    public int hashCode() {
    	// TODO
    	return 0;
    }

    @Override
    public String toString() {
    	return new ToStringBuilder(this)
    		.appendSuper(super.toString())
    		.append("messageId", messageId.toString())
    		.append("header", header.toString())
    		.toString();
    }

    /* ---- Getters and Setters ---- */

    public MessageIdBO getMessageId() {
		return messageId;
	}

	public void setMessageId(MessageIdBO messageId) {
		this.messageId = messageId;
	}

	public MessageHeaderBO getHeader() {
		return header;
	}

	public void setHeader(MessageHeaderBO header) {
		this.header = header;
	}

	public PayloadBO getBody() {
		return body;
	}

	public void setBody(PayloadBO body) {
		this.body = body;
	}

	public Set<PayloadBO> getPayloads() {
		return payloads;
	}

}
