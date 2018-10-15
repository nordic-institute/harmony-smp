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

import eu.europa.ec.bdmsl.ws.soap.*;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmlConnectorTest.class)
@Configuration
@TestPropertySource(properties = {
        "bdmsl.integration.enabled=true"})
public class SmlConnectorTest {

    private static List<IManageParticipantIdentifierWS> smlClientMocks = new ArrayList<>();
    private static final ParticipantIdentifierType PARTICIPANT_ID = new ParticipantIdentifierType("sample:value", "sample:scheme");
//    private static final DBDomain DEFAULT_DOMAIN = new DBDomain("default_domain_id", null, null, "SAMPLE-SMP-ID", null);

    @Autowired
    private SmlConnector smlConnector;

    @Before
    public void setup() {
        smlClientMocks = new ArrayList<>();
    }

    @Bean
    public SmlConnector smlConnector() {
        return new SmlConnector();
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public IManageParticipantIdentifierWS smlClientMock(String clientKeyAlias, String clientCertHttpHeader) {
        IManageParticipantIdentifierWS clientMock = Mockito.mock(IManageParticipantIdentifierWS.class);
        smlClientMocks.add(clientMock);
        return clientMock;
    }

    @Test
    public void testRegisterInDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
  /*      //when
        smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertEquals(1, smlClientMocks.size());
        verify(smlClientMocks.get(0)).create(any());
        Mockito.verifyNoMoreInteractions(smlClientMocks.toArray());
        */
    }
/*
    @Test
    public void testRegisterInDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
        smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertEquals(2, smlClientMocks.size());
        verify(smlClientMocks.get(0)).create(any());
        verify(smlClientMocks.get(1)).create(any());
        Mockito.verifyNoMoreInteractions(smlClientMocks.toArray());
    }

    @Test
    public void testUnregisterFromDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertEquals(1, smlClientMocks.size());
        verify(smlClientMocks.get(0)).delete(any());
        Mockito.verifyNoMoreInteractions(smlClientMocks.toArray());
    }

    @Test
    public void testUnregisterFromDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertEquals(2, smlClientMocks.size());
        verify(smlClientMocks.get(0)).delete(any());
        verify(smlClientMocks.get(1)).delete(any());
        Mockito.verifyNoMoreInteractions(smlClientMocks.toArray());
    } */
}
