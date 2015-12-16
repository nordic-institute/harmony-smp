package eu.europa.ec.digit.domibus.domain.gateway;

import org.apache.commons.lang.builder.ToStringBuilder;

import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayBody;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayHeader;

public class GatewayEnvelope {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	
	private GatewayHeader gatewayHeader = null;
	private GatewayBody gatewayBody = null;

	/* ---- Constructors ---- */
	
	public GatewayEnvelope(GatewayHeader gatewayHeader, GatewayBody gatewayBody) {
		this.gatewayHeader = gatewayHeader;
		this.gatewayBody = gatewayBody;
	}
	
	/* ---- Business Methods ---- */
	
	public String toString() {
		return new ToStringBuilder(this)
		.appendSuper(super.toString())
		.append("header", this.gatewayHeader)
		.append("body", this.gatewayBody)
		.toString();
	}

	/* ---- Getters and Setters ---- */

	public GatewayHeader getGatewayHeader() {
		return gatewayHeader;
	}

	public void setGatewayHeader(GatewayHeader gatewayHeader) {
		this.gatewayHeader = gatewayHeader;
	}

	public GatewayBody getGatewayBody() {
		return gatewayBody;
	}

	public void setGatewayBody(GatewayBody gatewayBody) {
		this.gatewayBody = gatewayBody;
	}

}
