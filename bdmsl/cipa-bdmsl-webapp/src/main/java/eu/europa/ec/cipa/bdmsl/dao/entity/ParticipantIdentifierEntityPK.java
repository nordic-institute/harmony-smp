package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by feriaad on 15/06/2015.
 */
public class ParticipantIdentifierEntityPK implements Serializable {

    @Id
    @Column(name = "participant_id")
    private String participantId;

    @Id
    @Column(name = "scheme")
    private String scheme;

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantIdentifierEntityPK that = (ParticipantIdentifierEntityPK) o;

        if (participantId != null ? !participantId.equals(that.participantId) : that.participantId != null)
            return false;
        return !(scheme != null ? !scheme.equals(that.scheme) : that.scheme != null);

    }

    @Override
    public int hashCode() {
        int result = participantId != null ? participantId.hashCode() : 0;
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        return result;
    }
}
