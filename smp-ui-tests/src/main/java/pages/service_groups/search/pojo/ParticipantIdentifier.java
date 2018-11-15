package pages.service_groups.search.pojo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("ParticipantIdentifier")
@XStreamConverter(value= ToAttributedValueConverter.class, strings={"participantIdentifier"})
public class ParticipantIdentifier {


	String scheme;


	String participantIdentifier;

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getParticipantIdentifier() {
		return participantIdentifier;
	}

	public void setParticipantIdentifier(String participantIdentifier) {
		this.participantIdentifier = participantIdentifier;
	}

	@Override
	public String toString() {
		return "ParticipantIdentifier{" +
				"scheme='" + scheme + '\'' +
				", participantIdentifier='" + participantIdentifier + '\'' +
				'}';
	}
}
