package eu.europa.ec.digit.domibus.base.policy.domibus;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.springframework.stereotype.Component;

import eu.europa.ec.digit.domibus.base.policy.NotificationEndpointPolicy;
import eu.europa.ec.digit.domibus.common.aggregate.components.Acknowledgement;
import eu.europa.ec.digit.domibus.common.exception.DomibusParsingException;
import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;
import eu.europa.ec.digit.domibus.wsdl.endpoint.basic.DomibusBasicInterface;
import eu.europa.ec.digit.domibus.wsdl.endpoint.basic.FaultResponse;

@Component
public class BasicNotificationEndpointPolicy extends NotificationEndpointPolicy<GatewayEnvelope, Acknowledgement> {

	/* ---- Constants ---- */
	
	public final static String NAMESPACE_URI = "http://ec.europa.eu/digit/domibus/wsdl/endpoint/basic";
	public final static String LOCAL_PART = "DomibusBasicService";

	/* ---- Instance Variables ---- */
	
	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
	@Override
	public Acknowledgement submit(GatewayEnvelope envelope) {
		try {
			// Create client service
			URL wsdlURL = new URL(environment.getProperty("basic.endpoint.uri"));
			QName qName = new QName(NAMESPACE_URI, LOCAL_PART);
			Service service = Service.create(wsdlURL, qName);
			DomibusBasicInterface client = service.getPort(DomibusBasicInterface.class);

			// Invoke request
			return client.submit(envelope.getGatewayHeader(), envelope.getGatewayBody());
		} catch (MalformedURLException e) {
			throw new DomibusParsingException(
				"message.domibus.parsing.error.ws.002",
				environment.getProperty("basic.endpoint.uri"));
		} catch (FaultResponse e) {
			throw new DomibusProgramException(
				"message.domibus.program.error.ws.005",
				environment.getProperty("basic.endpoint.uri"));
		}
	}
	/* ---- Getters and Setters ---- */

}
