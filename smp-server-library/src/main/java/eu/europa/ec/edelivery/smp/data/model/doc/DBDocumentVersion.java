/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
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

@NamedQueries({
        @NamedQuery(name = QUERY_DOCUMENT_VERSION_CURRENT_FOR_RESOURCE, query = "SELECT dv FROM DBResource r join r.document d join d.documentVersions dv " +
                " WHERE dv.version = d.currentVersion " +
                " AND r.id= :resource_id "),
        @NamedQuery(name = QUERY_DOCUMENT_VERSION_LIST_FOR_RESOURCE, query = "SELECT dv FROM DBResource r join r.document.documentVersions dv " +
                " WHERE r.id= :resource_id order by dv.version desc"),

        @NamedQuery(name = QUERY_DOCUMENT_VERSION_CURRENT_FOR_SUBRESOURCE, query = "SELECT dv FROM " +
                "   DBSubresource sr join sr.document d join d.documentVersions dv " +
                " WHERE dv.version = d.currentVersion " +
                " AND sr.id= :subresource_id "),
        @NamedQuery(name = QUERY_DOCUMENT_VERSION_LIST_FOR_SUBRESOURCE, query = "SELECT dv FROM " +
                "   DBSubresource sr join sr.document.documentVersions dv " +
                " WHERE sr.id= :subresource_id order by dv.version desc")
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


    @Column(name = "VERSION", nullable = false)
    private int version;


    // lob fetch it only when needed!
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "DOCUMENT_CONTENT")
    @ColumnDescription(comment = "Document content")
    byte[] content;

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
}
