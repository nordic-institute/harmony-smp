package eu.europa.ec.cipa.busdox.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 *
 *@author Flavio Santos
 * */
public class CommonUtil {

    private static final String TIMEZONE_REGEX = "^(\\d{4}-\\d{2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2})(.*)$";
    private static final String DEFAULT_TIMEZONE = "Z";

    public static String getTimezoneFromDate(String date) {
        String newDate = null;

        Pattern pattern = Pattern.compile(TIMEZONE_REGEX);
        Matcher matcher = pattern.matcher(date);

        if (matcher.find()) {
            newDate = matcher.group(1);
            try {
                newDate = newDate + (matcher.group(2).trim().isEmpty() ? "Z" : matcher.group(2));
            } catch (Exception exc) {
                newDate = newDate + "Z";
            }
        }
        return newDate == null ? date : newDate;
    }

    public static Calendar addTimezoneIfNotPresent(String dateStr) throws DatatypeConfigurationException {
        DatatypeFactory DATA_TYPE_FACTORY = DatatypeFactory.newInstance();
        XMLGregorianCalendar xmlGregorianCalendar = DATA_TYPE_FACTORY.newXMLGregorianCalendar(dateStr);
        boolean isTimezoneNotSpecified = DatatypeConstants.FIELD_UNDEFINED == xmlGregorianCalendar.getTimezone();
        if (isTimezoneNotSpecified) {
            xmlGregorianCalendar = DATA_TYPE_FACTORY.newXMLGregorianCalendar(dateStr + DEFAULT_TIMEZONE);
        }
        return xmlGregorianCalendar.toGregorianCalendar();
    }
}
