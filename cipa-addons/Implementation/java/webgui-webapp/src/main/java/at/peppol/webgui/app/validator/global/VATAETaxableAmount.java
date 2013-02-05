package at.peppol.webgui.app.validator.global;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Component;

public class VATAETaxableAmount extends BaseValidation {

  public VATAETaxableAmount (final InvoiceType inv, final Component c) {
    super (inv, c);
    ruleID = "";
    errorMessage = "";

  }

  @Override
  public ValidationError run () {
    // TODO Auto-generated method stub
    return null;
  }

}
