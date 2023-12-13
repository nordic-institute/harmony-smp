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

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.bdmsl.ws.soap.BadRequestFault;
import eu.europa.ec.bdmsl.ws.soap.InternalErrorFault;
import eu.europa.ec.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.bdmsl.ws.soap.UnauthorizedFault;
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SmlIntegrationConfiguration.class,
        SMLIntegrationService.class})
public class SMLIntegrationServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    IdentifierService identifierService;
    @Autowired
    SmlIntegrationConfiguration integrationMock;
    @Autowired
    protected SmlConnector smlConnector;
    @Autowired
    protected SMLIntegrationService testInstance;
    @Autowired
    ConfigurationService configurationService;

    @Before
    @Transactional
    public void prepareDatabase() {
        ReflectionTestUtils.setField(testInstance, "identifierService", identifierService);

        identifierService.configureParticipantIdentifierFormatter(null, false, Pattern.compile(".*"));

        configurationService = Mockito.spy(configurationService);
        smlConnector = Mockito.spy(smlConnector);
        Mockito.doNothing().when(smlConnector).configureClient(any(), any(), any());

        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
        ReflectionTestUtils.setField(smlConnector, "configurationService", configurationService);
        ReflectionTestUtils.setField(testInstance, "smlConnector", smlConnector);

        Mockito.doReturn(true).when(configurationService).isSMLIntegrationEnabled();

        integrationMock.reset();

        testUtilsDao.clearData();
        testUtilsDao.createResources();
    }

    @Test
    public void registerDomainToSml() throws UnauthorizedFault, InternalErrorFault, BadRequestFault {

        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(false);
        testUtilsDao.merge(testDomain01);

        // when
        testInstance.registerDomain(testDomain01);

        assertTrue(testDomain01.isSmlRegistered());
        assertEquals(1, integrationMock.getSmpManagerClientMocks().size());
        verify(integrationMock.getSmpManagerClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getSmpManagerClientMocks().toArray());

    }

    @Test
    public void unregisterDomainToSml() throws UnauthorizedFault, InternalErrorFault, BadRequestFault, NotFoundFault {

        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(true);

        // when
        testInstance.unRegisterDomain(testDomain01);

        assertFalse(testDomain01.isSmlRegistered());
        assertEquals(1, integrationMock.getSmpManagerClientMocks().size());
        verify(integrationMock.getSmpManagerClientMocks().get(0)).delete(testDomain01.getSmlSmpId());
        Mockito.verifyNoMoreInteractions(integrationMock.getSmpManagerClientMocks().toArray());

    }

    @Test
    public void registerParticipant() throws NotFoundFault, UnauthorizedFault, InternalErrorFault, BadRequestFault {
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(true);
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        resource.setSmlRegistered(false);
        // when
        testInstance.registerParticipant(resource, testDomain01);

        //then -- expect on call
        assertEquals(1, integrationMock.getParticipantManagmentClientMocks().size());
        verify(integrationMock.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getParticipantManagmentClientMocks().toArray());

    }
}
