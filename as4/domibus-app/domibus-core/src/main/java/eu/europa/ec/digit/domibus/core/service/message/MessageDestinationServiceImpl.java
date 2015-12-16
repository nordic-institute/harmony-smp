package eu.europa.ec.digit.domibus.core.service.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.core.util.NotificationFilter;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

@org.springframework.stereotype.Service
public class MessageDestinationServiceImpl implements MessageDestinationService {

	/* ---- Constants ---- */
	private final Logger log = new Logger(getClass());

	/* ---- Instance Variables ---- */

	@Autowired
	@Qualifier ("notificationFilterList")
	private Object notificationFilterList = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	@SuppressWarnings ("unchecked")
	public void destination(MessageBO messageBO, NotificationServiceContext notificationServiceContext) {
        for (NotificationFilter notificationFilter : (List<NotificationFilter>)notificationFilterList) {
            log.info("Notification filter " + notificationFilter.getNotificationFacadeName());
            if (notificationFilter.matches(createUserMessageForFiltering(messageBO))) {
                notificationServiceContext.setDestination(notificationFilter.getNotificationFacadeName());
            }
        }
	}

    private UserMessage createUserMessageForFiltering(MessageBO messageBO) {
        UserMessage userMessage = new UserMessage();
        CollaborationInfo collaborationInfo = new CollaborationInfo();
        Service service = new eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service();
        service.setValue(messageBO.getHeader().getService());
        service.setType(messageBO.getHeader().getServiceType());
        collaborationInfo.setService(service);
        collaborationInfo.setAction(messageBO.getHeader().getAction());
        userMessage.setCollaborationInfo(collaborationInfo);

        PartyInfo partyInfo = new PartyInfo();
        To to = new To();
        PartyId partyId = new PartyId();
        partyId.setValue(messageBO.getHeader().getToParty().getId());
        partyId.setType(messageBO.getHeader().getToParty().getType());
        to.getPartyId().add(partyId);
        partyInfo.setTo(to);

        From from = new From();
        PartyId fromPartyId = new PartyId();
        fromPartyId.setValue(messageBO.getHeader().getFromParty().getId());
        fromPartyId.setType(messageBO.getHeader().getFromParty().getType());
        from.getPartyId().add(fromPartyId);
        partyInfo.setFrom(from);
        userMessage.setPartyInfo(partyInfo);

        return userMessage;
    }

	/* ---- Getters and Setters ---- */

	public Object getNotificationFilterList() {
		return notificationFilterList;
	}

	public void setNotificationFilterList(Object notificationFilterList) {
		this.notificationFilterList = notificationFilterList;
	}

}
