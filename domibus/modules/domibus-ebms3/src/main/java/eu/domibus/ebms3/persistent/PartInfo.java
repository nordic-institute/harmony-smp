package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hamid Ben Malek
 */


@Entity
@Table(name = "TB_PART_INFO")
public class PartInfo extends AbstractBaseEntity {   //TODO: rename to TB_PART

    @Column(name = "HREF")
    private String href;

    @Column(name = "SCHEMA_LOCATION")
    private String schemaLocation;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "COMPRESSED")
    private boolean compressed;

    @JoinColumn(name = "PART_INFO_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<Property> properties;

    // eager loading because the payload is necessary in all cases
    @Column(name = "PAYLOAD_DATA")
    @Lob
    private byte[] payloadData;

    @Column(name = "BODY")
    private boolean body;

    public PartInfo() {
    }

    public PartInfo(final String href, final String schemaLocation, final String desc) {
        this.href = href;
        this.schemaLocation = schemaLocation;
        this.description = desc;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(final String href) {
        this.href = href;
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

    public void setDescription(final String desc) {
        this.description = desc;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void setCompressed(final boolean compressed) {
        this.compressed = compressed;
    }

    // A convenient method:
    public String getCid() {
        if ((this.href == null) || "".equals(this.href.trim())) {
            return null;
        }
        if (this.href.startsWith("cid:")) {
            return this.href.substring(4);
        } else {
            return null;
        }
    }

    public Collection<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(final Set<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(final Property property) {
        if (this.properties == null) {
            this.properties = new HashSet<Property>();
        }

        this.properties.add(property);
    }

    public void addProperty(final String name, final String value) {
        this.addProperty(new Property(name, value));
    }

    public byte[] getPayloadData() {
        return this.payloadData;
    }

    public void setPayloadData(final byte[] payloadData) {
        this.payloadData = payloadData;
    }

    public boolean isBody() {
        return this.body;
    }

    public void setBody(final boolean body) {
        this.body = body;
    }
}