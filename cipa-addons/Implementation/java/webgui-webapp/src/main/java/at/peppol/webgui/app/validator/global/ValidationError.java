package at.peppol.webgui.app.validator.global;

import com.vaadin.ui.Component;

public class ValidationError {
  String ruleID;
  String errorInfo;
  Component mainComponent;

  public ValidationError (final String ruleID, final String errorInfo, final Component c) {
    this.ruleID = ruleID;
    this.errorInfo = errorInfo;
    this.mainComponent = c;
  }

  public String getRuleID () {
    return ruleID;
  }

  public void setRuleID (final String ruleID) {
    this.ruleID = ruleID;
  }

  public String getErrorInfo () {
    return errorInfo;
  }

  public void setErrorInfo (final String errorInfo) {
    this.errorInfo = errorInfo;
  }

  public Component getMainComponent () {
    return mainComponent;
  }

  public void setMainComponent (final Component main) {
    this.mainComponent = main;
  }

}
