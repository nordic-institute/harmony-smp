package at.peppol.webgui.app.validator.global;

import com.vaadin.ui.Component;

import at.peppol.webgui.app.components.InvoiceTabForm;
import at.peppol.webgui.app.components.TabInvoiceLine;

public class InvoiceLinesNumberValidation extends BaseValidation {

	public InvoiceLinesNumberValidation(Component c) {
		super(c);
		ruleID = "BIIRULE-T10-R033";
		errorMessage = "An invoice must specify at least one line item";
	}
	
	
	
	@Override
	public String run() {
		int linesNum = 0;
		
		if (mainComponent instanceof InvoiceTabForm) {
			InvoiceTabForm tab = (InvoiceTabForm)mainComponent;
		
			linesNum = tab.getInvoiceLineTab().getInvoiceLineList().size();
		}
		else if (mainComponent instanceof TabInvoiceLine) {
			TabInvoiceLine tab = (TabInvoiceLine)mainComponent;
			linesNum = tab.getInvoiceLineList().size();
		}
		if (linesNum > 0)
			return null;
		else
			return ruleID+": "+errorMessage;
	}
}
