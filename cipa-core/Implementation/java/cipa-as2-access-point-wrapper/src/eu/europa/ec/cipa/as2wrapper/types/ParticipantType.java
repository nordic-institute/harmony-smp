package eu.europa.ec.cipa.as2wrapper.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ParticipantType
{

	private String scheme;
	
	private String value;

	
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
