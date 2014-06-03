package eu.domibus.common.soap;

import eu.domibus.common.util.DateUtil;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateUtilTest {

    private static final String utcdate_string = "2013-11-07T09:30:59";
    private static final Calendar cal = GregorianCalendar.getInstance();

    static {
        // calendar counts month from 0 to 11, 10 = november
        cal.set(2013, 10, 7, 10, 30, 59);

        // CET 0 central europe time, CET = UTC + 1
        cal.setTimeZone(TimeZone.getTimeZone("CET"));
    }


    @Test
    public void testDateToUtc() {
        assertEquals(utcdate_string, DateUtil.dateToUtc(cal.getTime()));
    }

}
