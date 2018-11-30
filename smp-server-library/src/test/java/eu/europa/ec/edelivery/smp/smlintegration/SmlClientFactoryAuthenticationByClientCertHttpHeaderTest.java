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
import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.sml.SmlClientFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.testutil.LocalPropertiesTestUtil.buildLocalProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SmlClientFactory.class,
        SecurityUtilsServices.class, UIKeystoreService.class,
        ConversionTestConfig.class, PropertiesSingleDomainTestConfig.class})
public class SmlClientFactoryAuthenticationByClientCertHttpHeaderTest {

    public static final String CLIENT_CERT_HTTP_HEADER = "value_of_ClientCert_HTTP_header";

    @Autowired
    private SmlClientFactory smlClientFactory;

    @Test
    public void factoryProducesPreconfiguredCxfClientThatAuthenticatesItselfWithGivenHttpHeader() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create(null, CLIENT_CERT_HTTP_HEADER, true);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        List clientCerts = (List) httpHeaders.get("Client-Cert");
        assertEquals(1, clientCerts.size());
        assertEquals(CLIENT_CERT_HTTP_HEADER, clientCerts.get(0));
        assertEquals("https://sml.url.pl/manageparticipantidentifier", requestContext.get(Message.ENDPOINT_ADDRESS));
    }

    @Test
    public void factoryProducesPreconfiguredCxfCSMPlientThatAuthenticatesItselfWithGivenHttpHeader() {
        //when
        IManageServiceMetadataWS client = smlClientFactory.createSmp(null, CLIENT_CERT_HTTP_HEADER, true);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        List clientCerts = (List) httpHeaders.get("Client-Cert");
        assertEquals(1, clientCerts.size());
        assertEquals(CLIENT_CERT_HTTP_HEADER, clientCerts.get(0));
        assertEquals("https://sml.url.pl/manageservicemetadata", requestContext.get(Message.ENDPOINT_ADDRESS));
    }

}
