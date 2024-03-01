/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.ui.AlertRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = UIAlertService.class)
class UIAlertServiceIntegrationTest extends AbstractServiceIntegrationTest {

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
    void getTableList() {
        ServiceResult<AlertRO> before = testInstance.getTableList(-1, -1, null, null, null);
        int newAddedValuesCount = 10;
        insertDataObjects(newAddedValuesCount);

        ServiceResult<AlertRO> result = testInstance.getTableList(-1, -1, null, null, null);

        assertEquals(before.getCount() + newAddedValuesCount, result.getCount().intValue());
    }


    @Test
    void convertToRo() {
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
