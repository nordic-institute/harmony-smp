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
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
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
        SmlConnector.class, DomainService.class})
public class DomainServiceSMLTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    SmlIntegrationConfiguration integrationMock;

    @Autowired
    SmlConnector smlConnector;

    @Autowired
    private SMLIntegrationService smlIntegrationService;


    @Autowired
    protected DomainService testInstance;

    @Before
    public void prepareDatabase() throws IOException {

        smlConnector = Mockito.spy(smlConnector);
        Mockito.doNothing().when(smlConnector).configureClient(any(), any(), any());

        ReflectionTestUtils.setField(smlIntegrationService,"smlConnector",smlConnector);
        ReflectionTestUtils.setField(testInstance,"smlIntegrationService",smlIntegrationService);

        resetKeystore();
        setDatabaseProperty(SMPPropertyEnum.SML_PHYSICAL_ADDRESS, "0.0.0.0");
        setDatabaseProperty(SMPPropertyEnum.SML_LOGICAL_ADDRESS, "http://localhost/smp");
        setDatabaseProperty(SMPPropertyEnum.SML_URL, "http://localhost/edelivery-sml");
        setDatabaseProperty(SMPPropertyEnum.SML_ENABLED, "true");

        integrationMock.reset();
        prepareDatabaseForSingleDomainEnv(false);

    }

    @Test
    public void getDomainForBlankCodeForSingleDomain() {

        // given
        assertEquals(1, domainDao.getAllDomains().size());

        //Only one domain is in database - get domain should return the one.
        DBDomain dmn = testInstance.getDomain(null);
        assertEquals(TEST_DOMAIN_CODE_1, dmn.getDomainCode());
        dmn = testInstance.getDomain("");
        assertEquals(TEST_DOMAIN_CODE_1, dmn.getDomainCode());
        dmn = testInstance.getDomain(" ");
        assertEquals(TEST_DOMAIN_CODE_1, dmn.getDomainCode());
    }

    @Test
    public void getDomainForBlankCodeForMultipleDomain() {
        // given
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);
        assertEquals(2, domainDao.getAllDomains().size());
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.MISSING_DOMAIN.getMessage());

        // when-then
        //Multiple domains in database - get domain should return the SMPRuntimeException.
        testInstance.getDomain(null);
    }


    @Test
    public void registerDomainAndParticipantsOK() throws NotFoundFault, UnauthorizedFault, InternalErrorFault, BadRequestFault {
        /* given (init database - check setup)
         * Domain: TEST_DOMAIN_CODE_1
         * Users: USERNAME_1, USER_CERT_2
         * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
         *    - Domain: TEST_DOMAIN_CODE_1
         *    - Owners: USERNAME_1, USER_CERT_2
         *    - Metadata:
         *          - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
         *
         *
         * ServiceGroup2: TEST_SG_ID_2, TEST_SG_SCHEMA_2
         *    - Domain: TEST_DOMAIN_CODE_1
         *    - Owners: USERNAME_1
         *    - Metadata: /
         */
        DBDomain testDomain01 = domainDao.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1).get();
        DBServiceGroupDomain serviceGroupDomain = serviceGroupDao.findServiceGroupDomain(
                TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1).get();
        DBServiceGroupDomain serviceGroupDomain2 = serviceGroupDao
                .findServiceGroupDomain(TEST_SG_ID_2, TEST_SG_SCHEMA_2, TEST_DOMAIN_CODE_1).get();
        assertFalse(testDomain01.isSmlRegistered());
        assertFalse(serviceGroupDomain.isSmlRegistered());
        assertFalse(serviceGroupDomain2.isSmlRegistered());

        // when
        testInstance.registerDomainAndParticipants(testDomain01);

        // then
        serviceGroupDomain = serviceGroupDao.findServiceGroupDomain(
                TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1).get();
        serviceGroupDomain2 = serviceGroupDao
                .findServiceGroupDomain(TEST_SG_ID_2, TEST_SG_SCHEMA_2, TEST_DOMAIN_CODE_1).get();
        assertTrue(testDomain01.isSmlRegistered());
        assertTrue(serviceGroupDomain.isSmlRegistered());
        assertTrue(serviceGroupDomain2.isSmlRegistered());

        // one sml domain create and two participant create was called
        assertEquals(1, integrationMock.getSmpManagerClientMocks().size());
        verify(integrationMock.getSmpManagerClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getSmpManagerClientMocks().toArray());

        assertEquals(2, integrationMock.getParticipantManagmentClientMocks().size());
        verify(integrationMock.getParticipantManagmentClientMocks().get(0)).create(any());
        verify(integrationMock.getParticipantManagmentClientMocks().get(1)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getParticipantManagmentClientMocks().toArray());

    }

    @Test
    public void registerDomainAndParticipantsFailed() throws NotFoundFault, UnauthorizedFault, InternalErrorFault, BadRequestFault {

        DBDomain testDomain01 = domainDao.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1).get();
        DBServiceGroupDomain serviceGroupDomain = serviceGroupDao.findServiceGroupDomain(
                TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1).get();
        DBServiceGroupDomain serviceGroupDomain2 = serviceGroupDao
                .findServiceGroupDomain(TEST_SG_ID_2, TEST_SG_SCHEMA_2, TEST_DOMAIN_CODE_1).get();

        assertFalse(testDomain01.isSmlRegistered());
        assertFalse(serviceGroupDomain.isSmlRegistered());
        assertFalse(serviceGroupDomain2.isSmlRegistered());
        integrationMock.setThrowExceptionAfterParticipantCallCount(1);


        // when
        try {
            testInstance.registerDomainAndParticipants(testDomain01);
            fail("Testcase should throw an error with code 400");
        } catch (Exception ex) {
            ex.printStackTrace();
            assertEquals(400, ((HTTPException) ExceptionUtils.getRootCause(ex)).getStatusCode());
       }


        // then
        serviceGroupDomain = serviceGroupDao.findServiceGroupDomain(
                TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1).get();
        serviceGroupDomain2 = serviceGroupDao
                .findServiceGroupDomain(TEST_SG_ID_2, TEST_SG_SCHEMA_2, TEST_DOMAIN_CODE_1).get();
        assertTrue(testDomain01.isSmlRegistered());
        assertTrue(serviceGroupDomain.isSmlRegistered());
        assertFalse(serviceGroupDomain2.isSmlRegistered());

        // one sml domain create and two participant create was called
        assertEquals(1, integrationMock.getSmpManagerClientMocks().size());
        verify(integrationMock.getSmpManagerClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getSmpManagerClientMocks().toArray());

        // only first succeeded
        assertEquals(1, integrationMock.getParticipantManagmentClientMocks().size());
        verify(integrationMock.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getParticipantManagmentClientMocks().toArray());

    }

    @Test
    @Transactional
    public void unregisterDomainAndParticipantsFromSmlOK() throws NotFoundFault, UnauthorizedFault, InternalErrorFault, BadRequestFault {
        /* given (init database - check setup)
         * Domain: TEST_DOMAIN_CODE_1
         * Users: USERNAME_1, USER_CERT_2
         * ServiceGroup1: TEST_SG_ID_1, TEST_SG_SCHEMA_1
         *    - Domain: TEST_DOMAIN_CODE_1
         *    - Owners: USERNAME_1, USER_CERT_2
         *    - Metadata:
         *          - TEST_DOC_ID_1, TEST_DOC_SCHEMA_1
         *
         *
         * ServiceGroup2: TEST_SG_ID_2, TEST_SG_SCHEMA_2
         *    - Domain: TEST_DOMAIN_CODE_1
         *    - Owners: USERNAME_1
         *    - Metadata: /
         */
        DBDomain testDomain01 = domainDao.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1).get();
        DBServiceGroupDomain serviceGroupDomain = serviceGroupDao.findServiceGroupDomain(
                TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1).get();
        DBServiceGroupDomain serviceGroupDomain2 = serviceGroupDao
                .findServiceGroupDomain(TEST_SG_ID_2, TEST_SG_SCHEMA_2, TEST_DOMAIN_CODE_1).get();
        testDomain01.setSmlRegistered(true);
        serviceGroupDomain.setSmlRegistered(true);
        serviceGroupDomain2.setSmlRegistered(true);
        serviceGroupDao.updateServiceGroupDomain(serviceGroupDomain);
        serviceGroupDao.updateServiceGroupDomain(serviceGroupDomain2);

        // when
        testInstance.unregisterDomainAndParticipantsFromSml(testDomain01);

        // then
        serviceGroupDomain = serviceGroupDao.findServiceGroupDomain(
                TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1).get();
        serviceGroupDomain2 = serviceGroupDao
                .findServiceGroupDomain(TEST_SG_ID_2, TEST_SG_SCHEMA_2, TEST_DOMAIN_CODE_1).get();
        assertFalse(testDomain01.isSmlRegistered());
        assertFalse(serviceGroupDomain.isSmlRegistered());
        assertFalse(serviceGroupDomain2.isSmlRegistered());

        // one sml domain create and two participant create was called
        assertEquals(1, integrationMock.getSmpManagerClientMocks().size());
        verify(integrationMock.getSmpManagerClientMocks().get(0)).delete(testDomain01.getSmlSmpId());
        Mockito.verifyNoMoreInteractions(integrationMock.getSmpManagerClientMocks().toArray());

        assertEquals(2, integrationMock.getParticipantManagmentClientMocks().size());
        verify(integrationMock.getParticipantManagmentClientMocks().get(0)).delete(any());
        verify(integrationMock.getParticipantManagmentClientMocks().get(1)).delete(any());
        Mockito.verifyNoMoreInteractions(integrationMock.getParticipantManagmentClientMocks().toArray());

    }

}
