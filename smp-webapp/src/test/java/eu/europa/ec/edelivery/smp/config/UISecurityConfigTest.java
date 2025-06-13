package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationProviderForUI;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class UISecurityConfigTest {

    protected SMPAuthenticationProviderForUI mokcSMPAuthenticationProviderForUI = Mockito.mock(SMPAuthenticationProviderForUI.class);
    protected ConfigurationService mockConfigurationService = Mockito.mock(ConfigurationService.class);
    protected MDCLogRequestFilter mockMDCLogRequestFilter = Mockito.mock(MDCLogRequestFilter.class);
    protected CsrfTokenRepository mockCsrfTokenRepository = Mockito.mock(CsrfTokenRepository.class);
    protected RequestMatcher mockCsrfURLMatcher = Mockito.mock(RequestMatcher.class);
    protected CasAuthenticationProvider mockCasAuthenticationProvider = Mockito.mock(CasAuthenticationProvider.class);
    protected CasAuthenticationFilter mockCasAuthenticationFilter = Mockito.mock(CasAuthenticationFilter.class);


    UISecurityConfig testInstance = new UISecurityConfig(mokcSMPAuthenticationProviderForUI,
            mockConfigurationService,
            mockMDCLogRequestFilter,
            mockCsrfTokenRepository,
            mockCsrfURLMatcher,
            mockCasAuthenticationProvider,
            mockCasAuthenticationFilter);

    @Test
    void testCreateCasAuthenticationEntryPoint() throws MalformedURLException {

        String casUrl = "http://cas-server.local/cas";
        String casCallbackUrl = "http://callback.local/smp";
        String casLoginPath = "login";
        doReturn(true).when(mockConfigurationService).isSSOEnabledForUserAuthentication();
        doReturn(new URL(casUrl)).when(mockConfigurationService).getCasURL();
        doReturn(casLoginPath).when(mockConfigurationService).getCasURLPathLogin();
        doReturn(new URL(casCallbackUrl)).when(mockConfigurationService).getCasCallbackUrl();

        CasAuthenticationEntryPoint result = testInstance.createCasAuthenticationEntryPoint();

        ServiceProperties serviceProperties = result.getServiceProperties();
        assertNotNull(serviceProperties);
        assertEquals(casUrl + "/" + casLoginPath, result.getLoginUrl());
        assertEquals(casCallbackUrl, serviceProperties.getService());
        assertEquals(ServiceProperties.DEFAULT_CAS_ARTIFACT_PARAMETER, serviceProperties.getArtifactParameter());
        assertTrue(serviceProperties.isAuthenticateAllArtifacts());
    }

    @Test
    void testCreateCasAuthenticationEntryPointDisabledSSO() {

        doReturn(false).when(mockConfigurationService).isSSOEnabledForUserAuthentication();
        CasAuthenticationEntryPoint result = testInstance.createCasAuthenticationEntryPoint();
        assertNull(result);
    }

    @Test
    void authenticationManagerBeanNotNull() {
        assertNotNull(testInstance.authenticationManagerBean());
    }

    @Test
    void getMDCLogRequestFilterNotNull() {
        assertNotNull(testInstance.getMDCLogRequestFilter());
    }

    @Test
    void tokenRepositoryNotNull() {
        assertNotNull(testInstance.tokenRepository());
    }

    @Test
    void csrfURLMatcherNotNull() {
        assertNotNull(testInstance.csrfURLMatcher());
    }

    @Test
    void smpCookieWriterNotNull() {
        assertNotNull(testInstance.smpCookieWriter());}

    @Test
    void httpSessionEventPublisherNotNull() {
        assertNotNull(testInstance.httpSessionEventPublisher());
    }

    @Test
    void smpForwardedHeaderTransformerNotNull() {
        assertNotNull(testInstance.smpForwardedHeaderTransformer());
    }
}