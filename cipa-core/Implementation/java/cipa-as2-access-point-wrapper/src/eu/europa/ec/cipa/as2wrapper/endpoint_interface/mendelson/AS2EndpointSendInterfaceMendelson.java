package eu.europa.ec.cipa.as2wrapper.endpoint_interface.mendelson;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;


import eu.europa.ec.cipa.as2wrapper.endpoint_interface.IAS2EndpointSendInterface;
import eu.europa.ec.cipa.as2wrapper.types.DocumentType;
import eu.europa.ec.cipa.as2wrapper.types.MessageMetaDataType;
import eu.europa.ec.cipa.as2wrapper.util.PropertiesUtil;

public class AS2EndpointSendInterfaceMendelson extends IAS2EndpointSendInterface
{
	
	public static final String MENDELSON_INSTALLATION_PATH = "mendelson_installation_path";

	
	public String send(StandardBusinessDocument sbdh)
	{
		String result = "";
		String senderId = sbdh.getStandardBusinessDocumentHeader().getSender().get(0).getIdentifier().getValue();
		senderId = senderId.replace('-', '_');
		String recipientId = sbdh.getStandardBusinessDocumentHeader().getReceiver().get(0).getIdentifier().getValue();
		recipientId = recipientId.replace('-', '_');
		String documentId = "";
		List<Scope> scopes = sbdh.getStandardBusinessDocumentHeader().getBusinessScope().getScope();
		for (Scope scope : scopes)
		{
			if ("DOCUMENTID".equalsIgnoreCase(scope.getType()))
				documentId = scope.getInstanceIdentifier();
		}

		Properties properties = PropertiesUtil.getProperties();
		String path = properties.getProperty(MENDELSON_INSTALLATION_PATH);
		
		//first we compose the path where we are going to copy the file to
		if (path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		path += "/messages/";
		path += recipientId;
		path += "/outbox/";
		path += senderId + "/" + documentId;
		
		try
		{
			JAXBContext context = JAXBContext.newInstance(StandardBusinessDocument.class);
			Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    m.marshal(sbdh, new File(path));    
		}
		catch (Exception e)
		{
			result = (e.getMessage()!=null && !e.getMessage().isEmpty())? e.getMessage() : e.getCause().getLocalizedMessage();
		}
		
		return result;
	}
	
}
