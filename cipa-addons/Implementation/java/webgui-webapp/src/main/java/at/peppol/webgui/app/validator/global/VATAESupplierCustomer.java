package at.peppol.webgui.app.validator.global;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class VATAESupplierCustomer extends BaseValidation {

  public VATAESupplierCustomer (final InvoiceType inv) {
    super (inv);
    ruleID = "EUGEN-T10-R015";
    errorMessage = "IF VAT = \"AE\" (reverse charge) THEN it MUST contain Supplier VAT id and Customer VAT.<br/>"
                   + "Please review 'Customer' and 'Supplier' tabs";
  }

  public VATAESupplierCustomer (final InvoiceType inv, final Component c) {
    super (inv, c);
    ruleID = "EUGEN-T10-R015";
    errorMessage = "IF VAT = \"AE\" (reverse charge) THEN it MUST contain Supplier VAT id and Customer VAT.<br/>"
                   + "Please review 'Customer' and 'Supplier' tabs";
  }

  @Override
  public ValidationError run () {
    boolean flag = false;
    final List <TaxSubtotalType> list1 = invoice.getTaxTotal ().get (0).getTaxSubtotal ();
    final List <InvoiceLineType> list2 = invoice.getInvoiceLine ();
    final List <AllowanceChargeType> list3 = invoice.getAllowanceCharge ();

    for (final TaxSubtotalType ts : list1) {
      if (ts.getTaxCategory ().getTaxScheme ().getID ().getValue ().equals ("VAT") &&
          ts.getTaxCategory ().getID ().getValue ().equals ("AE")) {

        flag = true;
        break;
      }
    }
    if (!flag) {
      for (final InvoiceLineType line : list2) {
        if (line.getTaxTotal ().get (0).getTaxSubtotal ().size () > 0) {
          if (line.getTaxTotal ()
                  .get (0)
                  .getTaxSubtotal ()
                  .get (0)
                  .getTaxCategory ()
                  .getTaxScheme ()
                  .getID ()
                  .getValue ()
                  .equals ("VAT") &&
              line.getTaxTotal ()
                  .get (0)
                  .getTaxSubtotal ()
                  .get (0)
                  .getTaxCategory ()
                  .getID ()
                  .getValue ()
                  .equals ("AE")) {

            flag = true;
            break;
          }
        }
      }
    }
    if (!flag) {
      for (final AllowanceChargeType ac : list3) {

        if (ac.getTaxCategory ().get (0).getTaxScheme ().getID ().getValue ().equals ("VAT") &&
            ac.getTaxCategory ().get (0).getID ().getValue ().equals ("AE")) {

          flag = true;
          break;
        }
      }
    }

    if (flag) {
      if (invoice.getAccountingSupplierParty ().getParty ().getPartyTaxScheme ().get (0).getCompanyID () == null ||
          invoice.getAccountingSupplierParty ()
                 .getParty ()
                 .getPartyTaxScheme ()
                 .get (0)
                 .getCompanyID ()
                 .getValue ()
                 .trim ()
                 .equals ("") ||
          invoice.getAccountingCustomerParty ().getParty ().getPartyTaxScheme ().get (0).getCompanyID () == null ||
          invoice.getAccountingCustomerParty ()
                 .getParty ()
                 .getPartyTaxScheme ()
                 .get (0)
                 .getCompanyID ()
                 .getValue ()
                 .trim ()
                 .equals (""))

        return error ();
    }

    return null;
  }

}
