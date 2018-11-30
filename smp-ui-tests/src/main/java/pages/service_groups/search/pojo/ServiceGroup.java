package pages.service_groups.search.pojo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("ServiceGroup")
public class ServiceGroup {

	@XStreamAlias("ParticipantIdentifier")
	ParticipantIdentifier participantIdentifier;

	@XStreamAlias("ServiceMetadataReferenceCollection")
	List<ServiceMetadataReference> serviceMetadataReferenceCollection;

	public ParticipantIdentifier getParticipantIdentifier() {
		return participantIdentifier;
	}

	public void setParticipantIdentifier(ParticipantIdentifier participantIdentifier) {
		this.participantIdentifier = participantIdentifier;
	}

	public List<ServiceMetadataReference> getServiceMetadataReferenceCollection() {
		return serviceMetadataReferenceCollection;
	}

	public void setServiceMetadataReferenceCollection(List<ServiceMetadataReference> serviceMetadataReferenceCollection) {
		this.serviceMetadataReferenceCollection = serviceMetadataReferenceCollection;
	}

	@Override
	public String toString() {
		return "ServiceGroup{" +
				"participantIdentifier=" + participantIdentifier +
				", serviceMetadataReferenceCollection=" + serviceMetadataReferenceCollection +
				'}';
	}
}
