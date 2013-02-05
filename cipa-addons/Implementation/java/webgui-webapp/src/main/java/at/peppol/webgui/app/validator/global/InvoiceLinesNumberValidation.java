package at.peppol.webgui.app.validator.global;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

import at.peppol.webgui.app.components.InvoiceTabForm;
import at.peppol.webgui.app.components.TabInvoiceLine;

public class InvoiceLinesNumberValidation extends BaseValidation {

	public InvoiceLinesNumberValidation(InvoiceType inv) {
		super(inv);
		ruleID = "BIIRULE-T10-R033";
		errorMessage = "An invoice must specify at least one line item.<br/>Check 'Invoice lines' tab";
	}
	
	public InvoiceLinesNumberValidation(InvoiceType inv, Component c) {
		super(inv,c);
		ruleID = "BIIRULE-T10-R033";
		errorMessage = "An invoice must specify at least one line item.<br/>Check 'Invoice lines' tab";
	}
	
	@Override
	public ValidationError run() {
		int linesNum = invoice.getInvoiceLine().size();
		
		if (linesNum > 0) {
			return null;
		}
		else {
			return error();
		}
	}
}
