package eu.europa.ec.edelivery.smp.auth.cas;

import eu.europa.ec.edelivery.smp.utils.SmpUrlBuilder;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SMPCasConfigurerTest {
    ConfigurationService mockConfigService = Mockito.mock(ConfigurationService.class);
    SmpUrlBuilder mockSmpUrlBuilder = Mockito.mock(SmpUrlBuilder.class);

    SMPCasConfigurer testInstance = new SMPCasConfigurer(mockSmpUrlBuilder, mockConfigService);

    @Test
    public void serviceProperties() throws MalformedURLException {
        String callbackString = "http://callback.local/smp";
        URL callBackURL = new  URL(callbackString);
        doReturn(callBackURL).when(mockConfigService).getCasCallbackUrl();
        ServiceProperties serviceProperties = testInstance.serviceProperties();

        assertNotNull(serviceProperties);
        assertEquals(callbackString, serviceProperties.getService());
        assertEquals(ServiceProperties.DEFAULT_CAS_ARTIFACT_PARAMETER, serviceProperties.getArtifactParameter());
        assertEquals(true, serviceProperties.isAuthenticateAllArtifacts());
    }

    @Test
    public void casAuthenticationEntryPoint() throws MalformedURLException {
        String casUrl = "http://cas-server.local/cas";
        String casLoginPath = "login";
        doReturn(true).when(mockConfigService).isSSOEnabledForUserAuthentication();
        doReturn(new URL(casUrl)).when(mockConfigService).getCasURL();
        doReturn(casLoginPath).when(mockConfigService).getCasURLPathLogin();
        ServiceProperties serviceProperties = testInstance.serviceProperties();

        CasAuthenticationEntryPoint result = testInstance.casAuthenticationEntryPoint(serviceProperties);
        assertNotNull(serviceProperties);
        assertEquals(casUrl+"/"+casLoginPath,result.getLoginUrl() );
        assertEquals(serviceProperties,result.getServiceProperties() );
    }

    @Test
    public void ecasServiceTicketValidator() throws MalformedURLException {
        String casUrl = "http://cas-server.local/cas";
        String tokenValidator = "laxValidate";

        doReturn(true).when(mockConfigService).isSSOEnabledForUserAuthentication();
        doReturn(new URL(casUrl)).when(mockConfigService).getCasURL();
        doReturn(tokenValidator).when(mockConfigService).getCasURLTokenValidation();

        SMPCas20ServiceTicketValidator result = testInstance.ecasServiceTicketValidator();
        assertNotNull(result);
        assertEquals(tokenValidator, result.getUrlSuffix());
    }

    @Test
    public void getCustomParameters() {
        Map<String, String> testMap = new HashMap<>();
        testMap.put("key1","val1");
        testMap.put("key2","val2");
        List<String> groups = Arrays.asList("list1","list2");
        doReturn(testMap).when(mockConfigService).getCasTokenValidationParams();
        doReturn(groups).when(mockConfigService).getCasURLTokenValidationGroups();

        Map<String, String> result = testInstance.getCustomParameters();

        assertEquals(4, result.size());
        assertEquals("true", result.get("userDetails"));
        assertEquals(String.join(",", groups), result.get("groups"));
        assertEquals("val1", result.get("key1"));
        assertEquals("val2", result.get("key2"));
    }

    @Test
    public void casAuthenticationProvider() {
        ServiceProperties serviceProperties = mock(ServiceProperties.class);
        SMPCas20ServiceTicketValidator smpCas20ServiceTicketValidator = mock(SMPCas20ServiceTicketValidator.class);
        SMPCasUserService smpCasUserService = mock(SMPCasUserService.class);

        doReturn(true).when(mockConfigService).isSSOEnabledForUserAuthentication();


        CasAuthenticationProvider provider = testInstance.casAuthenticationProvider(serviceProperties, smpCas20ServiceTicketValidator, smpCasUserService);

        assertNotNull(provider);

    }

}
