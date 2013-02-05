package at.peppol.webgui.app.validator.global;

import java.util.List;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATAEOtherVAT extends BaseValidation {

	public VATAEOtherVAT(InvoiceType inv) {
		super(inv);
		ruleID = "EUGEN-T10-R016";
		errorMessage = "IF VAT = \"AE\" (reverse charge) THEN VAT MAY NOT contain other VAT categories.<br/>" +
				"Check 'Invoice lines', 'Tax Total', 'Allowances/Charges' tabs";
	}
	public VATAEOtherVAT(InvoiceType inv, Component c) {
		super(inv,c);
		ruleID = "EUGEN-T10-R016";
		errorMessage = "IF VAT = \"AE\" (reverse charge) THEN VAT MAY NOT contain other VAT categories.";
	}
	
	@Override
	public ValidationError run() {
		boolean foundVATAE = false;
		boolean foundVATnonAE = false;
		
		List<TaxSubtotalType> list1 = invoice.getTaxTotal().get(0).getTaxSubtotal();
		List<InvoiceLineType> list2 = invoice.getInvoiceLine();
		List<AllowanceChargeType> list3 = invoice.getAllowanceCharge();
		
		for (TaxSubtotalType ts : list1) {
			if (ts.getTaxCategory().getTaxScheme().getID().getValue().equals("VAT")) {  
				if (ts.getTaxCategory().getID().getValue().equals("AE")	) {
					foundVATAE = true;
				}
				else {
					foundVATnonAE = true;
				}
			}
		}
		for (InvoiceLineType line : list2) {
			if (line.getTaxTotal().get(0).getTaxSubtotal().size() > 0) {
				if (line.getTaxTotal().get(0).getTaxSubtotal().get(0).getTaxCategory().
						getTaxScheme().getID().getValue().equals("VAT")) {
					if (line.getTaxTotal().get(0).getTaxSubtotal().get(0).getTaxCategory().
						getID().getValue().equals("AE")) {
						
						foundVATAE = true;
					}
					else {
						foundVATnonAE = true;
					}
				}
			}
		}
		
		for (AllowanceChargeType ac : list3) {
			if (ac.getTaxCategory().get(0).getTaxScheme().getID().getValue().equals("VAT")) { 
				if (ac.getTaxCategory().get(0).getID().getValue().equals("AE")) {
					foundVATAE = true;
				}
				else {
					foundVATnonAE = true;
				}
			}
		}
		
		
		if (foundVATAE && foundVATnonAE) {
			return error();
		}
		
		return null;
	}

}
