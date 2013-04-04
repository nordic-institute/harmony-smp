package eu.europa.ec.cipa.webgui.app.validator.global;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class InvoiceLinesNumberValidation extends BaseValidation {

  public InvoiceLinesNumberValidation (final InvoiceType inv) {
    super (inv);
    ruleID = "BIIRULE-T10-R033";
    errorMessage = "An invoice must specify at least one line item.<br/>Check 'Invoice lines' tab";
  }

  public InvoiceLinesNumberValidation (final InvoiceType inv, final Component c) {
    super (inv, c);
    ruleID = "BIIRULE-T10-R033";
    errorMessage = "An invoice must specify at least one line item.<br/>Check 'Invoice lines' tab";
  }

  @Override
  public ValidationError run () {
    final int linesNum = invoice.getInvoiceLine ().size ();

    if (linesNum > 0) {
      return null;
    }
    else {
      return error ();
    }
  }
}
