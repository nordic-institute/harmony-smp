package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.ServicesBeansConfiguration;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.cron.CronTriggerConfig;
import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.services.mail.MailService;
import eu.europa.ec.edelivery.smp.services.spi.SmpXmlSignatureService;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.smp.testutil.DBAssertion;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

/**
 * Purpose of class is to setup integration test properties and init database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IdentifierService.class, SmlConnector.class, SmpXmlSignatureService.class, MailService.class,
        DomainService.class,
        ResourceDao.class, SubresourceDao.class, DomainDao.class, UserDao.class, DBAssertion.class, ConfigurationDao.class, AlertDao.class,
        UITruststoreService.class, UIKeystoreService.class, ConversionTestConfig.class, SMLIntegrationService.class,
        CRLVerifierService.class,
        ConfigurationService.class,
        ServicesBeansConfiguration.class,
        CredentialsAlertService.class,
        CronTriggerConfig.class})
@Sql(scripts = {"classpath:cleanup-database.sql",
        "classpath:basic_conf_data-h2.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class AbstractServiceIntegrationTest extends AbstractBaseDao {


    protected Path resourceDirectory = Paths.get("src", "test", "resources", "keystores");
    protected Path targetDirectory = Paths.get("target", "keystores");

    @Autowired
    protected ResourceDao serviceGroupDao;

    @Autowired
    protected SubresourceDao serviceMetadataDao;

    @Autowired
    protected DomainDao domainDao;

    @Autowired
    protected ConfigurationDao configurationDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected CredentialDao credentialDao;

    @Autowired
    protected AlertDao alertDao;

    @Autowired
    DBAssertion dbAssertion;


    @Before
    public void before() throws IOException {
        resetKeystore();
    }

    /**
     * Domain: TEST_DOMAIN_CODE_1
     * Users: USERNAME_1, USER_CERT_2
     * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
     * - Domain: TEST_DOMAIN_CODE_1
     * - Owners: USERNAME_1, USER_CERT_2
     * - Metadata:
     * - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     * <p>
     * <p>
     * ServiceGroup2: TEST_SG_ID_2, TEST_SG_SCHEMA_2
     * - Domain: TEST_DOMAIN_CODE_1
     * - Owners: USERNAME_1
     * - Metadata: /
     */
    public void prepareDatabaseForSingleDomainEnv() {
        prepareDatabaseForSingleDomainEnv(true);
    }

    public void prepareDatabaseForSingleDomainEnv(boolean domainSMLRegister) {
        DBDomain testDomain01 = TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
        testDomain01.setSmlRegistered(domainSMLRegister);
        domainDao.persistFlushDetach(testDomain01);

        DBUser u1 = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);
        DBCredential c1 = TestDBUtils.createDBCredentialForUser(u1, null, null, null);
        c1.setValue(BCrypt.hashpw(USERNAME_1_PASSWORD, BCrypt.gensalt()));
        DBUser u2 = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_2);
        DBUser u3 = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_2);
        userDao.persistFlushDetach(u1);
        userDao.persistFlushDetach(u2);
        userDao.persistFlushDetach(u3);
        credentialDao.persistFlushDetach(c1);

        DBResource sg1d1 = TestDBUtils.createDBResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        DBSubresource sg1md1 = TestDBUtils.createDBSubresource(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);

        /*

        sg1d1.addDomain(testDomain01);
        sg1d1.getResourceDomains().get(0).addServiceMetadata(sg1md1);
        sg1d1.getMembers().add(new DBResourceMember(sg1d1, u1));
        sg1d1.getMembers().add(new DBResourceMember(sg1d1, u1));
        serviceGroupDao.persistFlushDetach(sg1d1);

        DBResource sg2d1 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        sg2d1.getMembers().add(new DBResourceMember(sg2d1, u1));
        sg2d1.addDomain(testDomain01);
        serviceGroupDao.update(sg2d1);


        DBResource sg2NoScheme = TestDBUtils.createDBServiceGroup(TEST_SG_ID_NO_SCHEME, null);
        DBSubresource sg1mdNoScheme = TestDBUtils.createDBSubresource(TEST_SG_ID_NO_SCHEME, null,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        sg2NoScheme.addDomain(testDomain01);
        sg2NoScheme.getResourceDomains().get(0).addServiceMetadata(sg1mdNoScheme);
        sg2NoScheme.getMembers().add(new DBResourceMember(sg2NoScheme, u1));
        sg2NoScheme.getMembers().add(new DBResourceMember(sg2NoScheme, u1));
        serviceGroupDao.persistFlushDetach(sg2NoScheme);

         */
    }

    /**
     * Domain: TEST_DOMAIN_CODE_1,TEST_DOMAIN_CODE_2
     * Users: USERNAME_1, USER_CERT_2
     * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
     * - Domain: TEST_DOMAIN_CODE_1
     * - Owners: USERNAME_1, USER_CERT_2
     * - Metadata: TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     * <p>
     * <p>
     * ServiceGroup2: TEST_SG_ID_1, TEST_SG_SCHEMA_2
     * - Owners: USERNAME_1
     * - Domain: TEST_DOMAIN_CODE_1
     * - Metadata: /
     * <p>
     * ServiceGroup3: TEST_SG_ID_3, TEST_SG_SCHEMA_3
     * - Owners: USERNAME_1
     * - Domain: TEST_DOMAIN_CODE_2
     * - Metadata: /
     */
    public void prepareDatabaseForMultipeDomainEnv() {

        prepareDatabaseForSingleDomainEnv();
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);

        DBUser u1 = userDao.findUserByUsername(USERNAME_1).get();

        DBResource sg2d2 = TestDBUtils.createDBResource(TEST_SG_ID_3, TEST_SG_SCHEMA_1);
        // sg2d2.getUsers().add(u1);
        serviceGroupDao.update(sg2d2);
    }


    public void setDatabaseProperty(SMPPropertyEnum prop, String value) {
        configurationDao.setPropertyToDatabase(prop, value, "Test property");
        configurationDao.reloadPropertiesFromDatabase();
    }

    /**
     * Domain: TEST_DOMAIN_CODE_1,TEST_DOMAIN_CODE_2
     * Users: USERNAME_1, USER_CERT_2
     * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
     * - Owners: USERNAME_1, USER_CERT_2
     * - Domain: TEST_DOMAIN_CODE_1
     * - Metadata:
     * - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     * - TEST_DOC_ID_2, TEST_DOC_SCHEMA_2
     * <p>
     * <p>
     * ServiceGroup2: TEST_SG_ID_1, TEST_SG_SCHEMA_2
     * - Domain: TEST_DOMAIN_CODE_1
     * - Owners: USERNAME_1
     * - Metadata: /
     * <p>
     * ServiceGroup3: TEST_SG_ID_3, TEST_SG_SCHEMA_3
     * - Owners: USERNAME_1
     * - Domain: TEST_DOMAIN_CODE_2
     * - Metadata:
     * - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     * - Domain: TEST_DOMAIN_CODE_2
     * - Metadata:
     * - TEST_DOC_ID_2, TEST_DOC_SCHEMA_2
     */

    public void prepareDatabaseForMultipeDomainWithMetadataEnv() {
        prepareDatabaseForMultipeDomainEnv();
        /*
        DBResource sg1 = serviceGroupDao.findServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1).get();
        DBSubresource sg1md2 = TestDBUtils.createDBSubresource(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg1.getResourceDomains().get(0).addServiceMetadata(sg1md2);

        serviceGroupDao.update(sg1);

        DBResource sg3 = serviceGroupDao.findServiceGroup(TEST_SG_ID_3, TEST_SG_SCHEMA_1).get();
        DBSubresource sg3md1 = TestDBUtils.createDBSubresource(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        DBSubresource sg3md2 = TestDBUtils.createDBSubresource(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg3.getResourceDomains().get(0).addServiceMetadata(sg3md1);
        sg3.getResourceDomains().get(1).addServiceMetadata(sg3md2);
        serviceGroupDao.update(sg3);

         */
    }

    protected void resetKeystore() throws IOException {
        FileUtils.deleteDirectory(targetDirectory.toFile());
        FileUtils.copyDirectory(resourceDirectory.toFile(), targetDirectory.toFile());
    }
}
