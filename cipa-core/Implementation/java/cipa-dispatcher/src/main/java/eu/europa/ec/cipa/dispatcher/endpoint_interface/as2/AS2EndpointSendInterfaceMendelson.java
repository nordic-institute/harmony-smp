package eu.europa.ec.cipa.dispatcher.endpoint_interface.as2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.busdox.servicemetadata.publishing._1.EndpointType;

import de.mendelson.comm.as2.clientserver.message.PartnerConfigurationChanged;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;


public class AS2EndpointSendInterfaceMendelson implements IAS2EndpointSendInterface
{
	
	private static final Logger s_aLogger = Logger.getLogger (AS2EndpointSendInterfaceMendelson.class);
	
	public static final String MENDELSON_INSTALLATION_PATH = "mendelson_installation_path";
	public static final String MENDELSON_MESSAGE_SERVER_HOST = "mendelson_message_server_host";
	public static final String MENDELSON_MESSAGE_SERVER_PORT = "mendelson_message_server_port";
	
	private static Object synchronizedObject = new Object();
	private static Map<String,SignalObject> waitingMessages = new HashMap<String,SignalObject>();
	private static Properties properties = PropertiesUtil.getProperties();
	private static MySessionHandlerCallback callback;

	public AS2EndpointSendInterfaceMendelson() {
		//connect to Mendelson message server, it'll keep us updated on the transmission state
		callback = new MySessionHandlerCallback(waitingMessages);
		callback.connect(new InetSocketAddress(properties.getProperty(MENDELSON_MESSAGE_SERVER_HOST), Integer.parseInt(properties.getProperty(MENDELSON_MESSAGE_SERVER_PORT))), 5000);
		if (!callback.isconnected()){
			s_aLogger.error("Couldn't connect to Mendelson message server");
			throw (new RuntimeException("Couldn't connect to Mendelson message server"));
		}
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
			s_aLogger.error("Impossible to update Mendelson's metadata about your partner. Sending the message will be cancelled. Reason:",e);
			return "Impossible to update Mendelson's metadata about your partner. Sending the message will be cancelled. Reason: " + ((e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage());
		}
		
		String path = properties.getProperty(MENDELSON_INSTALLATION_PATH);

		String fileName = new File(documentFilePath).getName();
		
		//first we compose the path where we are going to copy the file to
		if (path.endsWith("/"))
			path = path.substring(0, path.length()-1);

		path += "/messages/" + receiverId;
		/* Workaround to allow the AP to send a message to itself, ie the recipient is also the sender. Mendelson doesn't support this so we directly copy the message into the inbox folder */
		boolean senderEqualsReceiver = senderId.equalsIgnoreCase(receiverId);
		if (senderEqualsReceiver) {
			path += "/inbox/";
		} else {
			path += "/outbox/";
		}

		path += senderId;

		try
		{
			if (newPartner)
			{
				for (int i=0 ; i<10 ; i++)   //in case we are connected but not yet logged in, wait a maximum of 10 seconds
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
						try	{ Thread.sleep(1000);
						}	catch(InterruptedException e) {
							s_aLogger.error("Error: thread interrumpted while waiting for mendelson partner update",e);
							return "Error: thread interrumpted";

						}
				}
			}

			//put the file on the right folder for Mendelson to send it
			path += "/" + documentName;
		    SignalObject signalObject = new SignalObject();

		    synchronized(synchronizedObject) //giving the file for Mendelson to send and populating waitingMessages form an atomic operation
		    {
			 //   boolean success = tempFile.renameTo(mendelsonFile);
			    Path source =  Paths.get(documentFilePath);
			    Path target = Paths.get(path);

				File targetFolder = new File(path).getParentFile();
				if (!targetFolder.exists()) {
					boolean result = targetFolder.mkdirs();
					if (!result) {
						s_aLogger.error("Error: Couldn't create the folder " + targetFolder.getAbsolutePath() + ". This is highly likely to create problems to send messages with Mendelson");
					}
				}

			    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING );
//			    if (!success){
//			    	
//			    }else
//			    s_aLogger.error("Error: Couldn't move the temp file " + documentFilePath + " into Mendelson folder " + path + " to be sent.");
//			    	return "Error: Couldn't move the temp file " + documentFilePath + " into Mendelson folder " + path + " to be sent.";
			    waitingMessages.put(documentName, signalObject);
		    }

			if (!senderEqualsReceiver) {
				synchronized (signalObject) {
					while (signalObject.getResponseValue() == 0) {
						try {
							signalObject.wait();
						} catch (InterruptedException e) {
						}
					}
				}
				if (signalObject.getResponseValue() == SignalObject.RETURN_SUCCESS)
					return null;
				else {
					s_aLogger.error("Mendelson AS2 endpoint wasn't able to send the message");
					return "Mendelson AS2 endpoint wasn't able to send the message";
				}
			} else {
				// rename the file to "simulate" the mendelson genuine processing
				String newFileName = "mendelson_opensource_AS2_" + fileName + "_0@" + senderId + "_" + receiverId;
				File renameToFile = new File(new File(path).getParent() + File.separator + newFileName);

				boolean renamed = new File(path).renameTo(renameToFile);
				if (!renamed) {
					s_aLogger.warn("Couldn't rename the file");
				}

				//copy to the "sent" directory
				try {
					Path source = Paths.get(renameToFile.toURI());
					File sentFolderFile = new File(new File(path).getParentFile().getParentFile().getParentFile().getAbsolutePath() + File.separator + "sent" + File.separator + receiverId + File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date()));
					if (!sentFolderFile.exists()) {
						sentFolderFile.mkdirs();
					}
					Path target = Paths.get(sentFolderFile.getAbsolutePath() + File.separator + newFileName + ".payload");
					Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
				} catch(final Exception exc) {
					s_aLogger.warn("Couldn't copy the file to the 'sent' folder");
				}

				s_aLogger.info("The sender and the receiver are from/to the same Access Point. The message has been directly moved to " + renameToFile.getAbsolutePath() + " and wasn't sent to Mendelson");
				return null;
			}

		}
		catch (Exception e)
		{
			s_aLogger.error(e.getMessage(),e);
			return (e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage();
		}
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


