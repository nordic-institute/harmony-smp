package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

/**
 * Created by feriaad on 12/06/2015.
 */
public class WildcardBO extends AbstractBusinessObject {

    private String scheme;
    private String certificateId;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WildcardBO)) return false;

        WildcardBO that = (WildcardBO) o;

        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return !(certificateId != null ? !certificateId.equals(that.certificateId) : that.certificateId != null);

    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (certificateId != null ? certificateId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WildcardBO{" +
                "scheme='" + scheme + '\'' +
                ", certificateId='" + certificateId + '\'' +
                '}';
    }
}
