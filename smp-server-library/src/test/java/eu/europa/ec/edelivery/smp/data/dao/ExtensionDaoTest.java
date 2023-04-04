package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
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
public class ExtensionDaoTest extends AbstractBaseDao {

    @Autowired
    ExtensionDao testInstance;


    @Test
    public void persistTest() {
        // set
        String testName = "TestClassName";
        DBExtension testData = TestDBUtils.createDBExtension(testName);
        // execute
        testInstance.persistFlushDetach(testData);

        // test
        List<DBExtension> res = testInstance.getAllExtensions();
        assertEquals(1, res.size());
        assertEquals(testData, res.get(0)); // test equal method
    }

    @Test
    public void persistDuplicate() {
        // set
        String testName = "TestClassName";
        DBExtension testData = TestDBUtils.createDBExtension(testName);
        testInstance.persistFlushDetach(testData);
        DBExtension testData2 = TestDBUtils.createDBExtension(testName);
        // execute
        PersistenceException result = assertThrows(PersistenceException.class, () -> testInstance.persistFlushDetach(testData2));
        assertEquals("org.hibernate.exception.ConstraintViolationException: could not execute statement", result.getMessage());
    }

    @Test
    public void getDomainByImplementationName() {
        String testName = "TestClassName";
        DBExtension testData = TestDBUtils.createDBExtension(testName);
        testInstance.persistFlushDetach(testData);
        // test
        Optional<DBExtension> res = testInstance.getExtensionByImplementationName(testName);
        assertTrue(res.isPresent());
        assertEquals(testName, res.get().getImplementationName());
    }

}
