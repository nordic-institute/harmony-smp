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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class DatatypeConverter {
    @FunctionalInterface
    private interface ConvertToOffsetDateTime {
        OffsetDateTime method(String string);
    }

    static final Logger LOG = LoggerFactory.getLogger(DatatypeConverter.class);

    private static final List<ConvertToOffsetDateTime> PARSER_FORMATS = Arrays.asList(
            value -> OffsetDateTime.parse(value, ISO_DATE_TIME),
            value -> {
                LocalDateTime ldt = LocalDateTime.parse(value, ISO_DATE_TIME);
                return ldt.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            },
            value -> OffsetDateTime.parse(value, ISO_DATE),
            value -> {
                LocalDate ldt = LocalDate.parse(value, ISO_DATE);
                return ldt.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
            });

    protected DatatypeConverter() {
    }

    public static OffsetDateTime parseDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        OffsetDateTime dateTime = PARSER_FORMATS.stream()
                .map(parser -> parseDateTime(value, parser))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);

        if (dateTime == null) {
            LOG.warn("Can not parse date value [{}]!", value);
        }
        return dateTime;
    }

    private static OffsetDateTime parseDateTime(String value, ConvertToOffsetDateTime parser) {
        // first try to pase offset
        try {
            return parser.method(value);
        } catch (DateTimeParseException ex) {
            LOG.debug("Can not parse date [{}], Error: [{}]!", value, ex.getMessage());
        }
        return null;
    }

    public static String printDateTime(OffsetDateTime value) {
        return value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String printDate(OffsetDateTime value) {
        return value.format(DateTimeFormatter.ISO_OFFSET_DATE);
    }
}
