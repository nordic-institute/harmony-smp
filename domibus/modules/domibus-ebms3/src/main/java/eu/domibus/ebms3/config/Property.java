package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Property", strict = false)
public class Property implements Serializable {
    private static final long serialVersionUID = 3026385474435552009L;

    @Attribute
    protected String name;

    @Attribute(required = false)
    protected String type;

    @Attribute(required = false)
    protected String description;

    @Attribute(required = false)
    protected boolean required;

    public Property() {
    }

    public Property(final String name, final String type, final String desc, final boolean required) {
        this.name = name;
        this.type = type;
        this.description = desc;
        this.required = required;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }
}