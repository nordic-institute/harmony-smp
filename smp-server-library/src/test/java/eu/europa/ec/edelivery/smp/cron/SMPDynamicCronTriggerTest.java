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
package eu.europa.ec.edelivery.smp.cron;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronExpression;

import java.time.Clock;

import static org.junit.Assert.*;

public class SMPDynamicCronTriggerTest {

    @Test
    public void nextExecutionTime() {
        SMPPropertyEnum propertyEnum = SMPPropertyEnum.SMP_ALERT_CREDENTIALS_CRON;
        SMPDynamicCronTrigger testInstance = new SMPDynamicCronTrigger(propertyEnum.getDefValue(), propertyEnum);
        // not yet triggered
        assertNull(testInstance.getNextExecutionDate());

        TriggerContext triggerContext = Mockito.mock(TriggerContext.class);
        Mockito.doReturn(Clock.systemDefaultZone()).when(triggerContext).getClock();
        testInstance.nextExecutionTime(triggerContext);

        assertNotNull(testInstance.getNextExecutionDate());
    }

    @Test
    public void getExpression() {
        SMPPropertyEnum propertyEnum = SMPPropertyEnum.SMP_ALERT_CREDENTIALS_CRON;
        SMPDynamicCronTrigger testInstance = new SMPDynamicCronTrigger(propertyEnum.getDefValue(), propertyEnum);

        assertEquals(propertyEnum.getDefValue(), testInstance.getExpression());
    }

    @Test
    public void updateCronExpression() {
        String newCronExpression = "0 */10 * * * *";
        SMPPropertyEnum propertyEnum = SMPPropertyEnum.SMP_ALERT_CREDENTIALS_CRON;
        SMPDynamicCronTrigger testInstance = new SMPDynamicCronTrigger(propertyEnum.getDefValue(), propertyEnum);
        assertEquals(propertyEnum.getDefValue(), testInstance.getExpression());

        testInstance.updateCronExpression(CronExpression.parse(newCronExpression));

        assertEquals(newCronExpression, testInstance.getExpression());
        assertNotNull(testInstance.getNextExecutionDate());
    }

    @Test
    public void getCronExpressionProperty() {
        SMPPropertyEnum propertyEnum = SMPPropertyEnum.SMP_ALERT_CREDENTIALS_CRON;
        SMPDynamicCronTrigger testInstance = new SMPDynamicCronTrigger(propertyEnum.getDefValue(), propertyEnum);

        assertEquals(propertyEnum, testInstance.getCronExpressionProperty());
    }
}
