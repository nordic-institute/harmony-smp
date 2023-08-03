package eu.europa.ec.springboot.smp;


import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;


/**
 * Entry point of the spring boot application.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
public class SMPApplication implements ApplicationRunner {

    private static final String APPLICATION_NAME = "smp.war";

    private static final Logger LOG = LoggerFactory.getLogger(SMPApplication.class);

    private static ConfigurableApplicationContext APPLICATION_CONTEXT;

    @Bean
    public TomcatServletWebServerFactory servletContainerFactory() {
        return new TomcatServletWebServerFactory() {

            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                File appFolder = new File(tomcat.getServer().getCatalinaBase(), "webapps");
                appFolder.mkdirs();
                Context appContext = null;
                try {
                    appContext = tomcat.addWebapp("/smp", Thread.currentThread().getContextClassLoader().getResource(APPLICATION_NAME));
                } catch (IOException e) {
                    LOG.error("ERROR reading the resource [" + APPLICATION_NAME + "]", e);
                    System.exit(-1);
                }
                appContext.setParentClassLoader(Thread.currentThread().getContextClassLoader());
                return super.getTomcatWebServer(tomcat);
            }
        };
    }


    /**
     * Entry point if the sprint boot application
     *
     * @param args - application parameters
     */
    public static void main(String... args) {
        // validate parameters
        LOG.info("Start the SMP with parameters: [{}].", String.join(",", args));
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        // start spring boot application
        APPLICATION_CONTEXT = SpringApplication.run(SMPApplication.class, args);
    }


    /**
     * Spring boot startup Callback method invoked after springboot initialization
     *
     * @param args - Application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
    }

}
