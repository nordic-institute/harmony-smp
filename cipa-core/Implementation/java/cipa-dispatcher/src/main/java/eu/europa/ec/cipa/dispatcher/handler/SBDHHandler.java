package eu.europa.ec.cipa.dispatcher.handler;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

public class SBDHHandler extends DefaultHandler {
	
	

	private FileOutputStream file; // whole SBDH document sent by the client
	private FileOutputStream file2; // only the payload without the SBDH
									// headers sent by the client
	private PrintStream stream;
	private PrintStream stream2;
	private String position = "";
	private String scheme;
	private String scopeType;
	private Map<String, String> resultMap;
	private boolean inPayload = false;

	private static final String senderIdentifierPosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>Sender>Identifier";
	private static final String receiverIdentifierPosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>Receiver>Identifier";
	private static final String instanceIdentifierPosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>DocumentIdentification>InstanceIdentifier";
	private static final String businessScopeTypePosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>BusinessScope>Scope>Type";
	private static final String businessScopeInstanceIdentifierPosition = ">StandardBusinessDocument>StandardBusinessDocumentHeader>BusinessScope>Scope>InstanceIdentifier";

	public Map<String, String> getResultMap() {
		return this.resultMap;
	}

	public void startDocument() throws SAXException {
		try {
			int randomInt = 100000000 + (int) (Math.random() * 900000000); // Min
																			// +
																			// (int)(Math.random()
																			// *
																			// ((Max
																			// -
																			// Min)
																			// +
																			// 1))
																			// ,
																			// Min
																			// =
																			// 100000000,
																			// Max
																			// =
																			// 999999999
			Properties properties = PropertiesUtil.getProperties(null);
			String tempFilePath = properties
					.getProperty(PropertiesUtil.TEMP_FOLDER_PATH);
			if (!tempFilePath.endsWith("/") && !tempFilePath.endsWith("\\"))
				tempFilePath += "/";
			tempFilePath += randomInt;
			file = new FileOutputStream(tempFilePath);
			stream = new PrintStream(file);
			file2 = new FileOutputStream(tempFilePath + "_payload");
			stream2 = new PrintStream(file2);

			resultMap = new HashMap<String, String>();
			resultMap.put("tempFilePath", tempFilePath);
			resultMap.put("tempFile2Path", tempFilePath + "_payload");
		} catch (Exception e) {
			throw new SAXException(e);
		}
	}

	public void endDocument() throws SAXException {
		try {
			stream.flush();
			stream.close();
			file.flush();
			file.close();
			stream2.flush();
			stream2.close();
			file2.flush();
			file2.close();
		} catch (Exception e) {
			throw new SAXException(e);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String tag = localName != null && !localName.isEmpty() ? localName
				: qName;

		stream.print("<" + tag);
		if (inPayload)
			stream2.print("<" + tag);

		position += ">" + tag;

		if (attributes != null) {
			String attName;
			for (int i = 0; i < attributes.getLength(); i++) {
				attName = attributes.getLocalName(i) != null ? attributes
						.getLocalName(i) : attributes.getQName(i);
				stream.print(' ');
				stream.print(attName);
				stream.print('=');
				stream.print('"' + attributes.getValue(i) + '"');
				if (inPayload) {
					stream2.print(' ');
					stream2.print(attName);
					stream2.print('=');
					stream2.print('"' + attributes.getValue(i) + '"');
				}

				if (attName.equalsIgnoreCase("Authority"))
					scheme = attributes.getValue(i);
			}
		}

		stream.print(">");
		if (inPayload)
			stream2.print(">");
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String tag = localName != null && !localName.isEmpty() ? localName
				: qName;

		if (tag.equalsIgnoreCase("StandardBusinessDocument"))
			inPayload = false;

		stream.print("</" + tag + ">");
		if (inPayload)
			stream2.print("</" + tag + ">");

		position = position.substring(0, position.lastIndexOf('>'));

		if (tag.equalsIgnoreCase("StandardBusinessDocumentHeader"))
			inPayload = true;
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (position.equalsIgnoreCase(senderIdentifierPosition)) {
			resultMap.put("senderIdentifier", new String(ch, start, length));
			resultMap.put("senderScheme", scheme);
		} else if (position.equalsIgnoreCase(receiverIdentifierPosition)) {
			resultMap.put("receiverIdentifier", new String(ch, start, length));
			resultMap.put("receiverScheme", scheme);
		} else if (position.equalsIgnoreCase(instanceIdentifierPosition)) {
			resultMap.put("instanceIdentifier", new String(ch, start, length));
		} else if (position.equalsIgnoreCase(businessScopeTypePosition)) {
			scopeType = new String(ch, start, length);
		} else if (position
				.equalsIgnoreCase(businessScopeInstanceIdentifierPosition)) {
			if (scopeType.equalsIgnoreCase("DOCUMENTID"))
				resultMap.put("documentIdentifier", new String(ch, start,
						length));
			if (scopeType.equalsIgnoreCase("PROCESSID"))
				resultMap.put("processIdentifier",
						new String(ch, start, length));
			if (scopeType.equalsIgnoreCase("CORRELATIONID"))
				resultMap.put("correlationId", new String(ch, start, length));
		}

		// stream.print(new String(ch, start, length)); avoiding the
		// creation of potentially big objects
		for (int offset = 0; offset < length; offset++) {
			stream.print(ch[start + offset]);
			if (inPayload)
				stream2.print(ch[start + offset]);
		}
	}

}