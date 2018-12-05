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

package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesMultipleDomainTestConfig;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SmlClientFactory.class,
        SecurityUtilsServices.class, UIKeystoreService.class,
        ConversionTestConfig.class, PropertiesMultipleDomainTestConfig.class})
public class SmlClientFactoryAuthenticationByClientCertFromKeystoreTest {
/*
    @Configuration
    @ComponentScan({"eu.europa.ec.edelivery.smp.sml","eu.europa.ec.edelivery.smp.services",  "eu.europa.ec.edelivery.smp.services.ui"})
    static class Config {
        Path resourceDirectory = Paths.get("src", "test", "resources",  "keystores", "service_integration_signatures_multiple_domains.jks");
        @Bean
        public PropertySourcesPlaceholderConfigurer setLocalProperties() {
            Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
            return buildLocalProperties(new String[][]{
                    {"bdmsl.integration.url", "https://sml.url.pl"},
                    {"bdmsl.integration.keystore.path", resourceDirectory.toFile().getAbsolutePath()},
                    {"bdmsl.integration.keystore.password", "test123"}
            });
        }
    }

*/


    @Autowired
    private SmlClientFactory smlClientFactory;

    @Test
    public void factoryProducesPreconfiguredCxfClientThatAuthenticatesItselfWithGivenCertAlias() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create("second_domain_alias", null, false);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("C=BE,O=CEF Digital,OU=SMP,CN=Secodn domain", clientCert.getSubjectDN().getName());
        assertEquals("http://localhost:8080/manageparticipantidentifier/manageparticipantidentifier", requestContext.get(Message.ENDPOINT_ADDRESS));
    }



    @Test
    public void factoryProducesPreconfiguredCxfSMPClientThatAuthenticatesItselfWithGivenCertAlias() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp("second_domain_alias", null, false);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("C=BE,O=CEF Digital,OU=SMP,CN=Secodn domain", clientCert.getSubjectDN().getName());
        assertEquals("http://localhost:8080/manageparticipantidentifier/manageservicemetadata", requestContext.get(Message.ENDPOINT_ADDRESS));
    }

    @Test
    public void factoryProducesClientWithAnotherCertFromKeystore() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create("single_domain_key", null, false);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("C=BE,O=European Commision,OU=DIGIT,CN=SMP Mock Services", clientCert.getSubjectDN().getName());
    }

    @Test
    public void factoryProducesSMPClientWithAnotherCertFromKeystore() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp("single_domain_key", null, false);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        X509Certificate clientCert = getClientCertFromKeystore(cxfClient);

        assertEquals("C=BE,O=European Commision,OU=DIGIT,CN=SMP Mock Services", clientCert.getSubjectDN().getName());
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
        IManageParticipantIdentifierWS client = smlClientFactory.create("second_domain_alias", null, false);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        assertTrue(httpHeaders == null || httpHeaders.isEmpty());
    }

    @Test
    public void factoryProducesPreconfiguredCxfSMPClientWithoutAnyHttpHeaderValue() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp("second_domain_alias", null, false);

        //then
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        assertTrue(httpHeaders == null || httpHeaders.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void configureClientAuthenticationDoesNotAcceptBothAuthentication() {
        smlClientFactory.configureClientAuthentication(null, null, "any_domain_alias", "any_header_value");
    }

}
