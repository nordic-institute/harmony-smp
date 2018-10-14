package eu.europa.ec.edelivery.smp.ui.filters;

public class ServiceGroupFilter {
    private String participantIdentifier;
    private String participantScheme;

    public String getParticipantIdentifierLike() {
        return participantIdentifier;
    }

    public void setParticipantIdentifierLike(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public String getParticipantSchemeLike() {
        return participantScheme;
    }

    public void setParticipantSchemeLike(String participantScheme) {
        this.participantScheme = participantScheme;
    }
}
