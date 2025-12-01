/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.enums.DocumentLevelType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import java.io.Serial;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * The domain document template entity.
 * Entity is used to link domain resource definition or subresource definition with default domain document template.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Entity
@Audited
@Table(name = "SMP_DOMAIN_DOC_TMPL", comment = "The domain document template",
        indexes = {@Index(name = "SMP_DOC_TMPL_UNIQ_IDX", columnList = "DOCUMENT_LEVEL, FK_DOREDEF_ID, FK_SUREDEF_ID", unique = true)})
@NamedQuery(name = QUERY_DOMAIN_DOC_TEMPLATES_BY_DOMAIN, query = "SELECT tmpl FROM DBDomainDocumentTemplate tmpl " +
        " WHERE tmpl.domainResourceDef.domain.id = :domain_id " )
@NamedQuery(name = QUERY_DOMAIN_DOC_TEMPLATES_BY_DOMAIN_RESDEF_SUBRESDEF, query = "SELECT tmpl FROM DBDomainDocumentTemplate tmpl " +
        " WHERE tmpl.domainResourceDef.id = :domain_resource_def_id"  +
        " AND (:subresource_def_id IS NULL AND  tmpl.subresourceDef IS NULL OR tmpl.subresourceDef.id = :subresource_def_id)"  +
        " AND tmpl.documentLevelType = :document_level_type")


public class DBDomainDocumentTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1008583888835630004L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOMAIN_DOC_TMPL_SEQ")
    @GenericGenerator(name = "SMP_DOMAIN_DOC_TMPL_SEQ", strategy = "native", parameters = {
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique domain document template id")
    Long id;

    // The domain to which the resource belongs
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "FK_DOREDEF_ID")
    private DBDomainResourceDef domainResourceDef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SUREDEF_ID")
    private DBSubresourceDef subresourceDef;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_DOCUMENT_ID")
    private DBDocument document;

    @Column(name = "DOCUMENT_LEVEL", nullable = false)
    @ColumnDescription(comment = "Document level type - resource or subresource")
    @Enumerated(value = EnumType.STRING)
    private DocumentLevelType documentLevelType;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBDomainResourceDef getDomainResourceDef() {
        return domainResourceDef;
    }

    public void setDomainResourceDef(DBDomainResourceDef domainResourceDef) {
        this.domainResourceDef = domainResourceDef;
    }

    public DBSubresourceDef getSubresourceDef() {
        return subresourceDef;
    }

    public void setSubresourceDef(DBSubresourceDef subresourceDef) {
        this.subresourceDef = subresourceDef;
    }

    public DBDocument getDocument() {
        return document;
    }

    public void setDocument(DBDocument document) {
        this.document = document;
    }

    public DocumentLevelType getDocumentLevelType() {
        return documentLevelType;
    }
    public void setDocumentLevelType(DocumentLevelType documentLevelType) {
        this.documentLevelType = documentLevelType;
    }
}
