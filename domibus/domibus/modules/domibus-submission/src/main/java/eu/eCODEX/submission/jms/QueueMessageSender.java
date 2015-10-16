/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.eCODEX.submission.jms;


import eu.eCODEX.submission.handler.MessageRetriever;
import eu.eCODEX.submission.validation.exception.ValidationException;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collection;
import java.util.Properties;

/**
 * This class is responsible for processing received messages (from another gateway) and forwarding them to a JMS destination.
 * Please note that this class is implemented as a {@link org.springframework.scheduling.quartz.QuartzJobBean} which is used in quartz scheduler.
 *
 * @author Padraig
 */
public class QueueMessageSender extends QuartzJobBean {

    private static final Logger LOG = Logger.getLogger(QueueMessageSender.class);
    private MessageRetriever<MapMessage> messageRetriever;
    private ConnectionFactory connectionFactory;
    private Destination receivingQueue;

    /**
     * This method checks for pending messages received by another gateway and processes them to a JMS destination
     *
     * @param ctx
     */
    public void executeInternal(JobExecutionContext ctx) {
        try {

            final Collection<String> ids = messageRetriever.listPendingMessages();

            if (!ids.isEmpty() || ids.size() > 0) {
                final String[] messageIds = ids.toArray(new String[ids.size()]);

                Connection connection = null;
                MessageProducer producer = null;

                connection = connectionFactory.createConnection();
                for (String messageId : messageIds) {
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    producer = session.createProducer(receivingQueue);
                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                    MapMessage resMessage = session.createMapMessage();
                    messageRetriever.downloadMessage(messageId, resMessage);
                    producer.send(resMessage);
                    producer.close();
                    session.close();
                }
                connection.close();
            } else {
                LOG.debug("No pending messages to send");
            }

        } catch (JMSException ex) {
            LOG.error(ex);
        } catch (ValidationException e) {
            LOG.error(e);
        }
    }

    /**
     * Setter for messageRetriever, in order to be able to inject the messageRetriever bean.
     *
     * @param messageRetriever
     */
    public void setMessageRetriever(MessageRetriever<MapMessage> messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setReceivingQueue(Destination receivingQueue) {
        this.receivingQueue = receivingQueue;
    }
}