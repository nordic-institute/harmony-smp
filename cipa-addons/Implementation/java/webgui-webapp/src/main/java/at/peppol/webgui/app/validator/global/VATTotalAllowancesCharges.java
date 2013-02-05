package at.peppol.webgui.app.validator.global;

import java.util.List;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATTotalAllowancesCharges extends BaseValidation {

	VATTotalAllowancesCharges(InvoiceType inv) {
		super(inv);
		ruleID = "EUGEN-T10-R006";
		errorMessage = "If the VAT total amount in an invoice exists then " +
						"an Allowances Charges amount on document level MUST " +
						"have Tax category for VAT.<br/ >Please review 'Allowances/Charges' and 'Invoice lines' tabs";
	}
	VATTotalAllowancesCharges(InvoiceType inv, Component c) {
		super(inv,c);
		ruleID = "EUGEN-T10-R006";
		errorMessage = "If the VAT total amount in an invoice exists then " +
						"an Allowances Charges amount on document level MUST " +
						"have Tax category for VAT.<br/ >Please review 'Allowances/Charges' and 'Invoice lines' tabs";
	}
	
	@Override
	public ValidationError run() {
		boolean vat = false;
		List<TaxSubtotalType> list = invoice.getTaxTotal().get(0).getTaxSubtotal();
		for (TaxSubtotalType t : list) {
			if (t.getTaxCategory().getTaxScheme().getID().getValue().equals("VAT")) {
				vat = true;
				break;
			}
		}
		
		if (vat) {
			List<AllowanceChargeType> acList = invoice.getAllowanceCharge();
			for (AllowanceChargeType ac : acList) {
				if (ac.getTaxCategory().get(0).getTaxScheme().getID().getValue().equals("VAT")) {
					return null;
				}
			}
			
			List<InvoiceLineType> ilList= invoice.getInvoiceLine();
			for (InvoiceLineType il : ilList) {
				for (AllowanceChargeType acl : il.getAllowanceCharge()) {
					if (acl.getTaxCategory().get(0).getTaxScheme().getID().getValue().equals("VAT")) {
						return null;
					}
				}
			}
			
			return error();
		}
		
		return null;
	}

}
