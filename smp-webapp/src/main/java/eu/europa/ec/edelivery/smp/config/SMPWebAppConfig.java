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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.edelivery.smp.error.ServiceErrorControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Created by gutowpa on 11/07/2017.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "eu.europa.ec.edelivery.smp",
        "eu.europa.ec.edelivery.smp.config",
        "eu.europa.ec.edelivery.smp.monitor",
        "eu.europa.ec.edelivery.smp.ui",
        "eu.europa.ec.smp.spi",})
@Import({GlobalMethodSecurityConfig.class,
        ServiceErrorControllerAdvice.class,
        ServicesBeansConfiguration.class})
public class SMPWebAppConfig implements WebMvcConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(SMPWebAppConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.setOrder(HIGHEST_PRECEDENCE)
                .addResourceHandler("/index.html", "/favicon.png", "/favicon.ico").addResourceLocations("/html/");

        registry.setOrder(HIGHEST_PRECEDENCE - 2)
                .addResourceHandler("/ui/rest/").addResourceLocations("/"); // ui rest resources
        registry.setOrder(HIGHEST_PRECEDENCE - 3)
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
        LOG.debug("Register MappingJackson2HttpMessageConverter.");
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
}
