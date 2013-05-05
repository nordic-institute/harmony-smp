package eu.europa.ec.cipa.webgui.app.validator.global;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class VATTotalSupplier extends BaseValidation {

  VATTotalSupplier (final InvoiceType inv) {
    super (inv);
    ruleID = "EUGEN-T10-R007";
    errorMessage = "If the VAT total amount in an invoice exists, it MUST contain the suppliers VAT number";
  }

  VATTotalSupplier (final InvoiceType inv, final Component c) {
    super (inv, c);
    ruleID = "EUGEN-T10-R007";
    errorMessage = "If the VAT total amount in an invoice exists, it MUST contain the suppliers VAT number";
  }

  @Override
  public ValidationError run () {
    boolean flag = false;
    final List <TaxSubtotalType> list = invoice.getTaxTotal ().get (0).getTaxSubtotal ();
    for (final TaxSubtotalType t : list) {
      if (t.getTaxCategory ().getTaxScheme ().getID ().getValue ().trim ().equals ("VAT")) {
        flag = true;
        break;
      }
    }

    if (flag) {
      final String taxScheme = invoice.getAccountingSupplierParty ()
                                      .getParty ()
                                      .getPartyTaxScheme ()
                                      .get (0)
                                      .getTaxScheme ()
                                      .getID ()
                                      .getValue ();

      final String taxID = invoice.getAccountingSupplierParty ()
                                  .getParty ()
                                  .getPartyTaxScheme ()
                                  .get (0)
                                  .getCompanyID ()
                                  .getValue ();

      if (taxScheme != null) {
        if (!taxScheme.equals ("VAT"))
          return error ();
      }
      else {
        return error ();
      }

      if (taxID != null) {
        if (taxID.equals (""))
          return error ();
      }
      else
        return error ();
    }

    return null;
  }

}
