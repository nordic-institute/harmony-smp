package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;

public class SmpConfigRO implements Serializable {
    private static final long serialVersionUID = -49712226560325303L;

    boolean smlIntegrationOn;
    boolean smlParticipantMultiDomainOn;
    String participantSchemaRegExp;
    String participantSchemaRegExpMessage;


    public boolean isSmlIntegrationOn() {
        return smlIntegrationOn;
    }

    public void setSmlIntegrationOn(boolean smlIntegrationOn) {
        this.smlIntegrationOn = smlIntegrationOn;
    }

    public boolean isSmlParticipantMultiDomainOn() {
        return smlParticipantMultiDomainOn;
    }

    public void setSmlParticipantMultiDomainOn(boolean smlParticipantMultidomainOn) {
        this.smlParticipantMultiDomainOn = smlParticipantMultidomainOn;
    }

    public String getParticipantSchemaRegExp() {
        return participantSchemaRegExp;
    }

    public void setParticipantSchemaRegExp(String participantSchemaRegExp) {
        this.participantSchemaRegExp = participantSchemaRegExp;
    }

    public String getParticipantSchemaRegExpMessage() {
        return participantSchemaRegExpMessage;
    }

    public void setParticipantSchemaRegExpMessage(String participantSchemaRegExpMessage) {
        this.participantSchemaRegExpMessage = participantSchemaRegExpMessage;
    }
}
