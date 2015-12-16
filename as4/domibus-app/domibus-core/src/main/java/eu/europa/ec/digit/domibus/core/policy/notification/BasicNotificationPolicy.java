package eu.europa.ec.digit.domibus.core.policy.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.digit.domibus.base.dao.ws.WSDAO;
import eu.europa.ec.digit.domibus.common.aggregate.components.Acknowledgement;
import eu.europa.ec.digit.domibus.core.mapper.basic.BasicMessageMapper;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;
import eu.europa.ec.digit.domibus.domain.service.NotificationServiceContext;

@Component
public class BasicNotificationPolicy extends NotificationPolicy {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	
	@Autowired
	private WSDAO<GatewayEnvelope, Acknowledgement> wsDAO = null;
	
	@Autowired
	private BasicMessageMapper basicMessageMapper = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
	@Override
	public void notify(MessageBO message, NotificationServiceContext notificationServiceContext) {
		GatewayEnvelope envelope = this.convert(message, notificationServiceContext);
		wsDAO.submit(envelope, notificationServiceContext);
	}
	
	private GatewayEnvelope convert(MessageBO messageBO, NotificationServiceContext notificationServiceContext) {
		return basicMessageMapper.mapFrom(messageBO);
	}
	
	/* ---- Getters and Setters ---- */

	public WSDAO<GatewayEnvelope, Acknowledgement> getWsDAO() {
		return wsDAO;
	}

	public void setWsDAO(WSDAO<GatewayEnvelope, Acknowledgement> wsDAO) {
		this.wsDAO = wsDAO;
	}

	public BasicMessageMapper getBasicMessageMapper() {
		return basicMessageMapper;
	}

	public void setBasicMessageMapper(BasicMessageMapper basicMessageMapper) {
		this.basicMessageMapper = basicMessageMapper;
	}

}
