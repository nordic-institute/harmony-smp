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
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionEventType;
import eu.europa.ec.edelivery.smp.data.enums.EventSourceType;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Document version event entity. The event entity allows user to track
 * changes in the document version. Note that the event is audited, because
 * the record is a special audit record, and it is not expected
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Entity
@Table(name = "SMP_DOCUMENT_VERSION_EVENT",
        indexes = {
                @Index(name = "SMP_DOCVEREVNT_DOCVER_IDX", columnList = "FK_DOCUMENT_VERSION_ID"),
        })
@org.hibernate.annotations.Table(appliesTo = "SMP_DOCUMENT_VERSION_EVENT", comment = "Document version Events.")
public class DBDocumentVersionEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_DOCVER_EVENT_SEQ")
    @GenericGenerator(name = "SMP_DOCVER_EVENT_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique document version event identifier")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOCUMENT_VERSION_ID")
    private DBDocumentVersion documentVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "EVENT_TYPE", nullable = false)
    @ColumnDescription(comment = "Document version event type")
    private DocumentVersionEventType eventType = DocumentVersionEventType.CREATE;

    @Column(name = "EVENT_ON")
    @ColumnDescription(comment = "Date time of the event")
    private OffsetDateTime eventOn;

    @Column(name = "EVENT_BY_USERNAME", length = CommonColumnsLengths.MAX_USERNAME_LENGTH)
    @ColumnDescription(comment = "username identifier of the user who triggered the event")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "EVENT_SOURCE", nullable = false)
    @ColumnDescription(comment = "Event source UI, API")
    private EventSourceType eventSourceType = EventSourceType.OTHER;

    @Column(name = "details", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    @ColumnDescription(comment = "Details of the event")
    private String details;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBDocumentVersion getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(DBDocumentVersion documentVersion) {
        this.documentVersion = documentVersion;
    }

    public DocumentVersionEventType getEventType() {
        return eventType;
    }

    public void setEventType(DocumentVersionEventType eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getEventOn() {
        return eventOn;
    }

    public void setEventOn(OffsetDateTime eventOn) {
        this.eventOn = eventOn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EventSourceType getEventSourceType() {
        return eventSourceType;
    }

    public void setEventSourceType(EventSourceType eventSourceType) {
        this.eventSourceType = eventSourceType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBDocumentVersionEvent that = (DBDocumentVersionEvent) o;
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
                ", documentVersion=" + documentVersion.id +
                '}';
    }
}
