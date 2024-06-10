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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.europa.ec.edelivery.smp.filter.FilterHandler;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import javax.cache.CacheManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * This is the entry point of the DomiSMP application (beans)configuration/setup.
 * <p>
 * The SMPWebAppConfig is initiated from the web.xml.
 * <p>
 * The following configurations: ServicesBeansConfiguration, SMPDatabaseConfig, UISecurityConfigurerAdapter, WSSecurityConfigurerAdapter
 * are configured from the SMPWebAppConfig
 *
 * <table border="1">
 * <tr><th>package</th><th>module</th><th>scan</th></tr>
 * <tr><td>auth</td><td>smp-server-library/smp-webapp</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>config</td><td>smp-server-library/smp-webapp</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>conversion</td><td>smp-server-library</td><td>ServicesBeansConfiguration</td></tr>
 * <tr><td>controller</td><td>smp-webapp</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>cron</td><td>smp-server-library</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>data</td><td>smp-server-library</td><td>SMPDatabaseConfig</td></tr>
 * <tr><td>exceptions</td><td>smp-server-library</td><td>No beans</td></tr>
 * <tr><td>error</td><td>smp-webapp</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>identifiers</td><td>smp-server-library</td><td>No beans</td></tr>
 * <tr><td>logging</td><td>smp-server-library</td><td>No beans</td></tr>
 * <tr><td>monitor</td><td>smp-webapp</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>security</td><td>smp-server-library</td><td>ServicesBeansConfiguration</td></tr>
 * <tr><td>services</td><td>smp-server-library</td><td>ServicesBeansConfiguration</td></tr>
 * <tr><td>servlet</td><td>smp-server-library</td><td>No beans</td></tr>
 * <tr><td>sml</td><td>smp-server-library</td><td>ServicesBeansConfiguration</td></tr>
 * <tr><td>utils</td><td>smp-server-library</td><td>SMPWebAppConfig</td></tr>
 * <tr><td>ui</td><td>smp-webapp</td><td>SMPWebAppConfig</td></tr>
 * </table>
 * <p>
 * <p>
 *  @author gutowpa
 *  @since 3.0.0
 */
@Configuration
@EnableWebMvc
@EnableCaching
@ComponentScan(basePackages = {
        "eu.europa.ec.edelivery.smp.auth",
        "eu.europa.ec.edelivery.smp.config",
        "eu.europa.ec.edelivery.smp.controllers",
        "eu.europa.ec.edelivery.smp.error",
        "eu.europa.ec.edelivery.smp.monitor",
        "eu.europa.ec.edelivery.smp.ui",
        // lib packages
        "eu.europa.ec.edelivery.smp.utils",
        "eu.europa.ec.edelivery.smp.cron",
        // spi properties
        "eu.europa.ec.smp.spi"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SMPWebAppConfig.class)})
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
public class SMPWebAppConfig implements WebMvcConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(SMPWebAppConfig.class);
    private static final int HIGHEST_ORDER = Integer.MAX_VALUE;
    private static final String EHCACHE_CONFIG_LOCATION = "/ehcache-default.xml";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(HIGHEST_ORDER)
                .addResourceHandler("/index.html", "/favicon.png", "/favicon.ico").addResourceLocations("/html/");

        registry.setOrder(HIGHEST_ORDER - 2)
                .addResourceHandler("/ui/rest/").addResourceLocations("/"); // ui rest resources
        registry.setOrder(HIGHEST_ORDER - 3)
                .addResourceHandler("/ui/**").addResourceLocations("/ui/"); // angular pages
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // do not decode path before mapping - that cause problems to identifiers with /
        UrlPathHelper urlPathHelper = configurer.getUrlPathHelper();
        if (urlPathHelper == null) {
            urlPathHelper = new UrlPathHelper();
            configurer.setUrlPathHelper(urlPathHelper);
        }
        urlPathHelper.setUrlDecode(false);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Configure Object Mapper with format date as: "2021-12-01T14:52:00Z"
        LOG.debug("Register MappingJackson2HttpMessageConverter");
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        converter.setObjectMapper(objectMapper);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getDefault());
        objectMapper.setDateFormat(dateFormat);

        converters.add(0, converter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        LOG.debug("Register FilterHandler");
        registry.addFormatterForFieldAnnotation(new FilterHandler());
    }

    @Bean
    public JCacheCacheManager cacheManager() throws URISyntaxException, IOException {
        EhcacheCachingProvider provider = new EhcacheCachingProvider(); //NOSONAR : if this would be closed here (with try-with-resources or in a finally block), it would crash with IllegalStateException everywhere it'll be used further
        ClassLoader classLoader = getClass().getClassLoader();
        //default cache
        final ClassPathResource classPathResource = new ClassPathResource(EHCACHE_CONFIG_LOCATION);

        CacheManager cacheManager = provider.getCacheManager(
                classPathResource.getURL().toURI(),
                classLoader);

        return new JCacheCacheManager(cacheManager);
    }
}
