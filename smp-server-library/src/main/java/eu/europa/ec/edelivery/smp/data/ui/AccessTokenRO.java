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
import java.time.OffsetDateTime;

public class AccessTokenRO implements Serializable {

    private static final long serialVersionUID = 9008583888835630002L;

    private String identifier;
    private String value;
    OffsetDateTime generatedOn;
    OffsetDateTime expireOn;

    CredentialRO credential;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OffsetDateTime getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(OffsetDateTime generatedOn) {
        this.generatedOn = generatedOn;
    }

    public OffsetDateTime getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(OffsetDateTime expireOn) {
        this.expireOn = expireOn;
    }

    public CredentialRO getCredential() {
        return credential;
    }

    public void setCredential(CredentialRO credential) {
        if (credential !=null) {
            identifier = credential.getName();
            expireOn = credential.getExpireOn();
            generatedOn = credential.getUpdatedOn();
        }

        this.credential = credential;
    }
}
