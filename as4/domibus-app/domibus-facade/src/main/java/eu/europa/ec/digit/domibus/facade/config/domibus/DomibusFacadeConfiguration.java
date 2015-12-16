package eu.europa.ec.digit.domibus.facade.config.domibus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import eu.europa.ec.digit.domibus.core.config.domibus.DomibusCoreConfiguration;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;
import eu.europa.ec.digit.domibus.facade.config.FacadeConfiguration;
import eu.europa.ec.digit.domibus.facade.policy.domibus.notification.DomibusNotificationPolicy;
import eu.europa.ec.digit.domibus.facade.policy.domibus.retrieval.DomibusBasicRetrievalPolicy;
import eu.europa.ec.digit.domibus.facade.policy.domibus.submission.DomibusBasicSubmissionPolicy;
import eu.europa.ec.digit.domibus.facade.service.message.MessageFacade;
import eu.europa.ec.digit.domibus.facade.service.message.MessageFacadeImpl;

@Configuration
@Import ({
	DomibusCoreConfiguration.class
})
public class DomibusFacadeConfiguration extends FacadeConfiguration {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	@Autowired
	private DomibusNotificationPolicy domibusNotificationPolicy = null;
	
	@Autowired
	private DomibusBasicSubmissionPolicy domibusBasicSubmissionPolicy = null;
	
	@Autowired
	private DomibusBasicRetrievalPolicy domibusBasicRetrievalPolicy = null;
	
	/* ---- Configuration Beans ---- */
	@Bean (name = "messageBasicFacade")
	public MessageFacade<GatewayEnvelope> messageGatewayFacade() {
		MessageFacadeImpl<GatewayEnvelope> messageFacade = new MessageFacadeImpl<GatewayEnvelope>();
		messageFacade.setSubmissionPolicy(domibusBasicSubmissionPolicy);
		messageFacade.setRetrievalPolicy(domibusBasicRetrievalPolicy);
		return messageFacade;
	}

	/* ---- Getters and Setters ---- */
	public DomibusNotificationPolicy getDomibusNotificationPolicy() {
		return domibusNotificationPolicy;
	}

	public void setDomibusNotificationPolicy(DomibusNotificationPolicy domibusNotificationPolicy) {
		this.domibusNotificationPolicy = domibusNotificationPolicy;
	}

	public DomibusBasicSubmissionPolicy getDomibusBasicSubmissionPolicy() {
		return domibusBasicSubmissionPolicy;
	}

	public void setDomibusBasicSubmissionPolicy(DomibusBasicSubmissionPolicy domibusBasicSubmissionPolicy) {
		this.domibusBasicSubmissionPolicy = domibusBasicSubmissionPolicy;
	}

	public DomibusBasicRetrievalPolicy getDomibusBasicRetrievalPolicy() {
		return domibusBasicRetrievalPolicy;
	}

	public void setDomibusBasicRetrievalPolicy(DomibusBasicRetrievalPolicy domibusBasicRetrievalPolicy) {
		this.domibusBasicRetrievalPolicy = domibusBasicRetrievalPolicy;
	}

}
