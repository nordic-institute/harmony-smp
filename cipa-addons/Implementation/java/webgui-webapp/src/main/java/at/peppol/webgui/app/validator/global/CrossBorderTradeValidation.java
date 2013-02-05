package at.peppol.webgui.app.validator.global;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class CrossBorderTradeValidation extends BaseValidation {

  CrossBorderTradeValidation (final InvoiceType inv) {
    super (inv);
    ruleID = "BIIRULE-T10-R003(4)";
    errorMessage = "In cross border trade the VAT identifier "
                   + "for the Supplier and Customer should be prefixed with country code.";
  }

  CrossBorderTradeValidation (final InvoiceType inv, final Component tab) {
    super (inv, tab);
    ruleID = "BIIRULE-T10-R003(4)";
    errorMessage = "In cross border trade the VAT identifier "
                   + "for the Supplier and Customer should be prefixed with country code.";
  }

  @Override
  public ValidationError run () {
    final String customerCountry = invoice.getAccountingCustomerParty ()
                                          .getParty ()
                                          .getPostalAddress ()
                                          .getCountry ()
                                          .getIdentificationCode ()
                                          .getValue ();

    final String supplierCountry = invoice.getAccountingSupplierParty ()
                                          .getParty ()
                                          .getPostalAddress ()
                                          .getCountry ()
                                          .getIdentificationCode ()
                                          .getValue ();

    if (!supplierCountry.equals (customerCountry)) {
      final String customerVAT = invoice.getAccountingCustomerParty ()
                                        .getParty ()
                                        .getPartyTaxScheme ()
                                        .get (0)
                                        .getTaxScheme ()
                                        .getID ()
                                        .getValue ();

      final String supplierVAT = invoice.getAccountingSupplierParty ()
                                        .getParty ()
                                        .getPartyTaxScheme ()
                                        .get (0)
                                        .getTaxScheme ()
                                        .getID ()
                                        .getValue ();

      if (!customerVAT.trim ().startsWith (customerCountry) || !supplierVAT.trim ().startsWith (supplierCountry)) {
        return error ();
      }
    }

    return null;
  }

}
