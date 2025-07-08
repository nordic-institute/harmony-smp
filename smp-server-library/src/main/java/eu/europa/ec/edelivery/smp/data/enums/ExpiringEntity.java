/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2025 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.data.enums;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.2
 */
public enum ExpiringEntity {
    USERNAME_PASSWORD,
    ACCESS_TOKEN,
    CERTIFICATE,
    SYSTEM_CERTIFICATE;

    public static ExpiringEntity getExpiringEntity(CredentialType credentialType) {
        switch (credentialType) {
            case USERNAME_PASSWORD:
                return ExpiringEntity.USERNAME_PASSWORD;
            case ACCESS_TOKEN:
                return ExpiringEntity.ACCESS_TOKEN;
            case CERTIFICATE:
                return ExpiringEntity.CERTIFICATE;
        }
        throw new IllegalArgumentException("Invalid credentialType: " + credentialType);
    }
}
