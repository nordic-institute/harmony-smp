/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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

import ec.services.wsdl.bdmsl.data._1.ExistsParticipantResponseType;
import ec.services.wsdl.bdmsl.data._1.ParticipantsType;
import eu.europa.ec.bdmsl.ws.soap.*;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static eu.europa.ec.edelivery.smp.sml.SmlConnectorTestConstants.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by JRC
 * since 4.1.
 */
class SmlConnectorParticipantTest extends AbstractServiceIntegrationTest {

    // Beans
    @SpyBean
    private ConfigurationService configurationService;
    @MockBean
    private IBDMSLServiceWS ibdmslServiceWS;
    @MockBean
    private IManageParticipantIdentifierWS iManageParticipantIdentifierWS;
    @SpyBean
    private SmlConnector testInstance;

    // Mocks
    @Mock
    private DBDomain domain;
    @Mock
    private Identifier identifier;

    @BeforeEach
    public void setup() {
        // default behaviour
        Mockito.doNothing().when(testInstance).configureClient(any(), any(), any());
        Mockito.doReturn(true).when(configurationService).isSMLIntegrationEnabled();

        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
        DEFAULT_DOMAIN.setSmlRegistered(true);
    }

    @Test
    void testRegisterInDns() throws Exception {
        //when
        boolean result = testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);

        //then
        assertTrue(result);
        verify(iManageParticipantIdentifierWS, times(1)).create(any(ServiceMetadataPublisherServiceForParticipantType.class));
        Mockito.verifyNoMoreInteractions(iManageParticipantIdentifierWS);
    }

    @Test
    void testRegisterInDnsAlreadyExists() throws Exception {
        //given
        Mockito.doThrow(new BadRequestFault(ERROR_PI_ALREADY_EXISTS)).when(iManageParticipantIdentifierWS).create(any(ServiceMetadataPublisherServiceForParticipantType.class));

        //when
        boolean result = testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);

        //then
        assertTrue(result);
        verify(iManageParticipantIdentifierWS, times(1)).create(any(ServiceMetadataPublisherServiceForParticipantType.class));
        Mockito.verifyNoMoreInteractions(iManageParticipantIdentifierWS);
    }

    @Test
    void testRegisterInDnsUnknownException() throws Exception {
        //given
        String message = "something unexpected";
        Mockito.doThrow(new InternalErrorFault(message)).when(iManageParticipantIdentifierWS).create(any(ServiceMetadataPublisherServiceForParticipantType.class));

        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null));

        //then
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsStringIgnoringCase(message));
    }

    @Test
    void testRegisterInDnsNewClientIsAlwaysCreated() throws Exception {
        //when
        testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);
        testInstance.registerInDns(PARTICIPANT_ID, DEFAULT_DOMAIN, null);

        //then
        verify(iManageParticipantIdentifierWS, times(2)).create(any(ServiceMetadataPublisherServiceForParticipantType.class));
        Mockito.verifyNoMoreInteractions(iManageParticipantIdentifierWS);
    }

    @Test
    void testUnregisterFromDns() throws Exception {
        //when
        boolean result = testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        verify(iManageParticipantIdentifierWS, times(1)).delete(any(ServiceMetadataPublisherServiceForParticipantType.class));
        Mockito.verifyNoMoreInteractions(iManageParticipantIdentifierWS);
    }

    @Test
    void testUnregisterFromDnsNewClientIsAlwaysCreated() throws Exception {
        //when
        testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);
        testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        verify(iManageParticipantIdentifierWS, times(2)).delete(any(ServiceMetadataPublisherServiceForParticipantType.class));
        Mockito.verifyNoMoreInteractions(iManageParticipantIdentifierWS);
    }

    @Test
    void testUnregisterFromDnsThrowUnknownBadRequestFault() throws Exception {
        doThrow(new BadRequestFault(ERROR_UNEXPECTED_MESSAGE)).when(iManageParticipantIdentifierWS).delete(any(ServiceMetadataPublisherServiceForParticipantType.class));

        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsStringIgnoringCase(ERROR_UNEXPECTED_MESSAGE));
    }

    @Test
    void testUnregisterFromDnsThrowUnknownException() throws Exception {
        String message = "something unexpected";
        doThrow(new InternalErrorFault(ERROR_UNEXPECTED_MESSAGE)).when(iManageParticipantIdentifierWS).delete(any(ServiceMetadataPublisherServiceForParticipantType.class));

        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsStringIgnoringCase(message));
    }

    @Test
    void testUnregisterFromDnsNotExists() throws Exception {
        //given
        Mockito.doThrow(new BadRequestFault(ERROR_PI_NO_EXISTS)).when(iManageParticipantIdentifierWS).delete(any(ServiceMetadataPublisherServiceForParticipantType.class));

        //when
        boolean suc = testInstance.unregisterFromDns(PARTICIPANT_ID, DEFAULT_DOMAIN);

        //then
        assertTrue(suc);
    }

    @Test
    void testIsOkMessageForParticipantNull() {
        //when
        boolean suc = testInstance.isOkMessage(PARTICIPANT_ID, null);

        //then
        assertFalse(suc);
    }

    @Test
    void testIsOkMessageForParticipantOk() {
        //when
        boolean suc = testInstance.isOkMessage(PARTICIPANT_ID, ERROR_PI_ALREADY_EXISTS);

        //then
        assertTrue(suc);
    }

    @Test
    void testIsOkMessageForParticipantFalse() {
        //when
        boolean suc = testInstance.isOkMessage(PARTICIPANT_ID, ERROR_UNEXPECTED_MESSAGE);

        //then
        assertFalse(suc);
    }

    @Test
    void testProcessSMLErrorMessageBadRequestFaultIgnore() {
        //given
        BadRequestFault ex = new BadRequestFault(ERROR_PI_ALREADY_EXISTS);

        //when
        boolean suc = testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID);

        //then
        assertTrue(suc);
    }

    @Test
    void testProcessSMLErrorMessageBadRequestFaultFailed() {
        //given
        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);

        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID));

        //then
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsStringIgnoringCase(ERROR_UNEXPECTED_MESSAGE));
    }

    @Test
    void testProcessSMLErrorMessageNoFoundFaultFailed() {
        //given
        NotFoundFault ex = new NotFoundFault(ERROR_UNEXPECTED_MESSAGE);

        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID));

        //then
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsStringIgnoringCase(ERROR_UNEXPECTED_MESSAGE));
    }

    @Test
    void testProcessSMLErrorMessageNoFoundFaultOk() {
        //given
        NotFoundFault ex = new NotFoundFault(ERROR_PI_NO_EXISTS);

        //when
        assertDoesNotThrow(() -> testInstance.processSMLErrorMessage(ex, PARTICIPANT_ID));
    }

    @Test
    void participantExists() throws Exception {
        // given
        ExistsParticipantResponseType existingParticipant = new ExistsParticipantResponseType();
        existingParticipant.setExist(true);
        Mockito.when(domain.isSmlRegistered()).thenReturn(true);
        Mockito.when(domain.getSmlSmpId()).thenReturn("smlSmpId");
        Mockito.when(identifier.getValue()).thenReturn("identifierValue");
        Mockito.when(identifier.getScheme()).thenReturn("identifierScheme");
        Mockito.when(ibdmslServiceWS.existsParticipantIdentifier(any(ParticipantsType.class))).thenReturn(existingParticipant);

        Mockito.doNothing().when(testInstance).configureClient(anyString(), any(), any(DBDomain.class));

        // when
        boolean result = testInstance.participantExists(identifier, domain);

        // then
        assertTrue(result, "Should have returned true when the participant exists");
    }

    @Test
    void participantExists_wrapsBadRequestFaultIntoSmpRuntimeException() throws Exception {
        // given
        String errorMessage = UUID.randomUUID().toString();
        Mockito.when(domain.isSmlRegistered()).thenReturn(true);
        Mockito.when(domain.getSmlSmpId()).thenReturn("smlSmpId");
        Mockito.when(identifier.getValue()).thenReturn("identifierValue");
        Mockito.when(identifier.getScheme()).thenReturn("identifierScheme");
        Mockito.when(ibdmslServiceWS.existsParticipantIdentifier(any(ParticipantsType.class))).thenThrow(new BadRequestFault(errorMessage));

        Mockito.doNothing().when(testInstance).configureClient(anyString(), any(), any(DBDomain.class));

        // when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.participantExists(identifier, domain));

        // then
        assertThat(smpRuntimeException.getMessage(),
                containsString("SML integration error!"));
    }

    @Test
    void participantExists_wrapsNotFoundFaultIntoSmpRuntimeException() throws Exception {
        // given
        String errorMessage = UUID.randomUUID().toString();
        Mockito.when(domain.isSmlRegistered()).thenReturn(true);
        Mockito.when(domain.getSmlSmpId()).thenReturn("smlSmpId");
        Mockito.when(identifier.getValue()).thenReturn("identifierValue");
        Mockito.when(identifier.getScheme()).thenReturn("identifierScheme");
        Mockito.when(ibdmslServiceWS.existsParticipantIdentifier(any(ParticipantsType.class))).thenThrow(new NotFoundFault(errorMessage));

        Mockito.doNothing().when(testInstance).configureClient(anyString(), any(), any(DBDomain.class));

        // when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.participantExists(identifier, domain));

        // then
        assertThat(smpRuntimeException.getMessage(),
                containsString("SML integration error!"));
    }

    @Test
    void participantExists_wrapsCheckedExceptionsIntoSmpRuntimeException() throws Exception {
        // given
        String errorMessage = UUID.randomUUID().toString();
        Mockito.when(domain.isSmlRegistered()).thenReturn(true);
        Mockito.when(domain.getSmlSmpId()).thenReturn("smlSmpId");
        Mockito.when(identifier.getValue()).thenReturn("identifierValue");
        Mockito.when(identifier.getScheme()).thenReturn("identifierScheme");
        // We need to match one of the checked exceptions present in the method signature, so we throw InternalErrorFault which will be handled aside
        Mockito.when(ibdmslServiceWS.existsParticipantIdentifier(any(ParticipantsType.class))).thenThrow(new InternalErrorFault(errorMessage));

        Mockito.doNothing().when(testInstance).configureClient(anyString(), any(), any(DBDomain.class));

        // when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.participantExists(identifier, domain));

        // then
        assertThat(smpRuntimeException.getMessage(),
                containsString("SML integration error!"));
    }

    @Test
    void participantExists_smlIntegrationDisabled() {
        // given
        Mockito.doReturn(false).when(configurationService).isSMLIntegrationEnabled();

        // when
        boolean result = testInstance.participantExists(identifier, domain);

        // then
        assertFalse(result, "The participant should have been returned as non-existing when the SML integration is not enabled");
    }

    @Test
    void participantExists_unregisteredDomain() {
        // given
        Mockito.when(domain.isSmlRegistered()).thenReturn(false);

        // when
        boolean result = testInstance.participantExists(identifier, domain);

        // then
        assertFalse(result, "The participant should have been returned as non-existing when the domain is not registered in SML");
    }
}
