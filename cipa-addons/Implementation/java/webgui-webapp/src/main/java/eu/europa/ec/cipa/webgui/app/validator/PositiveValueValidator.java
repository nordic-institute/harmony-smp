package eu.europa.ec.cipa.webgui.app.validator;

import java.math.BigDecimal;

import com.phloc.commons.string.StringParser;
import com.vaadin.data.Validator;

@SuppressWarnings ("serial")
public class PositiveValueValidator implements Validator {

  boolean isParsable = true;

  @Override
  public void validate (final Object value) throws InvalidValueException {
    if (isValid (value) == false) {
      if (isParsable) {
        throw new InvalidValueException ("Field value must be positive");
      }
      else {
        throw new InvalidValueException ("Only numerical values");
      }
    }
  }

  @Override
  public boolean isValid (final Object value) {
    if (value == null)
      return false;
    // if (((String)value).equals(""))
    // return false;
    if (value instanceof String) {
      try {
        final double d = StringParser.parseDouble ((String) value, -1);
        if (d < 0) {
          isParsable = true;
          return false;
        }
      }
      catch (final NumberFormatException e) {
        isParsable = false;
        return false;
      }
    }
    else
      if (value instanceof BigDecimal) {
        try {
          final BigDecimal test = (BigDecimal) value;
          if (test.compareTo (BigDecimal.ZERO) == -1) {
            isParsable = true;
            return false;
          }
        }
        catch (final Exception e) {
          isParsable = false;
          return false;
        }
      }

    return true;
  }

}
