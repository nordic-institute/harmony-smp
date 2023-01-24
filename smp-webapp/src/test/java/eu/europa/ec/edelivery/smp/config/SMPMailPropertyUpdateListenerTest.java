package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.properties.SMPMailPropertyUpdateListener;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static org.junit.Assert.*;

public class SMPMailPropertyUpdateListenerTest {

    JavaMailSenderImpl javaMailSender = Mockito.mock(JavaMailSenderImpl.class);
    SMPMailPropertyUpdateListener testInstance = new SMPMailPropertyUpdateListener(Optional.of(javaMailSender));


    @Test
    public void testUpdatePropertiesHost() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_HOST, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setHost(testStringValue);
    }

    @Test
    public void testUpdatePropertiesPort() {
        Integer portValue = 1122;
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PORT, portValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setPort(portValue);
    }

    @Test
    public void testUpdatePropertiesProtocol() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PROTOCOL, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setProtocol(testStringValue);
    }

    @Test
    public void testUpdatePropertiesUsername() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_USERNAME, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setUsername(testStringValue);
    }

    @Test
    public void testUpdatePropertiesPassword() {
        String testStringValue = "TestValue";
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PASSWORD, testStringValue);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setPassword(testStringValue);
    }

    @Test
    public void testUpdatePropertiesProperties() {
        Map<String, String> properties = new HashMap();
        properties.put("testkey", "testValue");
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(MAIL_SERVER_PROPERTIES, properties);
        testInstance.updateProperties(prop);
        Mockito.verify(javaMailSender, Mockito.times(1)).setJavaMailProperties(ArgumentMatchers.any());
    }

    @Test
    public void testHandledProperties() {
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
    public void testHandleProperty() {
        boolean resultTrue = testInstance.handlesProperty(MAIL_SERVER_HOST);
        assertTrue(resultTrue);
        boolean resultFalse = testInstance.handlesProperty(HTTP_PROXY_HOST);
        assertFalse(resultFalse);
    }
}