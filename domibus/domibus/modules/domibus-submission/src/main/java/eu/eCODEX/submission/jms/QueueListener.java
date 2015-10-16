/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.eCODEX.submission.jms;

import eu.eCODEX.submission.handler.MessageSubmitter;
import eu.eCODEX.submission.validation.exception.ValidationException;
import org.apache.log4j.Logger;

import javax.jms.*;

/**
 * This class is responsible for receiving and processing of incoming JMS Messages
 *
 * @author Padraig
 */
public class QueueListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(QueueListener.class);

    private MessageSubmitter<MapMessage> messageSubmitter;

    private ConnectionFactory cf;
    private Queue outQueue;


    /**
     * This method is called when a message was received at the incoming queue
     *
     * @param message The incoming JMS Message
     */
    @Override
    public void onMessage(Message message) {

        MapMessage map = (MapMessage) message;

        try {

            String messageID = this.submitMessage(map, messageSubmitter);

            Connection con = cf.createConnection();
            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MapMessage res = session.createMapMessage();

            res.setStringProperty("messageId", messageID);
            res.setJMSCorrelationID(map.getJMSCorrelationID());
            MessageProducer sender = session.createProducer(outQueue);
            sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            sender.send(res);
            sender.close();
            session.close();
            con.close();

        } catch (JMSException ex) {
            LOG.error("Error while sending response to queue", ex);
        }
    }

    /**
     * Handles the submission of message via an instance of {@link MessageSubmitter}
     *
     * @param mapMessage The incoming {@link javax.jms.MapMessage} before transformation
     * @param messageSubmitter Instance of a {@link eu.eCODEX.submission.handler.MessageSubmitter}
     * @return the ebMS message id of the requested message
     */
    private String submitMessage(MapMessage mapMessage, MessageSubmitter<MapMessage> messageSubmitter) {
        String messageID = "";
        try {
            messageID = messageSubmitter.submit(mapMessage);

        } catch (ValidationException ex) {
            LOG.error("Error during validation of message", ex);
        }

        return messageID;
    }

    /**
     * Setter for messageSubmitter, in order to be able to inject the messageSubmitter bean.
     *
     * @param messageSubmitter
     */
    public void setMessageSubmitter(MessageSubmitter<MapMessage> messageSubmitter) {
        this.messageSubmitter = messageSubmitter;
    }

    /**
     * Setter for connectionFactory, in order to be able to inject the connectionFactory bean.
     *
     * @param connectionFactory
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.cf = connectionFactory;
    }

    /**
     * Setter for outQueue, in order to be able to inject the outQueue bean.
     *
     * @param outQueue
     */
    public void setOutQueue(Queue outQueue) {
        this.outQueue = outQueue;
    }

}
