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
import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static eu.europa.ec.edelivery.smp.sml.SmlConnectorTestConstants.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by JRC
 * since 4.1.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SmlConnector.class, SmlIntegrationConfiguration.class})
public class SmlConnectorParticipantTest extends AbstractServiceIntegrationTest {


    @Autowired
    protected ConfigurationService configurationService;

    @Autowired
    protected SmlConnector testInstance;

    @Autowired
    SmlIntegrationConfiguration mockSml;

    @Before
    public void setup() {
        testInstance = Mockito.spy(testInstance);
        // default behaviour
        Mockito.doNothing().when(testInstance).configureClient(any(), any(), any());


        configurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
        Mockito.doReturn(true).when(configurationService).isSMLIntegrationEnabled();
        DEFAULT_DOMAIN.setSmlRegistered(true);
        mockSml.reset();
    }

    @Test
    public void testRegisterInDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        boolean result = testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);

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
        boolean result = testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testRegisterInDnsUnknownException() {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(message));
    }

    @Test
    public void testRegisterInDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);
        testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);

        //then
        assertEquals(2, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).create(any());
        verify(mockSml.getParticipantManagmentClientMocks().get(1)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testUnregisterFromDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        boolean result = testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getParticipantManagmentClientMocks().size());
        verify(mockSml.getParticipantManagmentClientMocks().get(0)).delete(any());
        Mockito.verifyNoMoreInteractions(mockSml.getParticipantManagmentClientMocks().toArray());
    }

    @Test
    public void testUnregisterFromDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
        testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

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

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(ERROR_UNEXPECTED_MESSAGE));

    }

    @Test
    public void testUnregisterFromDnsThrowUnknownException() {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(message));
    }

    @Test
    public void testUnregisterFromDnsNotExists() {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_PI_NO_EXISTS);
        mockSml.setThrowException(ex);
        boolean suc = testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        assertTrue(suc);
    }


    @Test
    public void testIsOkMessageForParticipantNull() {

        boolean suc = testInstance.isOkMessage(PARTICIPANT_ID, null);

        assertFalse(suc);
    }

    @Test
    public void testIsOkMessageForParticipantOk() {
        boolean suc = testInstance.isOkMessage(PARTICIPANT_ID, ERROR_PI_ALREADY_EXISTS);

        assertTrue(suc);
    }

    @Test
    public void testIsOkMessageForParticipantFalse() {
        boolean suc = testInstance.isOkMessage(PARTICIPANT_ID, ERROR_UNEXPECTED_MESSAGE);

        assertFalse(suc);
    }


    @Test
    public void testProcessSMLErrorMessageBadRequestFaultIgnore() {

        BadRequestFault ex = new BadRequestFault(ERROR_PI_ALREADY_EXISTS);
        boolean suc = testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID);

        assertTrue(suc);
    }

    @Test
    public void testProcessSMLErrorMessageBadRequestFaultFailed() {

        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(ERROR_UNEXPECTED_MESSAGE));
    }


    @Test
    public void testProcessSMLErrorMessageNoFoundFaultFailed() {

        NotFoundFault ex = new NotFoundFault(ERROR_UNEXPECTED_MESSAGE);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(ERROR_UNEXPECTED_MESSAGE));


    }

    @Test
    public void testProcessSMLErrorMessageNoFoundFaultOk() {

        NotFoundFault ex = new NotFoundFault(ERROR_PI_NO_EXISTS);

        testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID);
    }


}
