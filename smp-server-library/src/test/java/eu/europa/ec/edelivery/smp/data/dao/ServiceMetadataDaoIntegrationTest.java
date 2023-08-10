package eu.europa.ec.edelivery.smp.data.dao;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Ignore
public class ServiceMetadataDaoIntegrationTest extends AbstractBaseDao {

    @Autowired
    SubresourceDao testInstance;

    @Autowired
    DomainDao domainDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ResourceDao serviceGroupDao;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    @Test
    public void mockTest(){

    }
/*
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
        DBResource sg1d1 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        DBSubresource sg1md1 = TestDBUtils.createDBSubresource(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        DBSubresource sg1md2 = TestDBUtils.createDBSubresource(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);
        sg1d1.addDomain(testDomain01);
        sg1d1.getServiceGroupDomains().get(0).addServiceMetadata(sg1md1);
        sg1d1.getServiceGroupDomains().get(0).addServiceMetadata(sg1md2);
        sg1d1.getUsers().add(u1);
        sg1d1.getUsers().add(u2);
        serviceGroupDao.update(sg1d1);
        // create service group one document in two domains
        DBResource sg2 = TestDBUtils.createDBServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        DBSubresource sg2md1 = TestDBUtils.createDBSubresource(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        DBSubresource sg2md2 = TestDBUtils.createDBSubresource(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
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
        Optional<DBSubresource> osmd1 = testInstance.findServiceMetadata(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        Optional<DBSubresource> osmd2 = testInstance.findServiceMetadata(TEST_SG_ID_2, TEST_SG_SCHEMA_2,
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
        List<DBSubresource> lst1 = testInstance.getAllMetadataForServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        List<DBSubresource> lst2 = testInstance.getAllMetadataForServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        // test
        assertEquals(2, lst1.size());
        assertEquals(2, lst2.size());
    }*/

}
