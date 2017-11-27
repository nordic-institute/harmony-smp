/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_IDENTIFIER_SCHEME_LENGTH;
import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH;

@Embeddable
@ToString
@EqualsAndHashCode
public class DBServiceGroupId implements Serializable {

    private String participantIdScheme;
    private String participantIdValue;

    public DBServiceGroupId() {
    }

    public DBServiceGroupId(String participantIdScheme,
                            String participantIdValue) {

        setBusinessIdentifierScheme(participantIdScheme);
        setBusinessIdentifier(participantIdValue);
    }

    @Column(name = "businessIdentifierScheme", nullable = false, length = MAX_IDENTIFIER_SCHEME_LENGTH)
    public String getBusinessIdentifierScheme() {
        return participantIdScheme;
    }

    @Column(name = "businessIdentifier", nullable = false, length = MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
    public String getBusinessIdentifier() {
        return participantIdValue;
    }

    public void setBusinessIdentifierScheme(String participantIdScheme) {
        this.participantIdScheme = participantIdScheme;
    }

    public void setBusinessIdentifier(String participantIdValue) {
        this.participantIdValue = participantIdValue;
    }

}