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
package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for locale operations.
 * @since 5.1
 * @author Joze RIHTARSIC
 */
public class LocaleUtils {

    public static final String DEFAULT_LOCALE = "en";

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(LocaleUtils.class);

    private LocaleUtils() {
        // private constructor
    }

    /**
     * Method validates the locale string if locale is valid then returns "en"
     * @param locale localeString to validate
     * @return validated locale or "en" if locale is invalid
     */
    public static String validateLocale(String locale) {
        if (StringUtils.isBlank(locale)) {
            LOG.warn("Locale is not set, defaulting to [{}]", DEFAULT_LOCALE);
            return DEFAULT_LOCALE;
        }
        if (locale.length() != 2) {
            throw new SMPRuntimeException(ErrorMessageType.UI_VALIDATION_LOCALE)
                    .addParam(ErrorMessageArgument.LOCALE, locale);
        }
        return locale;
    }

    public static String getCurrentLocale() {
        SMPUserDetails sessionUserDetails = SessionSecurityUtils.getSessionUserDetails();
        if (sessionUserDetails != null && sessionUserDetails.getUser() != null) {
            return validateLocale(sessionUserDetails.getUser().getSmpLocale());
        }
        return DEFAULT_LOCALE;
    }
}
