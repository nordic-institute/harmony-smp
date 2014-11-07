package eu.europa.ec.cipa.dispatcher.endpoint_interface.as2;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.ServerShutdown;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.clientserver.TextClient;
import de.mendelson.util.security.BCCryptoHelper;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointDBInterface;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointInitAndDestroyInterface;

public class AS2EndpointInitInterfaceMendelson implements IAS2EndpointInitAndDestroyInterface
{

	public void init() throws Exception
	{
        //register the database drivers for the VM
        Class.forName("org.hsqldb.jdbcDriver");
        //initialize the security provider
        BCCryptoHelper helper = new BCCryptoHelper();
        helper.initialize();
        AS2Server as2Server = new AS2Server(false, false);
        
        IAS2EndpointDBInterface partnerInterface = new AS2EndpointDBInterfaceMendelson();
        partnerInterface.configureLocalStationIfNeeded();
	}
	
	
	public void destroy() throws Throwable
	{
		TextClient client = new TextClient();
        client.connectAndLogin("localhost", 1235, AS2ServerVersion.getFullProductName(), "admin", "admin".toCharArray(), 15000);
        client.sendAsync(new ServerShutdown());
        client.logout();
	}
}
