package at.peppol.webgui.app.validator.global;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class VATTotalAllowancesCharges extends BaseValidation {

  VATTotalAllowancesCharges (final InvoiceType inv) {
    super (inv);
    ruleID = "EUGEN-T10-R006";
    errorMessage = "If the VAT total amount in an invoice exists then "
                   + "an Allowances Charges amount on document level MUST "
                   + "have Tax category for VAT.<br/ >Please review 'Allowances/Charges' and 'Invoice lines' tabs";
  }

  VATTotalAllowancesCharges (final InvoiceType inv, final Component c) {
    super (inv, c);
    ruleID = "EUGEN-T10-R006";
    errorMessage = "If the VAT total amount in an invoice exists then "
                   + "an Allowances Charges amount on document level MUST "
                   + "have Tax category for VAT.<br/ >Please review 'Allowances/Charges' and 'Invoice lines' tabs";
  }

  @Override
  public ValidationError run () {
    boolean vat = false;
    final List <TaxSubtotalType> list = invoice.getTaxTotal ().get (0).getTaxSubtotal ();
    for (final TaxSubtotalType t : list) {
      if (t.getTaxCategory ().getTaxScheme ().getID ().getValue ().equals ("VAT")) {
        vat = true;
        break;
      }
    }

    if (vat) {
      final List <AllowanceChargeType> acList = invoice.getAllowanceCharge ();
      for (final AllowanceChargeType ac : acList) {
        if (ac.getTaxCategory ().get (0).getTaxScheme ().getID ().getValue ().equals ("VAT")) {
          return null;
        }
      }

      final List <InvoiceLineType> ilList = invoice.getInvoiceLine ();
      for (final InvoiceLineType il : ilList) {
        for (final AllowanceChargeType acl : il.getAllowanceCharge ()) {
          if (acl.getTaxCategory ().get (0).getTaxScheme ().getID ().getValue ().equals ("VAT")) {
            return null;
          }
        }
      }

      return error ();
    }

    return null;
  }

}
