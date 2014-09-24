package eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus;

import static org.junit.Assert.*;

import org.junit.Test;

public class AS4GatewayInterfaceTest {

	@Test
	public void testCreatePartner() throws Exception{
		AS4GatewayInterface gw = new  AS4GatewayInterface(); 
		gw.createPartner("TSTGW3","TSTGW2", "EPO", "FORM_B", "http://test");
	}

}
