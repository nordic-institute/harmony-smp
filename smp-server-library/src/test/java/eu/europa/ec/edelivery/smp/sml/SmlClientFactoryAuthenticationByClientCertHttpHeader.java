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
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SmlClientFactory.class, SmlConnector.class})
public class SmlClientFactoryAuthenticationByClientCertHttpHeader extends AbstractServiceIntegrationTest {

    public static final String CLIENT_CERT_HTTP_HEADER = "value_of_ClientCert_HTTP_header";
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

    @Autowired
    private SmlClientFactory smlClientFactory;

    @Autowired
    private SmlConnector testInstance;


    @Before
    public void before() throws MalformedURLException {

        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
        Mockito.doReturn(new URL("http://sml.someUrl.local/edelivery-sml")).when(configurationService).getSMLIntegrationUrl();

    }

    @Test
    public void factoryProducesPreconfiguredCxfClientThatAuthenticatesItselfWithGivenCertAlias() {
        //given
        IManageParticipantIdentifierWS client = smlClientFactory.create();
        DBDomain domain = new DBDomain();
        domain.setSmlClientCertAuth(true);
        // when
        testInstance.configureClient("manageparticipantidentifier", client, domain);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        List clientCerts = (List) httpHeaders.get("Client-Cert");
        assertEquals(1, clientCerts.size());
        assertEquals(CLIENT_CERT_HTTP_HEADER, clientCerts.get(0));
        assertEquals("http://sml.someUrl.local/edelivery-sml/manageparticipantidentifier", requestContext.get(Message.ENDPOINT_ADDRESS));
    }


    @Test
    public void factoryProducesPreconfiguredCxfSMPClientThatAuthenticatesItselfWithGivenCertAlias() {

        //given
        IManageServiceMetadataWS client = smlClientFactory.createSmp();
        DBDomain domain = new DBDomain();
        domain.setSmlClientCertAuth(true);
        // when
        testInstance.configureClient("manageservicemetadata", client, domain);

        //then
        assertNotNull(client);
        Client cxfClient = ClientProxy.getClient(client);
        Map<String, Object> requestContext = cxfClient.getRequestContext();
        Map httpHeaders = (Map) requestContext.get(Message.PROTOCOL_HEADERS);
        List clientCerts = (List) httpHeaders.get("Client-Cert");
        assertEquals(1, clientCerts.size());
        assertEquals(CLIENT_CERT_HTTP_HEADER, clientCerts.get(0));
        assertEquals("http://sml.someUrl.local/edelivery-sml/manageservicemetadata", requestContext.get(Message.ENDPOINT_ADDRESS));
    }


    @Test
    public void factoryProducesSMPClientNoDefinedAlias() {

        //given
        IManageServiceMetadataWS client = smlClientFactory.createSmp();
        DBDomain domain = new DBDomain();
        domain.setSmlClientKeyAlias(null);
        domain.setSmlClientCertAuth(true);

        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("SML integration is wrongly configured, at least one authentication option is required: 2-way-SSL or Client-Cert header");
        // when
        testInstance.configureClient("changedEndpoint", client, domain);
    }
}
