package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.ui.AlertRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;

@Ignore
@ContextConfiguration(classes = UIAlertService.class)
public class UIAlertServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected UIAlertService testInstance;

    @Autowired
    CredentialsAlertService alertService;

    protected void insertDataObjects(int size) {

        String username = "username-intg-test";
        TestDBUtils.createDBAlert(username);
        for (int i = 0; i < size; i++) {
            DBAlert alert = TestDBUtils.createDBAlert(username);
            alertDao.persistFlushDetach(alert);
        }
    }


    @Test
    public void getTableList() {
        ServiceResult<AlertRO> before = testInstance.getTableList(-1, -1, null, null, null);
        int newAddedValuesCount = 10;
        insertDataObjects(newAddedValuesCount);

        ServiceResult<AlertRO> result = testInstance.getTableList(-1, -1, null, null, null);

        assertEquals(before.getCount() + newAddedValuesCount, result.getCount().intValue());
    }


    @Test
    public void convertToRo() {
        DBAlert alert = TestDBUtils.createDBAlert("test");
        AlertRO alertRO = testInstance.convertToRo(alert);

        assertEquals(alert.getUsername(), alertRO.getUsername());
        assertEquals(alert.getAlertLevel(), alertRO.getAlertLevel());
        assertEquals(alert.getAlertStatus(), alertRO.getAlertStatus());
        assertEquals(alert.getAlertStatusDesc(), alertRO.getAlertStatusDesc());
        assertEquals(alert.getMailTo(), alertRO.getMailTo());
        assertEquals(alert.getProperties().size(), alertRO.getAlertDetails().size());
    }
}
