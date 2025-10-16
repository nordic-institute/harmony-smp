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


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationProviderForUI;
import eu.europa.ec.edelivery.smp.auth.URLCsrfIgnoreMatcher;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.error.SMPSecurityExceptionHandler;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SMPCookieWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.*;

/**
 * SMP UI Security configuration
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */


@Configuration
@EnableWebSecurity
public class UISecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(UISecurityConfig.class);
    SMPAuthenticationProviderForUI smpAuthenticationProviderForUI;

    MDCLogRequestFilter mdcLogRequestFilter;

    CsrfTokenRepository csrfTokenRepository;
    RequestMatcher csrfURLMatcher;
    ConfigurationService configurationService;
    AuthenticationManager authenticationManager;

    // cas authentication
    CasAuthenticationProvider casAuthenticationProvider;
    CasAuthenticationFilter casAuthenticationFilter;

    public UISecurityConfig(SMPAuthenticationProviderForUI smpAuthenticationProviderForUI,
                            ConfigurationService configurationService,
                            @Lazy MDCLogRequestFilter mdcLogRequestFilter,
                            @Lazy CsrfTokenRepository csrfTokenRepository,
                            @Lazy RequestMatcher csrfURLMatcher,
                            // optional cas authentication configuration
                            @Lazy CasAuthenticationProvider casAuthenticationProvider,
                            @Lazy @Qualifier(SMP_CAS_FILTER_BEAN) CasAuthenticationFilter casAuthenticationFilter
    ) {

        this.configurationService = configurationService;
        this.smpAuthenticationProviderForUI = smpAuthenticationProviderForUI;
        this.mdcLogRequestFilter = mdcLogRequestFilter;
        this.csrfTokenRepository = csrfTokenRepository;
        this.csrfURLMatcher = csrfURLMatcher;
        this.casAuthenticationProvider = casAuthenticationProvider;
        this.casAuthenticationFilter = casAuthenticationFilter;
    }

    /**
     * The bean is used to create the authentication manager for the UI.
     * It is used in the UI to authenticate the user.
     *
     * @return SecurityFilterChain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain filterChainUI(HttpSecurity httpSecurity) throws Exception {

        PathPatternRequestMatcher.Builder matcherBuilder = PathPatternRequestMatcher.withDefaults();
        AuthenticationManager manager = authenticationManagerBean();
        SMPSecurityExceptionHandler smpSecurityExceptionHandler = new SMPSecurityExceptionHandler();
        if (configurationService.isSSOEnabledForUserAuthentication()) {
            String casEndpointPath = SMP_SECURITY_PATH_CAS_AUTHENTICATE;
            LOG.debug("The CAS authentication is enabled. Set casAuthenticationEntryPoint for endpoint [{}]!", casEndpointPath);
            httpSecurity
                    .exceptionHandling(exceptionHandling -> exceptionHandling
                            .defaultAuthenticationEntryPointFor(createCasAuthenticationEntryPoint(), matcherBuilder.matcher(HttpMethod.GET, casEndpointPath))
                            .defaultAuthenticationEntryPointFor(smpSecurityExceptionHandler, matcherBuilder.matcher("/ui/**"))
                    ).addFilter(casAuthenticationFilter);
        } else {
            httpSecurity.exceptionHandling(
                    exceptionHandling -> exceptionHandling
                            .authenticationEntryPoint(smpSecurityExceptionHandler)
                            .accessDeniedHandler(smpSecurityExceptionHandler)
            );
        }


        List<RequestMatcher> matchers = new ArrayList<>();
        // add the default matcher for the UI
        matchers.add(matcherBuilder.matcher("/ui/**"));
        matchers.add(matcherBuilder.matcher("/smp/ui/**"));

        httpSecurity
                .securityMatcher(new OrRequestMatcher(matchers))
                .addFilterAfter(mdcLogRequestFilter, BasicAuthenticationFilter.class)
                .httpBasic(http -> http.authenticationEntryPoint(smpSecurityExceptionHandler))
                .anonymous(anon -> anon.authorities(SMPAuthority.S_AUTHORITY_ANONYMOUS.getAuthority()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/public/rest/application/info")).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/public/rest/application/name")).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/public/rest/locale/**")).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/public/rest/domain")).permitAll()
                        // allow anonymous access to the public resource search
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/public/rest/search/**")).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.POST, "/ui/public/rest/security/authentication")).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.POST, ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_USER_RESET)).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.POST, ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_USER_VALIDATE_RESET_TOKEN)).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.DELETE, "/ui/public/rest/security/authentication")).permitAll()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, SMP_SECURITY_PATH_CAS_AUTHENTICATE)).authenticated()
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.PUT, "/ui/public/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/public/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.POST, "/ui/public/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.DELETE, "/ui/public/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.POST, "/ui/internal/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.DELETE, "/ui/internal/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/internal/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.POST, "/ui/edit/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.PUT, "/ui/edit/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.DELETE, "/ui/edit/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/edit/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.PUT, "/ui/internal/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.DELETE, "/ui/internal/rest/**")).hasAnyAuthority(
                                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                        .requestMatchers(matcherBuilder.matcher(HttpMethod.GET, "/ui/**")).permitAll()
                );

        httpSecurity.authenticationManager(manager);
        configureSecurityHeaders(httpSecurity);
        return httpSecurity.build();
    }

    protected CasAuthenticationEntryPoint createCasAuthenticationEntryPoint() {
        if (!configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.warn("Bean [{}] is not configured because SSO CAS authentication is not enabled!", CasAuthenticationEntryPoint.class);
            return null;
        }
        String casUrl = configurationService.getCasURL().toString();
        String casLoginPath = configurationService.getCasURLPathLogin();
        String casUrlLogin = Strings.CS.removeEnd(casUrl, "/") + Strings.CS.prependIfMissing(casLoginPath, "/");
        URL path = configurationService.getCasCallbackUrl();

        // create service properties
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setArtifactParameter(ServiceProperties.DEFAULT_CAS_ARTIFACT_PARAMETER);
        serviceProperties.setService(path != null ? path.toExternalForm() : "null");
        serviceProperties.setAuthenticateAllArtifacts(true);
        // create entry point
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casUrlLogin);
        entryPoint.setServiceProperties(serviceProperties);
        LOG.info("Configured CAS CasAuthenticationEntryPoint Url: [{}]", entryPoint.getLoginUrl());
        return entryPoint;
    }

    protected void configureSecurityHeaders(HttpSecurity httpSecurity) throws Exception {
        // configure session and csrf headers
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("XSRF-TOKEN");
        httpSecurity
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(requestHandler)
                        .requireCsrfProtectionMatcher(csrfURLMatcher)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        // on authentication, a new HTTP Session is created, the old one is invalidated and the attributes from the old session are copied over.
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession)
                        //.sessionFixation().changeSessionId() // Change session ID on authentication
                        //In order to force only one  concurrent sessions for the same user,
                        .maximumSessions(1)
                );

        // configure default security headers and then set HSTS and CSP headers
        httpSecurity
                .headers(headers ->
                        headers
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                                .contentTypeOptions(Customizer.withDefaults())
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
            httpSecurity.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(contentSecurityPolicy)));
        }
    }

    @Bean(name = {SMP_UI_AUTHENTICATION_MANAGER_BEAN})
    public AuthenticationManager authenticationManagerBean() {
        if (authenticationManager == null) {
            List<AuthenticationProvider> authenticationProviderList = new ArrayList<>();
            if (configurationService.isSSOEnabledForUserAuthentication()) {
                LOG.info("[CAS] Authentication Provider enabled");
                authenticationProviderList.add(casAuthenticationProvider);
            }
            authenticationProviderList.add(smpAuthenticationProviderForUI);
            authenticationManager = new ProviderManager(authenticationProviderList);
        }
        return authenticationManager;
    }

    @Bean
    public MDCLogRequestFilter getMDCLogRequestFilter() {
        return new MDCLogRequestFilter();
    }

    @Bean
    public CsrfTokenRepository tokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public RequestMatcher csrfURLMatcher() {
        URLCsrfIgnoreMatcher requestMatcher = new URLCsrfIgnoreMatcher();
        // init pages
        requestMatcher.addIgnoreUrl("^$", HttpMethod.GET);
        //ignore CSRF for SMP rest API (or use CSRF for the UI)
        requestMatcher.addIgnoreUrl("^/(?!ui/)[^/]*(/services/.*)?$", HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.POST);
        requestMatcher.addIgnoreUrl("^(/smp)?/(index.html|ui/(#/)?|)$", HttpMethod.GET);
        // ignore for login and logout
        requestMatcher.addIgnoreUrl(ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY + "/authentication", HttpMethod.DELETE, HttpMethod.POST);
        requestMatcher.addIgnoreUrl(SMP_SECURITY_PATH_CAS_AUTHENTICATE, HttpMethod.GET);
        requestMatcher.addIgnoreUrl(ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_USER_RESET, HttpMethod.POST);
        // allow all gets except for rest services
        requestMatcher.addIgnoreUrl("/ui/.*", HttpMethod.GET);
        // monitor
        requestMatcher.addIgnoreUrl("/monitor/is-alive", HttpMethod.GET);
        return requestMatcher;
    }

    @Bean
    public SMPCookieWriter smpCookieWriter() {
        return new SMPCookieWriter();
    }

    /**
     * This is needed to enable the concurrent session-control support is to add the following listener
     *
     * @return HttpSessionEventPublisher
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /*
     * Bean removes "Forwarded" and "X-Forwarded-*" headers if 'smp.http.forwarded.headers.enabled' is set to false.
     * Else it extracts values from "Forwarded" and "X-Forwarded-*" headers to override the request URI so to reflects
     * the client-originated protocol and address.
     *
     * NOTE: Enable use of headers with "security considerations" since an application cannot know if the headers were
     * added by a proxy, as intended, or by a malicious client.
     */
    //@Bean(SMP_FORWARDED_HEADER_TRANSFORMER_BEAN)
    @Bean
    public ForwardedHeaderTransformer smpForwardedHeaderTransformer() {
        ForwardedHeaderTransformer forwardedHeaderTransformer = new ForwardedHeaderTransformer();
        forwardedHeaderTransformer.setRemoveOnly(false);
        return forwardedHeaderTransformer;

    }
}
