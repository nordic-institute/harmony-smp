package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class ResourceDaoTest extends AbstractBaseDao {
    @Autowired
    ResourceDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResources();
    }

    @Test
    @Transactional
    public void persistNewResourceWithDocument() {
        String testIdValue = "test-resource-id";
        String testIdSchema = "test-resource-scheme";
        DBResource testData = TestDBUtils.createDBResource(testIdValue, testIdSchema);
        testData.addGroup(testUtilsDao.getGroupD1G1());
        testData.setDomainResourceDef(testUtilsDao.getDomainResourceDefD1R1());

        DBDocument document  = TestDBUtils.createDBDocument();
        document.addNewDocumentVersion(TestDBUtils.createDBDocumentVersion());
        testData.setDocument(document);

        testInstance.persistFlushDetach(testData);

        Optional<DBResource> optResult =  testInstance.getResource(testIdValue,testIdSchema, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());

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
        Assert.assertEquals(docVersion+1, optResult.get().getDocument().getCurrentVersion());
        Assert.assertEquals(docCount+1, optResult.get().getDocument().getDocumentVersions().size());
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
}
