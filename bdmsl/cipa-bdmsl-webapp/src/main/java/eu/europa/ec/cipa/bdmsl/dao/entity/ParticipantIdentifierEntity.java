package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_PARTICIPANT_IDENTIFIER")
@IdClass(ParticipantIdentifierEntityPK.class)
public class ParticipantIdentifierEntity extends AbstractEntity {
    @Id
    @Column(name = "participant_id")
    private String participantId;

    @Id
    @Column(name = "scheme")
    private String scheme;

    @ManyToOne
    @JoinColumn(name = "FK_SMP_ID")
    private SmpEntity smp;

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

    public SmpEntity getSmp() {
        return smp;
    }

    public void setSmp(SmpEntity smp) {
        this.smp = smp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantIdentifierEntity)) return false;
        if (!super.equals(o)) return false;

        ParticipantIdentifierEntity that = (ParticipantIdentifierEntity) o;

        if (participantId != null ? !participantId.equals(that.participantId) : that.participantId != null)
            return false;
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return !(smp != null ? !smp.equals(that.smp) : that.smp != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (participantId != null ? participantId.hashCode() : 0);
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (smp != null ? smp.hashCode() : 0);
        return result;
    }
}
