package eu.europa.ec.digit.domibus.domain.service;


public enum MessageServiceType {

	/* ---- Enumeration Values ---- */

	NOTIFICATION("Notification"),
	RECEPTION("Reception"),
	SUBMISSION("Submission");

	/* ---- Instance Variables ---- */
    private String code = null;

	/* ---- Constructors ---- */
    private MessageServiceType(String code) {
        this.code = code;
    }

	/* ---- Business Methods ---- */

	/* ---- Getters and Setters ---- */

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
