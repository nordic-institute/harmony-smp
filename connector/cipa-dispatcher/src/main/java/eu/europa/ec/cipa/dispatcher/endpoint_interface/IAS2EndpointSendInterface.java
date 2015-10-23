package eu.europa.ec.cipa.dispatcher.endpoint_interface;

import org.busdox.servicemetadata.publishing._1.EndpointType;

public interface IAS2EndpointSendInterface
{

	/** Sends the document through the AS2 endpoint. If there were any errors in the transmission it returns a string with the error description. Returns null or empty string if everything went ok.
	 */
	public abstract String send(String senderId, String receiverId, String documentName, String documentFilePath, EndpointType receiverEndpoint);
}
