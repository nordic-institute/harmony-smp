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

import eu.europa.ec.bdmsl.ws.soap.BadRequestFault;
import eu.europa.ec.bdmsl.ws.soap.InternalErrorFault;
import eu.europa.ec.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.bdmsl.ws.soap.UnauthorizedFault;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by JRC
 * since 4.1.
 */
@RunWith(SpringRunner.class)
public class SmlConnectorTestParticipant extends SmlConnectorTestBase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
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
        expectedException.expectMessage(message);
        expectedException.expect(SMPRuntimeException.class);

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
    public void testUnregisterFromDnsThrowUnknownBadRequestFault() {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);
        mockSml.setThrowException(ex);
        expectedException.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedException.expect(SMPRuntimeException.class);

        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
    }

    @Test
    public void testUnregisterFromDnsThrowUnknownException() {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);
        expectedException.expectMessage(message);
        expectedException.expect(SMPRuntimeException.class);

        smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
    }

    @Test
    public void testUnregisterFromDnsNotExists() {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_PI_NO_EXISTS);
        mockSml.setThrowException(ex);
        boolean suc = smlConnector.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        assertTrue(suc);
    }


    @Test
    public void testIsOkMessageForParticipantNull() {

        boolean suc = smlConnector.isOkMessage(PARTICIPANT_ID, null);

        assertFalse(suc);
    }

    @Test
    public void testIsOkMessageForParticipantOk() {
        boolean suc = smlConnector.isOkMessage(PARTICIPANT_ID, ERROR_PI_ALREADY_EXISTS);

        assertTrue(suc);
    }

    @Test
    public void testIsOkMessageForParticipantFalse() {
        boolean suc = smlConnector.isOkMessage(PARTICIPANT_ID, ERROR_UNEXPECTED_MESSAGE);

        assertFalse(suc);
    }


    @Test
    public void testProcessSMLErrorMessageBadRequestFaultIgnore() {

        BadRequestFault ex = new BadRequestFault(ERROR_PI_ALREADY_EXISTS);
        boolean suc = smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);

        assertTrue(suc);
    }

    @Test
    public void testProcessSMLErrorMessageBadRequestFaultFailed() {

        expectedException.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedException.expect(SMPRuntimeException.class);
        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);

        smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }


    @Test
    public void testProcessSMLErrorMessageNoFoundFaultFailed() {

        expectedException.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedException.expect(SMPRuntimeException.class);
        NotFoundFault ex = new NotFoundFault(ERROR_UNEXPECTED_MESSAGE);

        smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }

    @Test
    public void testProcessSMLErrorMessageNoFoundFaultOk() {

        NotFoundFault ex = new NotFoundFault(ERROR_PI_NO_EXISTS);

        smlConnector.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }


}
