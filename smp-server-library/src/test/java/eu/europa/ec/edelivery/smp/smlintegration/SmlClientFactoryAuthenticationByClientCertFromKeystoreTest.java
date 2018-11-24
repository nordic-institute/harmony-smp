/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.smlintegration;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.edelivery.smp.sml.SmlClientFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.testutil.LocalPropertiesTestUtil.buildLocalProperties;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class SmlClientFactoryAuthenticationByClientCertFromKeystoreTest {

    @Configuration
    @ComponentScan("eu.europa.ec.edelivery.smp.sml")
    static class Config {
        @Bean
        public PropertySourcesPlaceholderConfigurer setLocalProperties() {
            String clientCertificatesKeystorePath = Thread.currentThread().getContextClassLoader().getResource("sml_integration_client_certificates.jks").getFile();
            return buildLocalProperties(new String[][]{
                    {"bdmsl.integration.url", "https://sml.url.pl"},
                    {"bdmsl.integration.keystore.path", clientCertificatesKeystorePath},
                    {"bdmsl.integration.keystore.password", "test123"}
            });
        }
    }


    @Before
    public void setup(){
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Autowired
    private SmlClientFactory smlClientFactory;

    @Test
    public void factoryProducesPreconfiguredCxfClientThatAuthenticatesItselfWithGivenCertAlias() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create("second_domain_alias", null);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("CN=second domain common name, OU=eDelivery, O=European Commission, C=PL", clientCert.getSubjectDN().getName());
        assertEquals("https://sml.url.pl", requestContext.get(Message.ENDPOINT_ADDRESS));
    }

    @Test
    public void factoryProducesPreconfiguredCxfSMPClientThatAuthenticatesItselfWithGivenCertAlias() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp("second_domain_alias", null);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("CN=second domain common name, OU=eDelivery, O=European Commission, C=PL", clientCert.getSubjectDN().getName());
        assertEquals("https://sml.url.pl", requestContext.get(Message.ENDPOINT_ADDRESS));
    }

    @Test
    public void factoryProducesClientWithAnotherCertFromKeystore() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create("single_domain_key", null);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("CN=SMP Mock Services, OU=DIGIT, O=European Commision, C=BE", clientCert.getSubjectDN().getName());
    }

    @Test
    public void factoryProducesSMPClientWithAnotherCertFromKeystore() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp("single_domain_key", null);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("CN=SMP Mock Services, OU=DIGIT, O=European Commision, C=BE", clientCert.getSubjectDN().getName());
    }

    private static X509Certificate getClientCertFromKeystore(Client cxfClient) {
        HTTPConduit httpConduit = (HTTPConduit) cxfClient.getConduit();
        TLSClientParameters tlsParams = httpConduit.getTlsClientParameters();
        String alias = tlsParams.getCertAlias();
        KeyManager keyManager = tlsParams.getKeyManagers()[0];
        assertTrue(keyManager instanceof X509KeyManager);
        PrivateKey key = ((X509KeyManager) keyManager).getPrivateKey(alias);
        assertNotNull(key);
        return ((X509KeyManager) keyManager).getCertificateChain(alias)[0];
    }

    @Test
    public void factoryProducesPreconfiguredCxfClientWithoutAnyHttpHeaderValue() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create("second_domain_alias", null);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        assertTrue(httpHeaders == null || httpHeaders.isEmpty());
    }

    @Test
    public void factoryProducesPreconfiguredCxfSMPClientWithoutAnyHttpHeaderValue() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp("second_domain_alias", null);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        assertTrue(httpHeaders == null || httpHeaders.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void factoryDoesNotAcceptBothAuthentication() {
        smlClientFactory.create("any_domain_alias", "any_header_value");
    }

    @Test(expected = IllegalStateException.class)
    public void factoryDoesNotAcceptBothAuthenticationSmpClient() {
        smlClientFactory.createSmp("any_domain_alias", "any_header_value");
    }
}
