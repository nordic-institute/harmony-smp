package eu.europa.ec.digit.domibus.endpoint.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ErrorHandler;

import eu.europa.ec.digit.domibus.endpoint.handler.JMSErrorHandler;

public class JMSConfiguration {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    @Autowired
    @Qualifier ("connectionFactory")
    private ConnectionFactory connectionFactory = null;

    @Autowired
    @Qualifier ("notificationMessageQueue")
    private Queue notificationMessageQueue = null;

    @Autowired
    @Qualifier ("notificationListener")
    private Object notificationListener = null;

    /* ---- Configuration Beans ---- */
    
    @Bean
    public DefaultMessageListenerContainer notificationMessageContainer() {
        DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setDestination(notificationMessageQueue);
        jmsContainer.setConnectionFactory(connectionFactory);
        jmsContainer.setMessageListener(notificationListener);
        jmsContainer.setErrorHandler(jmsErrorHandler());
        jmsContainer.setSessionTransacted(true);
        jmsContainer.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return jmsContainer;
    }
    
    @Bean
    public ErrorHandler jmsErrorHandler() {
        return new JMSErrorHandler();
    }

    /* ---- Getters and Setters ---- */

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Queue getNotificationMessageQueue() {
		return notificationMessageQueue;
	}

	public void setNotificationMessageQueue(Queue notificationMessageQueue) {
		this.notificationMessageQueue = notificationMessageQueue;
	}

	public Object getNotificationListener() {
		return notificationListener;
	}

	public void setNotificationListener(Object notificationListener) {
		this.notificationListener = notificationListener;
	}



}
