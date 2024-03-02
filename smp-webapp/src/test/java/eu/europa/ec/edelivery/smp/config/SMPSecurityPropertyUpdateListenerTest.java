/*-
 * #START_LICENSE#
 * smp-webapp
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
import eu.europa.ec.edelivery.smp.config.properties.SMPSecurityPropertyUpdateListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

class SMPSecurityPropertyUpdateListenerTest {

    WSSecurityConfigurerAdapter wsSecurityConfigurerAdapter = Mockito.mock(WSSecurityConfigurerAdapter.class);
    ForwardedHeaderTransformer forwardedHeaderTransformer = Mockito.mock(ForwardedHeaderTransformer.class);
    SMPSecurityPropertyUpdateListener testInstance = new SMPSecurityPropertyUpdateListener(wsSecurityConfigurerAdapter, forwardedHeaderTransformer);

    @Test
    void testPropertiesUpdateClientCertTrue() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, TRUE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(1)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(true);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(false);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(0)).setRemoveOnly(false);
    }

    @Test
    void testPropertiesUpdateSSLClientCertTrue() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED, TRUE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(false);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(1)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(true);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(0)).setRemoveOnly(false);
    }

    @Test
    void testPropertiesUpdateForwardedHeadersTrue() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        prop.put(HTTP_FORWARDED_HEADERS_ENABLED, FALSE);
        testInstance.updateProperties(prop);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithClientCertHeaderEnabled(false);
        Mockito.verify(wsSecurityConfigurerAdapter, Mockito.times(0)).setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(false);
        Mockito.verify(forwardedHeaderTransformer, Mockito.times(1)).setRemoveOnly(TRUE);
    }

    @Test
    void testPropertiesUpdateFalse() {
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
    void testHandledProperties() {
        Map<SMPPropertyEnum, Object> prop = new HashMap();
        List<SMPPropertyEnum> result = testInstance.handledProperties();
        assertEquals(3, result.size());
        assertTrue(result.contains(EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED));
        assertTrue(result.contains(EXTERNAL_TLS_AUTHENTICATION_CERTIFICATE_HEADER_ENABLED));
        assertTrue(result.contains(HTTP_FORWARDED_HEADERS_ENABLED));
    }

    @Test
    void testHandleProperty() {
        boolean resultTrue = testInstance.handlesProperty(HTTP_FORWARDED_HEADERS_ENABLED);
        assertTrue(resultTrue);
        boolean resultFalse = testInstance.handlesProperty(HTTP_PROXY_HOST);
        assertFalse(resultFalse);
    }
}
