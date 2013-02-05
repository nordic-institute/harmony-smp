package at.peppol.webgui.app.validator;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;

@SuppressWarnings("serial")
public class RequiredFieldListener implements BlurListener {

	boolean passValidation;
	//AbstractTextField tf;
	String propertyName;
	String errorMessage;
	AbstractField tf;
	
	public RequiredFieldListener(AbstractField tf, String propertyName) {
		this.tf = tf;
		this.propertyName = propertyName;
		this.errorMessage = this.propertyName+" cannot be empty";
		
		passValidation = false;
	}
	
	public RequiredFieldListener(AbstractField tf, String propertyName, String errorMessage) {
		this.tf = tf;
		this.propertyName = propertyName;
		this.errorMessage = errorMessage;
		
		passValidation = false;
	}
	
	public AbstractField getField() {
		return tf;
	}
	
	public boolean isValid() {
		if (tf.getValue() == null)
			return false;
		else if (tf.getValue().equals(""))
			return false;
		else
			return true;
		//return passValidation;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	@Override
	public void blur(BlurEvent event) {
		if (tf.getValue() == null) {
			tf.setComponentError(new UserError(errorMessage));
	     	passValidation = false;
	    }
		else if (tf.getValue() instanceof String) {
			String value = (String)tf.getValue();
	        if (value.equals("")) {
	     	   tf.setComponentError(new UserError(errorMessage));
	     	   passValidation = false;
	        }
	        else {
	        	//tf.validate();
	        	tf.setComponentError(null);
	        	passValidation = true;
	        }
	    }
	}

}
