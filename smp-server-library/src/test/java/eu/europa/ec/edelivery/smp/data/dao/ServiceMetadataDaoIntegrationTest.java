package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class ServiceMetadataDaoIntegrationTest extends AbstractBaseDao {

    @Autowired
    ServiceMetadataDao testInstance;

    @Autowired
    DomainDao domainDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ServiceGroupDao serviceGroupDao;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    @Transactional
    public void prepareDatabase() {
        DBDomain testDomain01 =TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
        DBDomain testDomain02 =TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain01);
        domainDao.persistFlushDetach(testDomain02);

        DBUser u1 = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);
        DBUser u2 = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_2);
        userDao.persistFlushDetach(u1);
        userDao.persistFlushDetach(u2);

        // create service group with two documents in one domains
        DBServiceGroup sg1d1 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        DBServiceMetadata sg1md1 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        DBServiceMetadata sg1md2 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg1d1.addDomain(testDomain01);
        sg1d1.getServiceGroupDomains().get(0).addServiceMetadata(sg1md1);
        sg1d1.getServiceGroupDomains().get(0).addServiceMetadata(sg1md2);
        sg1d1.getUsers().add(u1);
        sg1d1.getUsers().add(u2);
        serviceGroupDao.update(sg1d1);
        // create service group one document in two domains
        DBServiceGroup sg2 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        DBServiceMetadata sg2md1 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        DBServiceMetadata sg2md2 = TestDBUtils.createDBServiceMetadata(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg2.addDomain(testDomain01);
        sg2.addDomain(testDomain02);
        sg2.getServiceGroupDomains().get(0).addServiceMetadata(sg2md1);
        sg2.getServiceGroupDomains().get(1).addServiceMetadata(sg2md2);
        sg2.getUsers().add(u1);
        sg2.getUsers().add(u2);
        serviceGroupDao.update(sg2);
    }

    @Test
    @Transactional
    public void testFindServiceMetadata() {
        // given
        // when
        Optional<DBServiceMetadata> osmd1 = testInstance.findServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        Optional<DBServiceMetadata> osmd2 = testInstance.findServiceMetadata(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);

        // test
        assertTrue(osmd1.isPresent());
        assertTrue(osmd2.isPresent());
    }

    @Test
    @Transactional
    public void testFindServiceMetadataList() {
        // given
        // when
        List<DBServiceMetadata> lst1 = testInstance.getAllMetadataForServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        List<DBServiceMetadata> lst2 = testInstance.getAllMetadataForServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        // test
        assertEquals(2, lst1.size());
        assertEquals(2, lst2.size());
    }


}