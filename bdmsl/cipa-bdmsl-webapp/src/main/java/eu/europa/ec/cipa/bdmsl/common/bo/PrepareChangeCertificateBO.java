package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

import java.util.Calendar;

/**
 * Created by feriaad on 14/07/2015.
 */
public class PrepareChangeCertificateBO extends AbstractBusinessObject {
    private CertificateBO currentCertificate;
    private Calendar migrationDate;
    private String publicKey;

    public CertificateBO getCurrentCertificate() {
        return currentCertificate;
    }

    public void setCurrentCertificate(CertificateBO currentCertificate) {
        this.currentCertificate = currentCertificate;
    }

    public Calendar getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(Calendar migrationDate) {
        this.migrationDate = migrationDate;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrepareChangeCertificateBO)) return false;

        PrepareChangeCertificateBO that = (PrepareChangeCertificateBO) o;

        if (currentCertificate != null ? !currentCertificate.equals(that.currentCertificate) : that.currentCertificate != null)
            return false;
        if (migrationDate != null ? !migrationDate.equals(that.migrationDate) : that.migrationDate != null)
            return false;
        return !(publicKey != null ? !publicKey.equals(that.publicKey) : that.publicKey != null);

    }

    @Override
    public int hashCode() {
        int result = currentCertificate != null ? currentCertificate.hashCode() : 0;
        result = 31 * result + (migrationDate != null ? migrationDate.hashCode() : 0);
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrepareChangeCertificateBO{" +
                "currentCertificate=" + currentCertificate +
                ", migrationDate=" + migrationDate +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
