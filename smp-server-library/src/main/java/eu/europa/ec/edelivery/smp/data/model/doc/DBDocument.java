package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Database optimization: load service metadata xml only when needed and
 * keep blobs/clobs in separate table!
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Audited
@Table(name = "SMP_DOCUMENT")
@org.hibernate.annotations.Table(appliesTo = "SMP_DOCUMENT", comment = "SMP document entity for resources and subresources")
@NamedQueries({
        @NamedQuery(name = QUERY_DOCUMENT_FOR_RESOURCE, query = "SELECT d FROM DBResource r JOIN r.document d WHERE r.id =:resource_id"),
})
public class DBDocument extends BaseEntity {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBDocument.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOCUMENT_SEQ")
    @GenericGenerator(name = "SMP_DOCUMENT_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique document id")
    Long id;

    // list of all version with the latest version first!
    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY

    )
    List<DBDocumentVersion> documentVersions;

    @Column(name = "CURRENT_VERSION", nullable = false)
    private int currentVersion;


    @Column(name = "MIME_TYPE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String mimeType;

    @Column(name = "NAME")
    private String name;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    /**
     * Returns document version ordered from the latest version to first version
     * @return document versions
     */
    public List<DBDocumentVersion> getDocumentVersions() {
        if (documentVersions ==null) {
            documentVersions = new ArrayList<>();
        }
        return documentVersions;
    }

    public DBDocumentVersion addNewDocumentVersion(DBDocumentVersion documentVersion){
        if (documentVersion.getId() !=null && getDocumentVersions().contains(documentVersion)) {
            LOG.info("Document version [{}] already exists on document [{}]", documentVersion, this);
            return documentVersion;
        }
        documentVersion.setVersion(getNextVersionIndex());
        getDocumentVersions().add(documentVersion);
        documentVersion.setDocument(this);
        setCurrentVersion(documentVersion.getVersion());
        return documentVersion;
    }

    public void removeDocumentVersion(DBDocumentVersion documentVersion){
        boolean removed = getDocumentVersions().remove(documentVersion);
        if (removed){
            documentVersion.setDocument(null);
        }
    }


    protected int getNextVersionIndex(){
        List<DBDocumentVersion> list = getDocumentVersions();
        return  list.stream()
                .map(DBDocumentVersion::getVersion)
                .reduce(-1, (a, b) -> Integer.max(a, b))
                .intValue() + 1;
    }


    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
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

    @Override
    public String toString() {
        return "DBDocument{" +
                "id=" + id +
                ", currentVersion=" + currentVersion +
                ", mimeType='" + mimeType + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBDocument that = (DBDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
