package eu.europa.ec.digit.domibus.endpoint.listener.domibus;


import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.domibus.submission.transformer.impl.JMSMessageTransformer;
import eu.europa.ec.digit.domibus.common.exception.DomibusParsingException;
import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;
import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.common.log.Severity;
import eu.europa.ec.digit.domibus.facade.service.notification.NotificationFacade;

@Transactional
@Service
public class NotificationListener implements MessageListener {

    /* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

	/* ---- Instance Variables ---- */

	@Autowired
	private NotificationFacade notificationFacade = null;
	
    /* ---- Constructors ---- */

	public NotificationListener() {
		log.info("domibus-endpoint: notificationListener");
	}
    /* ---- Business Methods ---- */

    @Override
    public void onMessage(Message message) {
    	if (message instanceof MapMessage) {
    		try {
				MapMessage mapMessage = (MapMessage)message;
				String messageIdentifier = mapMessage.getStringProperty(JMSMessageTransformer.SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID);
				notificationFacade.notify(messageIdentifier);
			} catch (JMSException exception) {
				throw new DomibusParsingException("message.domibus.parsing.error.jms.001");
			}
    	} else {
    		log.businessLog(Severity.ERROR, LogEvent.BUS_NOTIFY_MESSAGE_FAILED, "No MapMessage in message queue");
    		throw new DomibusProgramException("message.domibus.program.error.jms.001");
    	}
    }

    /* ---- Getters and Setters ---- */

	public NotificationFacade getNotificationFacade() {
		return notificationFacade;
	}

	public void setNotificationFacade(NotificationFacade notificationFacade) {
		this.notificationFacade = notificationFacade;
	}
}
