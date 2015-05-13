/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.webgui.app.validator.global;

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