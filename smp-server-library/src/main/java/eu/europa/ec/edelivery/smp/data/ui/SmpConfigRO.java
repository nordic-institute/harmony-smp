package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SmpConfigRO properties. opposite to SmpInfoRO user must be logged in to retrieve values
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SmpConfigRO implements Serializable {
    private static final long serialVersionUID = -49712226560325303L;

    boolean smlIntegrationOn;
    boolean smlParticipantMultiDomainOn;
    boolean concatEBCorePartyId;
    boolean partyIDSchemeMandatory;
    String participantSchemaRegExp;
    String participantSchemaRegExpMessage;


    String passwordValidationRegExp;
    String passwordValidationRegExpMessage;
    List<String> webServiceAuthTypes = new ArrayList<>();


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

    public boolean isConcatEBCorePartyId() {
        return concatEBCorePartyId;
    }

    public void setConcatEBCorePartyId(boolean concatEBCorePartyId) {
        this.concatEBCorePartyId = concatEBCorePartyId;
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

    public boolean isPartyIDSchemeMandatory() {
        return partyIDSchemeMandatory;
    }

    public void setPartyIDSchemeMandatory(boolean partyIDSchemeMandatory) {
        this.partyIDSchemeMandatory = partyIDSchemeMandatory;
    }

    public String getPasswordValidationRegExp() {
        return passwordValidationRegExp;
    }

    public void setPasswordValidationRegExp(String passwordValidationRegExp) {
        this.passwordValidationRegExp = passwordValidationRegExp;
    }

    public String getPasswordValidationRegExpMessage() {
        return passwordValidationRegExpMessage;
    }

    public void setPasswordValidationRegExpMessage(String passwordValidationRegExpMessage) {
        this.passwordValidationRegExpMessage = passwordValidationRegExpMessage;
   }

    public List<String> getWebServiceAuthTypes() {
        return webServiceAuthTypes;
    }

    public void addWebServiceAuthTypes(List<String> webServiceAuthTypes) {
        this.webServiceAuthTypes.addAll(webServiceAuthTypes);
    }
}
