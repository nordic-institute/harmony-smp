package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.smp.testutil.DBAssertion;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.TEST_SG_ID_2;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.TEST_SG_SCHEMA_2;

/**
 * Purpose of class is to setup integration test properties and init database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {H2JPATestConfiguration.class,PropertiesSingleDomainTestConfig.class,
        CaseSensitivityNormalizer.class,SmlConnector.class,ServiceMetadataSigner.class,
        ServiceGroupService.class,ServiceDomain.class, ServiceMetadataService.class,
        ServiceGroupDao.class,ServiceMetadataDao.class, DomainDao.class, UserDao.class,DBAssertion.class})
@Sql(scripts = "classpath:cleanup-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig
        (transactionMode = SqlConfig.TransactionMode.ISOLATED,
                transactionManager = "transactionManager",
                dataSource = "h2DataSource"))
public abstract class AbstractServiceIntegrationTest {

    @Autowired
    protected ServiceGroupDao serviceGroupDao;

    @Autowired
    protected ServiceMetadataDao serviceMetadataDao;

    @Autowired
    protected DomainDao domainDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    DBAssertion dbAssertion;

    /**
     * Domain: TEST_DOMAIN_CODE_1
     * Users: USERNAME_1, USER_CERT_2
     * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
     *    - Domain: TEST_DOMAIN_CODE_1
     *    - Owners: USERNAME_1, USER_CERT_2
     *    - Metadata:
     *          - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     *
     *
     * ServiceGroup2: TEST_SG_ID_1, TEST_SG_SCHEMA_2
     *    - Domain: TEST_DOMAIN_CODE_1
     *    - Owners: USERNAME_1
     *    - Metadata: /
     */
    public void prepareDatabaseForSignleDomainEnv() {
        DBDomain testDomain01 =TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
        domainDao.persistFlushDetach(testDomain01);

        DBUser u1 = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);
        DBUser u2 = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_2);
        userDao.persistFlushDetach(u1);
        userDao.persistFlushDetach(u2);

        DBServiceGroup sg1d1 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        DBServiceMetadata sg1md1 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        sg1d1.addDomain(testDomain01);
        sg1d1.getServiceGroupDomains().get(0).addServiceMetadata(sg1md1);
        sg1d1.getUsers().add(u1);
        sg1d1.getUsers().add(u2);
        serviceGroupDao.persistFlushDetach(sg1d1);

        DBServiceGroup sg2d1 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        sg2d1.getUsers().add(u1);
        sg2d1.addDomain(testDomain01);
        serviceGroupDao.update(sg2d1);
    }

    /**
     * Domain: TEST_DOMAIN_CODE_1,TEST_DOMAIN_CODE_2
     * Users: USERNAME_1, USER_CERT_2
     * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
     *    - Domain: TEST_DOMAIN_CODE_1
     *    - Owners: USERNAME_1, USER_CERT_2
     *      - Metadata: TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     *
     *
     * ServiceGroup2: TEST_SG_ID_1, TEST_SG_SCHEMA_2
     *    - Owners: USERNAME_1
     *    - Domain: TEST_DOMAIN_CODE_1
     *      - Metadata: /
     *
     * ServiceGroup3: TEST_SG_ID_3, TEST_SG_SCHEMA_3
     *    - Owners: USERNAME_1
     *    - Domain: TEST_DOMAIN_CODE_2
     *      - Metadata: /
     *
     */

    public void prepareDatabaseForMultipeDomainEnv() {
        prepareDatabaseForSignleDomainEnv();
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);

        DBUser u1 = userDao.findUserByUsername(USERNAME_1).get();

        DBServiceGroup sg2d2 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_3, TEST_SG_SCHEMA_1);
        sg2d2.getUsers().add(u1);
        serviceGroupDao.update(sg2d2);
    }

    /**
     * Domain: TEST_DOMAIN_CODE_1,TEST_DOMAIN_CODE_2
     * Users: USERNAME_1, USER_CERT_2
     * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
     *    - Owners: USERNAME_1, USER_CERT_2
     *    - Domain: TEST_DOMAIN_CODE_1
     *      - Metadata:
     *          - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     *          - TEST_DOC_ID_2, TEST_DOC_SCHEMA_2
     *
     *
     * ServiceGroup2: TEST_SG_ID_1, TEST_SG_SCHEMA_2
     *    - Domain: TEST_DOMAIN_CODE_1
     *    - Owners: USERNAME_1
     *    - Metadata: /
     *
     * ServiceGroup3: TEST_SG_ID_3, TEST_SG_SCHEMA_3
     *    - Owners: USERNAME_1
     *    - Domain: TEST_DOMAIN_CODE_2
     *      - Metadata:
     *         - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
     *    - Domain: TEST_DOMAIN_CODE_2
     *      - Metadata:
     *         - TEST_DOC_ID_2, TEST_DOC_SCHEMA_2
     *
     */

    public void prepareDatabaseForMultipeDomainWithMetadataEnv() {
        prepareDatabaseForMultipeDomainEnv();
        DBServiceGroup sg1 = serviceGroupDao.findServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1).get();
        DBServiceMetadata sg1md2 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg1.getServiceGroupDomains().get(0).addServiceMetadata(sg1md2);

        serviceGroupDao.update(sg1);

        DBServiceGroup sg3 = serviceGroupDao.findServiceGroup(TEST_SG_ID_3, TEST_SG_SCHEMA_1).get();
        DBServiceMetadata sg3md1 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        DBServiceMetadata sg3md2 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg3.getServiceGroupDomains().get(0).addServiceMetadata(sg3md1);
        sg3.getServiceGroupDomains().get(1).addServiceMetadata(sg3md2);
        serviceGroupDao.update(sg3);
    }

}
