package at.peppol.webgui.app.validator.global;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATTotalLines extends BaseValidation {

	public VATTotalLines(InvoiceType inv) {
		super(inv);
		invoice = inv;
		ruleID = "EUGEN-T10-R011";
		errorMessage = "If the VAT total amount in an invoice exists, " +
						"then each invoice line item MUST have a VAT category ID.<br/>" +
						"Check 'Invoice lines' tab";
	}
	public VATTotalLines(InvoiceType inv, Component c) {
		super(inv,c);
		invoice = inv;
		ruleID = "EUGEN-T10-R011";
		errorMessage = "If the VAT total amount in an invoice exists, " +
						"then each invoice line item MUST have a VAT category ID.";
	}
	
	@Override
	public ValidationError run() {
		BigDecimal total = invoice.getTaxTotal().get(0).getTaxAmount().getValue();
		if (total.doubleValue() > 0) {
			List<InvoiceLineType> lines = invoice.getInvoiceLine();
			for (InvoiceLineType line : lines) {
				if (line.getItem().getClassifiedTaxCategory().
						get(0).getTaxScheme().getID().getValue().equals("")) {
					return error();
				}
					 
			}
		}
		
		return null;
	}

}
