/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;


@Entity
@Audited
@Table(name = "SMP_SUBRESOURCE",
        indexes = {@Index(name = "SMP_SRS_UNIQ_ID_RES_SRT_IDX", columnList = "FK_RESOURCE_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME", unique = true),
                @Index(name = "SMP_SMD_DOC_ID_IDX", columnList = "IDENTIFIER_VALUE", unique = false),
                @Index(name = "SMP_SMD_DOC_SCH_IDX", columnList = "IDENTIFIER_SCHEME", unique = false)
        })
@org.hibernate.annotations.Table(appliesTo = "SMP_SUBRESOURCE", comment = "Service metadata")
@NamedQuery(name = QUERY_SUBRESOURCE_BY_IDENTIFIER_RESOURCE_SUBRESDEF, query = "SELECT d FROM DBSubresource d WHERE d.resource.id = :resource_id " +
        " AND d.subresourceDef.urlSegment=:url_segment" +
        " AND d.identifierValue = :identifier_value " +
        " AND (:identifier_scheme IS NULL AND d.identifierScheme IS NULL " +
        " OR d.identifierScheme = :identifier_scheme)"
)
@NamedQuery(name = QUERY_SUBRESOURCE_BY_RESOURCE_SUBRESDEF , query = "SELECT d FROM DBSubresource d WHERE d.subresourceDef.identifier = :subresource_def_identifier " +
        " AND d.resource.identifierValue=:resource_identifier " +
        " AND d.resource.identifierScheme=:resource_scheme order by id asc"
)
@NamedQuery(name = "DBSubresource.deleteById", query = "DELETE FROM DBSubresource d WHERE d.id = :id")
public class DBSubresource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_SUBRESOURCE_SEQ")
    @GenericGenerator(name = "SMP_SUBRESOURCE_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Shared primary key with master table SMP_SUBRESOURCE")
    Long id;

    @Column(name = "IDENTIFIER_VALUE", length = CommonColumnsLengths.MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH, nullable = false)
    String identifierValue;

    @Column(name = "IDENTIFIER_SCHEME", length = CommonColumnsLengths.MAX_DOCUMENT_TYPE_IDENTIFIER_SCHEME_LENGTH)
    String identifierScheme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_RESOURCE_ID")
    private DBResource resource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_SUREDEF_ID")
    private DBSubresourceDef subresourceDef;


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOCUMENT_ID")
    private DBDocument document;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifierValue() {
        return this.identifierValue;
    }

    public void setIdentifierValue(String documentIdentifier) {
        this.identifierValue = documentIdentifier;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public void setIdentifierScheme(String documentIdentifierScheme) {
        this.identifierScheme = documentIdentifierScheme;
    }

    public DBSubresourceDef getSubresourceDef() {
        return subresourceDef;
    }

    public void setSubresourceDef(DBSubresourceDef subresourceDef) {
        this.subresourceDef = subresourceDef;
    }

    @Transient

    public DBResource getResource() {
        return resource;
    }

    public void setResource(DBResource resource) {
        this.resource = resource;
    }

    public DBDocument getDocument() {
        return document;
    }

    public byte[] getCurrentContent() {
        return getCurrentContent();
    }

    public void setDocument(DBDocument document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "DBSubresource{" +
                "id=" + id +
                ", identifierValue='" + identifierValue + '\'' +
                ", identifierScheme='" + identifierScheme + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBSubresource that = (DBSubresource) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
