package eu.europa.ec.digit.domibus.domain.domibus;

import org.apache.commons.lang.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.domain.AbstractBaseBO;

public class PropertyBO extends AbstractBaseBO {

    /* ---- Constants ---- */

	private static final long serialVersionUID = 8852276863749501804L;

	/* ---- Instance Variables ---- */

	private String name = null;
    private String value = null;
    private String type = null;

	/* ---- Constructors ---- */

    public PropertyBO() {
		super();
	}

	public PropertyBO(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/* ---- Business Methods ---- */

	@Override
    public boolean equals(Object o) {
    	if (this == o) return true;
        if (!(o instanceof PropertyBO)) return false;

        PropertyBO that = (PropertyBO) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;

        return !(type != null ? !type.equals(that.type) : that.type != null);
    }

    @Override
    public int hashCode() {
    	int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
    	return new ToStringBuilder(this)
    		.appendSuper(super.toString())
    		.append("name", this.name)
    		.append("value", this.value)
    		.append("type", this.type)
    		.toString();
    }

	/* ---- Getters and Setters ---- */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
