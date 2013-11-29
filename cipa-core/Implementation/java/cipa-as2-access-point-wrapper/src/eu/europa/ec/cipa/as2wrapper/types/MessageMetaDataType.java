package eu.europa.ec.cipa.as2wrapper.types;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MessageMetaDataType
{

	private ParticipantType sender;

	private ParticipantType recipient;

	private DocumentInfoType documentInfo;
	
	private String documentId;
	
	private String documentScheme;

	private String processId;
	
	private String processScheme;

	
	public ParticipantType getSender() {
		return sender;
	}

	public void setSender(ParticipantType sender) {
		this.sender = sender;
	}

	public ParticipantType getRecipient() {
		return recipient;
	}

	public void setRecipient(ParticipantType recipient) {
		this.recipient = recipient;
	}

	public DocumentInfoType getDocumentInfo() {
		return documentInfo;
	}

	public void setDocumentInfo(DocumentInfoType documentInfo) {
		this.documentInfo = documentInfo;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getDocumentScheme() {
		return documentScheme;
	}

	public void setDocumentScheme(String documentScheme) {
		this.documentScheme = documentScheme;
	}

	public String getProcessScheme() {
		return processScheme;
	}

	public void setProcessScheme(String processScheme) {
		this.processScheme = processScheme;
	}
	  
}
