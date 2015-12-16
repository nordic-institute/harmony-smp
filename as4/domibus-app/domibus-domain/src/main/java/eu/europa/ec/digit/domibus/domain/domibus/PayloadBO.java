package eu.europa.ec.digit.domibus.domain.domibus;

import java.io.InputStream;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.domain.AbstractBaseBO;

public class PayloadBO extends AbstractBaseBO {

    /* ---- Constants ---- */

	private static final long serialVersionUID = 8434037601604342797L;

	/* ---- Instance Variables ---- */

	private String id = null;
    private String description = null;
    private String schemaLocation = null;
    private Set<PropertyBO> properties = null;
    private InputStream data = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayloadBO)) {
            return false;
        }

        PayloadBO that = (PayloadBO) o;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (schemaLocation != null ? !schemaLocation.equals(that.schemaLocation) : that.schemaLocation != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        return !(data != null ? !data.equals(that.data) : that.data != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (schemaLocation != null ? schemaLocation.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
    	return new ToStringBuilder(this)
    		.appendSuper(super.toString())
    		.append("id", this.id)
    		.append("description", this.description)
    		.append("schemaLocation", this.schemaLocation)
    		.append("properties", this.properties.toString())
    		.append("data", this.data)
    		.toString();
    }

	/* ---- Getters and Setters ---- */


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public Set<PropertyBO> getProperties() {
        return properties;
    }

    public void setProperties(Set<PropertyBO> properties) {
        this.properties = properties;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

}
