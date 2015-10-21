package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by feriaad on 15/06/2015.
 */
public class AllowedWildcardEntityPK implements Serializable {
    @Id
    @Column(name = "scheme")
    private String scheme;

    @Id
    @JoinColumn(name = "fk_certificate_id")
    @ManyToOne
    private CertificateEntity certificate;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public CertificateEntity getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateEntity certificate) {
        this.certificate = certificate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllowedWildcardEntityPK)) return false;

        AllowedWildcardEntityPK that = (AllowedWildcardEntityPK) o;

        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return !(certificate != null ? !certificate.equals(that.certificate) : that.certificate != null);

    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        return result;
    }
}
