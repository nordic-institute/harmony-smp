/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.CredentialType;

import java.util.StringJoiner;


/**
 * Credential request reset object
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public class CredentialRequestResetRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630030L;

    String credentialName;
    CredentialType credentialType;

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public CredentialType getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(CredentialType credentialType) {
        this.credentialType = credentialType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CredentialRequestResetRO.class.getSimpleName() + "[", "]")
                .add("credentialName=[" + credentialName + "],")
                .add("credentialType=[" + credentialType + "]")
                .toString();
    }
}
