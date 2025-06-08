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

package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.security.ClientCertAuthenticationFilter;
import eu.europa.ec.edelivery.security.EDeliveryX509AuthenticationFilter;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationProvider;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.error.SMPSecurityExceptionHandler;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.SMP_AUTHENTICATION_MANAGER_BEAN;
import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.SMP_SECURITY_PATH_AUTHENTICATE;


/**
 * SMP Security configuration
 *
 * @author gutowpa
 * @since 3.0
 */
@Configuration
@EnableWebSecurity
public class WSSecurityConfig {
    private static final Logger LOG = LoggerFactory.getLogger(WSSecurityConfig.class);

    SMPAuthenticationProvider smpAuthenticationProvider;
    MDCLogRequestFilter mdcLogRequestFilter;

    CsrfTokenRepository csrfTokenRepository;
    RequestMatcher csrfURLMatcher;
    ConfigurationService configurationService;

    // Accounts supporting automated application functionalities
    ClientCertAuthenticationFilter clientCertAuthenticationFilter;
    EDeliveryX509AuthenticationFilter x509AuthenticationFilter;

    AuthenticationManager authenticationManager;


    public WSSecurityConfig(SMPAuthenticationProvider smpAuthenticationProvider,
                            @Lazy ConfigurationService configurationService,
                            @Lazy MDCLogRequestFilter mdcLogRequestFilter,
                            @Lazy CsrfTokenRepository csrfTokenRepository,
                            @Lazy RequestMatcher csrfURLMatcher
    ) {

        this.configurationService = configurationService;
        this.smpAuthenticationProvider = smpAuthenticationProvider;
        this.mdcLogRequestFilter = mdcLogRequestFilter;
        this.csrfTokenRepository = csrfTokenRepository;
        this.csrfURLMatcher = csrfURLMatcher;
    }

    /**
     * The main security filter chain for the SMP API.
     * <p>
     * This method configures the security headers, exception handling, authentication filters, and authorization rules.
     *
     * @param httpSecurity the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChainAPI(HttpSecurity httpSecurity) throws Exception {
        configureSecurityHeaders(httpSecurity);
        SMPSecurityExceptionHandler smpSecurityExceptionHandler = new SMPSecurityExceptionHandler();


        httpSecurity
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(smpSecurityExceptionHandler)
                );

        httpSecurity
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentTypeOptions(withDefaults -> {
                        })
                );


        PathPatternRequestMatcher.Builder pathPatternRequestMatcherBuilder = PathPatternRequestMatcher.withDefaults();

        httpSecurity
                .addFilterAfter(mdcLogRequestFilter, EDeliveryX509AuthenticationFilter.class)
                .addFilter(getClientCertAuthenticationFilter())
                .addFilter(getEDeliveryX509AuthenticationFilter())
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(smpSecurityExceptionHandler)
                )
                .anonymous(anonymous -> anonymous
                        .authorities(SMPAuthority.S_AUTHORITY_ANONYMOUS.getAuthority())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(pathPatternRequestMatcherBuilder.matcher(HttpMethod.GET, "/")).permitAll()
                        .requestMatchers(HttpMethod.DELETE, SMP_SECURITY_PATH_AUTHENTICATE).permitAll()
                        .requestMatchers(HttpMethod.POST, SMP_SECURITY_PATH_AUTHENTICATE).permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.DELETE, "^/(?!ui/)([^/]+)(/[^/]+){0,4}$")).hasAnyRole(
                                ApplicationRoleType.USER.getAPIRole(),
                                ApplicationRoleType.SYSTEM_ADMIN.getAPIRole())
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "^/(?!ui/)([^/]+)(/[^/]+){0,4}$")).hasAnyRole(
                                ApplicationRoleType.USER.getAPIRole(),
                                ApplicationRoleType.SYSTEM_ADMIN.getAPIRole())
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "^/(?!ui/)([^/]+)(/[^/]+){0,4}$")).permitAll()
                );
        // set authentication manager
        httpSecurity.authenticationManager(getAPIAuthenticationManager());
        return httpSecurity.build();

    }

    /**
     * The authentication manager bean for the SMP.
     * <p>
     * This bean is used to authenticate users and is set as the primary authentication manager.
     *
     * @return the authentication manager
     */
    @Primary
    @Bean(name = {BeanIds.AUTHENTICATION_MANAGER, SMP_AUTHENTICATION_MANAGER_BEAN})
    public AuthenticationManager authenticationManagerBean() {
        return getAPIAuthenticationManager();
    }

    protected void configureSecurityHeaders(HttpSecurity httpSecurity) throws Exception {

// Configure session and CSRF headers using non-deprecated API
        httpSecurity.csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .requireCsrfProtectionMatcher(csrfURLMatcher)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
                        .maximumSessions(1)
                );

// set HstsMAxAge
        Integer maxAge = configurationService.getHttpHeaderHstsMaxAge();
        if (maxAge == null || maxAge < 0) {
            LOG.info("The httpStrictTransportSecurity (HSTS) policy is set for HTTPS/1Y!");
            httpSecurity.headers(headers -> headers
                    .httpStrictTransportSecurity(hsts -> hsts
                            .includeSubDomains(true)
                            .preload(false)
                            .maxAgeInSeconds(31536000)
                    )
            );
        } else if (maxAge == 0) {
            LOG.warn("The httpStrictTransportSecurity (HSTS) policy is disabled!");
            httpSecurity.headers(headers -> headers
                    .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
            );
        } else {
            LOG.info("The httpStrictTransportSecurity (HSTS) policy is set to [{}] for http and https!", maxAge);
            httpSecurity.headers(headers -> headers
                    .httpStrictTransportSecurity(hsts -> hsts
                            .includeSubDomains(true)
                            .preload(false)
                            .maxAgeInSeconds(maxAge)
                            .requestMatcher(AnyRequestMatcher.INSTANCE)
                    )
            );
        }

        String contentSecurityPolicy = configurationService.getHttpHeaderContentSecurityPolicy();
        if (StringUtils.isNotBlank(contentSecurityPolicy)) {
            httpSecurity.headers(headers -> headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(contentSecurityPolicy))
            );
        }

    }

    @Bean
    public HttpFirewall smpHttpFirewall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(configurationService.encodedSlashesAllowedInUrl());
        return firewall;
    }

    private AuthenticationManager getAPIAuthenticationManager() {
        if (authenticationManager == null) {
            // create authentication managerAuthenticationManagerBuilder
            authenticationManager = new ProviderManager(smpAuthenticationProvider);
        }
        return authenticationManager;
    }


    public ClientCertAuthenticationFilter getClientCertAuthenticationFilter() {
        if (clientCertAuthenticationFilter == null) {
            clientCertAuthenticationFilter = new ClientCertAuthenticationFilter();
            clientCertAuthenticationFilter.setAuthenticationManager(getAPIAuthenticationManager());
            clientCertAuthenticationFilter.setClientCertAuthenticationEnabled(configurationService.isExternalTLSAuthenticationWithClientCertHeaderEnabled());
        }
        return clientCertAuthenticationFilter;
    }

    public EDeliveryX509AuthenticationFilter getEDeliveryX509AuthenticationFilter() {
        if (x509AuthenticationFilter == null) {
            x509AuthenticationFilter = new EDeliveryX509AuthenticationFilter();
            x509AuthenticationFilter.setAuthenticationManager(getAPIAuthenticationManager());
            x509AuthenticationFilter.setHttpHeaderAuthenticationEnabled(configurationService.isExternalTLSAuthenticationWithSSLClientCertHeaderEnabled());

        }
        return x509AuthenticationFilter;
    }

    public void setExternalTlsAuthenticationWithClientCertHeaderEnabled(boolean clientCertEnabled) {
        try {
            getClientCertAuthenticationFilter().setClientCertAuthenticationEnabled(clientCertEnabled);
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error occurred while setting the ClientCert feature (enable [" + clientCertEnabled + "])", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public void setExternalTlsAuthenticationWithX509CertificateHeaderEnabled(boolean sslClientCertEnabled) {
        try {
            getEDeliveryX509AuthenticationFilter().setHttpHeaderAuthenticationEnabled(sslClientCertEnabled);
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error occurred while setting the ClientCert feature (enable [" + sslClientCertEnabled + "])", ExceptionUtils.getRootCauseMessage(e));
        }
    }
}
