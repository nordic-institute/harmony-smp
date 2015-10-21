package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

/**
 * Created by feriaad on 12/06/2015.
 */
public class ParticipantBO extends AbstractBusinessObject {

    private String participantId;
    private String smpId;
    private String scheme;
    private String type = "Meta:SMP";

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getSmpId() {
        return smpId;
    }

    public void setSmpId(String smpId) {
        this.smpId = smpId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantBO)) return false;

        ParticipantBO that = (ParticipantBO) o;

        if (participantId != null ? !participantId.equals(that.participantId) : that.participantId != null)
            return false;
        if (smpId != null ? !smpId.equals(that.smpId) : that.smpId != null) return false;
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = participantId != null ? participantId.hashCode() : 0;
        result = 31 * result + (smpId != null ? smpId.hashCode() : 0);
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParticipantBO{" +
                "participantId='" + participantId + '\'' +
                ", smpId='" + smpId + '\'' +
                ", scheme='" + scheme + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
