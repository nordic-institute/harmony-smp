package eu.europa.ec.digit.domibus.domain.service;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


public class ServiceContext implements Serializable {

    /* ---- Constants ---- */
    public static final long serialVersionUID = 201508141449L;

    /* ---- Instance Variables ---- */

    protected MessageServiceType messageServiceType = null;

/* ---- Constructors ---- */

    public ServiceContext() {
        super();
    }

    public ServiceContext(MessageServiceType messageServiceType) {
        this.messageServiceType = messageServiceType;
    }

    /* ---- Business Methods ---- */

    public String toString() {
        return new ToStringBuilder(this)
        .appendSuper(super.toString())
        .append("messageServiceType", messageServiceType.name())
        .toString();
    }

    /* ---- Getters and Setters ---- */

	public MessageServiceType getMessageServiceType() {
		return messageServiceType;
	}

	public void setMessageServiceType(MessageServiceType messageServiceType) {
		this.messageServiceType = messageServiceType;
	}

}
