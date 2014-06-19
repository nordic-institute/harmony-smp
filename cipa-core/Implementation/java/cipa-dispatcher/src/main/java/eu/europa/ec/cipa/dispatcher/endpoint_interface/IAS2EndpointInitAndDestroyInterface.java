package eu.europa.ec.cipa.dispatcher.endpoint_interface;

import java.util.Properties;

public interface IAS2EndpointInitAndDestroyInterface
{

	public void init() throws Exception;
	
	public void destroy() throws Throwable;
	
}
