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

package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by gutowpa on 08/01/2018.
 */
@ContextConfiguration(classes = {SmlClientFactory.class, SmlConnector.class})
class SmlClientFactoryAuthenticationByClientCertHttpHeader extends AbstractServiceIntegrationTest {

    public static final String CLIENT_CERT_HTTP_HEADER = "value_of_ClientCert_HTTP_header";

    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

    @Autowired
    private SmlClientFactory smlClientFactory;

    @Autowired
    private SmlConnector testInstance;


    @BeforeEach
    public void before() throws MalformedURLException {

        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
        Mockito.doReturn(new URL("http://sml.someUrl.local/edelivery-sml")).when(configurationService).getSMLIntegrationUrl();

    }

    @Test
    void factoryProducesPreconfiguredCxfClientThatAuthenticatesItselfWithGivenCertAlias() {
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
    void factoryProducesPreconfiguredCxfSMPClientThatAuthenticatesItselfWithGivenCertAlias() {

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
    void factoryProducesSMPClientNoDefinedAlias() {

        //given
        IManageServiceMetadataWS client = smlClientFactory.createSmp();
        DBDomain domain = new DBDomain();
        domain.setSmlClientKeyAlias(null);
        domain.setSmlClientCertAuth(true);
        // when
        IllegalStateException result = assertThrows(IllegalStateException.class, () -> testInstance.configureClient("changedEndpoint", client, domain));

        MatcherAssert.assertThat(result.getMessage(),
                CoreMatchers.containsString("SML integration is wrongly configured, at least one authentication option is required"));
    }
}
