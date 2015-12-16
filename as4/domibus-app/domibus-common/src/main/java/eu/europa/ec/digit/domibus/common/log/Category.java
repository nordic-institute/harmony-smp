package eu.europa.ec.digit.domibus.common.log;

public enum Category {

	/* ---- Constants ---- */

	SECURITY ("SECURITY"),
	BUSINESS ("BUSINESS");

	/* ---- Instance Variables ---- */

	private String name = null;

	/* --- Constructors ---- */

	private Category(String name) {
		this.name = name;
	}

	/* ---- Business Methods ---- */

	/* ---- Getters and Setters ---- */


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
