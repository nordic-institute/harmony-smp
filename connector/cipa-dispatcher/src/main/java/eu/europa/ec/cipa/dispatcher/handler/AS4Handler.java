package eu.europa.ec.cipa.dispatcher.handler;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AS4Handler extends DefaultHandler {

	private String position = "";
	private Map<String,String> resultMap;
	
	private static final String certificatePosition = ">soapenv:Envelope>soapenv:Header>wsse:Security>wsse:BinarySecurityToken";
	private static final String senderIdentifierPosition = ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:PartyInfo>eb:From>eb:PartyId";
	private static final String receiverIdentifierPosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:PartyInfo>eb:To>eb:PartyId";
	private static final String instanceIdentifierPosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:CollaborationInfo>eb:ConversationId";
	private static final String processTypePosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:CollaborationInfo>eb:Service";
	private static final String documentTypePosition= ">soapenv:Envelope>soapenv:Header>eb:Messaging>eb:UserMessage>eb:CollaborationInfo>eb:Action";
	
	
	public Map<String,String> getResultMap()
	{
		return this.resultMap;
	}
	
    public void startDocument() throws SAXException
    {
		resultMap = new HashMap<String,String>();
    }

    public void endDocument() throws SAXException
    {
    }
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		String tag = localName!=null && !localName.isEmpty()? localName : qName;
		
		position += ">" + tag;
	}
 
	public void endElement(String uri, String localName,String qName) throws SAXException
	{
		position = position.substring(0, position.lastIndexOf('>')); 
	}
 
	public void characters(char ch[], int start, int length) throws SAXException
	{
		if (position.equalsIgnoreCase(senderIdentifierPosition))
		{
			resultMap.put("senderIdentifier", new String(ch, start, length));
		}
		else if (position.equalsIgnoreCase(receiverIdentifierPosition))
		{
			resultMap.put("receiverIdentifier", new String(ch, start, length));
		}
		else if (position.equalsIgnoreCase(instanceIdentifierPosition))
		{
			resultMap.put("instanceIdentifier", new String(ch, start, length));
		}			
		else if (position.equalsIgnoreCase(certificatePosition))
		{
			resultMap.put("certificate", new String(ch, start, length));
		}
		else if (position.equalsIgnoreCase(processTypePosition))
		{
			resultMap.put("processIdentifier", new String(ch, start, length));
		}
		else if (position.equalsIgnoreCase(documentTypePosition))
		{
			resultMap.put("documentIdentifier", new String(ch, start, length));
		}
	}
}	

