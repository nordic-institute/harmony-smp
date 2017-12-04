package eu.europa.ec.cipa.busdox.util;

import eu.europa.ec.cipa.busdox.exception.DateFormatException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.TimeZone;


/*
 *
 *@author Flavio Santos
 * */
public class CommonUtil {

    private static final String DEFAULT_TIMEZONE = "Z";
    public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone(DEFAULT_TIMEZONE);


    public static Calendar addTimezoneIfNotPresent(String dateStr) throws DateFormatException {
        try {
            DatatypeFactory DATA_TYPE_FACTORY = DatatypeFactory.newInstance();
            XMLGregorianCalendar xmlGregorianCalendar = DATA_TYPE_FACTORY.newXMLGregorianCalendar(dateStr);
            boolean isTimezoneNotSpecified = DatatypeConstants.FIELD_UNDEFINED == xmlGregorianCalendar.getTimezone();
            if (isTimezoneNotSpecified) {
                xmlGregorianCalendar = DATA_TYPE_FACTORY.newXMLGregorianCalendar(dateStr + DEFAULT_TIMEZONE);
            }
            return xmlGregorianCalendar.toGregorianCalendar();
        } catch (DatatypeConfigurationException exc) {
            throw new DateFormatException("Exception while trying to handle timezone for " + dateStr, exc);
        }
    }
}
