package eu.europa.ec.cipa.dispatcher;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.ClientSessionHandlerCallback;
import de.mendelson.util.clientserver.ClientsideMessageProcessor;
import de.mendelson.util.clientserver.messages.ClientServerMessage;

public class ClientConnectTest
{

	public static void main(String[] args)
	{
		MySessionHandlerCallback callback = new MySessionHandlerCallback();
		callback.connect(new InetSocketAddress("localhost", 1235), 5000);
		String s = "";
		s = s + "";
	}
	
}


class MyMessageProcessor implements ClientsideMessageProcessor
{
	Connection connection;
	String messageId = null;
	
	public MyMessageProcessor()
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:3333/runtime", "sa", "as2dbadmin");
		}
		catch (Exception e)
		{
			connection = null;
		}
	}
	
	/**Returns if the message has been processed by the instance*/
    public boolean processMessageFromServer( ClientServerMessage message )
    {    	
    	System.out.println(message.getClass().toString());
    	
    	if (message instanceof RefreshClientMessageOverviewList) // UploadRequestFile  UploadRequestChunk  DownloadRequestFile  FileDeleteRequest  ManualSendRequest  DeleteMessageRequest  RefreshClientCEMDisplay  RefreshClientCEMDisplay
    	{
    		RefreshClientMessageOverviewList m = (RefreshClientMessageOverviewList) message;
    		if (m.getOperation() == RefreshClientMessageOverviewList.OPERATION_PROCESSING_UPDATE)
    		{
    			try
    			{
    				Statement statement = connection.createStatement();
    				ResultSet result;
    				if (messageId == null)
    				{
		    			result = statement.executeQuery("SELECT TOP 1 messageid FROM messages ORDER BY initdate DESC");
		    			if (result.next())
		    			{
		    				messageId = result.getString(1);
		    			}
    				}

	    			result = statement.executeQuery("SELECT state FROM messages WHERE messageid = '" + messageId + "'");
	    			if (result.next())
	    			{
	    				System.out.println("ESTADO DEL MENSAJE: " + result.getInt(1));
	    			}
	    			
    			}
    			catch (Exception e)
    			{
    				e.getCause();
    			}
    			
    	        
    		}
    		    		
    		
    	}
    	
    	
    	return true;
    }
}


class MySessionHandlerCallback implements ClientSessionHandlerCallback
{
	
	BaseClient client = new BaseClient(this);
	MyMessageProcessor processor = new MyMessageProcessor();
	
	
    @Override
    public void loginRequestedFromServer()
    {
        this.client.login("admin", "admin".toCharArray(), AS2ServerVersion.getFullProductName());
    }
    
    @Override
    public void connected(SocketAddress socketAddress)
    {
    	String s = "";
    }
	
//    /**Callback if the user has been logged in successfully
//     */
//    @Override
//    public void loggedIn(User user)
//    {
//        //login successful: pass a user to the base client
//        this.client.setUser(user);
//    }

//    @Override
//    public void loginFailure(String username)
//    {
//        String s = "";
//    }

    @Override
    public void loggedOut()
    {
    	String s = "";
    }

    @Override
    public void disconnected()
    {
        System.exit(1);
    }
    
//    @Override
//    public void loginFailureIncompatibleClient()
//    {
//        System.exit(1);
//    }
    
//    /**The server requests a password for the user
//     */
//    @Override
//    public void loginFailureServerRequestsPassword(String user)
//    {
//    	String s = "";
//    }
    
    /**Overwrite this in the client implementation for user defined processing
     */
    @Override
    public void messageReceivedFromServer(ClientServerMessage message) {
        //there is no user defined processing for sync responses
        if (message._isSyncRequest()) {
            return;
        }
        synchronized (this.processor) {
            processor.processMessageFromServer(message);
        }
    }

    @Override
    public void error(String message)
    {
    	String s = "";
    }
    
    @Override
    public Logger getLogger()
    {
    	return Logger.getLogger("de.mendelson.as2.client");
    }
    
    /**Makes this a ClientSessionCallback*/
    @Override
    public void syncRequestFailed(Throwable throwable)
    {
    	String s = "";
    }
    
    /**Logs something to the clients log
     */
    @Override
    public void log(Level logLevel, String message)
    {
    	String s = "";
    }
    
    public void connect(final InetSocketAddress address, final long timeout)
    {
        client.connect(address, timeout);
    }
    
}
