package at.peppol.webgui.app.validator.global;

import com.vaadin.ui.Component;

public class ValidationError {
	String ruleID;
	String errorInfo;
	Component mainComponent;
	
	public ValidationError(String ruleID, String errorInfo, Component c) {
		this.ruleID = ruleID;
		this.errorInfo = errorInfo;
		this.mainComponent = c;
	}
	
	public String getRuleID() {
		return ruleID;
	}
	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public Component getMainComponent() {
		return mainComponent;
	}
	public void setMainComponent(Component main) {
		this.mainComponent = main;
	}
	
}
