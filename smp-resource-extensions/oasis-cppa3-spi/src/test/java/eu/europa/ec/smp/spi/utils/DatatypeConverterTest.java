package eu.europa.ec.smp.spi.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatatypeConverterTest {

    @ParameterizedTest
    @CsvSource({"2020,1,1,10,0,1, 2020-01-01T10:00:00.000+01:00",
            "2020,1,1,10,0,-1, 2020-01-01T10:00:00.000-01:00",
            "2020,2,1,10,0,1, 2020-02-01T10:00:00.000+01:00",
            "2020,1,2,10,0,1, 2020-01-02T10:00:00.000+01:00",
            "2020,1,1,12,0,1, 2020-01-01T12:00:00.000+01:00",
            "2020,1,1,10,1,1, 2020-01-01T10:01:00.000+01:00",
            "2020,1,1,10,1,1, 2020-01-01T10:01:00+01:00",
            "2020,1,1,10,1,1, 2020-01-01T10:01+01:00",
            "2020,1,1,10,0,0, 2020-01-01T10:00:00Z"})
    void parseDateTime(int year, int mont, int day, int hour, int minutes, int offset, String value) {
        OffsetDateTime dateTime = DatatypeConverter.parseDateTime(value);
        assertNotNull(dateTime);
        assertEquals(year, dateTime.getYear());
        assertEquals(mont, dateTime.getMonthValue());
        assertEquals(day, dateTime.getDayOfMonth());
        assertEquals(hour, dateTime.getHour());
        assertEquals(minutes, dateTime.getMinute());
        assertEquals(offset, dateTime.getOffset().getTotalSeconds() / 3600);
    }

    @Test
    void printDateTime() {

        String value = DatatypeConverter.printDateTime(OffsetDateTime.now());
        assertNotNull(value);
    }

    @Test
    void printDate() {
        String value = DatatypeConverter.printDate(OffsetDateTime.now());
        assertNotNull(value);
    }
}
