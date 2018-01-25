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
import eu.europa.ec.edelivery.smp.sml.SmlClientFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SmlClientFactoryTest.class)
@Configuration
@ComponentScan("eu.europa.ec.edelivery.smp.sml")
@TestPropertySource(properties = {
        "bdmsl.integration.url=https://sml.url.pl"})
public class SmlClientFactoryTest {

    @Autowired
    private SmlClientFactory smlClientFactory;

    @Test
    public void factoryProducedPreconfiguredCxfClient() {
        //when
        IManageParticipantIdentifierWS client = smlClientFactory.create(null, "value_of_ClientCert_HTTP_header");

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        List clientCerts = (List) httpHeaders.get("Client-Cert");
        assertEquals(1, clientCerts.size());
        assertEquals("value_of_ClientCert_HTTP_header", clientCerts.get(0));
        assertEquals("https://sml.url.pl", requestContext.get(Message.ENDPOINT_ADDRESS));
    }
}
