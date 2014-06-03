package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import javax.persistence.*;
import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "payload")
@Entity
@Table(name = "TB_EBMS3_PAYLOAD")
public class EbmsPayload extends AbstractBaseEntity {

    @Column(name = "CID")
    @Attribute(required = false)
    private String cid;

    @Attribute(required = false)
    @Column(name = "COMPRESSED")
    protected boolean compressed = false;

    @Attribute(required = false)
    @Column(name = "DESCRIPTION")
    protected String description;

    @Attribute(required = false)
    @Column(name = "CONTENT_TYPE")
    protected String contentType;

    @Attribute(required = false)
    @Column(name = "SCHEMA_LOCATION")
    protected String schemaLocation;

    @Element(required = true)
    @Column(name = "FILE_NAME")
    protected String file;

    @Element(name = "PartProperties", required = false)
    @JoinColumn(name = "EBMS_PAYLOAD_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    protected PartProperties partProperties;

    public String getCid() {
        return cid;
    }

    public void setCid(final String cid) {
        this.cid = cid;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(final boolean compressed) {
        this.compressed = compressed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(final String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getFile() {
        return file;
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public PartProperties getPartProperties() {
        return partProperties;
    }

    public void setPartProperties(final PartProperties partProperties) {
        this.partProperties = partProperties;
    }

    public Map<String, String> getPartPropertiesMap() {
        if (partProperties != null) {
            return partProperties.getPartProperties();
        } else {
            return null;
        }
    }

    public void setPartPropertiesMap(final Map<String, String> props) {
        if (partProperties == null) {
            partProperties = new PartProperties();
        }
        partProperties.setPartProperties(props);
    }

    public void addPartProperties(final String name, final String value) {
        if (partProperties == null) {
            partProperties = new PartProperties();
        }
        partProperties.addProperty(name, value);
    }

    public String getPartProperties(final String propertyName) {
        if (partProperties == null) {
            partProperties = new PartProperties();
        }
        return partProperties.getProperty(propertyName);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "EbmsPayload [cid=" + cid + ", compressed=" + compressed + ", description=" + description +
               ", contentType=" + contentType + ", schemaLocation=" + schemaLocation + ", file=" + file +
               ", partProperties=" + partProperties + "]";
    }

}