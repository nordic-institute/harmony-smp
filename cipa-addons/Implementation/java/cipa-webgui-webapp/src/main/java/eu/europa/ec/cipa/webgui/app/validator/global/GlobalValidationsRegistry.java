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

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.cipa.webgui.app.components.InvoiceTabForm;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class GlobalValidationsRegistry {

  private static List <BaseValidation> list = new ArrayList <BaseValidation> ();

  public static void setMainComponents (final InvoiceTabForm invoiceTabForm, final InvoiceType inv) {
    list.add (new InvoiceLinesNumberValidation (inv, invoiceTabForm.getInvoiceLineTab ()));
    list.add (new CrossBorderTradeValidation (inv, invoiceTabForm.getSupplierForm ()));
    list.add (new CrossBorderTradeValidation (inv, invoiceTabForm.getCustomerForm ()));
    list.add (new VATTotalTaxes (inv, invoiceTabForm.getTabInvoiceTaxTotal ()));
    list.add (new VATTotalSupplier (inv, invoiceTabForm.getSupplierForm ()));
    list.add (new VATTotalAllowancesCharges (inv, invoiceTabForm.getTabInvoiceAllowanceCharge ()));
    list.add (new VATTotalAllowancesCharges (inv, invoiceTabForm.getInvoiceLineTab ()));
    list.add (new VATAESupplierCustomer (inv, invoiceTabForm.getSupplierForm ()));
    list.add (new VATAESupplierCustomer (inv, invoiceTabForm.getCustomerForm ()));
    list.add (new VATAEOtherVAT (inv, invoiceTabForm.getInvoiceLineTab ()));
    list.add (new VATAEOtherVAT (inv, invoiceTabForm.getTabInvoiceAllowanceCharge ()));
    list.add (new VATAEOtherVAT (inv, invoiceTabForm.getTabInvoiceTaxTotal ()));
    list.add (new PaymentMeansDueDate (inv, invoiceTabForm.getTabInvoicePayment ()));
    list.add (new VATTotalLines (inv, invoiceTabForm.getInvoiceLineTab ()));
  }

  public static List <ValidationError> runAll () {
    final List <ValidationError> resList = new ArrayList <ValidationError> ();
    for (int i = 0; i < list.size (); i++) {
      final BaseValidation bv = list.get (i);
      final ValidationError error = bv.run ();
      if (error != null)
        // if (!resList.contains(error))
        if (!listContains (error, resList))
          resList.add (error);
    }

    return resList;
  }

  public static boolean listContains (final ValidationError error, final List <ValidationError> list) {
    for (int i = 0; i < list.size (); i++) {
      if (error.getRuleID ().equals (list.get (i).getRuleID ()))
        return true;
    }

    return false;
  }
}
