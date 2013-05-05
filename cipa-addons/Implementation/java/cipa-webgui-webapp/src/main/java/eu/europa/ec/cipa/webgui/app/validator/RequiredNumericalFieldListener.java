package eu.europa.ec.cipa.webgui.app.validator;

import com.phloc.commons.string.StringParser;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;

@SuppressWarnings ("serial")
public class RequiredNumericalFieldListener extends RequiredFieldListener {

  public RequiredNumericalFieldListener (final AbstractTextField tf, final String propertyName) {
    super (tf, propertyName);
    this.errorMessage = this.propertyName + " cannot be empty";
  }

  boolean isNumericInput (final String value) {
    return StringParser.isDouble (value);
  }

  @Override
  public void blur (final BlurEvent event) {
    final String value = (String) tf.getValue ();
    if (value == null) {
      tf.setComponentError (new UserError (errorMessage));
      passValidation = false;
    }
    else
      if (value.length () == 0) {
        tf.setComponentError (new UserError (errorMessage));
        passValidation = false;
      }
      else {
        if (isNumericInput (value)) {
          // tf.validate();
          tf.setComponentError (null);
          passValidation = true;
        }
        else {
          tf.setComponentError (new UserError (this.propertyName + " must be numeric"));
          passValidation = false;
        }
      }
  }
}
