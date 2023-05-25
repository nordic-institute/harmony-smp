package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class ResourceDaoTest extends AbstractBaseDao {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDaoTest.class);
    @Autowired
    ResourceDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourceMemberships();
    }

    @Test
    @Transactional
    public void persistNewResourceWithDocument() {
        String testIdValue = "test-resource-id";
        String testIdSchema = "test-resource-scheme";
        DBResource testData = TestDBUtils.createDBResource(testIdValue, testIdSchema);
        testData.setGroup(testUtilsDao.getGroupD1G1());
        testData.setDomainResourceDef(testUtilsDao.getDomainResourceDefD1R1());

        DBDocument document = TestDBUtils.createDBDocument();
        document.addNewDocumentVersion(TestDBUtils.createDBDocumentVersion());
        testData.setDocument(document);

        testInstance.persistFlushDetach(testData);

        Optional<DBResource> optResult = testInstance.getResource(testIdValue, testIdSchema, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());

        Assert.assertTrue(optResult.isPresent());
        Assert.assertNotNull(optResult.get().getDocument());
        Assert.assertNotNull(optResult.get().getDocument().getId());
        Assert.assertEquals(0, optResult.get().getDocument().getCurrentVersion());
        Assert.assertEquals(1, optResult.get().getDocument().getDocumentVersions().size());
        Assert.assertNotNull(optResult.get().getDocument().getDocumentVersions().get(0).getId());
        Assert.assertEquals(0, optResult.get().getDocument().getDocumentVersions().get(0).getVersion());
    }

    @Test
    @Transactional
    public void persistNewVersionToResourceWithDocument() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        DBResource resource = testInstance.find(optResource.get().getId());

        int docCount = resource.getDocument().getDocumentVersions().size();
        int docVersion = resource.getDocument().getCurrentVersion();

        resource.getDocument().addNewDocumentVersion(TestDBUtils.createDBDocumentVersion());

        testInstance.persistFlushDetach(resource);
        testInstance.clearPersistenceContext();

        Optional<DBResource> optResult = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());

        Assert.assertTrue(optResult.isPresent());
        Assert.assertNotNull(optResult.get().getDocument());
        Assert.assertEquals(docVersion + 1, optResult.get().getDocument().getCurrentVersion());
        Assert.assertEquals(docCount + 1, optResult.get().getDocument().getDocumentVersions().size());
    }

    @Test
    public void getResourceOK() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        Assert.assertTrue(optResource.isPresent());
        Assert.assertEquals(testUtilsDao.getResourceD1G1RD1().getId(), optResource.get().getId());
    }

    @Test
    public void getResourceOKNullSchema() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_2, null, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD2());
        Assert.assertTrue(optResource.isPresent());
        Assert.assertEquals(testUtilsDao.getResourceD2G1RD1().getId(), optResource.get().getId());
    }

    @Test
    public void getResourceNotExists() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, "WrongSchema", testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        Assert.assertFalse(optResource.isPresent());
    }

    @Test
    public void getResourceWrongDomain() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD2());
        Assert.assertFalse(optResource.isPresent());
    }

    @Test
    public void getResourceWrongResourceDef() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefCpp(), testUtilsDao.getD1());
        Assert.assertFalse(optResource.isPresent());
    }

    @Test
    public void deleteResourceSimpleOK() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        Assert.assertTrue(optResource.isPresent());
        // then
        testInstance.remove(optResource.get());
        Optional<DBResource> optResult = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        Assert.assertFalse(optResult.isPresent());
    }

    @Test
    public void deleteResourceJoinTableOK() {
        testUtilsDao.createSubresources();
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        Assert.assertTrue(optResource.isPresent());
        // then
        testInstance.remove(optResource.get());
        Optional<DBResource> optResult = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        Assert.assertFalse(optResult.isPresent());
    }


    @Test
    public void getAllPublicResources() {
        List<DBResource> result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(null, null, null));
        //System.out.println(result.get(0));
        Assert.assertEquals(2, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().identifierFilter("test").build());
        Assert.assertEquals(2, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().identifierFilter("actorid").build());
        Assert.assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(0, 1, creatResourceFilter(null, null, null));
        Assert.assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(), null, null));
        Assert.assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1,creatResourceFilter( null, testUtilsDao.getD1(), null));
        Assert.assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(null, null, testUtilsDao.getResourceDefSmp()));
        Assert.assertEquals(2, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(), testUtilsDao.getD1(), testUtilsDao.getResourceDefSmp()));
        Assert.assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1,creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(), testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        Assert.assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1,creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        Assert.assertEquals(1, result.size());


        result = testInstance.getResourcesForFilter(-1, -1,creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser2(), MembershipRoleType.ADMIN));
        Assert.assertEquals(0, result.size());
    }


    /**
     * test filter. - TODO when moving to JUNIT5 parametrize this method!
     */
    @Test
    public void getAllResourcesCount() {

        Long result = testInstance.getResourcesForFilterCount(creatResourceFilter(null, null, null));
        Assert.assertEquals(2, result.intValue());
        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(), null, null));
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount( DBResourceFilter.createBuilder().identifierFilter("test").build());
        Assert.assertEquals(2, result.intValue());

        result = testInstance.getResourcesForFilterCount( DBResourceFilter.createBuilder().identifierFilter("actorid").build());
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(null, testUtilsDao.getD1(), null));
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(null, null, testUtilsDao.getResourceDefSmp()));
        Assert.assertEquals(2, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(), testUtilsDao.getD1(), testUtilsDao.getResourceDefSmp()));
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(), testUtilsDao.getD1(), testUtilsDao.getResourceDefSmp()));
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(), testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        Assert.assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        Assert.assertEquals(1, result.intValue());


        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser2(), MembershipRoleType.ADMIN));
        Assert.assertEquals(0, result.intValue());
    }

    protected static DBResourceFilter creatResourceFilter(DBGroup group, DBDomain domain, DBResourceDef resourceDef) {
            return creatResourceFilter(group, domain, resourceDef, null, null);
    }
    protected static DBResourceFilter creatResourceFilter(DBGroup group, DBDomain domain, DBResourceDef resourceDef, DBUser user, MembershipRoleType membershipRoleType) {
        return DBResourceFilter.createBuilder()
                .resourceDef(resourceDef)
                .domain(domain)
                .group(group)
                .user(user)
                .membershipRoleType(membershipRoleType)
                .build();
    }
}
