package eu.europa.ec.cipa.busdox.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;


/*
 *
 *@author Flavio Santos
 * */
public class CommonUtil {

    private static final String DEFAULT_TIMEZONE = "Z";

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
