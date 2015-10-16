package eu.eCODEX.submission.jmsdemo;

import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: kochc01
 * Date: 15.04.14
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public class JmsExample implements MessageListener {

    private ConnectionFactory cf;
    private Queue outQueue;

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.cf = connectionFactory;
    }

    public void setOutQueue(Queue outQueue) {
        this.outQueue = outQueue;
    }

    // send the incomming message to the outqueue
    public void onMessage(Message message) {
        try {

            Connection con = this.cf.createConnection();
            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(this.outQueue);
            sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            sender.send(message);
            sender.close();
            session.close();
            con.close();  
        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}