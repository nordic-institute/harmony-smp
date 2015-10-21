package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

/**
 * Created by feriaad on 12/06/2015.
 */
public class CertificateDomainBO extends AbstractBusinessObject {

    private String rootCertificateAlias;
    private String domain;
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

        CertificateDomainBO that = (CertificateDomainBO) o;

        if (rootCertificateAlias != null ? !rootCertificateAlias.equals(that.rootCertificateAlias) : that.rootCertificateAlias != null)
            return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        return !(crl != null ? !crl.equals(that.crl) : that.crl != null);

    }

    @Override
    public int hashCode() {
        int result = rootCertificateAlias != null ? rootCertificateAlias.hashCode() : 0;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (crl != null ? crl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CertificateDomainBO{" +
                "rootCertificateAlias='" + rootCertificateAlias + '\'' +
                ", domain='" + domain + '\'' +
                ", crl='" + crl + '\'' +
                '}';
    }
}
