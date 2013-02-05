package at.peppol.webgui.app.validator.global;

import java.util.ArrayList;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import at.peppol.webgui.app.components.InvoiceTabForm;

import com.vaadin.ui.Component;

public class GlobalValidationsRegistry {
	
	private static List<BaseValidation> list = new ArrayList<BaseValidation>();
	
	public static void setMainComponents(InvoiceTabForm invoiceTabForm, InvoiceType inv) {
		list.add(new InvoiceLinesNumberValidation(inv, invoiceTabForm.getInvoiceLineTab()));
		list.add(new CrossBorderTradeValidation(inv, invoiceTabForm.getSupplierForm()));
		list.add(new CrossBorderTradeValidation(inv, invoiceTabForm.getCustomerForm()));
		list.add(new VATTotalTaxes(inv, invoiceTabForm.getTabInvoiceTaxTotal()));
		list.add(new VATTotalSupplier(inv, invoiceTabForm.getSupplierForm()));
		list.add(new VATTotalAllowancesCharges(inv, invoiceTabForm.getTabInvoiceAllowanceCharge()));
		list.add(new VATTotalAllowancesCharges(inv, invoiceTabForm.getInvoiceLineTab()));
		list.add(new VATAESupplierCustomer(inv, invoiceTabForm.getSupplierForm()));
		list.add(new VATAESupplierCustomer(inv, invoiceTabForm.getCustomerForm()));
		list.add(new VATAEOtherVAT(inv, invoiceTabForm.getInvoiceLineTab()));
		list.add(new VATAEOtherVAT(inv, invoiceTabForm.getTabInvoiceAllowanceCharge()));
		list.add(new VATAEOtherVAT(inv, invoiceTabForm.getTabInvoiceTaxTotal()));
		list.add(new PaymentMeansDueDate(inv, invoiceTabForm.getTabInvoicePayment()));
		list.add(new VATTotalLines(inv, invoiceTabForm.getInvoiceLineTab()));
	}
	
	public static List<ValidationError> runAll() {
		List<ValidationError> resList = new ArrayList<ValidationError>();
		for (int i=0;i<list.size();i++) {
			BaseValidation bv = list.get(i);
			ValidationError error = bv.run();
			if (error != null)
				//if (!resList.contains(error))
				if (!listContains(error, resList))
					resList.add(error);
		}
		
		return resList;
	}
	
	public static boolean listContains(ValidationError error, List<ValidationError> list) {
		for (int i=0;i<list.size();i++) {
			if (error.getRuleID().equals(list.get(i).getRuleID()))
				return true;
		}
		
		return false;
	}
}
