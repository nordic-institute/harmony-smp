package at.peppol.webgui.app.validator;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;

public class PositiveValueListener extends RequiredFieldListener {

	public PositiveValueListener(AbstractTextField tf, String propertyName) {
		super(tf,propertyName);
		this.errorMessage = this.propertyName+" cannot be negative";
	}
	
	public PositiveValueListener(AbstractTextField tf, String propertyName, String errorMessage) {
		super(tf,propertyName,errorMessage);
	}
	
	@Override
	public void blur(BlurEvent event) {
		String value = (String)tf.getValue();
		try {
			double d = Double.parseDouble(value);
			if (d<0) {
				tf.setComponentError(new UserError(errorMessage));
				passValidation = false;
			}
			else {
				tf.setComponentError(null);
				passValidation = true;
			}
		}catch (Exception e) {
			tf.setComponentError(new UserError(this.propertyName+" must be numeric"));
			passValidation = false;
		}
	}

}
