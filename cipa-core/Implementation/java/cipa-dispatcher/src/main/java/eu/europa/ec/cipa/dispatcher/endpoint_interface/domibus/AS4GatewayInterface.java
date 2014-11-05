package eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus;

import eu.domibus.ebms3.config.PModePool;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus.service.AS4PModeService;
import eu.europa.ec.cipa.dispatcher.exception.DispatcherConfigurationException;

public class AS4GatewayInterface implements IAS4GatewayInterface {

	public final static String PMODE_ROLE = "GW";
	public final static String ID_TYPE = "urn:oasis:names:tc:ebcore:partyid-type:iso3166-1";
	public final static String MPC = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC";
	public final static String MEP_NAME = "One-Way/Push";
	public final static String SOAP_VERSION = "1.2";
	public AS4PModeService pmodeService = new AS4PModeService();

	/**
	 * <tns:Binding name="CZ_EC_EPO_Form_E"> <tns:MEP name="One-Way/Push">
	 * <tns:Leg number="1" producer="CZ-GW" mpc=
	 * "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC"
	 * userService="EPO_Form_E_EC_GW" security="sign-body-header_CZ_EC">
	 * <tns:Endpoint address=
	 * "https://webgate.acceptance.ec.europa.eu/eprior/Phase2a/holodeck/services/msh"
	 * soapVersion="1.2"/> <tns:As4Receipt method="response">
	 * <tns:As4Reliability duplicateElimination="true" maxRetries="3"
	 * interval="5" shutdown="10"/> </tns:As4Receipt> </tns:Leg> </tns:MEP>
	 * </tns:Binding>
	 * 
	 */
	@Override
	public void createPartner(String senderGatewayId, String receiverGatewayId, String processId, String documentId, String receiverGWUlr)
			throws DispatcherConfigurationException {
		pmodeService.createPartner(senderGatewayId, receiverGatewayId, processId, documentId, receiverGWUlr);
	}

	public PModePool getPmodePool() {
		return pmodeService.getPmodePool();
	}

	public void setPmodePool(PModePool pmodePool) {
		pmodeService.setPmodePool(pmodePool);
	}

}
