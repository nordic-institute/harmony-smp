package at.peppol.webgui.app.validator;

import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class PositiveValueValidator implements Validator {

	boolean isParsable = true;
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		if (isValid(value) == false) {
			if (isParsable) {
				throw new InvalidValueException("Field value must be positive");
			}
			else {
				throw new InvalidValueException("Only numerical values");
			}
		}
	}

	@Override
	public boolean isValid(Object value) {
		if (value == null)
			return false;
		if (((String)value).equals(""))
			return false;
		
		try {
			double d = Double.valueOf((String)value);
			if (d < 0)
				return false;
		}catch (NumberFormatException e) {
			isParsable = false;
			return false;
		}
		
		return true;
	}

}
