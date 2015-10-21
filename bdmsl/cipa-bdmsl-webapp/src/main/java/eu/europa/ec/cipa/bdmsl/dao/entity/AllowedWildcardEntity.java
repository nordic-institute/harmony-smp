package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_ALLOWED_WILDCARD")
@IdClass(AllowedWildcardEntityPK.class)
public class AllowedWildcardEntity extends AbstractEntity {
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
        if (!(o instanceof AllowedWildcardEntity)) return false;
        if (!super.equals(o)) return false;

        AllowedWildcardEntity that = (AllowedWildcardEntity) o;

        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return !(certificate != null ? !certificate.equals(that.certificate) : that.certificate != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        return result;
    }
}
