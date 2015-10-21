package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

/**
 * Created by feriaad on 12/06/2015.
 */
public class ServiceMetadataPublisherBO extends AbstractBusinessObject {

    private String smpId;
    private String certificateId;
    private String physicalAddress;
    private String logicalAddress;

    public String getSmpId() {
        return smpId;
    }

    public void setSmpId(String smpId) {
        this.smpId = smpId;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getLogicalAddress() {
        return logicalAddress;
    }

    public void setLogicalAddress(String logicalAddress) {
        this.logicalAddress = logicalAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceMetadataPublisherBO that = (ServiceMetadataPublisherBO) o;

        if (smpId != null ? !smpId.equals(that.smpId) : that.smpId != null) return false;
        if (certificateId != null ? !certificateId.equals(that.certificateId) : that.certificateId != null)
            return false;
        if (physicalAddress != null ? !physicalAddress.equals(that.physicalAddress) : that.physicalAddress != null)
            return false;
        return !(logicalAddress != null ? !logicalAddress.equals(that.logicalAddress) : that.logicalAddress != null);

    }

    @Override
    public int hashCode() {
        int result = smpId != null ? smpId.hashCode() : 0;
        result = 31 * result + (certificateId != null ? certificateId.hashCode() : 0);
        result = 31 * result + (physicalAddress != null ? physicalAddress.hashCode() : 0);
        result = 31 * result + (logicalAddress != null ? logicalAddress.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServiceMetadataPublisherBO{" +
                "smpId='" + smpId + '\'' +
                ", certificateId='" + certificateId + '\'' +
                ", physicalAddress='" + physicalAddress + '\'' +
                ", logicalAddress='" + logicalAddress + '\'' +
                '}';
    }
}
