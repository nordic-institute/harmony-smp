
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

import eu.europa.ec.bdmsl.ws.soap.BadRequestFault;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.bdmsl.ws.soap.InternalErrorFault;
import eu.europa.ec.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static eu.europa.ec.edelivery.smp.sml.SmlConnectorTestConstants.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by JRC
 * since 4.1.
 */
class SmlConnectorDomainTest extends AbstractServiceTest {

    // Beans
    @SpyBean
    private ConfigurationService configurationService;
    @MockBean
    private IManageServiceMetadataWS iManageServiceMetadataWS;
    @SpyBean
    private SmlConnector testInstance;

    @BeforeEach
    public void setup() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createDomains();

        Mockito.doNothing().when(testInstance).configureClient(any(), any(), any());
        Mockito.doReturn(true).when(configurationService).isSMLIntegrationEnabled();

        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);
    }

    @Test
    void testRegisterDomainInDns() throws Exception {
        //when
        boolean result = testInstance.registerDomain(testUtilsDao.getD1());

        //then
        assertTrue(result);
        verify(iManageServiceMetadataWS, times(1)).create(any(ServiceMetadataPublisherServiceType.class));
    }

    @Test
    void testRegisterDomainInDnsAlreadyExists() throws Exception {
        //given
        DBDomain domain = testUtilsDao.getD1();
        Mockito.doThrow(new BadRequestFault("[ERR-106] The SMP '" + domain.getSmlSmpId() + "' already exists"))
                .when(iManageServiceMetadataWS).create(any(ServiceMetadataPublisherServiceType.class));

        //when
        boolean result = testInstance.registerDomain(domain);

        //then
        assertTrue(result);
        verify(iManageServiceMetadataWS, times(1)).create(any(ServiceMetadataPublisherServiceType.class));
    }

    @Test
    void testRegisterDomainInDnsUnknownException() throws Exception {
        //given
        String message = "something unexpected";
        Mockito.doThrow(new InternalErrorFault(message)).when(iManageServiceMetadataWS).create(any(ServiceMetadataPublisherServiceType.class));

        //when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.registerDomain(testUtilsDao.getD1()));

        //then
        assertEquals("SML integration error! Error: InternalErrorFault: " + message, smpRuntimeException.getMessage().trim());
        verify(iManageServiceMetadataWS, times(1)).create(any(ServiceMetadataPublisherServiceType.class));
    }

    @Test
    void testRegisterDomainInDnsNewClientIsAlwaysCreated() throws Exception {
        //when
        testInstance.registerDomain(testUtilsDao.getD1());
        testInstance.registerDomain(testUtilsDao.getD1());

        //then
        verify(iManageServiceMetadataWS, times(2)).create(any(ServiceMetadataPublisherServiceType.class));
    }

    @Test
    void testDomainUnregisterFromDns() throws Exception {
        //when
        testInstance.unregisterDomain(testUtilsDao.getD1());

        //then
        verify(iManageServiceMetadataWS, times(1)).delete(anyString());
    }

    @Test
    void testUnregisterDomainFromDnsNewClientIsAlwaysCreated() throws Exception {
        //when
        testInstance.unregisterDomain(testUtilsDao.getD1());
        testInstance.unregisterDomain(testUtilsDao.getD1());

        //then
        verify(iManageServiceMetadataWS, times(2)).delete(anyString());
    }

    @Test
    void testUnregisterDomainFromDnsThrowUnknownBadRequestFault() throws Exception {
        // given
        Mockito.doThrow(new BadRequestFault(ERROR_UNEXPECTED_MESSAGE)).when(iManageServiceMetadataWS).delete(anyString());

        //when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.unregisterDomain(testUtilsDao.getD1()));

        //then
        assertEquals("SML integration error! Error: BadRequestFault: " + ERROR_UNEXPECTED_MESSAGE, smpRuntimeException.getMessage().trim());
        verify(iManageServiceMetadataWS, times(1)).delete(anyString());

    }

    @Test
    void testUnregisterDomainFromDnsThrowUnknownException() throws Exception {
        //given
        Mockito.doThrow(new InternalErrorFault("something unexpected")).when(iManageServiceMetadataWS).delete(anyString());

        //when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.unregisterDomain(testUtilsDao.getD1()));

        //then
        assertEquals("SML integration error! Error: InternalErrorFault: something unexpected", smpRuntimeException.getMessage().trim());
        verify(iManageServiceMetadataWS, times(1)).delete(anyString());
    }

    @Test
    void testUnregisterDomainFromDnsNotExists() throws Exception {
        //given
        Mockito.doThrow(new BadRequestFault("[ERR-100] The SMP '" +testUtilsDao.getD1().getSmlSmpId()+ "' doesn't exist")).when(iManageServiceMetadataWS).delete(anyString());

        //when
        Assertions.assertDoesNotThrow(() -> testInstance.unregisterDomain(testUtilsDao.getD1()));
    }

    @Test
    void testIsOkMessageForDomainNull() {
        //when
        boolean suc = testInstance.isOkMessage(testUtilsDao.getD1(), null);

        //then
        assertFalse(suc);
    }

    @Test
    void testIsOkMessageForDomainFalse() {
        //when
        boolean suc = testInstance.isOkMessage(testUtilsDao.getD1(), ERROR_UNEXPECTED_MESSAGE);

        //then
        assertFalse(suc);
    }

    @Test
    void testGetSmlClientKeyAliasForDomain() {
        //given
        DBDomain domain = new DBDomain();
        domain.setSmlClientKeyAlias(UUID.randomUUID().toString());
        domain.setSmlClientCertAuth(false);

        //when
        String alias = testInstance.getSmlClientKeyAliasForDomain(domain);

        //then
        assertEquals(domain.getSmlClientKeyAlias(), alias);
    }

    @Test
    @Disabled("Randomly fails on bamboo ")
    void testGetSmlClientKeyAliasForDomainNulForSingleKey() {
        //given
        DBDomain domain = new DBDomain();
        domain.setSmlClientKeyAlias(null);
        domain.setSmlClientCertAuth(false);

        //when
        String alias = testInstance.getSmlClientKeyAliasForDomain(domain);

        //then
        assertEquals("single_domain_key", alias);
    }

    @Test
    void isDomainValid() throws Exception {
        //given
        ServiceMetadataPublisherServiceType existingDomain = new ServiceMetadataPublisherServiceType();
        Mockito.when(iManageServiceMetadataWS.read(any(ServiceMetadataPublisherServiceType.class))).thenReturn(existingDomain);

        //when
        boolean result = testInstance.isDomainValid(testUtilsDao.getD1());

        //then
        assertTrue(result, "Should have returned true when the participant exists");
    }

    @Test
    void isDomainValid_wrapsBadRequestFaultIntoSmpRuntimeException() throws Exception {
        //given
        String errorMessage = UUID.randomUUID().toString();
        Mockito.when(iManageServiceMetadataWS.read(any(ServiceMetadataPublisherServiceType.class))).thenThrow(new BadRequestFault(errorMessage));

        //when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.isDomainValid(testUtilsDao.getD1()));

        //then
        assertThat(smpRuntimeException.getMessage(),
                containsString("SML integration error!"));
    }

    @Test
    void isDomainValid_wrapsNotFoundFaultIntoSmpRuntimeException() throws Exception {
        //given
        String errorMessage = UUID.randomUUID().toString();
        Mockito.when(iManageServiceMetadataWS.read(any(ServiceMetadataPublisherServiceType.class))).thenThrow(new NotFoundFault(errorMessage));

        //when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.isDomainValid(testUtilsDao.getD1()));

        //then
        assertThat(smpRuntimeException.getMessage(),
                containsString("SML integration error!"));
    }

    @Test
    void isDomainValid_wrapsCheckedExceptionsIntoSmpRuntimeException() throws Exception {
        //given
        String errorMessage = UUID.randomUUID().toString();
        // We need to match one of the checked exceptions present in the method signature, so we throw InternalErrorFault which will be handled aside
        Mockito.when(iManageServiceMetadataWS.read(any(ServiceMetadataPublisherServiceType.class))).thenThrow(new InternalErrorFault(errorMessage));

        //when
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () ->
                testInstance.isDomainValid(testUtilsDao.getD1()));

        //then
        assertThat(smpRuntimeException.getMessage(),
                containsString("SML integration error!"));

    }

    @Test
    void isDomainValid_smlIntegrationDisabled() {
        //given
        Mockito.doReturn(false).when(configurationService).isSMLIntegrationEnabled();

        //when
        boolean result = testInstance.isDomainValid(testUtilsDao.getD1());

        //then
        assertFalse(result, "Should have returned the domain as not valid when the SML integration is not enabled");
        Mockito.verifyNoMoreInteractions(iManageServiceMetadataWS);
    }
}
