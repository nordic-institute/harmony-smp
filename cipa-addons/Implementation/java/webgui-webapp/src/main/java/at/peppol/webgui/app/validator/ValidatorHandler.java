package at.peppol.webgui.app.validator;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class ValidatorHandler implements ValidationEventHandler {
	
	Component c;
	boolean error;
	
	public ValidatorHandler(Component c) {
		this.c = c;
		error = true;
	}
	
	public void clearErrors() {
		error = true;
	}
	
	public boolean handleEvent(ValidationEvent event) {
        System.out.println("Event");
        System.out.println("Severity:  " + event.getSeverity());
        System.out.println("Message:  " + event.getMessage());
        System.out.println("Linked Exception:  " + event.getLinkedException());
        System.out.println("Locator:::");
        System.out.println("    Line Nbr:  " + event.getLocator().getLineNumber());
        System.out.println("    Column Nbr:  " + event.getLocator().getColumnNumber());
        System.out.println("    Offset:  " + event.getLocator().getOffset());
        System.out.println("    Objct:  " + event.getLocator().getObject());
        System.out.println("    Node:  " + event.getLocator().getNode());
        System.out.println("    URL:  " + event.getLocator().getURL());
		
        if (error) {
        	c.getParent().getWindow().showNotification("Error in document", event.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        	error = false;
        }
		
		return true;
    }

}
