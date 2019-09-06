
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
public class SmlConnectorDomainTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected ConfigurationService configurationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    protected SmlConnector smlConnector;
    @Autowired
    SmlIntegrationConfiguration mockSml;

    @Before
    public void setup() {

        configurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(smlConnector,"configurationService",configurationService);

        Mockito.doReturn(true).when(configurationService).isSMLIntegrationEnabled();

        mockSml.reset();
    }

    @Test
    public void testRegisterDomainInDns() throws UnauthorizedFault, InternalErrorFault, BadRequestFault {
        //when

        boolean result = smlConnector.registerDomain(DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getSmpManagerClientMocks().size());
        verify(mockSml.getSmpManagerClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getSmpManagerClientMocks().toArray());
    }

    @Test
    public void testRegisterDomainInDnsAlreadyExists() throws UnauthorizedFault, InternalErrorFault, BadRequestFault {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_SMP_ALREADY_EXISTS);
        mockSml.setThrowException(ex);
        boolean result = smlConnector.registerDomain(DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getSmpManagerClientMocks().size());
        verify(mockSml.getSmpManagerClientMocks().get(0)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getSmpManagerClientMocks().toArray());
    }

    @Test
    public void testRegisterDomainInDnsUnknownException() {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);
        expectedException.expectMessage(message);
        expectedException.expect(SMPRuntimeException.class);

        smlConnector.registerDomain(DEFAULT_DOMAIN);
    }

    @Test
    public void testRegisterDomainInDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.registerDomain(DEFAULT_DOMAIN);
        smlConnector.registerDomain(DEFAULT_DOMAIN);

        //then
        assertEquals(2, mockSml.getSmpManagerClientMocks().size());
        verify(mockSml.getSmpManagerClientMocks().get(0)).create(any());
        verify(mockSml.getSmpManagerClientMocks().get(1)).create(any());
        Mockito.verifyNoMoreInteractions(mockSml.getSmpManagerClientMocks().toArray());
    }

    @Test
    public void testDomainUnregisterFromDns() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        boolean result = smlConnector.unregisterDomain(DEFAULT_DOMAIN);

        //then
        assertTrue(result);
        assertEquals(1, mockSml.getSmpManagerClientMocks().size());
        verify(mockSml.getSmpManagerClientMocks().get(0)).delete(any());
        Mockito.verifyNoMoreInteractions(mockSml.getSmpManagerClientMocks().toArray());
    }

    @Test
    public void testUnregisterDomainFromDnsNewClientIsAlwaysCreated() throws UnauthorizedFault, NotFoundFault, InternalErrorFault, BadRequestFault {
        //when
        smlConnector.unregisterDomain(DEFAULT_DOMAIN);
        smlConnector.unregisterDomain(DEFAULT_DOMAIN);

        //then
        assertEquals(2, mockSml.getSmpManagerClientMocks().size());
        verify(mockSml.getSmpManagerClientMocks().get(0)).delete(any());
        verify(mockSml.getSmpManagerClientMocks().get(1)).delete(any());
        Mockito.verifyNoMoreInteractions(mockSml.getSmpManagerClientMocks().toArray());
    }

    @Test
    public void testUnregisterDomainFromDnsThrowUnknownBadRequestFault() {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_UNEXPECTED_MESSAGE);
        mockSml.setThrowException(ex);
        expectedException.expectMessage(ERROR_UNEXPECTED_MESSAGE);
        expectedException.expect(SMPRuntimeException.class);

        smlConnector.unregisterDomain(DEFAULT_DOMAIN);
    }

    @Test
    public void testUnregisterDomainFromDnsThrowUnknownException() {
        //when
        String message = "something unexpected";
        Exception ex = new Exception(message);
        mockSml.setThrowException(ex);
        expectedException.expectMessage(message);
        expectedException.expect(SMPRuntimeException.class);

        smlConnector.unregisterDomain(DEFAULT_DOMAIN);
    }

    @Test
    public void testUnregisterDomainFromDnsNotExists() {
        //when
        BadRequestFault ex = new BadRequestFault(ERROR_SMP_NOT_EXISTS);
        mockSml.setThrowException(ex);
        boolean suc = smlConnector.unregisterDomain(DEFAULT_DOMAIN);

        assertTrue(suc);
    }

    @Test
    public void testIsOkMessageForDomainNull() {
        boolean suc = smlConnector.isOkMessage(DEFAULT_DOMAIN, null);

        assertFalse(suc);
    }

    @Test
    public void testIsOkMessageForDomainFalse() {

        boolean suc = smlConnector.isOkMessage(DEFAULT_DOMAIN, ERROR_UNEXPECTED_MESSAGE);

        assertFalse(suc);
    }
}
