/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
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

        @NamedQuery(name = QUERY_DOCUMENT_FOR_RESOURCE, query = "SELECT d FROM DBResource r JOIN r.document d WHERE r.id =:resource_id")
        @NamedQuery(name = QUERY_DOCUMENT_BY_RESOURCE_DEF_SHARING, query = "SELECT d FROM DBResource r " +
                "INNER JOIN r.document d " +
                "INNER JOIN r.domainResourceDef.resourceDef rdef " +
                "  WHERE rdef.identifier =:resource_def_identifier and d.sharingEnabled =:sharing_enabled")
        @NamedQuery(name = QUERY_DOCUMENT_FOR_SUBRESOURCE, query = "SELECT d FROM DBSubresource  sr JOIN sr.document d WHERE sr.id =:subresource_id")
        @NamedQuery(name = QUERY_DOCUMENT_BY_SUBRESOURCE_DEF_SHARING, query = "SELECT d FROM DBSubresource rs " +
                "INNER JOIN rs.document d " +
                "INNER JOIN rs.subresourceDef rdef " +
                "  WHERE rdef.identifier =:subresource_def_identifier and d.sharingEnabled =:sharing_enabled")
public class DBDocument extends BaseEntity {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBDocument.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOCUMENT_SEQ")
    @GenericGenerator(name = "SMP_DOCUMENT_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique document id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_REF_DOCUMENT_ID")
    private DBDocument referenceDocument;

    // list of all version with the latest version first!
    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("id DESC")
    List<DBDocumentVersion> documentVersions;

    @Column(name = "CURRENT_VERSION", nullable = false)
    private int currentVersion;


    @Column(name = "MIME_TYPE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    private String mimeType;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SHARING_ENABLED")
    private Boolean sharingEnabled = Boolean.FALSE;

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DBDocumentProperty> documentProperties = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBDocument getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(DBDocument referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    /**
     * Returns document version ordered from the latest version to first version
     *
     * @return document versions
     */
    public List<DBDocumentVersion> getDocumentVersions() {
        if (documentVersions == null) {
            documentVersions = new ArrayList<>();
        }
        return documentVersions;
    }

    /**
     * Method add new document version to the document and set the version number.
     * The version number is set to the highest version number + 1 and set as current version.
     * Also existing published version is set to retired.
     *
     * @param documentVersion document version
     * @return document version
     */
    public DBDocumentVersion addNewDocumentVersion(DBDocumentVersion documentVersion) {
        if (documentVersion.getId() != null && getDocumentVersions().contains(documentVersion)) {
            LOG.info("Document version [{}] already exists on document [{}]", documentVersion, this);
            return documentVersion;
       }

        documentVersion.setVersion(getNextVersionIndex());
        documentVersion.setDocument(this);
        setCurrentVersion(documentVersion.getVersion());
        // ADD TO THE LIST to the first position (latest version)
        getDocumentVersions().add(0, documentVersion);
        return documentVersion;
    }

    public void removeDocumentVersion(DBDocumentVersion documentVersion) {
        boolean removed = getDocumentVersions().remove(documentVersion);
        if (removed) {
            documentVersion.setDocument(null);
        }
    }


    protected int getNextVersionIndex() {
        List<DBDocumentVersion> list = getDocumentVersions();
        return list.stream()
                .map(DBDocumentVersion::getVersion)
                .reduce(0, Integer::max) + 1;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public List<DBDocumentProperty> getDocumentProperties() {
        if (documentProperties == null) {
            documentProperties = new ArrayList<>();
        }
        return documentProperties;
    }

    private void removeTransientProperties() {
        getDocumentProperties().removeIf(DBDocumentProperty::isTransient);
    }

    public Boolean getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(Boolean sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    @Override
    public void prePersist() {
        super.prePersist();
        removeTransientProperties();
    }

    @Override
    public void preUpdate() {
        super.preUpdate();
        removeTransientProperties();
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
