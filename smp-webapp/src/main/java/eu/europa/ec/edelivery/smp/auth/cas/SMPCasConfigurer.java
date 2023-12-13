/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.auth.cas;

import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SmpUrlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.*;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SSO_CAS_URL;


/**
 * The purpose of the class is to setup SMP for to use CAS
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Configuration
public class SMPCasConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(SMPCasConfigurer.class);

    final SmpUrlBuilder smpUrlBuilder;
    final ConfigurationService configurationService;

    public SMPCasConfigurer(SmpUrlBuilder smpUrlBuilder, ConfigurationService configurationService) {
        this.smpUrlBuilder = smpUrlBuilder;
        this.configurationService = configurationService;
    }

    /**
     * Configure CAS ServiceProperties
     *
     * @return ServiceProperties - Bean which stores properties related to this SMP CAS service.
     */
    @Bean(name = SMP_CAS_PROPERTIES_BEAN)
    public ServiceProperties serviceProperties() {
        URL path = configurationService.getCasCallbackUrl();
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setArtifactParameter(ServiceProperties.DEFAULT_CAS_ARTIFACT_PARAMETER);
        serviceProperties.setService(path != null ? path.toExternalForm() : "null");
        serviceProperties.setAuthenticateAllArtifacts(true);
        LOG.info("Configured CAS ServiceProperties with callback Url: [{}]", serviceProperties.getService());
        return serviceProperties;
    }

    /**
     * The entry point of Spring Security authentication process (based on CAS).
     * The user's browser will be redirected to the CAS login page.
     *
     * @return
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(@Nullable @Qualifier(SMP_CAS_PROPERTIES_BEAN) ServiceProperties serviceProperties) {

        if (!configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.debug("Bean [{}] is not configured because SSO CAS authentication is not enabled!", SMP_CAS_PROPERTIES_BEAN);
            return null;
        }

        String casUrl = configurationService.getCasURL().toString();
        String casLoginPath = configurationService.getCasURLPathLogin();
        String casUrlLogin = StringUtils.removeEnd(casUrl, "/") + StringUtils.prependIfMissing(casLoginPath, "/");

        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casUrlLogin);
        entryPoint.setServiceProperties(serviceProperties);
        LOG.info("Configured CAS CasAuthenticationEntryPoint Url: [{}]", entryPoint.getLoginUrl());
        return entryPoint;
    }

    @Bean
    public SMPCas20ServiceTicketValidator ecasServiceTicketValidator() {
        if (!configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.debug("Bean [{}] is not configured because SSO CAS authentication is not enabled!", SMP_CAS_PROPERTIES_BEAN);
            return null;
        }
        if (configurationService.getCasURL() == null) {
            LOG.error("Bean  [{}]  is not created! Missing Service parameter [{}]!", SMP_CAS_PROPERTIES_BEAN, SSO_CAS_URL.getProperty());
            return null;
        }

        String casUrl = configurationService.getCasURL().toString();
        String casTokenValidationSuffix = configurationService.getCasURLTokenValidation();
        SMPCas20ServiceTicketValidator validator = new SMPCas20ServiceTicketValidator(casUrl, casTokenValidationSuffix);
        validator.setCustomParameters(getCustomParameters());
        validator.setRenew(false);
        return validator;
    }

    /**
     * Generate properties for SMPCas20ServiceTicketValidator
     *
     * @return CAS properties
     */
    public Map<String, String> getCustomParameters() {
        Map<String, String> map = new HashMap<>();
        // always return details
        map.put("userDetails", "true");
        map.putAll(configurationService.getCasTokenValidationParams());
        List<String> groupList = configurationService.getCasURLTokenValidationGroups();
        if (!groupList.isEmpty()) {
            map.put("groups", String.join(",", groupList));
        }
        LOG.debug("Set ticket validation parameters: [{}]", map);
        return map;
    }


    /**
     * The authentication provider that integrates with CAS.
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(
            @Nullable @Qualifier(SMP_CAS_PROPERTIES_BEAN) ServiceProperties serviceProperties,
            @Nullable SMPCas20ServiceTicketValidator serviceTicketValidator,
            @Nullable SMPCasUserService smpCasUserService) {

        if (!configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.debug("Bean [CasAuthenticationProvider:{}] is not configured because SSO CAS authentication is not enabled!", SMP_CAS_PROPERTIES_BEAN);
            return null;
        }

        LOG.debug("Configure Bean [CasAuthenticationProvider:{}]!", SMP_CAS_PROPERTIES_BEAN);
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties);
        provider.setTicketValidator(serviceTicketValidator);
        provider.setAuthenticationUserDetailsService(smpCasUserService);
        //A Key is required so CasAuthenticationProvider can identify tokens it previously authenticated
        provider.setKey(SMP_CAS_KEY + UUID.randomUUID());
        return provider;
    }

    /**
     * Create CAS filter to processes a CAS service ticket
     *
     * @param authenticationManager
     * @param casServiceProperties
     * @return Filter
     * @throws Exception
     */
    @Bean(SMP_CAS_FILTER_BEAN)
    public CasAuthenticationFilter casAuthenticationFilter(
            @Qualifier(SMP_AUTHENTICATION_MANAGER_BEAN) AuthenticationManager authenticationManager,
            @Qualifier(SMP_CAS_PROPERTIES_BEAN) ServiceProperties casServiceProperties) {

        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setFilterProcessesUrl(SMP_SECURITY_PATH_CAS_AUTHENTICATE + "/login");
        filter.setServiceProperties(casServiceProperties);
        filter.setAuthenticationManager(authenticationManager);
        LOG.info("Created CAS Filter: [{}] with the properties: [{}]", filter.getClass().getSimpleName() , casServiceProperties.getArtifactParameter());
        return filter;
    }
}
