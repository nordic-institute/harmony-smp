package eu.europa.ec.cipa.as2wrapper.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestType
{

	private MessageMetaDataType metaData;
	
	private DocumentType document;

	
	public MessageMetaDataType getMetaData() {
		return metaData;
	}

	public void setMetaData(MessageMetaDataType metaData) {
		this.metaData = metaData;
	}

	public DocumentType getDocument() {
		return document;
	}

	public void setDocument(DocumentType document) {
		this.document = document;
	}
	
}
