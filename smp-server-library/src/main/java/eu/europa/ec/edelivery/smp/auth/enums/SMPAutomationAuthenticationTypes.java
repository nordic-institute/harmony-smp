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
package eu.europa.ec.edelivery.smp.auth.enums;

import org.slf4j.Logger;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Authentication types for application accounts supporting automated application functionalities. The application accounts
 * are used for SMP web-service integrations.
 * <p>
 * Supported authentication types
 * - BASIC_TOKEN: the application username/password (Note:automation-user authentication is different than ui-user
 * password and it can be used only for web-services!).
 * - CERTIFICATE: certificate authentication username/password.,
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum SMPAutomationAuthenticationTypes {
    BASIC_TOKEN,
    CERTIFICATE,
    JWT;
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SMPAutomationAuthenticationTypes.class);

    /**
     * Returns the enum value for the given string representation.
     *
     * @param type the string representation of the enum value
     * @return the corresponding enum value, or null if the input is blank or does not match any enum value
     */
    public static SMPAutomationAuthenticationTypes fromString(String type) {
        if (isBlank(type)) {
            return null;
        }
        try {
            return SMPAutomationAuthenticationTypes.valueOf(upperCase(trim(type)));
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid SMPAutomationAuthenticationTypes value: [{}]", type);
            return null;
        }
    }
}
