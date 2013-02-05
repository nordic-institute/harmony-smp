package at.peppol.webgui.app.utils;

import java.io.File;
import java.io.IOException;
/*import java.nio.file.FileSystems; //jdk1.7
import java.nio.file.Path; //jdk1.7
import java.nio.file.Paths; //jdk1.7
import java.nio.file.WatchEvent; //jdk1.7
import java.nio.file.WatchKey; //jdk1.7
import java.nio.file.WatchService; //jdk1.7
import java.nio.file.StandardWatchEventKinds.*; //jdk1.7 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
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
	
	public static void validateFormFields(Form form) throws InvalidValueException {
		Collection<String> props = (Collection<String>) form.getItemPropertyIds();
		List<Field> fields = new ArrayList<Field>();
		for (String property : props) {
			fields.add(form.getField(property));
		}
		List<BlurListener> listeners = new ArrayList<BlurListener>();
		for (Field f : fields) {
			if (f instanceof AbstractField) {
				AbstractField ff = (AbstractField)f;
				listeners.addAll((Collection<BlurListener>) ff.getListeners(BlurEvent.class));
			}
		}
		ValidatorsList.validateListenersNotify(listeners);
		form.validate();
	}
	
	public static Collection<BlurListener> getFieldListeners(Form form) {
		Collection<?> propertyIds = form.getItemPropertyIds();
		List<BlurListener> listeners = new ArrayList<BlurListener>(propertyIds.size());
		for (Object itemPropertyId : propertyIds) {
			Field f = form.getField(itemPropertyId);
			if (f instanceof AbstractField) {
				AbstractField field = (AbstractField )f;
				Collection<?> c = field.getListeners(BlurEvent.class);
				for (Object l : c) {
					listeners.add((BlurListener)l);
				}
			}
			
		}
		
		return listeners;
	}
	
	/*public static void registerWatcher(Path dir, Button button) {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			for (;;) {
				// wait for key to be signaled
			    WatchKey key = dir.register(watcher,
			    		java.nio.file.StandardWatchEventKinds.ENTRY_CREATE,
			    		java.nio.file.StandardWatchEventKinds.ENTRY_DELETE,
			    		java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
			    try {
			        key = watcher.take();
			    } catch (InterruptedException x) {
			        return;
			    }
	
			    for (WatchEvent<?> event: key.pollEvents()) {
			        WatchEvent.Kind<?> kind = event.kind();
	
			        if (kind == java.nio.file.StandardWatchEventKinds.OVERFLOW) {
			            continue;
			        }
	
			        WatchEvent<Path> ev = (WatchEvent<Path>)event;
			        Path filename = ev.context();
	
		            Path child = dir.resolve(filename);
	
			        System.out.println("Modified filename: "+filename);
			        System.out.println("Child: "+child);
			        button.click();
			    }
	
			    boolean valid = key.reset();
			    if (!valid) {
			        break;
			    }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
