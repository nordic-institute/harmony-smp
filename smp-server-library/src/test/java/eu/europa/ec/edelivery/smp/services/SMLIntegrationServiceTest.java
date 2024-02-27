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

import eu.europa.ec.bdmsl.ws.soap.*;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMLIntegrationServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    IdentifierService identifierService;
    @MockBean
    private IManageServiceMetadataWS iManageServiceMetadataWS;
    @MockBean
    private IManageParticipantIdentifierWS iManageParticipantIdentifierWS;
    @SpyBean
    protected SmlConnector smlConnector;
    @Autowired
    protected SMLIntegrationService testInstance;
    @SpyBean
    ConfigurationService configurationService;

    @Before
    @Transactional
    public void prepareDatabase() {
        identifierService.configureParticipantIdentifierFormatter(null, false, Pattern.compile(".*"));

        ReflectionTestUtils.setField(smlConnector, "configurationService", configurationService);
        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
        ReflectionTestUtils.setField(testInstance, "smlConnector", smlConnector);
        ReflectionTestUtils.setField(testInstance, "identifierService", identifierService);

        Mockito.reset(smlConnector);
        Mockito.doNothing().when(smlConnector).configureClient(any(), any(), any());

        testUtilsDao.clearData();
        testUtilsDao.createResources();
    }

    @Test
    public void registerDomainToSml() throws UnauthorizedFault, InternalErrorFault, BadRequestFault {
        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(false);
        testUtilsDao.merge(testDomain01);
        givenSmlIntegrationEnabled(true);

        // when
        testInstance.registerDomain(testDomain01);

        assertTrue(testDomain01.isSmlRegistered());
        verify(iManageServiceMetadataWS, times(1)).create(any(ServiceMetadataPublisherServiceType.class));
    }

    @Test
    public void unregisterDomainToSml() throws UnauthorizedFault, InternalErrorFault, BadRequestFault, NotFoundFault {
        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(true);
        givenSmlIntegrationEnabled(true);

        // when
        testInstance.unRegisterDomain(testDomain01);

        assertFalse(testDomain01.isSmlRegistered());
        verify(iManageServiceMetadataWS, times(1)).delete(testDomain01.getSmlSmpId());

    }

    @Test
    public void registerParticipant() throws Exception {
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(true);
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        resource.setSmlRegistered(false);
        givenSmlIntegrationEnabled(true);

        // when
        testInstance.registerParticipant(resource, testDomain01);

        //then
        verify(iManageParticipantIdentifierWS, times(1)).create(any(ServiceMetadataPublisherServiceForParticipantType.class));
    }

    @Test
    public void participantExists() {
        // given
        DBDomain domain = testUtilsDao.getD1();
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        givenSmlIntegrationEnabled(true);

        Identifier identifier = identifierService.normalizeParticipant(resource.getIdentifierScheme(), resource.getIdentifierValue());
        Mockito.doReturn(true).when(smlConnector).participantExists(identifier, domain);

        // when
        boolean participantExists = testInstance.participantExists(resource, domain);

        // then
        Assert.assertTrue(participantExists);
    }

    @Test
    public void registerOnlyDomainToSml_smlIntegrationDisabled() {
        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(false);

        givenSmlIntegrationEnabled(false);

        // when
        SMPRuntimeException result = Assert.assertThrows(SMPRuntimeException.class, () -> testInstance.registerDomain(testDomain01));
        Assert.assertEquals("Configuration error: [SML integration is not enabled!]!", result.getMessage());
    }

    @Test
    public void unregisterOnlyDomainToSml_smlIntegrationDisabled() {
        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(true);

        givenSmlIntegrationEnabled(false);

        // when
        SMPRuntimeException result = Assert.assertThrows(SMPRuntimeException.class, () -> testInstance.unRegisterDomain(testDomain01));
        Assert.assertEquals("Configuration error: [SML integration is not enabled!]!", result.getMessage());
    }

    @Test
    public void registerParticipant_smlIntegrationDisabled() {
        DBDomain testDomain01 = testUtilsDao.getD1();
        DBResource resource = testUtilsDao.getResourceD1G1RD1();

        givenSmlIntegrationEnabled(false);

        // nothing is expected to be thrown
        testInstance.registerParticipant(resource, testDomain01);
    }

    @Test
    public void unregisterParticipant_smlIntegrationDisabled() {
        DBDomain testDomain01 = testUtilsDao.getD1();
        DBResource resource = testUtilsDao.getResourceD1G1RD1();

        givenSmlIntegrationEnabled(false);

        // nothing is expected to be thrown
        testInstance.unregisterParticipant(resource, testDomain01);
    }

    @Test
    public void participantExists_smlIntegrationDisabled() {
        DBDomain domain = testUtilsDao.getD1();
        DBResource resource = testUtilsDao.getResourceD1G1RD1();

        givenSmlIntegrationEnabled(false);

        SMPRuntimeException result = Assert.assertThrows(SMPRuntimeException.class, () -> testInstance.participantExists(resource, domain));
        Assert.assertEquals("Configuration error: [SML integration is not enabled!]!", result.getMessage());
    }

    @Test
    public void isDomainValid_smlIntegrationDisabled() {
        DBDomain domain = testUtilsDao.getD1();

        givenSmlIntegrationEnabled(false);

        SMPRuntimeException result = Assert.assertThrows(SMPRuntimeException.class, () -> testInstance.isDomainValid(domain));
        Assert.assertEquals("Configuration error: [SML integration is not enabled!]!", result.getMessage());
    }

    private void givenSmlIntegrationEnabled(boolean enabled) {
        Mockito.doReturn(enabled).when(configurationService).isSMLIntegrationEnabled();
    }
}
