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
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Document property entity
 *
 * @since 5.1
 * @author Joze Rihtarsic
 */

@Entity
@Audited
@Table(name = "SMP_DOCUMENT_PROPERTY",
        indexes = {@Index(name = "SMP_DOC_PROP_IDX", columnList = "FK_DOCUMENT_ID, PROPERTY_NAME", unique = true)
        })
public class DBDocumentProperty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOC_PROP_SEQ")
    @GenericGenerator(name = "SMP_DOC_PROP_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique document property id")
    Long id;

    @NotNull
    @Column(name = "PROPERTY_NAME")
    protected String property;

    @Column(name = "PROPERTY_VALUE", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    private String value;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "Property description")
    String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FK_DOCUMENT_ID")
    private DBDocument document;

    public DBDocumentProperty() {
    }

    public DBDocumentProperty(String property, String value, DBDocument document) {
        this.property = property;
        this.value = value;
        this.document = document;
    }

    public DBDocumentProperty(String property, OffsetDateTime value, DBDocument document) {
        this.property = property;
        this.document = document;
        setDateTime(value);
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public DBDocument getDocument() {
        return document;
    }

    public void setDocument(DBDocument document) {
        this.document = document;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Transient
    public void setDateTime(OffsetDateTime date) {
        setValue(date == null ? null : date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Transient
    public OffsetDateTime getDateTime() {
        return StringUtils.isBlank(value) ? null : OffsetDateTime.parse(getValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBDocumentProperty that = (DBDocumentProperty) o;
        return Objects.equals(id, that.id) && Objects.equals(property, that.property) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, property, value, document);
    }

    public boolean isTransient() {
        return TransientDocumentPropertyType.fromPropertyName(getProperty()) != null;
    }
}
