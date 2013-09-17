package eu.europa.ec.cipa.as2wrapper.send;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointPartnerInterface;
import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.as2wrapper.endpoint_interface.mendelson.AS2EndpointPartnerInterfaceMendelson;
import eu.europa.ec.cipa.as2wrapper.types.DocumentType;
import eu.europa.ec.cipa.as2wrapper.types.MessageMetaDataType;
import eu.europa.ec.cipa.as2wrapper.types.RequestType;
import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.sml.CSMLDefault;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;


@Path ("/send")
public class SendWebService
{

	Properties properties = PropertiesUtil.getProperties();
	
	
	public SendWebService() {}

	
	@GET
	public Response getDocument()
	{
		return Response.ok().build();
	}
	
	
	@POST
	public Response sendDocument (RequestType request) throws Throwable
	{
		try
		{
			
			//If the AS2 endpoint doesn't know about the recipient, we download the metadata from the SMP
			String className = properties.getProperty(PropertiesUtil.PARTNER_INTERFACE_IMPLEMENTATION_CLASS);
			IAS2EndpointPartnerInterface partnerInterface = (IAS2EndpointPartnerInterface) Class.forName(className).newInstance();
			try
			{
				String receiverId = request.getMetaData().getRecipient().getValue();
				if (!partnerInterface.isPartnerUrlKnown(receiverId))
				{
					SMPServiceCaller aSMPClient;
					ParticipantIdentifierType recipient = new ParticipantIdentifierType();
					recipient.setValue(request.getMetaData().getRecipient().getValue());
					recipient.setScheme(request.getMetaData().getRecipient().getScheme());
				    DocumentIdentifierType documentIdentifier = new DocumentIdentifierType();
				    documentIdentifier.setValue(request.getMetaData().getDocumentId());
				    documentIdentifier.setScheme("????");  //TODO: determine this value
				    ProcessIdentifierType process = new ProcessIdentifierType();
				    process.setValue(request.getMetaData().getProcessId());
				    process.setScheme("????");   //TODO: determine this value
			    	
				    if ("DIRECT_SMP".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SMP_MODE)))
				    {
				    	aSMPClient = new SMPServiceCaller (new URI (properties.getProperty(PropertiesUtil.SMP_URL)));
				    }
				    else if ("PRODUCTION".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SMP_MODE)))
				    {
				    	aSMPClient = new SMPServiceCaller (recipient, ESML.PRODUCTION);
				    }
				    else  //in NO_SMP mode --> we can't access the SMP so we can't handle a send to a participant we dont know 
				    {
				    	return Response.status (Status.INTERNAL_SERVER_ERROR).type("text/plain").entity("Impossible to handle send request to an unknown participant in NO_SMP mode").build();
				    }
				    
				    EndpointType endpoint = aSMPClient.getEndpoint(recipient, documentIdentifier, process);
				    String endpointURL = endpoint==null? null : W3CEndpointReferenceUtils.getAddress(endpoint.getEndpointReference());
				    
				    partnerInterface.updatePartner(receiverId, receiverId, endpointURL, null, null);
				}
			}
			catch (Exception e)
			{
				return Response.status (Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(e.getMessage()).build();
			}
			
			
			//transform the request into an SBDH request
			StandardBusinessDocument sbdhDoc = transformIntoSBDHRequest(request);
			
			//perform the actual send, implementation dependent
			className = properties.getProperty(PropertiesUtil.SEND_INTERFACE_IMPLEMENTATION_CLASS);
			IAS2EndpointSendInterface sendInterface = (IAS2EndpointSendInterface) Class.forName(className).newInstance();
			
			String result = sendInterface.send(sbdhDoc);
			
			if (result != null && !result.isEmpty())
				return Response.status (Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(result).build();
			else
				return Response.ok().build();
			
		}
		catch (Throwable ex)
		{
			throw ex;
		}
	}
	
	
	private StandardBusinessDocument transformIntoSBDHRequest(RequestType request)
	{
		StandardBusinessDocument sbdh = new StandardBusinessDocument();
		
		StandardBusinessDocumentHeader sbdhHeader = new StandardBusinessDocumentHeader();
		sbdhHeader.setHeaderVersion("1.3");
		
		BusinessScope sbdhBusinessScope = new BusinessScope();
		List<Scope> scopes = sbdhBusinessScope.getScope();
		Scope scope = new Scope();
		scope.setType("DOCUMENTID");
		scope.setInstanceIdentifier(request.getMetaData().getDocumentId());
		scopes.add(scope);
		scope = new Scope();
		scope.setType("PROCESSID");
		scope.setInstanceIdentifier(request.getMetaData().getProcessId());
		scopes.add(scope);
		sbdhHeader.setBusinessScope(sbdhBusinessScope);
		
		List<Partner> senders = sbdhHeader.getSender();
		Partner partner = new Partner();
		PartnerIdentification partnerIdentification = new PartnerIdentification();
		partnerIdentification.setAuthority(request.getMetaData().getSender().getScheme());
		partnerIdentification.setValue(request.getMetaData().getSender().getValue());
		partner.setIdentifier(partnerIdentification);
		senders.add(partner);
		
		List<Partner> receivers = sbdhHeader.getReceiver();
		partner = new Partner();
		partnerIdentification = new PartnerIdentification();
		partnerIdentification.setAuthority(request.getMetaData().getRecipient().getScheme());
		partnerIdentification.setValue(request.getMetaData().getRecipient().getValue());
		partner.setIdentifier(partnerIdentification);
		receivers.add(partner);
		
		DocumentIdentification sbdhDocId = new DocumentIdentification();
		GregorianCalendar cal_aux = new GregorianCalendar();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date;
	    try
	    {
	    	date = sdf.parse(request.getMetaData().getDocumentInfo().getCreationDateAndTime());
	    }
	    catch (Exception e)
	    {
	    	date = new Date();
	    }
	    cal_aux.setTime(date);
		XMLGregorianCalendar cal;
		try
		{
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal_aux);
		}
		catch (Exception e)
		{
			cal = null;
		}
		sbdhDocId.setCreationDateAndTime(cal);
		sbdhDocId.setInstanceIdentifier(request.getMetaData().getDocumentInfo().getInstanceIdentifier());
		sbdhDocId.setStandard(request.getMetaData().getDocumentInfo().getStandard());
		sbdhDocId.setType(request.getMetaData().getDocumentInfo().getType());
		sbdhDocId.setTypeVersion(request.getMetaData().getDocumentInfo().getTypeVersion());
		sbdhHeader.setDocumentIdentification(sbdhDocId);
		
		sbdh.setStandardBusinessDocumentHeader(sbdhHeader);
		sbdh.setAny(request.getDocument().getDocument());
		
		return sbdh;
	}
	
}
