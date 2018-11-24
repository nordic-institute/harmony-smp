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

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.bdmsl.ws.soap.BadRequestFault;
import eu.europa.ec.bdmsl.ws.soap.InternalErrorFault;
import eu.europa.ec.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.bdmsl.ws.soap.UnauthorizedFault;
import eu.europa.ec.edelivery.smp.config.H2JPATestConfig;
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {SmlIntegrationConfiguration.class,
        SmlConnector.class,SMLIntegrationService.class,
        H2JPATestConfig.class} )
@TestPropertySource(properties = {"bdmsl.integration.enabled=true"})
public class SMLIntegrationServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    SmlIntegrationConfiguration integrationMock;


    @Autowired
    protected SMLIntegrationService testInstance;

    @Before
    @Transactional
    public void prepareDatabase() {
        integrationMock.reset();
        prepareDatabaseForSignleDomainEnv();
    }

    @Test
    public void registerDomainToSml() throws UnauthorizedFault, InternalErrorFault, BadRequestFault {

         // given
        DBDomain testDomain01 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        assertFalse(testDomain01.isSmlRegistered());

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
        DBDomain testDomain01 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        testDomain01.setSmlRegistered(true);


        // when
        testInstance.unRegisterDomain(testDomain01);

        assertTrue(!testDomain01.isSmlRegistered());
        assertEquals(1, integrationMock.getSmpManagerClientMocks().size());
        verify(integrationMock.getSmpManagerClientMocks().get(0)).delete(testDomain01.getSmlSmpId());
        Mockito.verifyNoMoreInteractions(integrationMock.getSmpManagerClientMocks().toArray());

    }

    @Test
    public void registerParticipantToSML() throws NotFoundFault, UnauthorizedFault, InternalErrorFault, BadRequestFault {
        /* given (init database - check setup)
         * Domain: TEST_DOMAIN_CODE_1
         * Users: USERNAME_1, USER_CERT_2
         * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
         *    - Domain: TEST_DOMAIN_CODE_1
        */
        // when
        testInstance.registerParticipant(TEST_SG_ID_1,TEST_SG_SCHEMA_1,TEST_DOMAIN_CODE_1 );

        //then -- expect on call
        assertEquals(1, integrationMock.getParticipantManagmentClientMocks().size());
        verify(integrationMock.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getParticipantManagmentClientMocks().toArray());

    }

    @Test
    public void unRegisterParticipantFromSML() throws NotFoundFault, UnauthorizedFault, InternalErrorFault, BadRequestFault {
        /* given (init database - check setup)
         * Domain: TEST_DOMAIN_CODE_1
         * Users: USERNAME_1, USER_CERT_2
         * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
         *    - Domain: TEST_DOMAIN_CODE_1
         */
        // when
        testInstance.registerParticipant(TEST_SG_ID_1,TEST_SG_SCHEMA_1,TEST_DOMAIN_CODE_1 );

        //then -- expect on call
        assertEquals(1, integrationMock.getParticipantManagmentClientMocks().size());
        verify(integrationMock.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getParticipantManagmentClientMocks().toArray());

    }

    @Test
    public void registerParticipantToSML_NotExists(){
        expectedExeption.expect(SMPRuntimeException.class);
        String notExistsId = TEST_SG_ID_1+"NotExists";
        expectedExeption.expectMessage("ServiceGroup not found (part. id: '"+TEST_SG_ID_1+"NotExists', part. sch.: '"+TEST_SG_SCHEMA_1+"')!");

        // when
        testInstance.registerParticipant(notExistsId,TEST_SG_SCHEMA_1,TEST_DOMAIN_CODE_1 );
    }

    @Test
    public void registerParticipantToSML_NotOnDomain(){
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage("Service group not registered for domain (domain: "+TEST_DOMAIN_CODE_2+", part. id: '"+TEST_SG_ID_1+"', part. sch.: '"+TEST_SG_SCHEMA_1+"')!");

        // when
        testInstance.registerParticipant(TEST_SG_ID_1,TEST_SG_SCHEMA_1,TEST_DOMAIN_CODE_2 );
    }



}
