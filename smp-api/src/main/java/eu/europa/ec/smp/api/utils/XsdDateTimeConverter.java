package eu.europa.ec.smp.api.utils;

/**
 * Created by migueti on 23/01/2017.
 */
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.GregorianCalendar;

public class XsdDateTimeConverter {

    public static Date unmarshal(String dateTime) {
        return DatatypeConverter.parseDate(dateTime).getTime();
    }

    public static String marshalDate(Date date) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return DatatypeConverter.printDate(calendar);
    }

    public static String marshalDateTime(Date dateTime) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dateTime);
        return DatatypeConverter.printDateTime(calendar);
    }

}
