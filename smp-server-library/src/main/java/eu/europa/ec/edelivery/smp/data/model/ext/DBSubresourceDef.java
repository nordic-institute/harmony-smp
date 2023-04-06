package eu.europa.ec.edelivery.smp.data.model.ext;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Database table containing registered extensions data/description
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Entity
@Audited
@Table(name = "SMP_SUBRESOURCE_DEF",
        indexes = {@Index(name = "SMP_RD_UNIQ_RDID_UCTX_IDX", columnList = "FK_RESOURCE_DEF_ID,URL_SEGMENT", unique = true),
                @Index(name = "SMP_RESDEF_UNIQ_URL_SEG", columnList = "URL_SEGMENT", unique = true),
                @Index(name = "SMP_RESDEF_UNIQ_IDENTIFIER", columnList = "IDENTIFIER", unique = true)
})
@org.hibernate.annotations.Table(appliesTo = "SMP_SUBRESOURCE_DEF", comment = "SMP extension subresource definitions")
@NamedQuery(name = QUERY_SUBRESOURCE_DEF_ALL, query = "SELECT d FROM DBSubresourceDef d order by d.id asc")
@NamedQuery(name = QUERY_SUBRESOURCE_DEF_BY_IDENTIFIER, query = "SELECT d FROM DBSubresourceDef d WHERE d.identifier = :identifier")
@NamedQuery(name = QUERY_SUBRESOURCE_DEF_URL_SEGMENT, query = "SELECT d FROM DBSubresourceDef d WHERE d.urlSegment = :url_segment")

public class DBSubresourceDef extends BaseEntity {
    private static final long serialVersionUID = 1008583888835630002L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_SUBRESOURCE_DEF_SEQ")
    @GenericGenerator(name = "SMP_SUBRESOURCE_DEF_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique id")
    Long id;

    @Column(name = "IDENTIFIER", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128, unique = true)
    private String identifier;
    @Column(name = "NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String name;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_RESOURCE_DEF_ID")
    private DBResourceDef resourceDef;

    @Column(name = "MIME_TYPE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String mimeType;

    @Column(name = "URL_SEGMENT", length = CommonColumnsLengths.MAX_TEXT_LENGTH_64)
    @ColumnDescription(comment = "Subresources are published under url_segment. It must be unique for resource type")
    private String urlSegment;

    @Column(name = "HANDLER_IMPL_NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_256 )
    private String handlerImplementationName;

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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public DBResourceDef getResourceDef() {
        return resourceDef;
    }

    public void setResourceDef(DBResourceDef resourceDef) {
        this.resourceDef = resourceDef;
    }

    public String getUrlSegment() {
        return urlSegment;
    }

    public void setUrlSegment(String urlSegment) {
        this.urlSegment = urlSegment;
    }

    public String getHandlerImplementationName() {
        return handlerImplementationName;
    }

    public void setHandlerImplementationName(String handlerImplementationName) {
        this.handlerImplementationName = handlerImplementationName;
    }

    @Override
    public String toString() {
        return "DBSubresourceDef{" +
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

        DBSubresourceDef that = (DBSubresourceDef) o;

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


