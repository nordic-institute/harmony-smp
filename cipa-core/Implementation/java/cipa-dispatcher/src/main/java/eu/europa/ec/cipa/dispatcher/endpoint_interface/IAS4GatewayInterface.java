package eu.europa.ec.cipa.dispatcher.endpoint_interface;

import java.security.cert.X509Certificate;

import eu.europa.ec.cipa.dispatcher.exception.DispatcherConfigurationException;

public interface IAS4GatewayInterface {

	/**
	 * This method creats the Pmode to register a partner in the underlying as4 gateway
	 * 
	 * @param senderGatewayId Common Name of the sender gateway certificate 
	 * @param receiverGatewayId Common Name of the receiver gateway certificate
	 * @param processId busdox process id, to be mapped to ebms3/AS4 pmode service
	 * @param documentId busdox process id, to be mapped to ebms3/AS4 pmode action
	 * @param gatewayURL receiver GW url
	 * @param cert
	 * @throws DispatcherConfigurationException
	 */
	public void createPartner(String senderGatewayId,String receiverGatewayId,String processId,String documentId,String gatewayURL) throws DispatcherConfigurationException;

} 
