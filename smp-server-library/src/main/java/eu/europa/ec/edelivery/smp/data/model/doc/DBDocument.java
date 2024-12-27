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
@NamedQuery(name = QUERY_SEARCH_DOCUMENT_REFERENCES, query =
        "SELECT new eu.europa.ec.edelivery.smp.data.model.doc.DBSearchReferenceDocumentMapping(" +
        "   d.id, " +
        "   r.id, " +
        "   d.name, " +
        "   r.identifierValue, " +
        "   r.identifierScheme, " +
        "   dom.domainCode," +
        "   rdef.urlSegment)" +
        " FROM DBResource r " +
        " INNER JOIN r.document d " +
        " INNER JOIN r.group gr" +
        " INNER JOIN r.domainResourceDef drd" +
        " INNER JOIN drd.domain dom" +
        " INNER JOIN drd.resourceDef rdef " +
        "  WHERE rdef.id =:resource_def_id " +
        "   AND r.id !=:resource_id " +
        "   AND d.sharingEnabled =:sharing_enabled " +
        "   AND r.visibility =:resource_visibility " +
        "   AND (gr.visibility=:group_visibility OR gr.id =:group_id)" +
        "   AND (dom.visibility=:domain_visibility OR dom.id =:domain_id)"  +
        "   AND (:resource_identifier IS NULL OR lower(r.identifierValue) like (:resource_identifier))" +
        "   AND (:resource_scheme IS NULL OR lower(r.identifierScheme) like (:resource_scheme))")
@NamedQuery(name = QUERY_SEARCH_DOCUMENT_REFERENCES_COUNT, query = "SELECT count(d.id) " +
        " FROM DBResource r " +
        " INNER JOIN r.document d " +
        " INNER JOIN r.group gr" +
        " INNER JOIN r.domainResourceDef drd" +
        " INNER JOIN drd.domain dom" +
        " INNER JOIN drd.resourceDef rdef " +
        "  WHERE rdef.id =:resource_def_id " +
        "   AND r.id !=:resource_id " +
        "   AND d.sharingEnabled =:sharing_enabled " +
        "   AND r.visibility =:resource_visibility " +
        "   AND (gr.visibility=:group_visibility OR gr.id =:group_id)" +
        "   AND (dom.visibility=:domain_visibility OR dom.id =:domain_id)"  +
        "   AND (:resource_identifier IS NULL OR lower(r.identifierValue) like (:resource_identifier))" +
        "   AND (:resource_scheme IS NULL OR lower(r.identifierScheme) like (:resource_scheme))")
@NamedQuery(name = QUERY_DOCUMENT_FOR_SUBRESOURCE, query = "SELECT d FROM DBSubresource  sr JOIN sr.document d WHERE sr.id =:subresource_id")
@NamedQuery(name = QUERY_SEARCH_DOCUMENT_REFERENCES_FOR_SUBRESOURCES, query =
        "SELECT new eu.europa.ec.edelivery.smp.data.model.doc.DBSearchReferenceDocumentMapping(" +
                "   d.id, " +
                "   r.id, " +
                "   sr.id, " +
                "   d.name, " +
                "   r.identifierValue, " +
                "   r.identifierScheme, " +
                "   sr.identifierValue, " +
                "   sr.identifierScheme, " +
                "   dom.domainCode," +
                "   rdef.urlSegment," +
                "   srdef.urlSegment)" +
                " FROM DBSubresource sr " +
                " INNER JOIN sr.subresourceDef srdef " +
                " INNER JOIN sr.document d " +
                " INNER JOIN sr.resource r " +
                " INNER JOIN r.domainResourceDef drd" +
                " INNER JOIN r.group gr" +
                " INNER JOIN drd.domain dom" +
                " INNER JOIN drd.resourceDef rdef " +
                "  WHERE " +
                "   srdef.id =:subresource_def_id " +
                "   AND sr.id !=:subresource_id " +
                "   AND d.sharingEnabled =:sharing_enabled " +
                "   AND r.visibility =:resource_visibility " +
                "   AND (gr.visibility=:group_visibility OR gr.id =:group_id)" +
                "   AND (dom.visibility=:domain_visibility OR dom.id =:domain_id)"  +
                "   AND (:resource_identifier IS NULL OR lower(r.identifierValue) like (:resource_identifier))" +
                "   AND (:resource_scheme IS NULL OR lower(r.identifierScheme) like (:resource_scheme))" +
                "   AND (:subresource_identifier IS NULL OR lower(sr.identifierValue) like (:subresource_identifier))" +
                "   AND (:subresource_scheme IS NULL OR lower(sr.identifierScheme) like (:subresource_scheme))")
@NamedQuery(name = QUERY_SEARCH_DOCUMENT_REFERENCES_FOR_SUBRESOURCES_COUNT, query = "SELECT count(d.id) " +
        " FROM DBSubresource sr " +
        " INNER JOIN sr.subresourceDef srdef " +
        " INNER JOIN sr.document d " +
        " INNER JOIN sr.resource r " +
        " INNER JOIN r.domainResourceDef drd" +
        " INNER JOIN r.group gr" +
        " INNER JOIN drd.domain dom" +
        " INNER JOIN drd.resourceDef rdef " +
        "  WHERE " +
        "   srdef.id =:subresource_def_id " +
        "   AND sr.id !=:subresource_id " +
        "   AND d.sharingEnabled =:sharing_enabled " +
        "   AND r.visibility =:resource_visibility " +
        "   AND (gr.visibility=:group_visibility OR gr.id =:group_id)" +
        "   AND (dom.visibility=:domain_visibility OR dom.id =:domain_id)"  +
        "   AND (:resource_identifier IS NULL OR lower(r.identifierValue) like (:resource_identifier))" +
        "   AND (:resource_scheme IS NULL OR lower(r.identifierScheme) like (:resource_scheme))" +
        "   AND (:subresource_identifier IS NULL OR lower(sr.identifierValue) like (:subresource_identifier))" +
        "   AND (:subresource_scheme IS NULL OR lower(sr.identifierScheme) like (:subresource_scheme))")

@NamedQuery(name = QUERY_DOCUMENT_LIST_FOR_TARGET_DOCUMENT, query = "SELECT d FROM DBDocument d WHERE d.referenceDocument.id =:document_id")
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

    @Column(name = "REF_DOCUMENT_URL", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    private String referenceDocumentUrl;

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

    public String getReferenceDocumentUrl() {
        return referenceDocumentUrl;
    }

    public void setReferenceDocumentUrl(String referenceDocumentAddress) {
        this.referenceDocumentUrl = referenceDocumentAddress;
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

        if (getCurrentVersion() <= 0 || documentVersion.getStatus() == DocumentVersionStatusType.PUBLISHED) {
            setCurrentVersion(documentVersion.getVersion());
        }

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
