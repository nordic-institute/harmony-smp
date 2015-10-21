package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_SMP")
public class SmpEntity extends AbstractEntity {
    @Id
    @Column(name = "smp_id")
    private String smpId;

    @JoinColumn(name = "fk_certificate_id")
    @ManyToOne
    private CertificateEntity certificate;

    @Basic
    @Column(name = "endpoint_physical_address")
    private String endpointPhysicalAddress;

    @Basic
    @Column(name = "endpoint_logical_address")
    private String endpointLogicalAddress;

    public String getSmpId() {
        return smpId;
    }

    public void setSmpId(String smpId) {
        this.smpId = smpId;
    }

    public CertificateEntity getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateEntity certificate) {
        this.certificate = certificate;
    }

    public String getEndpointPhysicalAddress() {
        return endpointPhysicalAddress;
    }

    public void setEndpointPhysicalAddress(String endpointPhysicalAddress) {
        this.endpointPhysicalAddress = endpointPhysicalAddress;
    }

    public String getEndpointLogicalAddress() {
        return endpointLogicalAddress;
    }

    public void setEndpointLogicalAddress(String endpointLogicalAddress) {
        this.endpointLogicalAddress = endpointLogicalAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SmpEntity)) return false;
        if (!super.equals(o)) return false;

        SmpEntity smpEntity = (SmpEntity) o;

        if (smpId != null ? !smpId.equals(smpEntity.smpId) : smpEntity.smpId != null) return false;
        if (certificate != null ? !certificate.equals(smpEntity.certificate) : smpEntity.certificate != null)
            return false;
        if (endpointPhysicalAddress != null ? !endpointPhysicalAddress.equals(smpEntity.endpointPhysicalAddress) : smpEntity.endpointPhysicalAddress != null)
            return false;
        return !(endpointLogicalAddress != null ? !endpointLogicalAddress.equals(smpEntity.endpointLogicalAddress) : smpEntity.endpointLogicalAddress != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (smpId != null ? smpId.hashCode() : 0);
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        result = 31 * result + (endpointPhysicalAddress != null ? endpointPhysicalAddress.hashCode() : 0);
        result = 31 * result + (endpointLogicalAddress != null ? endpointLogicalAddress.hashCode() : 0);
        return result;
    }
}
