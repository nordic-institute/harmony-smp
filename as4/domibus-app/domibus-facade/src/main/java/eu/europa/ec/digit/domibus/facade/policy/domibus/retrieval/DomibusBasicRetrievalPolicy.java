package eu.europa.ec.digit.domibus.facade.policy.domibus.retrieval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.digit.domibus.core.service.message.MessageConversionService;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;
import eu.europa.ec.digit.domibus.facade.policy.domibus.RetrievalPolicy;

@Component
public class DomibusBasicRetrievalPolicy extends RetrievalPolicy<GatewayEnvelope> {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	
	@Autowired
	private MessageConversionService messageConversionService = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
	@Override
	protected void log(MessageBO messageObject) {

	}

	@Override
	protected GatewayEnvelope convert(MessageBO messageObject) {
		return messageConversionService.convertBasic(messageObject);
	}

	@Override
	protected void send(GatewayEnvelope message) {
		// TODO Auto-generated method stub

	}

	public MessageConversionService getMessageConversionService() {
		return messageConversionService;
	}

	public void setMessageConversionService(MessageConversionService messageConversionService) {
		this.messageConversionService = messageConversionService;
	}
	
	/* ---- Getters and Setters ---- */

}
