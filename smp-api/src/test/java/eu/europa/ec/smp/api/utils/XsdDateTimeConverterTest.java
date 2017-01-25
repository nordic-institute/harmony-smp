package eu.europa.ec.smp.api.utils;

import eu.europa.ec.smp.api.utils.XsdDateTimeConverter;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by migueti on 23/01/2017.
 */
public class XsdDateTimeConverterTest {

    @Test
    public void testUnmarshallSuccess() {
        // given

        // when
        Date unmarshal = XsdDateTimeConverter.unmarshal("2013-06-06T11:06:02.000+02:00");

        // then
        assertNotNull(unmarshal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnmarshallFailure() {
        // given

        // when
        Date unmarshal = XsdDateTimeConverter.unmarshal("2013-06A-0611:06:02.000+02:00");

        // then
        assertNotNull(unmarshal);
    }

    @Test
    public void testMarshalDate() {
        // given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2013,10,10);
        Date date = calendar.getTime();

        // when
        String marshal = XsdDateTimeConverter.marshalDate(date);

        // then
        assertEquals("2013-11-10+01:00", marshal);
    }


    @Test
    public void testMarshalDateTime() {
        // given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(1000);  // 1970-01-01T01:00:01+01:00
        Date date = calendar.getTime();

        // when
        String marshal = XsdDateTimeConverter.marshalDateTime(date);

        // then
        assertEquals("1970-01-01T01:00:01+01:00", marshal);
    }
}
