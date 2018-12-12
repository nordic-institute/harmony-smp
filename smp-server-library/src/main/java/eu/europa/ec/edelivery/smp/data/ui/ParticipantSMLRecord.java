package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMLAction;

import java.io.Serializable;

public class ParticipantSMLRecord implements Serializable {

    private SMLAction status = SMLAction.REGISTER;

    private String participantIdentifier;
    private String participantScheme;
    private DBDomain domain;

    public ParticipantSMLRecord(SMLAction status, String participantId,String participantScheme, DBDomain domain  ) {
        this.status = status;
        this.participantIdentifier = participantId;
        this.participantScheme = participantScheme;
        this.domain = domain;

    }

    public SMLAction getStatus() {
        return status;
    }

    public void setStatus(SMLAction status) {
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
