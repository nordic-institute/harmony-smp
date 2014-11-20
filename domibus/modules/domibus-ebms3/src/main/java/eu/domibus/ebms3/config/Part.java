package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Part", strict = false)
public class Part implements Serializable {
    private static final long serialVersionUID = 989385474049852009L;

    @Attribute
    protected String cid;

    @Attribute(required = false)
    protected String mimeType;

    @Attribute(required = false)
    protected String schemaLocation;

    @Attribute(required = false)
    protected String description;

    public Part() {
    }

    public Part(final String cid, final String mimeType, final String schemaLocation, final String desc) {
        this.cid = cid;
        this.mimeType = mimeType;
        this.schemaLocation = schemaLocation;
        this.description = desc;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(final String cid) {
        this.cid = cid;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public void setSchemaLocation(final String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}