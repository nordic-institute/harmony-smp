package eu.europa.ec.cipa.sml.server.dns;

/**
 * Created by feriaad on 20/10/2015.
 */
public class MigrationRecord {
    private String recValue;

    private String migrationCode;

    private String scheme;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getRecValue() {
        return recValue;
    }

    public void setRecValue(String recValue) {
        this.recValue = recValue;
    }

    public String getMigrationCode() {
        return migrationCode;
    }

    public void setMigrationCode(String migrationCode) {
        this.migrationCode = migrationCode;
    }
}
