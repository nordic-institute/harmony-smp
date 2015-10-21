package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_MIGRATE")
@IdClass(MigrateEntityPK.class)
public class MigrateEntity extends AbstractEntity {
    @Id
    @Column(name = "scheme")
    private String scheme;

    @Id
    @Column(name = "participant_id")
    private String participantId;

    @Id
    @Column(name = "migration_key")
    private String migrationKey;

    @Id
    @Column(name = "old_smp_id")
    private String oldSmpId;

    @Column(name = "new_smp_id")
    private String newSmpId;

    @Column(name = "migrated")
    private boolean migrated;

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

    public String getMigrationKey() {
        return migrationKey;
    }

    public void setMigrationKey(String migrationKey) {
        this.migrationKey = migrationKey;
    }

    public String getOldSmpId() {
        return oldSmpId;
    }

    public void setOldSmpId(String oldSmpId) {
        this.oldSmpId = oldSmpId;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public String getNewSmpId() {
        return newSmpId;
    }

    public void setNewSmpId(String newSmpId) {
        this.newSmpId = newSmpId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MigrateEntity)) return false;
        if (!super.equals(o)) return false;

        MigrateEntity that = (MigrateEntity) o;

        if (migrated != that.migrated) return false;
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        if (participantId != null ? !participantId.equals(that.participantId) : that.participantId != null)
            return false;
        if (migrationKey != null ? !migrationKey.equals(that.migrationKey) : that.migrationKey != null) return false;
        if (oldSmpId != null ? !oldSmpId.equals(that.oldSmpId) : that.oldSmpId != null) return false;
        return !(newSmpId != null ? !newSmpId.equals(that.newSmpId) : that.newSmpId != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (participantId != null ? participantId.hashCode() : 0);
        result = 31 * result + (migrationKey != null ? migrationKey.hashCode() : 0);
        result = 31 * result + (oldSmpId != null ? oldSmpId.hashCode() : 0);
        result = 31 * result + (newSmpId != null ? newSmpId.hashCode() : 0);
        result = 31 * result + (migrated ? 1 : 0);
        return result;
    }
}
