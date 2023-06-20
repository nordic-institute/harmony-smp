package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationProviderForUI;
import eu.europa.ec.edelivery.smp.auth.URLCsrfIgnoreMatcher;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.error.SMPSecurityExceptionHandler;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SMPCookieWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.SMP_SECURITY_PATH_CAS_AUTHENTICATE;
import static eu.europa.ec.edelivery.smp.config.SMPSecurityConstants.SMP_UI_AUTHENTICATION_MANAGER_BEAN;

/**
 * SMP UI Security configuration
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Order(2)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class UISecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(UISecurityConfigurerAdapter.class);

    SMPAuthenticationProviderForUI smpAuthenticationProviderForUI;
    MDCLogRequestFilter mdcLogRequestFilter;

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
                                       @Lazy HttpFirewall httpFirewall
    ) {
        super(false);
        this.configurationService = configurationService;
        this.smpAuthenticationProviderForUI = smpAuthenticationProviderForUI;
        this.mdcLogRequestFilter = mdcLogRequestFilter;
        this.csrfTokenRepository = csrfTokenRepository;
        this.csrfURLMatcher = csrfURLMatcher;
        this.httpFirewall = httpFirewall;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity = httpSecurity.antMatcher("/ui/**");
        configureSecurityHeaders(httpSecurity);

        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigurer = httpSecurity.exceptionHandling();
        SMPSecurityExceptionHandler smpSecurityExceptionHandler = new SMPSecurityExceptionHandler();

        exceptionHandlingConfigurer.authenticationEntryPoint(smpSecurityExceptionHandler);
        httpSecurity = exceptionHandlingConfigurer
                .accessDeniedHandler(smpSecurityExceptionHandler)

                .and()
                .headers().frameOptions().deny()
                .contentTypeOptions().and()
                .xssProtection().xssProtectionEnabled(true).and()
                .and();
        httpSecurity
                .addFilterAfter(mdcLogRequestFilter, BasicAuthenticationFilter.class)
                .httpBasic().authenticationEntryPoint(smpSecurityExceptionHandler).and() // username
                .anonymous().authorities(SMPAuthority.S_AUTHORITY_ANONYMOUS.getAuthority()).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, SMP_SECURITY_PATH_CAS_AUTHENTICATE).authenticated()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE).hasAnyAuthority(
                        SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                        SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                .antMatchers(HttpMethod.PUT).hasAnyAuthority(
                        SMPAuthority.S_AUTHORITY_USER.getAuthority(),
                        SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET).permitAll().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/ui/**").hasAnyAuthority(
                        SMPAuthority.S_AUTHORITY_USER.getAuthority(),
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

        String contentSecurityPolicy = configurationService.getHttpHeaderContentSecurityPolicy();
        if (StringUtils.isNotBlank(contentSecurityPolicy)) {
            httpSecurity = httpSecurity.headers().contentSecurityPolicy(contentSecurityPolicy).and().and();
        }
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.httpFirewall(httpFirewall);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        LOG.info("configureAuthenticationManagerBuilder, set SMP provider ");
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
        forwardedHeaderTransformer.setRemoveOnly(false);
        return forwardedHeaderTransformer;

    }
}
