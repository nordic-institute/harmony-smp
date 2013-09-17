package eu.europa.ec.cipa.as2wrapper.endpoint_interface;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

public abstract class IAS2EndpointSendInterface
{

	public abstract String send(StandardBusinessDocument doc);
	
}
