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
package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.ACCESS_TOKEN_FAIL_DELAY;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;
import static org.junit.jupiter.api.Assertions.*;

class PropertyUpdateListenerTest {

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
    void handlesProperty() {
        assertTrue(testInstance.handlesProperty(ACCESS_TOKEN_FAIL_DELAY));
        assertFalse(testInstance.handlesProperty(SMP_PROPERTY_REFRESH_CRON));
    }

    @Test
    void updateProperty() {
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
