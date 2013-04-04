package eu.europa.ec.cipa.webgui.app.utils;

import com.vaadin.ui.TextField;

public class MyField extends TextField {

  boolean requiredFlag = false;

  public MyField (final String caption) {
    super (caption);
  }

  public void setRequiredFlag (final boolean f) {
    requiredFlag = f;
  }

  public boolean getRequiredFlag () {
    return requiredFlag;
  }
}
