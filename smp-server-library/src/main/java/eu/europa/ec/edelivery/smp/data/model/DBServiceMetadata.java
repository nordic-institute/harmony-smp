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

package eu.europa.ec.edelivery.smp.data.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Audited
@Table(name = "SMP_SERVICE_METADATA",
        indexes = {@Index(name = "SMP_MT_UNIQ_SG_DOC_IDX", columnList = "FK_SG_DOM_ID, DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME", unique = true),
                @Index(name = "SMP_SMD_DOC_ID_IDX", columnList = "DOCUMENT_IDENTIFIER", unique = false),
                @Index(name = "SMP_SMD_DOC_SCH_IDX", columnList = "DOCUMENT_SCHEME", unique = false)
        })
@NamedQueries({
        @NamedQuery(name = "DBServiceMetadata.deleteById", query = "DELETE FROM DBServiceMetadata d WHERE d.id = :id"),
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "DBServiceMetadata.getBySGIdentifierAndSMDdentifier", query = "SELECT md.* FROM SMP_SERVICE_METADATA md  INNER JOIN SMP_SERVICE_GROUP_DOMAIN sgd ON sgd.ID = md.FK_SG_DOM_ID \n" +
                " INNER JOIN SMP_SERVICE_GROUP sg  ON sg.ID = sgd.FK_SG_ID\n" +
                " where sg.PARTICIPANT_IDENTIFIER = :partcId AND sg.PARTICIPANT_SCHEME=:partcSch" +
                " AND md.DOCUMENT_IDENTIFIER=:docId AND md.DOCUMENT_SCHEME=:docSch", resultClass=DBServiceMetadata.class),
        @NamedNativeQuery(name = "DBServiceMetadata.getBySGIdentifier", query = "SELECT md.* FROM SMP_SERVICE_METADATA md  INNER JOIN SMP_SERVICE_GROUP_DOMAIN sgd ON sgd.ID = md.FK_SG_DOM_ID \n" +
                " INNER JOIN SMP_SERVICE_GROUP sg  ON sg.ID = sgd.FK_SG_ID\n" +
                " where sg.PARTICIPANT_IDENTIFIER = :partcId AND sg.PARTICIPANT_SCHEME=:partcSch", resultClass=DBServiceMetadata.class)
})
public class DBServiceMetadata extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sgmd_generator")
    @SequenceGenerator(name = "sgmd_generator", sequenceName = "SMP_SERVICE_METADATA_SEQ")
    @Column(name = "ID")
    Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "FK_SG_DOM_ID", nullable = false)
    })
    private DBServiceGroupDomain serviceGroupDomain;

    @Column(name = "DOCUMENT_IDENTIFIER", length = CommonColumnsLengths.MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH, nullable = false)
    String documentIdentifier;
    // Specification 1.0 (and also 2.0) allows document schema (ebMS action schema) could be null.
    // http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/os/bdx-smp-v1.0-os.html#_Toc490131041
    @Column(name = "DOCUMENT_SCHEME", length = CommonColumnsLengths.MAX_DOCUMENT_TYPE_IDENTIFIER_SCHEME_LENGTH)
    String documentIdentifierScheme;

    @OneToOne(mappedBy = "serviceMetadata",
            cascade=CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private DBServiceMetadataXml serviceMetadataXml;

    @Column(name = "CREATED_ON" , nullable = false)
    LocalDateTime createdOn;
    @Column(name = "LAST_UPDATED_ON", nullable = false)
    LocalDateTime lastUpdatedOn;


    public DBServiceMetadata() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBServiceGroupDomain getServiceGroupDomain() {
        return this.serviceGroupDomain;
    }

    public void setServiceGroupDomain(DBServiceGroupDomain serviceGroupDomain) {
        this.serviceGroupDomain = serviceGroupDomain;
    }

    public String getDocumentIdentifier() {
        return this.documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public String getDocumentIdentifierScheme() {
        return documentIdentifierScheme;
    }

    public void setDocumentIdentifierScheme(String documentIdentifierScheme) {
        this.documentIdentifierScheme = documentIdentifierScheme;
    }

    public DBServiceMetadataXml getServiceMetadataXml() {
        return this.serviceMetadataXml;
    }

    public void setServiceMetadataXml(DBServiceMetadataXml smdx) {

        if (smdx == null) {
            if (this.serviceMetadataXml != null) {
                this.serviceMetadataXml.setServiceMetadata(null);
            }
        }
        else {
            smdx.setServiceMetadata(this);
        }
        this.serviceMetadataXml = smdx;
    }

    @Transient
    public byte[] getXmlContent() {
        return getServiceMetadataXml() != null ? getServiceMetadataXml().getXmlContent() : null;
    }

    @Transient
    public void setXmlContent(byte[] extension) {

        if (extension == null) {
            if (this.serviceMetadataXml != null) {
                this.serviceMetadataXml.setXmlContent(null);
            }
        } else {
            if (this.serviceMetadataXml == null) {
                this.serviceMetadataXml = new DBServiceMetadataXml();
                this.serviceMetadataXml.setServiceMetadata(this);
            }
            this.serviceMetadataXml.setXmlContent(extension);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBServiceMetadata that = (DBServiceMetadata) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id);
    }

    @PrePersist
    public void prePersist() {
        if(createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        lastUpdatedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedOn = LocalDateTime.now();
    }

    // @Where annotation not working with entities that use inheritance
    // https://hibernate.atlassian.net/browse/HHH-12016
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(LocalDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
