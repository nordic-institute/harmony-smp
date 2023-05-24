package eu.europa.ec.edelivery.smp.config.init;

/**
 * The initial datasource
 */
public interface DatabaseConnectionProperties {

    String getDatabaseJNDI();

    String getJdbcUrl();

    String getJdbcDriver();

    String getJdbcUsername();

    String getJdbcPassword();

    String getDatabaseDialect();

    boolean updateDatabaseEnabled();

    boolean isShowSqlEnabled();
}
