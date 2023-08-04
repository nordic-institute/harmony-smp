package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.config.SMPDatabaseConfig;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.services.AbstractServiceTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum.*;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SMPDatabaseConfig.class,
        AbstractServiceTest.DomiSMPServicesConfig.class}
)
@DirtiesContext
@Sql(scripts = {"classpath:cleanup-database.sql",
        "classpath:basic_conf_data-h2.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class AbstractJunit5BaseDao {

    @Autowired
    protected ConfigurationDao configurationDao;

    @Autowired
    protected TestUtilsDao testUtilsDao;

    public static final String BUILD_FOLDER = "target";
    public static final Path SECURITY_PATH = Paths.get(BUILD_FOLDER, "smp");
    public static final String DATABASE_URL = "jdbc:h2:file:./target/DomiSmpTestDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_SERVER=TRUE;";
    public static final String DATABASE_USERNAME = "smp";
    public static final String DATABASE_PASS = "smp";
    public static final String DATABASE_DRIVER = "org.h2.Driver";
    public static final String DATABASE_DIALECT = "org.hibernate.dialect.H2Dialect";


    static {
        System.setProperty(JDBC_DRIVER.getProperty(), DATABASE_DRIVER);
        System.setProperty(HIBERNATE_DIALECT.getProperty(), DATABASE_DIALECT);
        System.setProperty(JDBC_URL.getProperty(), DATABASE_URL);
        System.setProperty(JDBC_USER.getProperty(), DATABASE_USERNAME);
        System.setProperty(JDBC_PASSWORD.getProperty(), DATABASE_PASS);
        System.setProperty(SMP_MODE_DEVELOPMENT.getProperty(), "true");
        System.setProperty(DATABASE_SHOW_SQL.getProperty(), "true");
        System.setProperty(DATABASE_CREATE_DDL.getProperty(), "true");
        System.setProperty(SECURITY_FOLDER.getProperty(), SECURITY_PATH.toFile().getPath());

    }

    protected Path resourceDirectory = Paths.get("src", "test", "resources", "keystores");

    protected void resetKeystore() throws IOException {
        FileUtils.deleteDirectory(SECURITY_PATH.toFile());
        FileUtils.copyDirectory(resourceDirectory.toFile(), SECURITY_PATH.toFile());
    }


    public void setDatabaseProperty(SMPPropertyEnum prop, String value) {
        configurationDao.setPropertyToDatabase(prop, value, "Test property");
        configurationDao.reloadPropertiesFromDatabase();
    }

}
