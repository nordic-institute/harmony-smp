package eu.europa.ec.edelivery.smp.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "2021-06-01T12:00:00Z, US,'2021-06-01 12:00:00 UTC'",
            "2021-06-01T12:00:00Z, DE, '01.06.21, 12:00:00 UTC'",
            "2021-06-01T12:00:00Z, IT, '01/06/21, 12:00:00 UTC'",
            "2007-12-03T10:15:30+01:00, FR, '03/12/2007 09:15:30 UTC'",
            "2021-06-01T12:00:00+02, SL, '1. 06. 21 10:00:00 UTC'",
    })
    void testFormatOffsetDateTimeAsLocalFormat(String offsetDateTimeStr, String locale, String expected) {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(offsetDateTimeStr);
        ZoneId zoneId = ZoneId.of("UTC");
        // Act and Assert
        assertEquals(expected, DateTimeUtils.formatOffsetDateTimeWithLocal(offsetDateTime, locale, zoneId));
    }
}
