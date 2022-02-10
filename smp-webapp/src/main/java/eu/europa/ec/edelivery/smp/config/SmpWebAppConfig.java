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

import eu.europa.ec.edelivery.smp.error.ErrorMappingControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Created by gutowpa on 11/07/2017.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "eu.europa.ec.edelivery.smp.controllers",
        "eu.europa.ec.edelivery.smp.validation",
        "eu.europa.ec.edelivery.smp.conversion",
        "eu.europa.ec.edelivery.smp.monitor",
        "eu.europa.ec.edelivery.smp.ui"})
@Import({GlobalMethodSecurityConfig.class, ErrorMappingControllerAdvice.class})
public class SmpWebAppConfig implements WebMvcConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(SmpWebAppConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.setOrder(HIGHEST_PRECEDENCE)
                .addResourceHandler("/index.html", "/favicon-16x16.png").addResourceLocations("/static_resources/");

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
}
