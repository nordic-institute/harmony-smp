package at.peppol.webgui.app.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.vaadin.ui.Label;

public class Utils {
	
	public static XMLGregorianCalendar DateToGregorian(Date date) {
		GregorianCalendar greg = new GregorianCalendar();
        greg.setTime(date);

        XMLGregorianCalendar XMLDate = null;
		try {
			XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			XMLDate.setYear(greg.get(Calendar.YEAR));
	        XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
	        XMLDate.setDay(greg.get(Calendar.DATE));
		} catch (DatatypeConfigurationException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
		}
        
		return XMLDate;
	}
	
	public static Label requiredLabel(String text) {
		return new Label("<span>"+text+" <span style=\"color: red;\">*</span></span>", Label.CONTENT_XHTML);
	}
}
