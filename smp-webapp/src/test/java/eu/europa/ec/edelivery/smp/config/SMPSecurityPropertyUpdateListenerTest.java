package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.properties.SMPSecurityPropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.*;

public class SMPSecurityPropertyUpdateListenerTest {


    WSSecurityConfigurerAdapter wsSecurityConfigurerAdapter = Mockito.mock(WSSecurityConfigurerAdapter.class);
    ForwardedHeaderTransformer forwardedHeaderTransformer = Mockito.mock(ForwardedHeaderTransformer.class);
    SMPSecurityPropertyUpdateListener testInstance = new SMPSecurityPropertyUpdateListener(wsSecurityConfigurerAdapter, forwardedHeaderTransformer);

    @Test
    public void testPropertiesUpdateClientCertTrue() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, TRUE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(1)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(true);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(false);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(0)).setRemoveOnly(false);
    }

    @Test
    public void testPropertiesUpdateSSLClientCertTrue() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED, TRUE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(false);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(1)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(true);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(0)).setRemoveOnly(false);
    }

    @Test
    public void testPropertiesUpdateForwardedHeadersTrue() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(HTTP_FORWARDED_HEADERS_ENABLED, FALSE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(false);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(false);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(1)).setRemoveOnly(TRUE);
    }

    @Test
    public void testPropertiesUpdateFalse() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, FALSE);
        prop.put(EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED, FALSE);
        prop.put(HTTP_FORWARDED_HEADERS_ENABLED, FALSE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(1)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(false);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(1)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(false);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(1)).setRemoveOnly(true);
    }

    @Test
    public void testHandledProperties() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        List<SMPPropertyEnum> result = testInstance.handledProperties();
        assertEquals(3, result.size());
        assertTrue(result.contains(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED));
        assertTrue(result.contains(EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED));
        assertTrue(result.contains(HTTP_FORWARDED_HEADERS_ENABLED));
    }

    @Test
    public void testHandleProperty() {
        boolean resultTrue = testInstance.handlesProperty(HTTP_FORWARDED_HEADERS_ENABLED);
        assertTrue(resultTrue);
        boolean resultFalse = testInstance.handlesProperty(HTTP_PROXY_HOST);
        assertFalse(resultFalse);
    }
}
