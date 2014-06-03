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
public class PartInfo extends AbstractBaseEntity {

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

    public PartInfo() {
    }

    public PartInfo(final String href, final String schemaLocation, final String desc) {
        this.href = href;
        this.schemaLocation = schemaLocation;
        this.description = desc;
    }

    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(final String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String desc) {
        this.description = desc;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(final boolean compressed) {
        this.compressed = compressed;
    }

    // A convenient method:
    public String getCid() {
        if (href == null || href.trim().equals("")) {
            return null;
        }
        if (href.startsWith("cid:")) {
            return href.substring(4);
        } else {
            return null;
        }
    }

    public Collection<Property> getProperties() {
        return properties;
    }

    public void setProperties(final Set<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(final Property property) {
        if (properties == null) {
            properties = new HashSet<Property>();
        }

        properties.add(property);
    }

    public void addProperty(final String name, final String value) {
        addProperty(new Property(name, value));
    }
}