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

import eu.europa.ec.bdmsl.ws.soap.*;
import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.verify;

/**
 * Created by JRC
 * since 4.1.
 */
@RunWith(SpringRunner.class)
public class SmlConnectorTestParticipant extends SmlConnectorTestBase {



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
    public void testRegisterInDnsAlreadyExists() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_PI_ALREADY_EXISTS);
        mockSml.setThrowException(ex);
        boolean result = smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testRegisterInDnsUnknownException() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);
        expectedExeption.expectMessage(message);
        expectedExeption.expect(SMPRuntimeException .class);

        smlConnector.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
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

    @Test
    public void testUnregisterFromDnsThrowUnknownBadRequestFault()  {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);
        mockSml.setThrowException(ex);
        expectedExeption.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedExeption.expect(SMPRuntimeException .class);

        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
    }

    @Test
    public void testUnregisterFromDnsThrowUnknownException()  {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);
        expectedExeption.expectMessage(message);
        expectedExeption.expect(SMPRuntimeException .class);

        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
    }

    @Test
    public void testUnregisterFromDnsNotExists()  {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_PI_NO_EXISTS);
        mockSml.setThrowException(ex);
        boolean  suc = smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        assertTrue(suc);
    }


    @Test
    public void testIsOkMessageForParticipantNull(){

        boolean suc = smlConnector.isOkMessage(PARTICIPANT_ID, null);

        assertFalse(suc);
    }

    @Test
    public void testIsOkMessageForParticipantOk(){
        boolean suc = smlConnector.isOkMessage(PARTICIPANT_ID, ERROR_PI_ALREADY_EXISTS);

        assertTrue(suc);
    }

    @Test
    public void testIsOkMessageForParticipantFalse(){
        boolean suc = smlConnector.isOkMessage(PARTICIPANT_ID, ERROR_UNEXPECTED_MESSAGE);

        assertFalse(suc);
    }


    @Test
    public void testProcessSMLErrorMessageBadRequestFaultIgnore(){

        BadRequestFault ex = new BadRequestFault(ERROR_PI_ALREADY_EXISTS);
        boolean suc = smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);

       assertTrue(suc);
    }

    @Test
    public void testProcessSMLErrorMessageBadRequestFaultFailed(){

        expectedExeption.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedExeption.expect(SMPRuntimeException .class);
        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);

        smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }


    @Test
    public void testProcessSMLErrorMessageNoFoundFaultFailed(){

        expectedExeption.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedExeption.expect(SMPRuntimeException .class);
        NotFoundFault ex = new NotFoundFault(ERROR_UNEXPECTED_MESSAGE);

        smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }

    @Test
    public void testProcessSMLErrorMessageNoFoundFaultOk(){

        NotFoundFault ex = new NotFoundFault(ERROR_PI_NO_EXISTS);

        smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }



}
