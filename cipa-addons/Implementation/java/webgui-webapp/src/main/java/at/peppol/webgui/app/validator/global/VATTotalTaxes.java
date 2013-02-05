package at.peppol.webgui.app.validator.global;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class VATTotalTaxes extends BaseValidation {
	
	VATTotalTaxes(InvoiceType inv) {
		super(inv);
		ruleID = "BIIRULE-T10-R028";
		errorMessage = "If the VAT total amount in an invoice exists " +
						"then the sum of taxable amount in sub categories " +
						"MUST equal the sum of invoice tax exclusive amount.<br>" +
						"Please review the 'Tax Total' tab";
	}
	VATTotalTaxes(InvoiceType inv, Component c) {
		super(inv,c);
		ruleID = "BIIRULE-T10-R028";
		errorMessage = "If the VAT total amount in an invoice exists " +
						"then the sum of taxable amount in sub categories " +
						"MUST equal the sum of invoice tax exclusive amount.<br>" +
						"Please review the 'Tax Total' tab";
	}
	
	@Override
	public ValidationError run() {
		BigDecimal total = new BigDecimal(0.00);
		List<TaxSubtotalType> list = invoice.getTaxTotal().get(0).getTaxSubtotal();
		for (TaxSubtotalType t : list) {
			total = total.add(t.getTaxableAmount().getValue());
		}
		
		BigDecimal taxEx = invoice.getLegalMonetaryTotal().getTaxExclusiveAmount().getValue();
		
		if (total.compareTo(taxEx) != 0) {
			return error();
		}
		else {
			return null;
		}
	}

}
