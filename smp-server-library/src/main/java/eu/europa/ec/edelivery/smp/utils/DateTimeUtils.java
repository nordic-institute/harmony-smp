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

import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Utility class for date operations.
 *
 * @author Joze RIHTARSIC
 * @since 5.1
 */
public class DateTimeUtils {


    private DateTimeUtils() {
        // Utility class
    }

    /**
     * Format OffsetDateTime with locale for given ZoneId. IF ZoneId is null, system default time zone is used.
     *
     * @param offsetDateTime OffsetDateTime to format
     * @param code           Locale code (e.g. "en", "de", "it", "fr", "sl"). If null/empty, "en" is used
     * @param zoneId         ZoneId. If null, system default time zone is used
     * @return formatted OffsetDateTime string representation
     */
    public static String formatOffsetDateTimeWithLocal(OffsetDateTime offsetDateTime, String code, ZoneId zoneId) {

        if (offsetDateTime == null) {
            return null;
        }
        //
        Locale locale = new Locale(StringUtils.isBlank(code) ? "en" : code);
        // Convert to ZonedDateTime using system default time zone
        ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId == null ? ZoneId.systemDefault() : zoneId);
        // DateTimeFormatter with locale
        DateTimeFormatter formatter = DateTimeFormatter.
                ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.LONG)
                .withLocale(locale);

        // Format OffsetDateTime
        return zonedDateTime.format(formatter);
    }

    public static String formatOffsetDateTimeWithLocal(OffsetDateTime offsetDateTime, String code) {
        return formatOffsetDateTimeWithLocal(offsetDateTime, code, ZoneId.systemDefault());
    }

}
