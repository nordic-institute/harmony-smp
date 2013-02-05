package at.peppol.webgui.app.validator.global;

import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class PaymentMeansDueDate extends BaseValidation {

  PaymentMeansDueDate (final InvoiceType inv) {
    super (inv);
    ruleID = "BIIRULE-T10-R006";
    errorMessage = " Payment means due date in an invoice SHOULD be later or equal than issue date.<br/>"
                   + "Check 'Payments' tab.";
  }

  PaymentMeansDueDate (final InvoiceType inv, final Component c) {
    super (inv, c);
    ruleID = "BIIRULE-T10-R006";
    errorMessage = " Payment means due date in an invoice SHOULD be later or equal than issue date.<br/>"
                   + "Check 'Payments' tab.";
  }

  @Override
  public ValidationError run () {
    final XMLGregorianCalendar issueDate = invoice.getIssueDate ().getValue ();
    final List <PaymentMeansType> means = invoice.getPaymentMeans ();
    for (final PaymentMeansType mean : means) {
      final XMLGregorianCalendar dueDate = mean.getPaymentDueDate ().getValue ();
      // System.out.println(issueDate+" -- "+dueDate);
      if (dueDate != null && issueDate != null) {
        final int res = dueDate.compare (issueDate);
        if (res == DatatypeConstants.LESSER) {
          // System.out.println("Due date less than issue date");
          return error ();
        }
      }
    }

    return null;
  }

}
