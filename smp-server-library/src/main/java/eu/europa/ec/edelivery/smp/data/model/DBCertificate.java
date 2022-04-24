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

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
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
@org.hibernate.annotations.Table(appliesTo = "SMP_CERTIFICATE", comment = "SMP user certificates")
public class DBCertificate extends BaseEntity {

    @Id
    @Column(name = "ID")
    @ColumnDescription(comment = "Shared primary key with master table SMP_USER")
    Long id;
    @Column(name = "CERTIFICATE_ID", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH, unique = true)
    @ColumnDescription(comment = "Formatted Certificate id using tags: cn, o, c:serialNumber")
    private String certificateId;
    @Column(name = "VALID_FROM")
    @ColumnDescription(comment = "Certificate valid from date.")
    private LocalDateTime validFrom;
    @Column(name = "VALID_TO")
    @ColumnDescription(comment = "Certificate valid to date.")
    private LocalDateTime validTo;
    @Column(name = "SUBJECT", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    @ColumnDescription(comment = "Certificate subject (canonical form)")
    private String subject;
    @Column(name = "ISSUER", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    @ColumnDescription(comment = "Certificate issuer (canonical form)")
    private String issuer;
    @Column(name = "SERIALNUMBER", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128)
    @ColumnDescription(comment = "Certificate serial number")
    private String serialNumber;

    @Column(name = "PEM_ENCODED_CERT")
    @ColumnDescription(comment = "PEM encoded  certificate")
    @Lob
    private String pemEncoding;

    @Column(name = "CRL_URL", length = CommonColumnsLengths.MAX_FREE_TEXT_LENGTH)
    @ColumnDescription(comment = "URL to the certificate revocation list (CRL)")
    private String crlUrl;

    @OneToOne
    @JoinColumn(name = "ID")
    @MapsId
    DBUser dbUser;

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


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPemEncoding() {
        return pemEncoding;
    }

    public void setPemEncoding(String pemEncoding) {
        this.pemEncoding = pemEncoding;
    }

    public String getCrlUrl() {
        return crlUrl;
    }

    public void setCrlUrl(String crlUrl) {
        this.crlUrl = crlUrl;
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
}
