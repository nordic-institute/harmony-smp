package at.peppol.webgui.app.validator;

import com.vaadin.data.Validator;

public class RequiredFieldValidator implements Validator {

  @Override
  public void validate (final Object value) throws InvalidValueException {
    // TODO Auto-generated method stub
    if (isValid (value) == false)
      throw new InvalidValueException ("Field of length > 1 is required");

  }

  @Override
  public boolean isValid (final Object value) {
    // TODO Auto-generated method stub
    if (value == null)
      return false;

    final String input = (String) value;
    if (input.length () < 2)
      return false;

    return true;
  }

}
