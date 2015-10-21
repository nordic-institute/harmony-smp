package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_CERTIFICATE_DOMAIN")
public class CertificateDomainEntity extends AbstractEntity {
    @Id
    @Column(name = "root_certificate_alias")
    private String rootCertificateAlias;

    @Column(name = "domain")
    private String domain;

    @Column(name = "crl_url")
    private String crl;

    public String getRootCertificateAlias() {
        return rootCertificateAlias;
    }

    public void setRootCertificateAlias(String rootCertificateAlias) {
        this.rootCertificateAlias = rootCertificateAlias;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCrl() {
        return crl;
    }

    public void setCrl(String crl) {
        this.crl = crl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CertificateDomainEntity that = (CertificateDomainEntity) o;

        if (rootCertificateAlias != null ? !rootCertificateAlias.equals(that.rootCertificateAlias) : that.rootCertificateAlias != null)
            return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        return !(crl != null ? !crl.equals(that.crl) : that.crl != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (rootCertificateAlias != null ? rootCertificateAlias.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (crl != null ? crl.hashCode() : 0);
        return result;
    }
}
