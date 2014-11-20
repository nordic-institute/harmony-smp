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
    protected boolean compressed;

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
    @Column(name = "TEMP_STORE")
    protected String fileName;

    @Element(name = "PartProperties", required = false)
    @JoinColumn(name = "EBMS_PAYLOAD_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    protected PartProperties partProperties;

    public String getCid() {
        return this.cid;
    }

    public void setCid(final String cid) {
        this.cid = cid;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void setCompressed(final boolean compressed) {
        this.compressed = compressed;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public void setSchemaLocation(final String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getFile() {
        return this.fileName.substring(this.fileName.lastIndexOf("/") + 1);
    }

    public void setQualifiedFileName(final String file) {
        this.fileName = file.replace('\\', '/');
    }

    public String getQualifiedFileName() {
        return this.fileName;
    }

    public PartProperties getPartProperties() {
        return this.partProperties;
    }

    public void setPartProperties(final PartProperties partProperties) {
        this.partProperties = partProperties;
    }

    public Map<String, String> getPartPropertiesMap() {
        if (this.partProperties != null) {
            return this.partProperties.getPartProperties();
        } else {
            return null;
        }
    }

    public void setPartPropertiesMap(final Map<String, String> props) {
        if (this.partProperties == null) {
            this.partProperties = new PartProperties();
        }
        this.partProperties.setPartProperties(props);
    }

    public void addPartProperties(final String name, final String value) {
        if (this.partProperties == null) {
            this.partProperties = new PartProperties();
        }
        this.partProperties.addProperty(name, value);
    }

    public String getPartProperties(final String propertyName) {
        if (this.partProperties == null) {
            this.partProperties = new PartProperties();
        }
        return this.partProperties.getProperty(propertyName);
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "EbmsPayload [cid=" + this.cid + ", compressed=" + this.compressed + ", description=" +
               this.description +
               ", contentType=" + this.contentType + ", schemaLocation=" + this.schemaLocation + ", file=" +
               this.fileName +
               ", partProperties=" + this.partProperties + "]";
    }

}