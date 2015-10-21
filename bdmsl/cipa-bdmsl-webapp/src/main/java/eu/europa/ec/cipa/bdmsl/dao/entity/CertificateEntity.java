package eu.europa.ec.cipa.bdmsl.dao.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_CERTIFICATE")
public class CertificateEntity extends AbstractEntity {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    @Column(name = "CERTIFICATE_ID")
    private String certificateId;

    @Column(name = "VALID_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar validFrom;

    @Column(name = "VALID_UNTIL")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar validTo;

    @Column(name = "PEM_ENCODING")
    private String pemEncoding;

    @Column(name = "NEW_CERT_CHANGE_DATE")
    private Calendar newCertificateChangeDate;

    @ManyToOne
    @JoinColumn(name = "NEW_CERT_ID")
    private CertificateEntity newCertificate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public Calendar getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Calendar validFrom) {
        this.validFrom = validFrom;
    }

    public Calendar getValidTo() {
        return validTo;
    }

    public void setValidTo(Calendar validTo) {
        this.validTo = validTo;
    }

    public String getPemEncoding() {
        return pemEncoding;
    }

    public void setPemEncoding(String pemEncoding) {
        this.pemEncoding = pemEncoding;
    }

    public Calendar getNewCertificateChangeDate() {
        return newCertificateChangeDate;
    }

    public void setNewCertificateChangeDate(Calendar newCertificateChangeDate) {
        this.newCertificateChangeDate = newCertificateChangeDate;
    }

    public CertificateEntity getNewCertificate() {
        return newCertificate;
    }

    public void setNewCertificate(CertificateEntity newCertificate) {
        this.newCertificate = newCertificate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CertificateEntity)) return false;

        CertificateEntity that = (CertificateEntity) o;

        if (id != that.id) return false;
        if (certificateId != null ? !certificateId.equals(that.certificateId) : that.certificateId != null)
            return false;
        if (validFrom != null ? !validFrom.equals(that.validFrom) : that.validFrom != null) return false;
        if (validTo != null ? !validTo.equals(that.validTo) : that.validTo != null) return false;
        if (pemEncoding != null ? !pemEncoding.equals(that.pemEncoding) : that.pemEncoding != null) return false;
        if (newCertificateChangeDate != null ? !newCertificateChangeDate.equals(that.newCertificateChangeDate) : that.newCertificateChangeDate != null)
            return false;
        return !(newCertificate != null ? !newCertificate.equals(that.newCertificate) : that.newCertificate != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (certificateId != null ? certificateId.hashCode() : 0);
        result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
        result = 31 * result + (validTo != null ? validTo.hashCode() : 0);
        result = 31 * result + (pemEncoding != null ? pemEncoding.hashCode() : 0);
        result = 31 * result + (newCertificateChangeDate != null ? newCertificateChangeDate.hashCode() : 0);
        result = 31 * result + (newCertificate != null ? newCertificate.hashCode() : 0);
        return result;
    }
}
