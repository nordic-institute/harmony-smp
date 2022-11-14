package eu.europa.ec.edelivery.smp.cron;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
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