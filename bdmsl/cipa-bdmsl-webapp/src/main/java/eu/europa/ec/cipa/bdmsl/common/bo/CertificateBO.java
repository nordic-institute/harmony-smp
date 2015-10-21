package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

import java.util.Calendar;

/**
 * Created by feriaad on 12/06/2015.
 */
public class CertificateBO extends AbstractBusinessObject {

    private Long id;
    private String certificateId;
    private Calendar validFrom;
    private Calendar validTo;
    private String pemEncoding;

    // the migration date for the new certificate
    private Calendar migrationDate;

    // the reference to the new certificate
    private Long newCertificateId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(Calendar migrationDate) {
        this.migrationDate = migrationDate;
    }

    public Long getNewCertificateId() {
        return newCertificateId;
    }

    public void setNewCertificateId(Long newCertificateId) {
        this.newCertificateId = newCertificateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CertificateBO)) return false;

        CertificateBO that = (CertificateBO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (certificateId != null ? !certificateId.equals(that.certificateId) : that.certificateId != null)
            return false;
        if (validFrom != null ? !validFrom.equals(that.validFrom) : that.validFrom != null) return false;
        if (validTo != null ? !validTo.equals(that.validTo) : that.validTo != null) return false;
        if (pemEncoding != null ? !pemEncoding.equals(that.pemEncoding) : that.pemEncoding != null) return false;
        if (migrationDate != null ? !migrationDate.equals(that.migrationDate) : that.migrationDate != null)
            return false;
        return !(newCertificateId != null ? !newCertificateId.equals(that.newCertificateId) : that.newCertificateId != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (certificateId != null ? certificateId.hashCode() : 0);
        result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
        result = 31 * result + (validTo != null ? validTo.hashCode() : 0);
        result = 31 * result + (pemEncoding != null ? pemEncoding.hashCode() : 0);
        result = 31 * result + (migrationDate != null ? migrationDate.hashCode() : 0);
        result = 31 * result + (newCertificateId != null ? newCertificateId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CertificateBO{" +
                "id=" + id +
                ", certificateId='" + certificateId + '\'' +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", pemEncoding='" + pemEncoding + '\'' +
                ", migrationDate=" + migrationDate +
                ", newCertificateId=" + newCertificateId +
                '}';
    }
}
