package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMLStatusEnum;

import java.io.Serializable;

public class ParticipantSMLRecord implements Serializable {

    private SMLStatusEnum status;

    private String participantIdentifier;
    private String participantScheme;
    private DBDomain domain;

    public ParticipantSMLRecord(SMLStatusEnum status, String participantId, String participantScheme, DBDomain domain  ) {
        this.status = status;
        this.participantIdentifier = participantId;
        this.participantScheme = participantScheme;
        this.domain = domain;

    }

    public SMLStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SMLStatusEnum status) {
        this.status = status;
    }

    public String getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantId(String participantId) {
        this.participantIdentifier = participantId;
    }

    public String getParticipantScheme() {
        return participantScheme;
    }

    public void setParticipantScheme(String participantScheme) {
        this.participantScheme = participantScheme;
    }

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }
}
