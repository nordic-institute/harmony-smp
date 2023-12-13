/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */
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
    private static final long serialVersionUID = 9008583888835630021L;

    private boolean smlIntegrationOn;
    private boolean concatEBCorePartyId;
    private boolean partyIDSchemeMandatory;
    private String participantSchemaRegExp;
    private String participantSchemaRegExpMessage;


    private String passwordValidationRegExp;
    private String passwordValidationRegExpMessage;
    private final List<String> webServiceAuthTypes = new ArrayList<>();


    public boolean isSmlIntegrationOn() {
        return smlIntegrationOn;
    }

    public void setSmlIntegrationOn(boolean smlIntegrationOn) {
        this.smlIntegrationOn = smlIntegrationOn;
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
