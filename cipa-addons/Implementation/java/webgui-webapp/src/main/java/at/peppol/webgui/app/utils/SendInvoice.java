package at.peppol.webgui.app.utils;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.xml.serialize.XMLReader;

import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.process.EPredefinedProcessIdentifier;
import at.peppol.commons.sml.ESML;
import at.peppol.smp.client.SMPServiceCaller;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;
import at.peppol.transport.start.client.AccessPointClient;

public class SendInvoice {
	//public static final String RECEIVER = "9914:ATU53309209"; //OK
	public static final String RECEIVER = "0088:el113766102";   //OK AP2
	//public static final String RECEIVER = "9912:el061828591";   //OK AP1
	//public static final String RECEIVER = "9914:ATU53309209";   //FOUND OK
	public static final boolean USE_PROXY = true;
	public static final String PROXY_HOST = "172.30.9.12";
	public static final String PROXY_PORT = "8080";
	  

	@Nonnull
	private static IMessageMetadata _createMetadata () {
		final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme ("9914:ATU00000003");
	    //final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:el113766102");
	    final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (RECEIVER);
	    final DocumentIdentifierType aDocumentType = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
	    final ProcessIdentifierType aProcessIdentifier = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();
	    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
	    return new MessageMetadata (sMessageID, "test-channel", aSender, aRecipient, aDocumentType, aProcessIdentifier);
	}
	
	@Nullable
	private static String _getAccessPointUrl (@Nonnull final IMessageMetadata aMetadata) throws Exception {
		// SMP client
		final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aMetadata.getRecipientID (), ESML.PRODUCTION);
		// get service info
		String ret = aServiceCaller.getEndpointAddress (aMetadata.getRecipientID (),
	                                            aMetadata.getDocumentTypeID (),
	                                            aMetadata.getProcessID ());
		//System.out.println(ret);
		return ret;
	}
	
	public static void sendDocument (final IReadableResource aXmlRes) throws Exception {
		System.setProperty ("java.net.useSystemProxies", "true");
	    if (USE_PROXY) {
	      System.setProperty ("http.proxyHost", PROXY_HOST);
	      System.setProperty ("http.proxyPort", PROXY_PORT);
	      System.setProperty ("https.proxyHost", PROXY_HOST);
	      System.setProperty ("https.proxyPort", PROXY_PORT);
	    }
	    
		final IMessageMetadata aMetadata = _createMetadata ();
	    //final String sAccessPointURL = false ? "http://localhost:8090/accessPointService" : _getAccessPointUrl (aMetadata);
	    //final String sAccessPointURL = false ? "https://ap2.peppol.gr/ap/accessPointService" : _getAccessPointUrl (aMetadata);
		final String sAccessPointURL = true ? "https://ap2.peppol.gr/ap/accessPointService" : _getAccessPointUrl (aMetadata);
	    //final String sAccessPointURL = true ? "https://localhost/peppol-transport-start-server-2.3.0-SNAPSHOT/accessPointService" : _getAccessPointUrl (aMetadata);
	    final Document aXMLDoc = XMLReader.readXMLDOM (aXmlRes);
	    AccessPointClient.send (sAccessPointURL, aMetadata, aXMLDoc);
	}	  
}
