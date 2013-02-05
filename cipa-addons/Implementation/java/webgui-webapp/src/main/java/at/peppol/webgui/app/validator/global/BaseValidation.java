package at.peppol.webgui.app.validator.global;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public abstract class BaseValidation {
	protected boolean valid;
	protected String errorMessage;
	protected String ruleID;
	protected InvoiceType invoice;
	protected Component mainForm;
	
	public BaseValidation(InvoiceType inv) {
		valid = false;
		invoice = inv;
		errorMessage = "";
	}
	
	public BaseValidation(InvoiceType inv, Component c) {
		valid = false;
		invoice = inv;
		errorMessage = "";
		this.mainForm = c;
	}
	
	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isValid() {
		return valid;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public ValidationError error() {
		return new ValidationError(ruleID, errorMessage, mainForm);
	}
	
	public abstract ValidationError run();
}
