package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ResourceDefDaoTest extends AbstractBaseDao {

    @Autowired
    ResourceDefDao testInstance;

    @Before
    public void init() {
        testUtilsDao.clearData();
        testUtilsDao.createExtension();
        testInstance.clearPersistenceContext();
    }

    @Test
    public void persistTest() {
        // set
        String testName = "TestClassName";
        DBResourceDef testData = TestDBUtils.createDBResourceDef(testName);
        testData.setExtension(testUtilsDao.getExtension());
        // execute
        testInstance.persistFlushDetach(testData);
        // test
        List<DBResourceDef> res = testInstance.getAllResourceDef();
        assertEquals(1, res.size());
        assertEquals(testData.getId(), res.get(0).getId()); // test equal method
    }


    @Test
    public void persistDuplicateUrlError() {
        // set
        String testURL = "TestClassName";
        DBResourceDef testData1 = TestDBUtils.createDBResourceDef("resourceDef1");
        DBResourceDef testData2 = TestDBUtils.createDBResourceDef("resourceDef2");
        testData1.setExtension(testUtilsDao.getExtension());
        testData2.setExtension(testUtilsDao.getExtension());

        testData1.setUrlSegment(testURL);
        testData2.setUrlSegment(testURL);
        testInstance.persistFlushDetach(testData1);

        // execute
        PersistenceException result = assertThrows(PersistenceException.class, () -> testInstance.persistFlushDetach(testData2));
        assertEquals("org.hibernate.exception.ConstraintViolationException: could not execute statement", result.getMessage());
    }

    @Test
    public void getExtensionByImplementationName() {
        String testName = "TestClassName";
        DBResourceDef testData = TestDBUtils.createDBResourceDef(testName);
        testData.setExtension(testUtilsDao.getExtension());
        testInstance.persistFlushDetach(testData);

        // test
        Optional<DBResourceDef> res = testInstance.getResourceDefByIdentifierAndExtension(testName, testUtilsDao.getExtension());
        assertTrue(res.isPresent());
        assertEquals(testName, res.get().getIdentifier());
    }

    @Test
    public void getResourceDefByURLSegment() {
        String testUrlSegment = "testUrlSegment";
        DBResourceDef testData = TestDBUtils.createDBResourceDef("Code");
        testData.setUrlSegment(testUrlSegment);
        testData.setExtension(testUtilsDao.getExtension());
        testInstance.persistFlushDetach(testData);
        // test
        Optional<DBResourceDef> res = testInstance.getResourceDefByURLSegment(testUrlSegment);
        assertTrue(res.isPresent());
        assertEquals(testUrlSegment, res.get().getUrlSegment());
    }
}
