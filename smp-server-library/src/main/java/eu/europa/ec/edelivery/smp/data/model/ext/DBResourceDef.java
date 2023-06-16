package eu.europa.ec.edelivery.smp.data.model.ext;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Database table containing registered extensions data/description
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Entity
@Audited
@Table(name = "SMP_RESOURCE_DEF",
        indexes = {@Index(name = "SMP_RESDEF_UNIQ_EXTID_CODE_IDX", columnList = "FK_EXTENSION_ID,IDENTIFIER", unique = true)
})
@org.hibernate.annotations.Table(appliesTo = "SMP_RESOURCE_DEF", comment = "SMP extension resource definitions")

@NamedQuery(name = QUERY_RESOURCE_DEF_ALL, query = "SELECT d FROM DBResourceDef d order by d.id asc")
@NamedQuery(name = QUERY_RESOURCE_DEF_BY_IDENTIFIER_EXTENSION, query = "SELECT d FROM DBResourceDef d WHERE d.extension.id = :extension_id AND d.identifier = :identifier")
@NamedQuery(name = QUERY_RESOURCE_DEF_BY_DOMAIN, query = "SELECT d FROM DBResourceDef d JOIN d.domainResourceDefs dr where dr.domain.id = :domain_id order by d.id asc")
@NamedQuery(name = QUERY_RESOURCE_DEF_URL_SEGMENT, query = "SELECT d FROM DBResourceDef d WHERE d.urlSegment = :url_segment")
@NamedQuery(name = QUERY_RESOURCE_DEF_BY_IDENTIFIER, query = "SELECT d FROM DBResourceDef d WHERE d.identifier = :identifier")
public class DBResourceDef extends BaseEntity {
    private static final long serialVersionUID = 1008583888835630001L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_RESOURCE_DEF_SEQ")
    @GenericGenerator(name = "SMP_RESOURCE_DEF_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique id")
    Long id;

    @Column(name = "IDENTIFIER", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128, unique = true)
    private String identifier;

    @Column(name = "NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String name;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_TEXT_LENGTH_512)
    private String description;

    @Column(name = "MIME_TYPE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String mimeType;

    @Column(name = "URL_SEGMENT", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128, unique = true)
    @ColumnDescription(comment = "resources are published under url_segment.")
    String urlSegment;

    @Column(name = "HANDLER_IMPL_NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_256 )
    private String handlerImplementationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_EXTENSION_ID")
    private DBExtension extension;

    @OneToMany(
            mappedBy = "resourceDef",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DBSubresourceDef> subresources = new ArrayList<>();

    @OneToMany(
            mappedBy = "resourceDef",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DBDomainResourceDef> domainResourceDefs = new ArrayList<>();


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DBExtension getExtension() {
        return extension;
    }

    public void setExtension(DBExtension extension) {
        this.extension = extension;
    }

    public String getUrlSegment() {
        return urlSegment;
    }

    public void setUrlSegment(String urlSegment) {
        this.urlSegment = urlSegment;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getHandlerImplementationName() {
        return handlerImplementationName;
    }

    public void setHandlerImplementationName(String handlerImplementationName) {
        this.handlerImplementationName = handlerImplementationName;
    }

    public List<DBSubresourceDef> getSubresources() {
        if (subresources == null) {
            subresources = new ArrayList<>();
        }
        return subresources;
    }

    public List<DBDomainResourceDef> getDomainResourceDefs() {
        if (domainResourceDefs == null) {
            domainResourceDefs = new ArrayList<>();
        }
        return domainResourceDefs;
    }

    @Override
    public String toString() {
        return "DBResourceDef{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", urlSegment='" + urlSegment + '\'' +
                ", handlerImplementationName='" + handlerImplementationName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DBResourceDef that = (DBResourceDef) o;

        return new EqualsBuilder().appendSuper(super.equals(o))
                .append(id, that.id)
                .append(identifier, that.identifier)
                .append(name, that.name)
                .append(description, that.description)
                .append(mimeType, that.mimeType)
                .append(urlSegment, that.urlSegment)
                .append(handlerImplementationName, that.handlerImplementationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(id).append(identifier).toHashCode();
    }
}
