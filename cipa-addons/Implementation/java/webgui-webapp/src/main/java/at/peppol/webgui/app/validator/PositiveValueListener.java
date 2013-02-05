package at.peppol.webgui.app.validator;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;

public class PositiveValueListener extends RequiredFieldListener {

  public PositiveValueListener (final AbstractTextField tf, final String propertyName) {
    super (tf, propertyName);
    this.errorMessage = this.propertyName + " cannot be negative";
  }

  public PositiveValueListener (final AbstractTextField tf, final String propertyName, final String errorMessage) {
    super (tf, propertyName, errorMessage);
  }

  @Override
  public void blur (final BlurEvent event) {
    final String value = (String) tf.getValue ();
    try {
      final double d = Double.parseDouble (value);
      if (d < 0) {
        tf.setComponentError (new UserError (errorMessage));
        passValidation = false;
      }
      else {
        tf.setComponentError (null);
        passValidation = true;
      }
    }
    catch (final Exception e) {
      tf.setComponentError (new UserError (this.propertyName + " must be numeric"));
      passValidation = false;
    }
  }

}
