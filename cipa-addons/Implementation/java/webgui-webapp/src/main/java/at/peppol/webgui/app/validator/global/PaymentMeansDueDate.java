package at.peppol.webgui.app.validator.global;

import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.vaadin.ui.Component;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class PaymentMeansDueDate extends BaseValidation {

	PaymentMeansDueDate(InvoiceType inv) {
		super(inv);
		ruleID = "BIIRULE-T10-R006";
		errorMessage = " Payment means due date in an invoice SHOULD be later or equal than issue date.<br/>" +
				"Check 'Payments' tab.";
	}
	PaymentMeansDueDate(InvoiceType inv, Component c) {
		super(inv,c);
		ruleID = "BIIRULE-T10-R006";
		errorMessage = " Payment means due date in an invoice SHOULD be later or equal than issue date.<br/>" +
				"Check 'Payments' tab.";
	}
	
	@Override
	public ValidationError run() {
		 XMLGregorianCalendar issueDate = invoice.getIssueDate().getValue();
		 List<PaymentMeansType> means = invoice.getPaymentMeans();
		 for (PaymentMeansType mean : means) {
			 XMLGregorianCalendar dueDate = mean.getPaymentDueDate().getValue();
			 //System.out.println(issueDate+" -- "+dueDate);
			 if (dueDate != null && issueDate != null) {
				 int res = dueDate.compare(issueDate);
				 if (res == DatatypeConstants.LESSER) {
					 //System.out.println("Due date less than issue date");
					 return error();
				 }
			 }
		 }
		
		 return null;
	}

}
