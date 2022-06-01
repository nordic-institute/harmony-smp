package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

import static eu.europa.ec.edelivery.smp.config.DatabaseConfigTest.*;
import static eu.europa.ec.edelivery.smp.config.FileProperty.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PropertyInitializationTest {

    PropertyInitialization testInstance = new PropertyInitialization();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testValidateProperties() {
        // when
        Properties properties = new Properties();

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("jdbc/smpDatasource");

        DataSource dataSource = testInstance.getDatasource(properties);
    }

    @Test
    public void getDatasourceWithoutConfiguration() {
        // when
        Properties properties = new Properties();

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("jdbc/smpDatasource");

        DataSource dataSource = testInstance.getDatasource(properties);
    }

    @Test
    public void getDatasourceWithoutConfigurationWithJndi() {
        // when
        Properties properties = new Properties();
        properties.setProperty(FileProperty.PROPERTY_DB_JNDI, "jdbc/notExists");

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("jdbc/notExists");

        DataSource dataSource = testInstance.getDatasource(properties);
    }

    @Test
    public void getDatasourceBadConfigurationWithUrl() {
        // when
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_DB_URL, "schema:/no@exists/db");

        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Property 'driverClassName' must not be empty");

        DataSource dataSource = testInstance.getDatasource(properties);
    }


    @Test
    public void getDatasourceByUrl() {
        Properties properties =  getTestFileProperties();

        DataSource dataSource = testInstance.getDatasource(properties);

        assertNotNull(dataSource);
    }

    @Test
    public void createDBEntry() {
        // given
        DBConfiguration entry = testInstance.createDBEntry("key", "value", "desc");
        // then
        assertEquals("key", entry.getProperty());
        assertEquals("value", entry.getValue());
        assertEquals("desc", entry.getDescription());
    }


    @Test
    public void createDBEntryProperty() {
        // given
        DBConfiguration entry = testInstance.createDBEntry(SMPPropertyEnum.CS_DOCUMENTS, "value");
        // then
        assertEquals(SMPPropertyEnum.CS_DOCUMENTS.getProperty(), entry.getProperty());
        assertEquals("value", entry.getValue());
        assertEquals(SMPPropertyEnum.CS_DOCUMENTS.getDesc(), entry.getDescription());
    }

    @Test
    public void getDatabaseProperties(){
        Properties properties =  getTestFileProperties();

        Properties databaseProperties = testInstance.getDatabaseProperties(properties);

        assertNotNull(databaseProperties);

    }

    protected Properties getTestFileProperties(){
        // create test database with SMP_CONFIGURATION TABLE
        String url="jdbc:h2:mem:testdb;INIT=RUNSCRIPT FROM 'classpath:/create-configuration-table-h2.ddl'";
        Properties properties = new Properties();
        properties.setProperty(SMPPropertyEnum.CONFIGURATION_DIR.getProperty(), "./target/prop-init-test");
        properties.setProperty(PROPERTY_DB_URL, url);
        properties.setProperty(PROPERTY_DB_DIALECT, DATABASE_DIALECT);
        properties.setProperty(FileProperty.PROPERTY_DB_DRIVER, DATABASE_DRIVER);
        properties.setProperty(FileProperty.PROPERTY_DB_USER, DATABASE_USERNAME);
        properties.setProperty(FileProperty.PROPERTY_DB_TOKEN, "");
        properties.setProperty(FileProperty.PROPERTY_SMP_MODE_DEVELOPMENT, "true");
        return properties;
    }

}