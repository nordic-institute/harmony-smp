package eu.europa.ec.cipa.as2wrapper.types;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class DocumentType
{

	private Object document;

	
	public Object getDocument() {
		return document;
	}

	public void setDocument(Object document) {
		this.document = document;
	}
	
}
