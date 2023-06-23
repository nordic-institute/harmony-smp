package eu.europa.ec.edelivery.smp.config;

import freemarker.cache.ClassTemplateLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
@ComponentScan(basePackages = {
        "eu.europa.ec.edelivery.smp.data.dao",
        "eu.europa.ec.edelivery.smp.conversion",
        "eu.europa.ec.edelivery.smp.security",
        "eu.europa.ec.edelivery.smp.services",
        "eu.europa.ec.edelivery.smp.sml",
        "eu.europa.ec.edelivery.smp.conversion"})
public class ServicesBeansConfiguration {

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean() {
        final FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean =
                new FreeMarkerConfigurationFactoryBean();
        freeMarkerConfigurationFactoryBean.setPreTemplateLoaders(new ClassTemplateLoader(ServicesBeansConfiguration.class, "/alert-mail-templates"));
        return freeMarkerConfigurationFactoryBean;
    }
}
