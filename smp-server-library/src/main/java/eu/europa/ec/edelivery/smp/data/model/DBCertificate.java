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

/**
 * Login certificate data.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Audited
@Table(name = "SMP_CERTIFICATE")
public class DBCertificate extends BaseEntity {

    @Id
    @Column(name = "ID")
    Long id;
    @Column(name = "CERTIFICATE_ID", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH, unique = true)
    private String certificateId;
    @Column(name = "VALID_FROM")
    private LocalDateTime validFrom;
    @Column(name = "VALID_TO")
    private LocalDateTime validTo;
    @Column(name = "CREATED_ON" , nullable = false)
    LocalDateTime createdOn;
    @Column(name = "LAST_UPDATED_ON", nullable = false)
    LocalDateTime lastUpdatedOn;

    @OneToOne
    @JoinColumn(name = "ID")
    @MapsId
    DBUser dbUser;

    public DBCertificate() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public DBUser getDbUser() {
        return dbUser;
    }

    public void setDbUser(DBUser dbUser) {
        this.dbUser = dbUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBCertificate that = (DBCertificate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(certificateId, that.certificateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, certificateId);
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
