package eu.europa.ec.digit.domibus.domain.domibus;

import org.apache.commons.lang.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.domain.AbstractBaseBO;

public class MessageIdBO extends AbstractBaseBO {

    /* ---- Constants ---- */
	private static final long serialVersionUID = -7806887961704155428L;

	/* ---- Instance Variables ---- */

	private String messageId;
	private String correlationId;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MessageIdBO))
			return false;

		MessageIdBO that = (MessageIdBO) o;

		if (messageId != null ? !messageId.equals(that.messageId) : that.messageId != null)
			return false;
		return !(correlationId != null ? !correlationId.equals(that.correlationId) : that.correlationId != null);

	}

	@Override
	public int hashCode() {
		int result = messageId != null ? messageId.hashCode() : 0;
		result = 31 * result + (correlationId != null ? correlationId.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("messageId", this.messageId)
			.append("correlationId", this.correlationId)
			.toString();
	}

	/* ---- Getters and Setters ---- */

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

}
