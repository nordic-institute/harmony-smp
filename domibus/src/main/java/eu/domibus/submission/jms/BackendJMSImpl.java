/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.submission.jms;

import eu.domibus.common.dao.ErrorLogDao;
import eu.domibus.common.model.logging.ErrorLogEntry;
import eu.domibus.submission.AbstractBackendConnector;
import eu.domibus.submission.MessageMetadata;
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.jms.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * TODO: add class description
 */
public class BackendJMSImpl extends AbstractBackendConnector<MapMessage, MapMessage> implements MessageListener {

    private static final Log LOG = LogFactory.getLog(BackendJMSImpl.class);

    private ConnectionFactory cf;
    private Queue outQueue;
    private Destination receivingQueue;

    @Autowired
    private ErrorLogDao errorLogDao;

    @Autowired
    private MessageRetrievalTransformer<MapMessage> messageRetrievalTransformer;

    @Autowired
    private MessageSubmissionTransformer<MapMessage> messageSubmissionTransformer;

    public BackendJMSImpl(String name) {
        super(name);
    }

    @Override
    public MessageSubmissionTransformer<MapMessage> getMessageSubmissionTransformer() {
        return this.messageSubmissionTransformer;
    }

    @Override
    public MessageRetrievalTransformer<MapMessage> getMessageRetrievalTransformer() {
        return this.messageRetrievalTransformer;
    }

    @Override
    public boolean isResponsible(final MessageMetadata metadata) {
        return false;
    }

    @Override
    @Async
    public Future<Boolean> deliverMessage(final MessageMetadata metadata) {
        if(metadata.getType()== MessageMetadata.Type.INBOUND){
        return new AsyncResult<>(this.submitToBackend(metadata.getMessageId()));     }
        else{
            return new AsyncResult<>(this.submitErrorMessage(metadata.getMessageId()));
        }
    }

    private Boolean submitErrorMessage(String messageId) {
        Connection connection;
                MessageProducer producer;
        List<ErrorLogEntry> errors = this.errorLogDao.getUnnotifiedErrorsForMessage(messageId);

                try {
                    connection = this.cf.createConnection();
                    final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    producer = session.createProducer(this.receivingQueue);
                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                    final MapMessage resMessage = session.createMapMessage();
                    for (int i = 0 ; i< errors.size(); ++i){
                        resMessage.setString(String.valueOf(i), errors.get(i).toString());
                        errors.get(i).setNotified(new Date());
                        this.errorLogDao.update(errors.get(i));
                    }

                    producer.send(resMessage);
                    producer.close();
                    session.close();

                    connection.close();
                } catch (JMSException e) {
                    BackendJMSImpl.LOG.error("", e);
                    return false;
                }
                return true;
    }

    private Boolean submitToBackend(final String messageId) {
        Connection connection;
        MessageProducer producer;

        try {
            connection = this.cf.createConnection();
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(this.receivingQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            final MapMessage resMessage = session.createMapMessage();
            this.downloadMessage(messageId, resMessage);
            producer.send(resMessage);
            producer.close();
            session.close();

            connection.close();
        } catch (JMSException | ValidationException e) {
            BackendJMSImpl.LOG.error("", e);
            return false;
        }
        return true;
    }


    /**
     * This method is called when a message was received at the incoming queue
     *
     * @param message The incoming JMS Message
     */
    @Override
    public void onMessage(final Message message) {
        final MapMessage map = (MapMessage) message;
        try {
            final Connection con = this.cf.createConnection();
            final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MapMessage res = session.createMapMessage();
            try {
                final String messageID = this.submit(map);
                res.setStringProperty("messageId", messageID);
            } catch (final TransformationException | ValidationException e) {
                BackendJMSImpl.LOG.error("Exception occurred: ", e);
                res.setString("ErrorMessage", e.getMessage());
            }

            res.setJMSCorrelationID(map.getJMSCorrelationID());
            final MessageProducer sender = session.createProducer(this.outQueue);
            sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            sender.send(res);
            sender.close();
            session.close();
            con.close();

        } catch (final JMSException ex) {
            BackendJMSImpl.LOG.error("Error while sending response to queue", ex);

        }
    }


    /**
     * Setter for connectionFactory, in order to be able to inject the connectionFactory bean.
     *
     * @param connectionFactory
     */
    public void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.cf = connectionFactory;
    }

    /**
     * Setter for outQueue, in order to be able to inject the outQueue bean.
     *
     * @param outQueue
     */
    public void setOutQueue(final Queue outQueue) {
        this.outQueue = outQueue;
    }


    /**
     * This method checks for pending messages received by another gateway and processes them to a JMS destination
     *
     * @param ctx
     */
    public void executeInternal(final JobExecutionContext ctx) {
        try {

            final Collection<String> ids = this.messageRetriever.listPendingMessages();

            if (!ids.isEmpty() || ids.size() > 0) {
                final String[] messageIds = ids.toArray(new String[ids.size()]);

                Connection connection;
                MessageProducer producer;

                connection = this.cf.createConnection();
                for (final String messageId : messageIds) {
                    final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    producer = session.createProducer(this.receivingQueue);
                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);
                    final MapMessage resMessage = session.createMapMessage();
                    this.downloadMessage(messageId, resMessage);
                    producer.send(resMessage);
                    producer.close();
                    session.close();
                }
                connection.close();
            } else {
                BackendJMSImpl.LOG.debug("No pending messages to send");
            }

        } catch (final JMSException | ValidationException ex) {
            BackendJMSImpl.LOG.error(ex);
        }
    }


    public void setReceivingQueue(final Destination receivingQueue) {
        this.receivingQueue = receivingQueue;
    }
}
