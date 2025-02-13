/*-
 * #START_LICENSE#
 * oasis-cppa3-spi
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
