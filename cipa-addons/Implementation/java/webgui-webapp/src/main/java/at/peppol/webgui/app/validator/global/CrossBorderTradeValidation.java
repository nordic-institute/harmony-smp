package at.peppol.webgui.app.validator.global;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class CrossBorderTradeValidation extends BaseValidation {

	CrossBorderTradeValidation(InvoiceType inv) {
		super(inv);
		ruleID = "BIIRULE-T10-R003(4)";
		errorMessage = "In cross border trade the VAT identifier " +
				"for the Supplier and Customer should be prefixed with country code.";
	}
	
	CrossBorderTradeValidation(InvoiceType inv, Component tab) {
		super(inv,tab);
		ruleID = "BIIRULE-T10-R003(4)";
		errorMessage = "In cross border trade the VAT identifier " +
				"for the Supplier and Customer should be prefixed with country code.";
	}
	
	@Override
	public ValidationError run() {
		String customerCountry = invoice.getAccountingCustomerParty().
									getParty().
									getPostalAddress().
									getCountry().
									getIdentificationCode().
									getValue();
		
		String supplierCountry = invoice.getAccountingSupplierParty().
				getParty().getPostalAddress().getCountry().
				getIdentificationCode().getValue();
		
		if (!supplierCountry.equals(customerCountry)) {
			String customerVAT = invoice.getAccountingCustomerParty().getParty().
									getPartyTaxScheme().get(0).getTaxScheme().getID().getValue();
			
			String supplierVAT = invoice.getAccountingSupplierParty().getParty().
					getPartyTaxScheme().get(0).getTaxScheme().getID().getValue();
			
			if (!customerVAT.trim().startsWith(customerCountry) || 
				!supplierVAT.trim().startsWith(supplierCountry)) {
				return error(); 
			}
		}
		
		return null;
	}

}
