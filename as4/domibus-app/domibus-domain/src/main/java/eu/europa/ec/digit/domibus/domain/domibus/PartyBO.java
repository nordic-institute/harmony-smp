package eu.europa.ec.digit.domibus.domain.domibus;

import org.apache.commons.lang.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.domain.AbstractBaseBO;

public class PartyBO extends AbstractBaseBO {

	/* ---- Constants ---- */

    private static final long serialVersionUID = 201511042317L;

    /* ---- Instance Variables ---- */

    private String id = null;
    private String type = null;
    private String role = null;

    /* ---- Constructors ---- */

    public PartyBO() {}

    public PartyBO(String id, String type, String role) {
        this.id = id;
        this.type = type;
        this.role = role;
    }

    /* ---- Business Methods ---- */


	@Override
    public boolean equals(Object o) {
    	if (this == o) return true;
        if (!(o instanceof PartyBO)) return false;

        PartyBO that = (PartyBO) o;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return !(role != null ? !role.equals(that.role) : that.role != null);
    }

    @Override
    public int hashCode() {
    	int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
    	return new ToStringBuilder(this)
    		.appendSuper(super.toString())
    		.append("id", this.id)
    		.append("type", this.type)
    		.append("role", this.role)
    		.toString();
    }


    /* ---- Getters and Setters ---- */

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}

