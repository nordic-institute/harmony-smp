package eu.europa.ec.edelivery.smp.data.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

/**
 * Database optimization: load service metadata xml only when needed and
 * keep blobs/clobs in separate table!
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Audited
@Table(name = "SMP_SERVICE_METADATA_XML")
@NamedQueries({
        @NamedQuery(name = "DBServiceMetadataXml.deleteById", query = "DELETE FROM DBServiceMetadataXml d WHERE d.id = :id"),
})
public class DBServiceMetadataXml extends BaseEntity {


    @Id
    private Long id;

    @Lob
    @Column(name = "XML_CONTENT")
    byte[] xmlContent;

    @OneToOne
    @JoinColumn(name = "ID")
    @MapsId
    DBServiceMetadata serviceMetadata;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBServiceMetadata getServiceMetadata() {
        return this.serviceMetadata;
    }

    public void setServiceMetadata(DBServiceMetadata smd) {
        this.serviceMetadata = smd;
    }

    public byte[] getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(byte[] xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBServiceMetadataXml that = (DBServiceMetadataXml) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
