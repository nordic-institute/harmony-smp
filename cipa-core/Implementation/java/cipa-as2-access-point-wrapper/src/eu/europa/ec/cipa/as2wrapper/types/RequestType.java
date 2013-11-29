package eu.europa.ec.cipa.as2wrapper.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class RequestType
{

	private MessageMetaDataType metaData;
	
    @XmlAnyElement(lax = true)
    private Object document;

	
	public MessageMetaDataType getMetaData() {
		return metaData;
	}

	public void setMetaData(MessageMetaDataType metaData) {
		this.metaData = metaData;
	}

	public Object getDocument() {
		return document;
	}

	public void setDocument(Object document) {
		this.document = document;
	}
	
}
