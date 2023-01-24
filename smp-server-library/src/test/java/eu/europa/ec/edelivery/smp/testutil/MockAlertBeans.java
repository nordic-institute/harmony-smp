package eu.europa.ec.edelivery.smp.testutil;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Override JavaMailSenderImpl bean to control mail sender method invocations.
 */
@Configuration
public class MockAlertBeans {
    public JavaMailSenderImpl javaMailSender = Mockito.spy(new JavaMailSenderImpl());

    @Bean
    @Primary
    public JavaMailSenderImpl getJavaMailSender(){
        return javaMailSender;
    };
}
