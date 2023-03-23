package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Ignore
public class AlertDaoIntegrationTest extends AbstractBaseDao {

    @Autowired
    AlertDao testInstance;

    @Test
    public void persistAlert() {
        // given
        long initCount = testInstance.getDataListCount(null);
        DBAlert entity = TestDBUtils.createDBAlert();
        assertNull(entity.getId());
        // when
        testInstance.persistFlushDetach(entity);
        //then
        assertNotNull(entity.getId());
        long newCount = testInstance.getDataListCount(null);
        assertEquals(initCount + 1, newCount);
    }
}
