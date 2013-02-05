package at.peppol.webgui.app.validator.global;

import java.math.BigDecimal;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class VATTotalLines extends BaseValidation {

  public VATTotalLines (final InvoiceType inv) {
    super (inv);
    invoice = inv;
    ruleID = "EUGEN-T10-R011";
    errorMessage = "If the VAT total amount in an invoice exists, "
                   + "then each invoice line item MUST have a VAT category ID.<br/>"
                   + "Check 'Invoice lines' tab";
  }

  public VATTotalLines (final InvoiceType inv, final Component c) {
    super (inv, c);
    invoice = inv;
    ruleID = "EUGEN-T10-R011";
    errorMessage = "If the VAT total amount in an invoice exists, "
                   + "then each invoice line item MUST have a VAT category ID.";
  }

  @Override
  public ValidationError run () {
    final BigDecimal total = invoice.getTaxTotal ().get (0).getTaxAmount ().getValue ();
    if (total.doubleValue () > 0) {
      final List <InvoiceLineType> lines = invoice.getInvoiceLine ();
      for (final InvoiceLineType line : lines) {
        if (line.getItem ().getClassifiedTaxCategory ().get (0).getTaxScheme ().getID ().getValue ().equals ("")) {
          return error ();
        }

      }
    }

    return null;
  }

}
