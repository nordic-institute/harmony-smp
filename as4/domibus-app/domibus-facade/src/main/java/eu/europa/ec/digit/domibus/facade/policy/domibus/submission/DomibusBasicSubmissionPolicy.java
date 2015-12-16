package eu.europa.ec.digit.domibus.facade.policy.domibus.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.digit.domibus.core.service.message.MessageConversionService;
import eu.europa.ec.digit.domibus.domain.domibus.MessageBO;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;
import eu.europa.ec.digit.domibus.facade.policy.domibus.SubmissionPolicy;

@Component
public class DomibusBasicSubmissionPolicy extends SubmissionPolicy<GatewayEnvelope> {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	
	@Autowired
	private MessageConversionService messageConversionService = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
	@Override
	protected MessageBO convert(GatewayEnvelope message) {
		return messageConversionService.convertBasic(message);
	}

	@Override
	protected void log(MessageBO messageObject) {

	}

	@Override
	protected GatewayEnvelope convert(MessageBO messageObject) {
		return messageConversionService.convertBasic(messageObject);
	}

	@Override
	protected void send(GatewayEnvelope message) {

	}
	/* ---- Getters and Setters ---- */

	public MessageConversionService getMessageConversionService() {
		return messageConversionService;
	}

	public void setMessageConversionService(MessageConversionService messageConversionService) {
		this.messageConversionService = messageConversionService;
	}

}
