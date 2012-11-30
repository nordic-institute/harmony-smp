package at.peppol.webgui.app.validator;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;

@SuppressWarnings("serial")
public class RequiredNumericalFieldListener extends RequiredFieldListener {

	public RequiredNumericalFieldListener(AbstractTextField tf, String propertyName) {
		super(tf,propertyName);
		this.errorMessage = this.propertyName+" cannot be empty";
	}
	
	boolean isNumericInput(String value) {
		try {
			double d = Double.parseDouble(value);
		}catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void blur(BlurEvent event) {
	    String value = (String)tf.getValue();
        if (value == null) {
     	   tf.setComponentError(new UserError(errorMessage));
     	   passValidation = false;
        }
        else if (value.equals("")) {
     	   tf.setComponentError(new UserError(errorMessage));
     	   passValidation = false;
        }
        else {
        	if (isNumericInput(value)) {
        		//tf.validate();
        		tf.setComponentError(null);
        		passValidation = true;
        	}
        	else {
        		tf.setComponentError(new UserError(this.propertyName+" must be numeric"));
           	    passValidation = false;
        	}
        }
	}
}
