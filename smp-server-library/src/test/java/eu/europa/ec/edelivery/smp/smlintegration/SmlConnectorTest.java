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
import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.sml.SmlClientFactory;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by gutowpa on 08/01/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SmlConnector.class,SmlIntegrationConfiguration.class,
        SecurityUtilsServices.class, UIKeystoreService.class,
        ConversionTestConfig.class, PropertiesSingleDomainTestConfig.class})
@Configuration
@TestPropertySource(properties = {
        "bdmsl.integration.enabled=true"})
public class SmlConnectorTest {

   // private static List<IManageParticipantIdentifierWS> smlClientMocks = new ArrayList<>();
    private static final ParticipantIdentifierType PARTICIPANT_ID = new ParticipantIdentifierType("sample:value", "sample:scheme");
    private static final DBDomain DEFAULT_DOMAIN;

    static {
        DEFAULT_DOMAIN = new DBDomain();
        DEFAULT_DOMAIN.setDomainCode("default_domain_id");
        DEFAULT_DOMAIN.setSmlSmpId("SAMPLE-SMP-ID");
    }

    @Autowired
    SmlIntegrationConfiguration mockSml;

    @Autowired
    private SmlConnector smlConnector;

    @Autowired
    public void setup(){
        mockSml.reset();
    }

    @Test
    public void testRegisterInDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        boolean result = smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testRegisterInDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
        smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertEquals(2, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).create(any());
        verify(mockSml.getParticipantManagmentClientMocks().get(1)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testUnregisterFromDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        boolean result = smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).delete(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testUnregisterFromDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertEquals(2, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).delete(any());
        verify(mockSml.getParticipantManagmentClientMocks().get(1)).delete(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }
}
