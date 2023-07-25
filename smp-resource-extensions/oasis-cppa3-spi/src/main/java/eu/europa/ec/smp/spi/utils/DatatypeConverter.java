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
