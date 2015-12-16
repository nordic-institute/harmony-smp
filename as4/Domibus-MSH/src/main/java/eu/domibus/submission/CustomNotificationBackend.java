package eu.domibus.submission;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import eu.domibus.common.MessageStatus;
import eu.domibus.common.model.logging.ErrorLogEntry;
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.transformer.impl.JMSMessageTransformer;
import eu.domibus.submission.validation.exception.ValidationException;

public class CustomNotificationBackend implements BackendConnector<MapMessage, MapMessage> {

    private static final Log LOG = LogFactory.getLog(CustomNotificationBackend.class);

    private final String name;
    private ConnectionFactory cf;
    private Destination receivingQueue;

//    @Autowired
//    private ErrorLogDao errorLogDao;
    @Override
    public Future<Boolean> messageNotification(MessageMetadata metadata) {
        final Future<Boolean> messageDelivered;
        messageDelivered = this.deliverMessage(metadata);
        return messageDelivered;
    }

    @Override
    @Async
    public Future<Boolean> deliverMessage(MessageMetadata metadata) {
        if (metadata.getType() == MessageMetadata.Type.INBOUND) {
            return new AsyncResult<>(this.submitToBackend(metadata));
        } else {
            //return new AsyncResult<>(this.submitErrorMessage(metadata.getMessageId()));
            return null;
        }
    }

//	private Boolean submitErrorMessage(String messageId) {
//        Connection connection;
//                MessageProducer producer;
//        List<ErrorLogEntry> errors = this.errorLogDao.getUnnotifiedErrorsForMessage(messageId);
//
//                try {
//                    connection = this.cf.createConnection();
//                    final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//                    producer = session.createProducer(this.receivingQueue);
//                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//                    final MapMessage resMessage = session.createMapMessage();
//                    for (int i = 0 ; i< errors.size(); ++i){
//                        resMessage.setString(String.valueOf(i), errors.get(i).toString());
//                        errors.get(i).setNotified(new Date());
//                        this.errorLogDao.update(errors.get(i));
//                    }
//
//                    producer.send(resMessage);
//                    producer.close();
//                    session.close();
//
//                    connection.close();
//                } catch (JMSException e) {
//                	CustomNotificationBackend.LOG.error("", e);
//                    return false;
//                }
//                return true;
//    }
    private Boolean submitToBackend(final MessageMetadata metadata) {

        Connection connection;
        MessageProducer producer;

        try {
            connection = this.cf.createConnection();
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(this.receivingQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            final MapMessage resMessage = session.createMapMessage();

            resMessage.setStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID, metadata.getMessageId());

            producer.send(resMessage);
            producer.close();
            session.close();

            connection.close();
        } catch (JMSException e) {
            CustomNotificationBackend.LOG.error("", e);
            return false;
        }
        return true;
    }

    public CustomNotificationBackend(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isResponsible(MessageMetadata metadata) {
        return true;
    }

    @Override
    public MessageSubmissionTransformer<MapMessage> getMessageSubmissionTransformer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MessageRetrievalTransformer<MapMessage> getMessageRetrievalTransformer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String submit(MapMessage message) throws ValidationException,
            TransformationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MapMessage downloadMessage(String messageId, MapMessage target)
            throws ValidationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> listPendingMessages() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MessageStatus getMessageStatus(String messageId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ErrorLogEntry> getErrorsForMessage(String messageId) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setReceivingQueue(final Destination receivingQueue) {
        this.receivingQueue = receivingQueue;
    }

    public void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.cf = connectionFactory;
    }
}
