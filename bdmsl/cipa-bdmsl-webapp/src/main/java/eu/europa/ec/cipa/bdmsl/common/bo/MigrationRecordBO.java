package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

/**
 * Created by feriaad on 12/06/2015.
 */
public class MigrationRecordBO extends AbstractBusinessObject {

    private String participantId;
    private String oldSmpId;
    private String newSmpId;
    private String scheme;
    private String migrationCode;
    private boolean migrated;

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

    public String getNewSmpId() {
        return newSmpId;
    }

    public void setNewSmpId(String newSmpId) {
        this.newSmpId = newSmpId;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getMigrationCode() {
        return migrationCode;
    }

    public void setMigrationCode(String migrationCode) {
        this.migrationCode = migrationCode;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MigrationRecordBO)) return false;

        MigrationRecordBO that = (MigrationRecordBO) o;

        if (migrated != that.migrated) return false;
        if (participantId != null ? !participantId.equals(that.participantId) : that.participantId != null)
            return false;
        if (oldSmpId != null ? !oldSmpId.equals(that.oldSmpId) : that.oldSmpId != null) return false;
        if (newSmpId != null ? !newSmpId.equals(that.newSmpId) : that.newSmpId != null) return false;
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return !(migrationCode != null ? !migrationCode.equals(that.migrationCode) : that.migrationCode != null);

    }

    @Override
    public int hashCode() {
        int result = participantId != null ? participantId.hashCode() : 0;
        result = 31 * result + (oldSmpId != null ? oldSmpId.hashCode() : 0);
        result = 31 * result + (newSmpId != null ? newSmpId.hashCode() : 0);
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (migrationCode != null ? migrationCode.hashCode() : 0);
        result = 31 * result + (migrated ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MigrationRecordBO{" +
                "participantId='" + participantId + '\'' +
                ", oldSmpId='" + oldSmpId + '\'' +
                ", newSmpId='" + newSmpId + '\'' +
                ", scheme='" + scheme + '\'' +
                ", migrationCode='" + migrationCode + '\'' +
                ", migrated=" + migrated +
                '}';
    }
}


