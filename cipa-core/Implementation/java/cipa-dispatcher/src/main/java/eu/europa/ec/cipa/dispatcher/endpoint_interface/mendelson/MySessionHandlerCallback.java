package eu.europa.ec.cipa.dispatcher.endpoint_interface.mendelson;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;


import java.util.logging.Logger;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.ClientSessionHandlerCallback;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.LoginRequired;
import de.mendelson.util.clientserver.messages.LoginState;
import de.mendelson.util.clientserver.user.User;

class MySessionHandlerCallback implements ClientSessionHandlerCallback
{
	
	//private static final Logger s_aLogger = LoggerFactory.getLogger (AS2EndpointSendInterfaceMendelson.class);
	
	AS2EndpointDBInterfaceMendelson mendelsonDBInterface = new AS2EndpointDBInterfaceMendelson();
	Map<String,SignalObject> waitingMessages;
	BaseClient client = new BaseClient(this);
	boolean loggedIn = false;
	
	
	public MySessionHandlerCallback(Map<String,SignalObject> waitingMessages)
	{
		this.waitingMessages = waitingMessages;
	}
	
	public BaseClient getBaseClient()
	{
		return this.client;
	}
	
    @Override
    public void loginRequestedFromServer()
    {
        LoginState state = this.client.login("admin", "admin".toCharArray(), AS2ServerVersion.getFullProductName());
        if (state.getState() == LoginState.STATE_AUTHENTICATION_SUCCESS)
        {
        	User returnedLoginUser = state.getUser();
        	client.setUser(returnedLoginUser);
        	loggedIn=true;
        }
        else
        {
        	getLogger().log(Level.SEVERE, "ERROR: Impossible to login to Mendelson server with default credentials.");
        }
    }
    
    @Override
    public void connected(SocketAddress socketAddress)
    {
    }

    @Override
    public void loggedOut()
    {
    }

    @Override
    public void disconnected()
    {
    }
    
    /**Overwrite this in the client implementation for user defined processing
     */
    @Override
    public void messageReceivedFromServer(ClientServerMessage message) {
        //there is no user defined processing for sync responses
        if (message._isSyncRequest()) {
            return;
        }
        synchronized (this)
        {
        	if (message instanceof LoginRequired)
        	{
        		loginRequestedFromServer();
        	}
        	if (message instanceof RefreshClientMessageOverviewList)
        	{
        		RefreshClientMessageOverviewList m = (RefreshClientMessageOverviewList) message;
        		if (m.getOperation() == RefreshClientMessageOverviewList.OPERATION_PROCESSING_UPDATE)
        		{
        			if (!waitingMessages.isEmpty())
	        			try
	        			{
	        				Set<String> set = waitingMessages.keySet();
	        				Map<String,Integer> updatedMessages = mendelsonDBInterface.getMessageStatesByOriginalFilenames(set);
	        				for (String s : updatedMessages.keySet())
	        				{
	        					int messageState = updatedMessages.get(s);
	        					SignalObject signalObject = waitingMessages.get(s);
	        					
		    	    			if (messageState==AS2Message.STATE_FINISHED)
		    	    			{
		    	    				//send ok signal
		    	    				synchronized(signalObject)
		    	    				{
		    	    					waitingMessages.remove(s);
			    	    				signalObject.setResponseValue(SignalObject.RETURN_SUCCESS);
			    	    				signalObject.notify();
		    	    				}
		    	    			}
		    	    			else if (messageState==AS2Message.STATE_STOPPED || messageState==0) //sending the message failed, or the messageId could no longer be found (probably the user deleted the message from the pending queue)
		    	    			{
		    	    				//send error signal
		    	    				synchronized(signalObject)
		    	    				{
		    	    					waitingMessages.remove(s);
			    	    				signalObject.setResponseValue(SignalObject.RETURN_ERROR);
			    	    				signalObject.notify();
		    	    				}
		    	    			}
		    	    			else
		    	    			{
		    	    				//if it's PENDING there's nothing to do
		    	    			}	
	        				}
	
	    	    			

	        			}
	        			catch (Exception e)
	        			{
	        				//we can't really do much here
	        			}   
        		}
        	}
        }
    }

    @Override
    public void error(String message)
    {
    }
    
    @Override
    public Logger getLogger()
    {
    	// Logger.getLogger("de.mendelson.as2.client")
    	return Logger.getLogger("de.mendelson.as2.client");
    }
    
    /**Makes this a ClientSessionCallback*/
    @Override
    public void syncRequestFailed(Throwable throwable)
    {
    }
    
    /**Logs something to the clients log
     */
    @Override
    public void log(Level logLevel, String message)
    {
    }
    
    public void connect(final InetSocketAddress address, final long timeout)
    {
        client.connect(address, timeout);
    }
    
    public boolean isconnected()
    {
    	return client.isConnected();
    }
}