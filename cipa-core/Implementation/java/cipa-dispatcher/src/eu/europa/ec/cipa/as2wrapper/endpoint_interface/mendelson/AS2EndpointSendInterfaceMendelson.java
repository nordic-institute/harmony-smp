package eu.europa.ec.cipa.as2wrapper.endpoint_interface.mendelson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;
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
import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;


public class AS2EndpointSendInterfaceMendelson implements IAS2EndpointSendInterface
{
	
	public static final String MENDELSON_INSTALLATION_PATH = "mendelson_installation_path";
	public static final String MENDELSON_MESSAGE_SERVER_HOST = "mendelson_message_server_host";
	public static final String MENDELSON_MESSAGE_SERVER_PORT = "mendelson_message_server_port";

	
	public String send(String senderId, String receiverId, String documentInstanceIdentifier, String documentFilePath, EndpointType endpoint)
	{

		if (endpoint==null)
			return "Impossible to send: endpoint information was not given";
		
		boolean newPartner = false;
		
		senderId = senderId.replace('-', '_');
		receiverId = receiverId.replace('-', '_');
//		List<Scope> scopes = sbdh.getStandardBusinessDocumentHeader().getBusinessScope().getScope();
//		for (Scope scope : scopes)
//		{
//			if ("DOCUMENTID".equalsIgnoreCase(scope.getType()))
//				documentId = scope.getInstanceIdentifier();
//		}

		//at this point the wrapper's cache contains the receiver enpoint's metadata, but Mendelson might not have it
		AS2EndpointPartnerInterfaceMendelson partnerInterface = new AS2EndpointPartnerInterfaceMendelson();
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
			//updating Mendelson wasn't possible, we'll just log the exception and try to send anyway.
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, (e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage());
		}
		
		Properties properties = PropertiesUtil.getProperties();
		String path = properties.getProperty(MENDELSON_INSTALLATION_PATH);
		
		//first we compose the path where we are going to copy the file to
		if (path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		path += "/messages/";
		path += receiverId;
		path += "/outbox/";
		path += senderId + "/" + documentInstanceIdentifier;
		
		try
		{
			//connect to Mendelson message server, it'll keep us updated on the transmission state
			SignalObject signalObject = new SignalObject();
			MySessionHandlerCallback callback = new MySessionHandlerCallback(signalObject);
			callback.connect(new InetSocketAddress(properties.getProperty(MENDELSON_MESSAGE_SERVER_HOST), Integer.parseInt(properties.getProperty(MENDELSON_MESSAGE_SERVER_PORT))), 5000);
			if (!callback.isconnected())
				return "Couldn't connect to Mendelson message server";
			
			if (newPartner)
			{
				ClientServerResponse resp = callback.getBaseClient().sendSync(new PartnerConfigurationChanged(), 2*1000); //timeout 2 seconds. It always waits until the timeout limit, so we have to make these milliseconds as low as possible.
				if (resp!=null && resp.getException()!=null)
				{
					callback.getBaseClient().disconnect();
					return "Error communicating to Mendelson endpoint: couldn't notify the creation of a new partner. Please retry again later";
				}
			}
			
			
			//put the file on the right folder for Mendelson to send it
		    File tempFile = new File(documentFilePath);
		    File mendelsonFile = new File(path);
		    boolean success = tempFile.renameTo(mendelsonFile);
		    if (!success)
		    {
		    	callback.getBaseClient().disconnect();
		       	return "Error: Couldn't move the temp file " + documentFilePath + " into Mendelson folder " + path + " to be sent.";
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
		    {
		    	callback.getBaseClient().disconnect();
		    	return null;
		    }
		    else
		    {
		    	callback.getBaseClient().disconnect();
		    	return "Mendelson AS2 endpoint wasn't able to send the message";
		    }
		    
		}
		catch (Exception e)
		{
			return (e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage();
		}
	}
	
}



class MySessionHandlerCallback implements ClientSessionHandlerCallback
{
	
	AS2EndpointPartnerInterfaceMendelson partnerInterface = new AS2EndpointPartnerInterfaceMendelson();
	String messageId = null;
	BaseClient client = new BaseClient(this);
	SignalObject signalObject;
	
	
	public MySessionHandlerCallback(SignalObject signalObject)
	{
		this.signalObject = signalObject;
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
        			try
        			{
        				if (messageId == null)
        				{
        					messageId = partnerInterface.getLatestMessageId();
        				}

    	    			int messageState = partnerInterface.getMessageState(messageId);
    	    			if (messageState==AS2Message.STATE_FINISHED)
    	    			{
    	    				//send ok signal
    	    				synchronized(signalObject)
    	    				{
	    	    				signalObject.setResponseValue(SignalObject.RETURN_SUCCESS);
	    	    				signalObject.notify();
    	    				}
    	    				client.logout();
    	    			}
    	    			else if (messageState==AS2Message.STATE_STOPPED || messageState==0) //sending the message failed, or the messageId could no longer be found (probably the user deleted the message from the pending queue)
    	    			{
    	    				//send error signal
    	    				synchronized(signalObject)
    	    				{
	    	    				signalObject.setResponseValue(SignalObject.RETURN_ERROR);
	    	    				signalObject.notify();
    	    				}
    	    				client.logout();
    	    			}
    	    			else
    	    			{
    	    				//if it's PENDING there's nothing to do
    	    			}	
        			}
        			catch (Exception e)
        			{
        				e.getCause();
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


