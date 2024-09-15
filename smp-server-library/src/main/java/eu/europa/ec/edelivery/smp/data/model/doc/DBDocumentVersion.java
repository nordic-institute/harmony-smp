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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Document content is stored in the database
 * keep blobs/clobs in separate table!
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

@Entity
@Audited
@Table(name = "SMP_DOCUMENT_VERSION",
        indexes = {
                @Index(name = "SMP_DOCVER_DOCUMENT_IDX", columnList = "FK_DOCUMENT_ID"),
                @Index(name = "SMP_DOCVER_UNIQ_VERSION_IDX", columnList = "FK_DOCUMENT_ID, VERSION", unique = true),

        })
@org.hibernate.annotations.Table(appliesTo = "SMP_DOCUMENT_VERSION", comment = "Document content for the document version.")
@NamedQuery(name = QUERY_DOCUMENT_VERSION_CURRENT_FOR_RESOURCE, query = "SELECT dv FROM DBResource r join r.document d join d.documentVersions dv " +
        " WHERE dv.version = d.currentVersion " +
        " AND r.id= :resource_id ")
@NamedQuery(name = QUERY_DOCUMENT_VERSION_LIST_FOR_RESOURCE, query = "SELECT dv FROM DBResource r join r.document.documentVersions dv " +
        " WHERE r.id= :resource_id order by dv.version desc")
@NamedQuery(name = QUERY_DOCUMENT_VERSION_CURRENT_FOR_SUBRESOURCE, query = "SELECT dv FROM " +
        "   DBSubresource sr join sr.document d join d.documentVersions dv " +
        " WHERE dv.version = d.currentVersion " +
        " AND sr.id= :subresource_id ")
@NamedQuery(name = QUERY_DOCUMENT_VERSION_LIST_FOR_SUBRESOURCE, query = "SELECT dv FROM " +
        "   DBSubresource sr join sr.document.documentVersions dv " +
        " WHERE sr.id= :subresource_id order by dv.version desc")
@NamedNativeQuery(name = QUERY_DOCUMENT_VERSION_UNDER_REVIEW_FOR_USER,
        query = "SELECT " +
                "    dv.ID AS ID, " +
                "    dv.LAST_UPDATED_ON AS LAST_UPDATED_ON," +
                "    dv.FK_DOCUMENT_ID AS DOCUMENT_ID," +
                "    dv.STATUS AS STATUS," +
                "    dv.VERSION AS VERSION," +
                "    r.ID AS RESOURCE_ID," +
                "    sr.ID AS SUBRESOURCE_ID," +
                "    r.IDENTIFIER_VALUE AS RIDENTIFIER_VALUE," +
                "    r.IDENTIFIER_SCHEME AS RIDENTIFIER_SCHEME," +
                "    sr.IDENTIFIER_VALUE AS SRIDENTIFIER_VALUE," +
                "    sr.IDENTIFIER_SCHEME AS SRIDENTIFIER_SCHEME," +
                "    CASE " +
                "        WHEN sr.ID IS NOT NULL THEN 'SUBRESOURCE'" +
                "        ELSE 'RESOURCE'" +
                "    END AS TARGET" +
                " FROM " +
                "    SMP_DOCUMENT_VERSION dv" +
                "    INNER JOIN SMP_DOCUMENT d ON dv.FK_DOCUMENT_ID = d.ID" +
                "    LEFT JOIN SMP_SUBRESOURCE sr ON d.ID = sr.FK_DOCUMENT_ID" +
                "    LEFT JOIN SMP_RESOURCE r ON d.ID = r.FK_DOCUMENT_ID OR r.ID = sr.FK_RESOURCE_ID" +
                "    INNER JOIN SMP_RESOURCE_MEMBER rmu ON r.ID = rmu.FK_RESOURCE_ID" +
                " WHERE " +
                "    dv.STATUS = :status" +
                "    AND r.REVIEW_ENABLED = :review_enabled" +
                "    AND rmu.FK_USER_ID = :user_id" +
                "    AND rmu.PERMISSION_REVIEW = :permission_can_review",

        resultSetMapping = "DBReviewDocumentVersionsMapping")

@SqlResultSetMapping(name = "DBReviewDocumentVersionsMapping",
        classes = {
                @ConstructorResult(targetClass = DBReviewDocumentVersion.class,
                        columns = {
                                @ColumnResult(name = "ID", type = Long.class),
                                @ColumnResult(name = "DOCUMENT_ID", type = Long.class),
                                @ColumnResult(name = "RESOURCE_ID", type = Long.class),
                                @ColumnResult(name = "SUBRESOURCE_ID", type = Long.class),
                                @ColumnResult(name = "VERSION", type = Integer.class),
                                @ColumnResult(name = "STATUS", type = String.class),
                                @ColumnResult(name = "RIDENTIFIER_VALUE", type = String.class),
                                @ColumnResult(name = "RIDENTIFIER_SCHEME", type = String.class),
                                @ColumnResult(name = "SRIDENTIFIER_VALUE", type = String.class),
                                @ColumnResult(name = "SRIDENTIFIER_SCHEME", type = String.class),
                                @ColumnResult(name = "TARGET", type = String.class),
                                @ColumnResult(name = "LAST_UPDATED_ON", type = OffsetDateTime.class),
                        })
        })
public class DBDocumentVersion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOCUMENT_VERSION_SEQ")
    @GenericGenerator(name = "SMP_DOCUMENT_VERSION_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique version document id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOCUMENT_ID")
    private DBDocument document;
    // list of all document events  with the latest event first!
    @OneToMany(
            mappedBy = "documentVersion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @NotAudited
    @OrderBy("id desc")
    List<DBDocumentVersionEvent> documentVersionEvents;
    // version of the document
    @Column(name = "VERSION", nullable = false)
    private int version;
    // lob fetch it only when needed!
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "DOCUMENT_CONTENT")
    @ColumnDescription(comment = "Document content")
    byte[] content;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @ColumnDescription(comment = "Document version status")
    private DocumentVersionStatusType status = DocumentVersionStatusType.DRAFT;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBDocument getDocument() {
        return document;
    }

    public void setDocument(DBDocument document) {
        this.document = document;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public DocumentVersionStatusType getStatus() {
        return status;
    }

    public void setStatus(DocumentVersionStatusType status) {
        this.status = status;
    }

    /**
     * Returns document version events
     *
     * @return list of events for the document versions
     */
    public List<DBDocumentVersionEvent> getDocumentVersionEvents() {
        if (documentVersionEvents == null) {
            documentVersionEvents = new ArrayList<>();
        }
        return documentVersionEvents;
    }

    /**
     * Add new document version event. Beause of the order of the events,
     * the new event is added to the beginning of the list.*
     *
     * @param event event to be added
     * @return added event
     */
    public DBDocumentVersionEvent addNewDocumentVersionEvent(DBDocumentVersionEvent event) {
        event.setDocumentVersion(this);
        getDocumentVersionEvents().add(0, event);
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBDocumentVersion that = (DBDocumentVersion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "DBDocumentVersion{" +
                "id=" + id +
                ", document=" + document.id +
                ", version=" + version +
                ", status=" + status +
                '}';
    }
}
