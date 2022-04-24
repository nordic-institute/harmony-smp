package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationProviderForUI;
import eu.europa.ec.edelivery.smp.auth.URLCsrfMatcher;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.error.SMPSecurityExceptionHandler;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SMPCookieWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.*;

/**
 * SMP UI Security configuration
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Order(1)
@ComponentScan("eu.europa.ec.edelivery.smp.auth")
public class UISecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(UISecurityConfigurerAdapter.class);

    SMPAuthenticationProviderForUI smpAuthenticationProviderForUI;
    CasAuthenticationProvider casAuthenticationProvider;
    MDCLogRequestFilter mdcLogRequestFilter;
    // User account
    CasAuthenticationFilter casAuthenticationFilter;
    CasAuthenticationEntryPoint casAuthenticationEntryPoint;

    CsrfTokenRepository csrfTokenRepository;
    HttpFirewall httpFirewall;
    RequestMatcher csrfURLMatcher;
    ConfigurationService configurationService;

    @Autowired
    public UISecurityConfigurerAdapter(SMPAuthenticationProviderForUI smpAuthenticationProviderForUI,
                                       ConfigurationService configurationService,
                                       @Lazy MDCLogRequestFilter mdcLogRequestFilter,
                                       @Lazy CsrfTokenRepository csrfTokenRepository,
                                       @Lazy RequestMatcher csrfURLMatcher,
                                       @Lazy HttpFirewall httpFirewall,
                                       // optional cas authentication configuration
                                       @Lazy CasAuthenticationProvider casAuthenticationProvider,
                                       @Lazy @Qualifier(SMP_CAS_FILTER_BEAN) CasAuthenticationFilter casAuthenticationFilter,
                                       @Lazy CasAuthenticationEntryPoint casAuthenticationEntryPoint
    ) {
        super(false);
        this.configurationService = configurationService;
        this.smpAuthenticationProviderForUI = smpAuthenticationProviderForUI;
        this.casAuthenticationProvider = casAuthenticationProvider;
        this.casAuthenticationFilter = casAuthenticationFilter;
        this.mdcLogRequestFilter = mdcLogRequestFilter;
        this.casAuthenticationEntryPoint = casAuthenticationEntryPoint;
        this.csrfTokenRepository = csrfTokenRepository;
        this.csrfURLMatcher = csrfURLMatcher;
        this.httpFirewall = httpFirewall;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity = httpSecurity.antMatcher("/ui/**");
        configureSecurityHeaders(httpSecurity);

        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigurer = httpSecurity.exceptionHandling();
        if (configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.debug("The CAS authentication is enabled. Set casAuthenticationEntryPoint!");
            exceptionHandlingConfigurer = exceptionHandlingConfigurer.defaultAuthenticationEntryPointFor(casAuthenticationEntryPoint, new AntPathRequestMatcher(SMP_SECURITY_PATH_CAS_AUTHENTICATE));
        }

        SMPSecurityExceptionHandler smpSecurityExceptionHandler = new SMPSecurityExceptionHandler();

        exceptionHandlingConfigurer.authenticationEntryPoint(smpSecurityExceptionHandler);
        httpSecurity = exceptionHandlingConfigurer
                .accessDeniedHandler(smpSecurityExceptionHandler)

                .and()
                .headers().frameOptions().deny()
                .contentTypeOptions().and()
                .xssProtection().xssProtectionEnabled(true).and()
                .and();

        if (configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.debug("The CAS authentication is enabled. Add CAS filter!");
            httpSecurity = httpSecurity.addFilter(casAuthenticationFilter);
        }


        httpSecurity
                .addFilterAfter(mdcLogRequestFilter, BasicAuthenticationFilter.class)
                .httpBasic().authenticationEntryPoint(smpSecurityExceptionHandler).and() // username
                .anonymous().authorities(SMPAuthority.S_AUTHORITY_ANONYMOUS.getAuthority()).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, SMP_SECURITY_PATH_CAS_AUTHENTICATE).authenticated()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE).hasAnyAuthority(
                SMPAuthority.S_AUTHORITY_SMP_ADMIN.getAuthority(),
                SMPAuthority.S_AUTHORITY_SERVICE_GROUP.getAuthority(),
                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                .antMatchers(HttpMethod.PUT).hasAnyAuthority(
                SMPAuthority.S_AUTHORITY_SMP_ADMIN.getAuthority(),
                SMPAuthority.S_AUTHORITY_SERVICE_GROUP.getAuthority(),
                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET).permitAll().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/ui/**").hasAnyAuthority(
                SMPAuthority.S_AUTHORITY_SMP_ADMIN.getAuthority(),
                SMPAuthority.S_AUTHORITY_SERVICE_GROUP.getAuthority(),
                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
        ;
    }

    protected void configureSecurityHeaders(HttpSecurity httpSecurity) throws Exception {
        // configure session and csrf headers
        httpSecurity
                .csrf().csrfTokenRepository(csrfTokenRepository).requireCsrfProtectionMatcher(csrfURLMatcher).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                //on authentication, a new HTTP Session is created, the old one is invalidated and the attributes from the old session are copied over.
                .sessionFixation().migrateSession()
                //In order to force only one  concurrent sessions for the same user,
                .maximumSessions(1).and()
                .and();

        // set HstsMAxAge
        Integer maxAge = configurationService.getHttpHeaderHstsMaxAge();
        if (maxAge == null || maxAge < 0) {
            LOG.info("The httpStrictTransportSecurity (HSTS) policy is set for HTTPS/1Y!");
            httpSecurity = httpSecurity.headers()
                    .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .preload(false)
                    .maxAgeInSeconds(31536000).and().and();
        } else if (maxAge == 0) {
            LOG.warn("The httpStrictTransportSecurity (HSTS) policy is disabled!");
            httpSecurity = httpSecurity.headers().httpStrictTransportSecurity().disable().and();
        } else {
            LOG.info("The httpStrictTransportSecurity (HSTS) policy is set to [{}] for http and https!", maxAge);
            httpSecurity = httpSecurity.headers()
                    .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .preload(false)
                    .maxAgeInSeconds(maxAge)
                    .requestMatcher(AnyRequestMatcher.INSTANCE).and().and();
        }
/*
        String contentSecurityPolicy = configurationService.getHttpHeaderContentSecurityPolicy();
        if (StringUtils.isNotBlank(contentSecurityPolicy)) {
            httpSecurity = httpSecurity.headers().contentSecurityPolicy(contentSecurityPolicy).and().and();
        }*/
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.httpFirewall(httpFirewall);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        LOG.info("configureAuthenticationManagerBuilder, set SMP provider ");
        if (configurationService.isSSOEnabledForUserAuthentication()) {
            LOG.info("[CAS] Authentication Provider enabled");
            auth.authenticationProvider(casAuthenticationProvider);
        }
        // add UI authentication provider
        auth.authenticationProvider(smpAuthenticationProviderForUI);
    }

    @Override
    @Bean(name = {SMP_UI_AUTHENTICATION_MANAGER_BEAN})
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public MDCLogRequestFilter getMDCLogRequestFilter() {
        MDCLogRequestFilter filter = new MDCLogRequestFilter();
        return filter;
    }

    @Bean
    public CsrfTokenRepository tokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        return repository;
    }

    @Bean
    public RequestMatcher csrfURLMatcher() {
        URLCsrfMatcher requestMatcher = new URLCsrfMatcher();
        // init pages
        requestMatcher.addIgnoreUrl("^$", HttpMethod.GET);
        requestMatcher.addIgnoreUrl("^(/smp)?/$", HttpMethod.GET);
        requestMatcher.addIgnoreUrl("/favicon(-[0-9x]{2,7})?.(png|ico)$", HttpMethod.GET);
        requestMatcher.addIgnoreUrl("^(/smp)?/(index.html|ui/(#/)?|)$", HttpMethod.GET);
        // Csrf ignore "SMP API 'stateless' calls! (each call is authenticated and session is not used!)"
        requestMatcher.addIgnoreUrl("/.*:+.*(/services/?.*)?", HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT);
        // ignore for login and logout
        requestMatcher.addIgnoreUrl(ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY + "/authentication", HttpMethod.DELETE, HttpMethod.POST);

        requestMatcher.addIgnoreUrl(SMP_SECURITY_PATH_CAS_AUTHENTICATE, HttpMethod.GET);
        // allow all gets
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
     * @return
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
        // WebHttpHandlerBuilder.forwardedHeaderTransformer(ForwardedHeaderTransformer);
        forwardedHeaderTransformer.setRemoveOnly(false);
        return forwardedHeaderTransformer;

    }
}
