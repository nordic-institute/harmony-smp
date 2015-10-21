package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by feriaad on 15/06/2015.
 */
public class MigrateEntityPK implements Serializable {
    @Id
    @Column(name = "scheme")
    private String scheme;

    @Id
    @Column(name = "participant_id")
    private String participantId;

    @Id
    @Column(name = "old_smp_id")
    private String oldSmpId;

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

    public String getOldSmpId() {
        return oldSmpId;
    }

    public void setOldSmpId(String oldSmpId) {
        this.oldSmpId = oldSmpId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MigrateEntityPK)) return false;

        MigrateEntityPK that = (MigrateEntityPK) o;

        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        if (participantId != null ? !participantId.equals(that.participantId) : that.participantId != null)
            return false;
        return !(oldSmpId != null ? !oldSmpId.equals(that.oldSmpId) : that.oldSmpId != null);

    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (participantId != null ? participantId.hashCode() : 0);
        result = 31 * result + (oldSmpId != null ? oldSmpId.hashCode() : 0);
        return result;
    }
}
