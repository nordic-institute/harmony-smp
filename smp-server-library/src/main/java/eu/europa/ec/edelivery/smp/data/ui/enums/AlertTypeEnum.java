/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.data.ui.enums;

/**
 * Enumeration of the alert types. The enumeration defines the mail template
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum AlertTypeEnum {
    TEST_ALERT("test_mail"),
    CREDENTIAL_IMMINENT_EXPIRATION("credential_imminent_expiration"),
    CREDENTIAL_EXPIRED("credential_expired"),
    CREDENTIAL_SUSPENDED("credential_suspended"),
    CREDENTIAL_VERIFICATION_FAILED("credential_verification_failed"),
    CREDENTIAL_REQUEST_RESET("credential_request_reset"),
    CREDENTIAL_CHANGED("credential_changed"),
    USER_CREATED_CONFIRMATION("user_created_confirmation"),
    USER_CREATED("user_created"),
    USER_UPDATED("user_updated"),
    ;

    private final String template;

    AlertTypeEnum(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
