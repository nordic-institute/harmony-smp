package eu.eCODEX.submission.jmsdemo;

import org.junit.Test;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kochc01
 * Date: 15.04.14
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class QueueSender {
   
   
    public void sendMessageToDemoQueue() throws NamingException, JMSException {
        
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "net.timewalker.ffmq3.jndi.FFMQInitialContextFactory");
        env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
        InitialContext jndi = new InitialContext(env);
        ConnectionFactory connectionFactory = null;
        Destination dest = null;
        connectionFactory = (ConnectionFactory) jndi.lookup("factory/ConnectionFactory");
        dest = (Destination) jndi.lookup("queue/inQueue");
        Connection connection = null;
        MessageProducer producer = null;
        connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(dest);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        TextMessage message = session.createTextMessage();
        for (int i = 0; i < 5; i++) {
            message.setText("This is message " + (i + 1));
            System.out.println("Sending message: " + message.getText());
            producer.send(message);
        }
        connection.close();
    }

    @Test
    public void sendMapMessageLoopbackToJMSQueue() throws NamingException, JMSException {
        sendMapMessage("UNRELIABLE_LOOPBACK_POSITIVE", "GW1");
    }

    @Test
    public void sendMapMessageToJMSQueue() throws NamingException, JMSException {
        sendMapMessage("UNRELIABLE_POSITIVE", "GW2");
    }

    private void sendMapMessage(String action, String toPartyId)throws NamingException, JMSException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "net.timewalker.ffmq3.jndi.FFMQInitialContextFactory");
        env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
        InitialContext jndi = new InitialContext(env);
        ConnectionFactory connectionFactory = null;
        Destination dest = null;
        connectionFactory = (ConnectionFactory) jndi.lookup("factory/ConnectionFactory");
        dest = (Destination) jndi.lookup("queue/inQueue");
        Connection connection = null;
        MessageProducer producer = null;
        connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(dest);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


        MapMessage msg = session.createMapMessage();
        msg.setStringProperty("Service", "JMSTEST");
        msg.setStringProperty("Action", action); // new action parameter for loopback pmode
        msg.setStringProperty("ConversationID", "");
        msg.setStringProperty("fromPartyID", "GW1");
        msg.setStringProperty("fromPartyIDType", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        msg.setStringProperty("fromRole", "GW");
        msg.setStringProperty("toPartyID", toPartyId); // important!! receiver and sender of message is GW1
        msg.setStringProperty("toPartyIDType", "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
        msg.setStringProperty("toRole", "GW");
        msg.setStringProperty("originalSender", "sending_court_id");
        msg.setStringProperty("finalRecipient", "receiving_court_id");
        msg.setStringProperty("serviceType", "");
        msg.setStringProperty("protocol", "AS4");
        msg.setStringProperty("refToMessageId", "");
        msg.setStringProperty("AgreementRef", "");
        msg.setStringProperty("endPointAddress", "");
        msg.setBytes("payload-1", "<root>abcdefghijklmnopqrstuvwxyz</root>".getBytes());
        msg.setStringProperty("payload-1-MimeContentID", "cid_of_payload_1");
        msg.setStringProperty("payload-1-MimeType", "mimetype_of_payload_1");
        msg.setStringProperty("totalNumberOfPayloads", "1");

        producer.send(msg);

        connection.close();
    }

}

