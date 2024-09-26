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

import org.hamcrest.MatcherAssert;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.OffsetDateTime;
import java.time.ZoneId;

class DateUtilsTest {

    // test with patterens becase different servers have different default patterns for locales
    // but the expected date/time separators are the smae
    @ParameterizedTest
    @CsvSource({
            "2021-10-15T12:00:00Z, DE, '15.10.(20)?21,? 12:00:00 UTC'",
            "2021-10-15T12:00:00Z, IT, '15/10/(20)?21,? 12(:|.)00(:|.)00 UTC'",
            "2007-12-15T10:15:30+01:00, FR, '15/12/(20)?07,? 09:15:30 UTC'",
            "2021-11-15T12:00:00+02:00, SL, '15.\\s?11.\\s?(20)?21,? 10:00:00 UTC'",
    })
    void testFormatOffsetDateTimeAsLocalFormat(String offsetDateTimeStr, String locale, String expectedPattern) {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(offsetDateTimeStr);
        ZoneId zoneId = ZoneId.of("UTC");
        // Act and Assert
        String result = DateTimeUtils.formatOffsetDateTimeWithLocal(offsetDateTime, locale, zoneId);
        MatcherAssert.assertThat(result, MatchesPattern.matchesPattern(expectedPattern));
    }
}
