package eu.europa.ec.edelivery.smp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;

/**
 * @author Sebastian-Ion TINCU
 */
@Configuration
@ComponentScan("eu.europa.ec.edelivery.smp.conversion")
public class ConversionTestConfig {

    @Bean
    public ConversionServiceFactoryBean conversionService() {
        return new ConversionServiceFactoryBean();
    }
}
