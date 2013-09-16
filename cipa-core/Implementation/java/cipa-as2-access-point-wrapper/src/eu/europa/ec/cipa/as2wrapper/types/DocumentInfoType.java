package eu.europa.ec.cipa.as2wrapper.types;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class DocumentInfoType
{

	private String standard;
	
	private String typeVersion;
	
	private String instanceIdentifier;
	
	private String type;
	
	private String creationDateAndTime;

	
	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getTypeVersion() {
		return typeVersion;
	}

	public void setTypeVersion(String typeVersion) {
		this.typeVersion = typeVersion;
	}

	public String getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(String instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreationDateAndTime() {
		return creationDateAndTime;
	}

	public void setCreationDateAndTime(String creationDateAndTime) {
		this.creationDateAndTime = creationDateAndTime;
	}
	
}
