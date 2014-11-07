package eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus;

import org.junit.Test;

import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.AS4GatewayInterface;

public class AS4GatewayInterfaceTest {

	@Test
	public void testCreatePartner() throws Exception{
		AS4GatewayInterface gw = new  AS4GatewayInterface(); 
		gw.createPartner("TSTGW3","TSTGW2", "EPO", "FORM_B", "http://test");
	}

}
