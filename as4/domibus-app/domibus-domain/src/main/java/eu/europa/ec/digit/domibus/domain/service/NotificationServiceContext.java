package eu.europa.ec.digit.domibus.domain.service;

import org.apache.commons.lang.builder.ToStringBuilder;

public class NotificationServiceContext extends ServiceContext {

    /* ---- Constants ---- */
    public static final long serialVersionUID = 201511091910L;

    /* ---- Instance Variables ---- */

    private String destination = null;

	/* ---- Constructors ---- */

    public NotificationServiceContext(MessageServiceType messageServiceType) {
        super(messageServiceType);
    }

    /* ---- Business Methods ---- */

    public String toString() {
        return new ToStringBuilder(this)
        .appendSuper(super.toString())
        .append("destination", this.destination)
        .toString();
    }

    /* ---- Getters and Setters ---- */

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
