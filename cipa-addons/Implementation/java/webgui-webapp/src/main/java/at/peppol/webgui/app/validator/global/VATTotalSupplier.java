package at.peppol.webgui.app.validator.global;

import java.util.List;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATTotalSupplier extends BaseValidation {
	
	VATTotalSupplier(InvoiceType inv) {
		super(inv);
		ruleID = "EUGEN-T10-R007";
		errorMessage = "If the VAT total amount in an invoice exists, it MUST contain the suppliers VAT number";
	}
	
	VATTotalSupplier(InvoiceType inv, Component c) {
		super(inv,c);
		ruleID = "EUGEN-T10-R007";
		errorMessage = "If the VAT total amount in an invoice exists, it MUST contain the suppliers VAT number";
	}
	
	@Override
	public ValidationError run() {
		boolean flag = false;
		List<TaxSubtotalType> list = invoice.getTaxTotal().get(0).getTaxSubtotal();
		for (TaxSubtotalType t : list) {
			if (t.getTaxCategory().getTaxScheme().getID().getValue().trim().equals("VAT")) {
				flag = true;
				break;
			}
		}
		
		if (flag) {
			String taxScheme = invoice.getAccountingSupplierParty().getParty().
				 getPartyTaxScheme().get(0).getTaxScheme().getID().getValue();
			
			String taxID = invoice.getAccountingSupplierParty().getParty().
					 getPartyTaxScheme().get(0).getCompanyID().getValue();
			
			if (taxScheme != null) {
				if (!taxScheme.equals("VAT"))
					return error();
			}
			else {
				return error();
			}
				
			
			if (taxID != null) {
				if (taxID.equals(""))
					return error();
			}
			else
				return error();
		}
				
		return null;
	}

}
