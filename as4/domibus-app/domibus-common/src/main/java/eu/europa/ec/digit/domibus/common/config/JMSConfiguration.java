package eu.europa.ec.digit.domibus.common.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

/**
 * JMS related components configuration
 *
 * @author Vincent Dijkstra
 *
 */
@Configuration
@Profile("production")
public class JMSConfiguration {

    /* ---- Constants ---- */
    public final static String JMS_BROKER_URL = "jms.broker.url";
    public final static String JMS_QUEUE_NOTIFICATION_MESSAGE_NAME = "jms.queue.notification.message.name";

    /* ---- Instance Variables ---- */

    @Resource
    private Environment environment = null;

    @Value("#{notificationMessageQueue}")
    private Queue notificationMessageQueue = null;;

    /* ---- Constructors ---- */

    /* ---- Configuration Beans ---- */

    @Bean
    public JndiTemplate jndiTemplate() {
        JndiTemplate jndiTemplate = new JndiTemplate();
        Properties properties = new Properties();
//        properties.put(Context.INITIAL_CONTEXT_FACTORY, environment.getProperty("jms.initial.context.factory"));
//        properties.put(Context.PROVIDER_URL, environment.getProperty("jms.provider.url"));
        jndiTemplate.setEnvironment(properties);
        return jndiTemplate;
    }

    @Bean
    public JndiObjectFactoryBean jmsConnectionFactory() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiTemplate(jndiTemplate());
        jndiObjectFactoryBean.setJndiName(environment.getProperty("jms.connection.factory"));
        return jndiObjectFactoryBean;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory((ConnectionFactory)jmsConnectionFactory().getObject());
        cachingConnectionFactory.setSessionCacheSize(10);
        return cachingConnectionFactory;
    }

    @Bean
    public DestinationResolver destinationResolver() {
        JndiDestinationResolver destinationResolver = new JndiDestinationResolver();
        destinationResolver.setJndiTemplate(jndiTemplate());
        destinationResolver.setCache(Boolean.TRUE);
        return destinationResolver;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setDestinationResolver(destinationResolver());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean(name = "notificationMessageQueue")
    public JndiObjectFactoryBean receptionMessagesQueue() {
        JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiTemplate(jndiTemplate());
        jndiObjectFactoryBean.setJndiName(environment.getProperty(JMS_QUEUE_NOTIFICATION_MESSAGE_NAME));
        return jndiObjectFactoryBean;
    }

    /* ---- Getters and Setters ---- */

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
