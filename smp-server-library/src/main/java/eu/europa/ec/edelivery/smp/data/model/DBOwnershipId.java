/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
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

@Embeddable
@ToString
@EqualsAndHashCode
public class DBOwnershipId implements Serializable {

    private String username;
    private String participantIdScheme;
    private String participantIdValue;

    public DBOwnershipId() {
    }

    public DBOwnershipId(String userName, String participantIdScheme, String participantIdValue) {
        username = userName;
        setBusinessIdentifierScheme(participantIdScheme);
        setBusinessIdentifier(participantIdValue);
    }

    @Column(name = "username", nullable = false, length = 256)
    public String getUsername() {
        return username;
    }

    @Column(name = "businessIdentifierScheme", nullable = false, length = CommonColumnsLengths.MAX_IDENTIFIER_SCHEME_LENGTH)
    public String getBusinessIdentifierScheme() {
        return participantIdScheme;
    }

    @Column(name = "businessIdentifier", nullable = false, length = CommonColumnsLengths.MAX_IDENTIFIER_VALUE_LENGTH)
    public String getBusinessIdentifier() {
        return participantIdValue;
    }

    public void setUsername(final String sUserName) {
        username = sUserName;
    }

    public void setBusinessIdentifierScheme(final String sBusinessIdentifierScheme) {
        participantIdScheme = sBusinessIdentifierScheme;
    }

    public void setBusinessIdentifier(final String sBusinessIdentifier) {
        participantIdValue = sBusinessIdentifier;
    }
}
