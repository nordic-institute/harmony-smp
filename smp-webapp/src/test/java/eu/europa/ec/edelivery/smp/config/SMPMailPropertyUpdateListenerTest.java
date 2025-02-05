/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
import eu.europa.ec.edelivery.smp.config.properties.SMPMailPropertyUpdateListener;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static org.junit.jupiter.api.Assertions.*;

class SMPMailPropertyUpdateListenerTest {

    JavaMailSenderImpl javaMailSender = Mockito.mock(JavaMailSenderImpl.class);
    SMPMailPropertyUpdateListener testInstance = new SMPMailPropertyUpdateListener(Optional.of(javaMailSender));


    @Test
    void testUpdatePropertiesHost() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_HOST, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setHost(testStringValue);
    }

    @Test
    void testUpdatePropertiesPort() {
        Integer portValue = 1122;
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PORT, portValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setPort(portValue);
    }

    @Test
    void testUpdatePropertiesProtocol() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PROTOCOL, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setProtocol(testStringValue);
    }

    @Test
    void testUpdatePropertiesUsername() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_USERNAME, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setUsername(testStringValue);
    }

    @Test
    void testUpdatePropertiesPassword() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PASSWORD, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setPassword(testStringValue);
    }

    @Test
    void testUpdatePropertiesProperties() {
        Map<String, String> properties = new HashMap();
        properties.put("testkey", "testValue");
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PROPERTIES, properties);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setJavaMailProperties(ArgumentMatchers.any());
    }

    @Test
    void testHandledProperties() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        List<SMPPropertyEnum> result = testInstance.handledProperties();
        assertEquals(6, result.size());
        assertTrue(result.contains(MAIL_SERVER_HOST));
        assertTrue(result.contains(MAIL_SERVER_PORT));
        assertTrue(result.contains(MAIL_SERVER_PROTOCOL));
        assertTrue(result.contains(MAIL_SERVER_USERNAME));
        assertTrue(result.contains(MAIL_SERVER_PASSWORD));
        assertTrue(result.contains(MAIL_SERVER_PROPERTIES));
    }

    @Test
    void testHandleProperty() {
        boolean resultTrue = testInstance.handlesProperty(MAIL_SERVER_HOST);
        assertTrue(resultTrue);
        boolean resultFalse = testInstance.handlesProperty(HTTP_PROXY_HOST);
        assertFalse(resultFalse);
    }
}
