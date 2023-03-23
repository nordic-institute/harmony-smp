package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.ACCESS_TOKEN_FAIL_DELAY;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;
import static org.junit.Assert.*;

public class PropertyUpdateListenerTest {

    PropertyUpdateListener testInstance = Mockito.spy(new PropertyUpdateListener() {
        @Override
        public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        }

        @Override
        public List<SMPPropertyEnum> handledProperties() {
            return Collections.singletonList(ACCESS_TOKEN_FAIL_DELAY);
        }
    });

    @Test
    public void handlesProperty() {
        assertTrue(testInstance.handlesProperty(ACCESS_TOKEN_FAIL_DELAY));
        assertFalse(testInstance.handlesProperty(SMP_PROPERTY_REFRESH_CRON));
    }

    @Test
    public void updateProperty() {
        Mockito.doNothing().when(testInstance).updateProperties(Mockito.anyMap());
        SMPPropertyEnum property = ACCESS_TOKEN_FAIL_DELAY;
        String testValue = "test";

        testInstance.updateProperty(property, testValue);

        ArgumentCaptor<Map<SMPPropertyEnum, Object>> propertyCapture = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(testInstance, Mockito.times(1)).updateProperties(propertyCapture.capture());
        assertEquals(1, propertyCapture.getValue().size());
        assertTrue(propertyCapture.getValue().containsKey(ACCESS_TOKEN_FAIL_DELAY));
        assertEquals(testValue, propertyCapture.getValue().get(ACCESS_TOKEN_FAIL_DELAY));
    }
}
