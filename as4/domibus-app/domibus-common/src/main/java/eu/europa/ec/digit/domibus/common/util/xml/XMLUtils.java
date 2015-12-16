package eu.europa.ec.digit.domibus.common.util.xml;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.europa.ec.digit.domibus.common.exception.DomibusProgramException;

public class XMLUtils {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
    public static XMLGregorianCalendar getDateTimeNow() {
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
            return now;
        } catch (DatatypeConfigurationException exception) {
            throw new DomibusProgramException("message.domibus.parsing.error.program.006", exception);
        }
    }

	/* ---- Getters and Setters ---- */

}
