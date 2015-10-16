package eu.domibus.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Util class for date formatting
 *
 * @author muell16
 */
public class DateUtil {

    final static SimpleDateFormat fullDateTimePatternFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    static {
        DateUtil.fullDateTimePatternFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Formats a given date with pattern {@link Util#fullDateTimePatternFormatter} in Timezone UTC
     *
     * @param date a given date object
     * @return date string formatted with pattern {@link Util#fullDateTimePatternFormatter} in Timezone UTC
     */
    public static String dateToUtc(final Date date) {
        if (date == null) {
            return null;
        }
        return DateUtil.fullDateTimePatternFormatter.format(date);
    }

}
