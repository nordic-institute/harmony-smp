package eu.europa.ec.cipa.dispatcher.endpoint_interface;

import java.security.cert.X509Certificate;

import eu.europa.ec.cipa.dispatcher.exception.DispatcherConfigurationException;

public interface IAS4GatewayInterface {

	/**
	 * This method creates the Pmode to register a partner in the underlying as4 gateway
	 * 
	 * @param partnerGatewayId id of the partner gateway
	 * @param processId busdox process id, to be mapped to ebms3/AS4 pmode service
	 * @param documentId busdox process id, to be mapped to ebms3/AS4 pmode action
	 * @param gatewayURL
	 * @param cert
	 * @throws DispatcherConfigurationException
	 */
	public void createPartner(String partnerGatewayId,String processId,String documentId,String gatewayURL, X509Certificate cert) throws DispatcherConfigurationException;

}
