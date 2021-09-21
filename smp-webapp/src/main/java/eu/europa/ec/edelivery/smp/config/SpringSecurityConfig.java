/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.security.BlueCoatAuthenticationFilter;
import eu.europa.ec.edelivery.security.EDeliveryX509AuthenticationFilter;
import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationProvider;
import eu.europa.ec.edelivery.smp.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.auth.URLCsrfMatcher;
import eu.europa.ec.edelivery.smp.error.SpringSecurityExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Created by gutowpa on 12/07/2017.
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
//@ImportResource("classpath:spring-security.xml")
@ComponentScan("eu.europa.ec.edelivery.smp.auth")
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(SpringSecurityConfig.class);

    SMPAuthenticationProvider smpAuthenticationProvider;
    BlueCoatAuthenticationFilter blueCoatAuthenticationFilter;
    EDeliveryX509AuthenticationFilter x509AuthenticationFilter;
    CsrfTokenRepository csrfTokenRepository;
    RequestMatcher csrfURLMatcher;

    @Value("${authentication.blueCoat.enabled:false}")
    boolean clientCertEnabled;
    @Value("${encodedSlashesAllowedInUrl:true}")
    boolean encodedSlashesAllowedInUrl;

    /**
     * Initialize beans. Use lazy initialization for filter to avoid circular dependencies
     *
     * @param smpAuthenticationProvider
     * @param blueCoatAuthenticationFilter
     * @param x509AuthenticationFilter
     */
    @Autowired
    public SpringSecurityConfig(SMPAuthenticationProvider smpAuthenticationProvider,
                                @Lazy BlueCoatAuthenticationFilter blueCoatAuthenticationFilter,
                                @Lazy EDeliveryX509AuthenticationFilter x509AuthenticationFilter,
                                @Lazy CsrfTokenRepository csrfTokenRepository,
                                @Lazy RequestMatcher csrfURLMatcher) {
        super(false);
        this.smpAuthenticationProvider = smpAuthenticationProvider;
        this.blueCoatAuthenticationFilter = blueCoatAuthenticationFilter;
        this.x509AuthenticationFilter = x509AuthenticationFilter;
        this.csrfTokenRepository = csrfTokenRepository;
        this.csrfURLMatcher = csrfURLMatcher;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // prepare filters
        blueCoatAuthenticationFilter.setBlueCoatEnabled(clientCertEnabled);

        httpSecurity
                .csrf().csrfTokenRepository(csrfTokenRepository).requireCsrfProtectionMatcher(csrfURLMatcher).and()
                .exceptionHandling().authenticationEntryPoint(new SpringSecurityExceptionHandler()).and()
                .headers().frameOptions().deny().contentTypeOptions().and().xssProtection().xssProtectionEnabled(true).and().and()

                .addFilter(blueCoatAuthenticationFilter)
                .addFilter(x509AuthenticationFilter)
                .httpBasic()
                .and() // username
                .anonymous().authorities(SMPAuthority.S_AUTHORITY_ANONYMOUS.getAuthority()).and()
                .authorizeRequests().antMatchers(HttpMethod.DELETE, "/ui/rest/security/authentication").permitAll()
                .antMatchers(HttpMethod.POST, "/ui/rest/security/authentication").permitAll()
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
                .authorizeRequests().antMatchers(HttpMethod.GET, "/ui/").hasAnyAuthority(
                SMPAuthority.S_AUTHORITY_SMP_ADMIN.getAuthority(),
                SMPAuthority.S_AUTHORITY_SERVICE_GROUP.getAuthority(),
                SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority()).and()
        ;


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.httpFirewall(smpHttpFirewall());
    }


    @Bean
    public HttpFirewall smpHttpFirewall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(encodedSlashesAllowedInUrl);
        return firewall;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        LOG.info("configureAuthenticationManagerBuilder, set SMP provider ");
        auth.authenticationProvider(smpAuthenticationProvider);
    }

    @Override
    @Bean(name = {BeanIds.AUTHENTICATION_MANAGER, "smpAuthenticationManager"})
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BlueCoatAuthenticationFilter getClientCertAuthenticationFilter(@Qualifier("smpAuthenticationManager") AuthenticationManager authenticationManager) {
        BlueCoatAuthenticationFilter blueCoatAuthenticationFilter = new BlueCoatAuthenticationFilter();
        blueCoatAuthenticationFilter.setAuthenticationManager(authenticationManager);
        return blueCoatAuthenticationFilter;
    }

    @Bean
    public EDeliveryX509AuthenticationFilter getEDeliveryX509AuthenticationFilter(@Qualifier("smpAuthenticationManager") AuthenticationManager authenticationManager) {
        EDeliveryX509AuthenticationFilter x509AuthenticationFilter = new EDeliveryX509AuthenticationFilter();
        x509AuthenticationFilter.setAuthenticationManager(authenticationManager);
        return x509AuthenticationFilter;
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
        requestMatcher.addIgnoreUrl("^/$", HttpMethod.GET);
        requestMatcher.addIgnoreUrl("favicon.ico$", HttpMethod.GET);
        requestMatcher.addIgnoreUrl("^/(index.html|ui/(#/)?|)$", HttpMethod.GET);
        // Csrf ignore "SMP API 'stateless' calls! (each call is authenticated and session is not used!)"
        requestMatcher.addIgnoreUrl("/.*::.*(/services/?.*)?", HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT);
        // ignore for login and logout
        requestMatcher.addIgnoreUrl("/ui/rest/security/authentication", HttpMethod.DELETE, HttpMethod.POST);
        // allow all gets
        requestMatcher.addIgnoreUrl("/ui/.*", HttpMethod.GET);
        // altternative fine tuned
        //requestMatcher.addIgnoreUrl("/ui/(index.html|styles.*|runtime.*|polyfills.*|main.*)", HttpMethod.GET);
        //requestMatcher.addIgnoreUrl("/ui/.*(\\.html|\\.css|\\.js)$", HttpMethod.GET);
        //requestMatcher.addIgnoreUrl("/ui/assets/.*", HttpMethod.GET); // allow to retrieve assets
        //requestMatcher.addIgnoreUrl("/ui/rest/(domain|search).*", HttpMethod.GET); // public methods
        //requestMatcher.addIgnoreUrl("/ui/rest/application/(info|rootContext|name)", HttpMethod.GET); // public methods
        // monitor
        requestMatcher.addIgnoreUrl("/monitor/is-alive", HttpMethod.GET);

        return requestMatcher;
    }
}
