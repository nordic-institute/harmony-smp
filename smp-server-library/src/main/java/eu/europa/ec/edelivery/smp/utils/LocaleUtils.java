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

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for locale operations.
 * @since 5.1
 * @author Joze RIHTARSIC
 */
public class LocaleUtils {
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
            LOG.warn("Locale is not set, defaulting to 'en'");
            return "en";
        }
        if (locale.length() != 2) {
            LOG.warn("Invalid locale [{}], defaulting to 'en'", locale);
            return "en";
        }
        return locale;
    }
}
