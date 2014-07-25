package eu.europa.ec.cipa.dispatcher.endpoint_interface.mendelson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.busdox.servicemetadata.publishing._1.EndpointType;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.PartnerConfigurationChanged;
import de.mendelson.comm.as2.clientserver.message.RefreshClientMessageOverviewList;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.ClientSessionHandlerCallback;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.clientserver.messages.LoginRequired;
import de.mendelson.util.clientserver.messages.LoginState;
import de.mendelson.util.clientserver.user.User;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;


public class AS2EndpointSendInterfaceMendelson implements IAS2EndpointSendInterface
{
	
	public static final String MENDELSON_INSTALLATION_PATH = "mendelson_installation_path";
	public static final String MENDELSON_MESSAGE_SERVER_HOST = "mendelson_message_server_host";
	public static final String MENDELSON_MESSAGE_SERVER_PORT = "mendelson_message_server_port";
	
	private static Object synchronizedObject = new Object();
	private static Map<String,SignalObject> waitingMessages = new HashMap<String,SignalObject>();
	private static Properties properties = PropertiesUtil.getProperties(null); 
	private static MySessionHandlerCallback callback;
	static
	{
		//connect to Mendelson message server, it'll keep us updated on the transmission state
		callback = new MySessionHandlerCallback(waitingMessages);
		callback.connect(new InetSocketAddress(properties.getProperty(MENDELSON_MESSAGE_SERVER_HOST), Integer.parseInt(properties.getProperty(MENDELSON_MESSAGE_SERVER_PORT))), 5000);
		if (!callback.isconnected())
			throw (new RuntimeException("Couldn't connect to Mendelson message server"));
	}
	
	
	public String send(String senderId, String receiverId, String documentName, String documentFilePath, EndpointType endpoint)
	{

		if (endpoint==null)
			return "Impossible to send through Mendelson: endpoint information was not given";
		
		boolean newPartner = false;
		
		senderId = senderId.replace('-', '_');
		receiverId = receiverId.replace('-', '_');

		//at this point the dispatcher's cache contains the receiver enpoint's metadata, but Mendelson might not have it
		AS2EndpointDBInterfaceMendelson partnerInterface = new AS2EndpointDBInterfaceMendelson();
		try
		{
			String endpointURL = W3CEndpointReferenceUtils.getAddress(endpoint.getEndpointReference());
			String databaseURL = partnerInterface.getPartnerUrl(receiverId);
			if (databaseURL==null)
				newPartner = true;
			if (!endpointURL.equals(databaseURL))
			{
				//we convert the certificate string to a X509Certificate
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(endpoint.getCertificate().getBytes()));
				//and update the partner's data
			    partnerInterface.updatePartner(receiverId, receiverId, endpointURL, null, cert);
			}
		}
		catch (Exception e)
		{
			//updating Mendelson wasn't possible, so it doesn't make sense to send
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, (e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage());
			return "Impossible to update Mendelson's metadata about your partner. Sending the message will be cancelled. Reason: " + ((e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage());
		}
		
		String path = properties.getProperty(MENDELSON_INSTALLATION_PATH);
		
		//first we compose the path where we are going to copy the file to
		if (path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		path += "/messages/" + receiverId + "/outbox/" + senderId;
		
		try
		{			
			if (newPartner)
			{
				for (int i=0 ; i<10 ; i++)   //in case we are conected but not yet loggedin, wait a maximum of 10 seconds
				{
					if (!callback.loggedIn)
						try	{ Thread.sleep(1000); }	catch(InterruptedException e) { return "Error: thread interrumpted"; }
					else
						break;
				}
				
				ClientServerResponse resp = callback.getBaseClient().sendSync(new PartnerConfigurationChanged(), 2000); //timeout 2 seconds. It always waits until the timeout limit, so we have to make these milliseconds as low as possible.
				if (resp!=null && resp.getException()!=null)
					return "Error communicating to Mendelson endpoint: couldn't notify the creation of a new partner. Please retry again later";
				
				File file_aux = new File(path);
				for (int i=0 ; i<10 ; i++) //we already sent the newPartner signal but it might take a while, wait maximum 10 seconds for the folder creation
				{
					if (file_aux.exists())
						break;
					else
						try	{ Thread.sleep(1000); }	catch(InterruptedException e) { return "Error: thread interrumpted"; }
				}
			}
			
			//put the file on the right folder for Mendelson to send it
			path += "/" + documentName;
		    File tempFile = new File(documentFilePath);
		    File mendelsonFile = new File(path);
		    SignalObject signalObject = new SignalObject();
		    
		    synchronized(synchronizedObject) //giving the file for Mendelson to send and populating waitingMessages form an atomic operation
		    {
			    boolean success = tempFile.renameTo(mendelsonFile);
			    if (!success)
			       	return "Error: Couldn't move the temp file " + documentFilePath + " into Mendelson folder " + path + " to be sent.";
			    else
			    	waitingMessages.put(documentName, signalObject);
		    }
		    
		    synchronized(signalObject)
		    {
		    	while(signalObject.getResponseValue()==0)
		        {
		    		try
		    		{
		    			signalObject.wait();
		            }
		    		catch(InterruptedException e)
		    		{}
		        }
		    }
		    if (signalObject.getResponseValue()==SignalObject.RETURN_SUCCESS)
		    	return null;
		    else
		    	return "Mendelson AS2 endpoint wasn't able to send the message";
		    
		}
		catch (Exception e)
		{
			return (e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage();
		}
	}
	
}



class MySessionHandlerCallback implements ClientSessionHandlerCallback
{
	
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
        	System.out.println("ERROR: Impossible to login to Mendelson server with default credentials.");
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



class SignalObject
{
	private int responseValue = 0;
	public static final int RETURN_SUCCESS = 1;
	public static final int RETURN_ERROR = 2;
	
	
	public int getResponseValue()
	{
		return responseValue;
	}
	
	public void setResponseValue(int responseValue)
	{
		this.responseValue = responseValue;
	}
	
}


