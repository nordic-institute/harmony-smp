package eu.europa.ec.edelivery.smp.services.ui.filters;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBUser;

public class ServiceGroupFilter {
    private String participantIdentifier;
    private String participantScheme;

    private DBUser owner;
    private DBDomain domain;

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

    public DBUser getOwner() {
        return owner;
    }

    public void setOwner(DBUser owner) {
        this.owner = owner;
    }

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }
}
