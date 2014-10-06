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
package eu.europa.ec.cipa.webgui.app.components.tables;

import java.math.BigDecimal;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Select;

import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceAllowanceChargeAdapter;

public class InvoiceLineAllowanceChargeTableEditor extends
                                                  GenericTableEditor <AllowanceChargeType, InvoiceAllowanceChargeAdapter> {

  public InvoiceLineAllowanceChargeTableEditor (final boolean editMode) {
    super (editMode);
  }

  public InvoiceLineAllowanceChargeTableEditor (final boolean editMode, final InvoiceType inv) {
    super (editMode, inv);
  }

  @Override
  public Form createTableForm (final InvoiceAllowanceChargeAdapter allowanceChargeItem,
                               final List <AllowanceChargeType> invoiceList) {

    final Form invoiceAllowanceChargeForm = new Form (new FormLayout (), new InvoiceLineAllowanceChargeFieldFactory ());
    invoiceAllowanceChargeForm.setImmediate (true);

    final NestedMethodProperty mp = new NestedMethodProperty (allowanceChargeItem, "ID.value");
    if (!editMode) {
      final IDType num = new IDType ();
      int max = 0;
      for (final AllowanceChargeType doc : invoiceList) {
        if (Integer.parseInt (doc.getID ().getValue ()) > max)
          max = Integer.parseInt (doc.getID ().getValue ());
      }
      num.setValue (String.valueOf (max + 1));
      allowanceChargeItem.setID (num);
    }
    else {
      mp.setReadOnly (true);
    }

    // invoiceAllowanceChargeForm.addItemProperty ("Line ID #", new
    // NestedMethodProperty(allowanceChargeItem, "ID.value") );
    // invoiceAllowanceChargeForm.addItemProperty ("Line ID #", mp);
    invoiceAllowanceChargeForm.addItemProperty ("Charge Indicator", new NestedMethodProperty (allowanceChargeItem,
                                                                                              "indicator"));
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Reason",
                                                new NestedMethodProperty (allowanceChargeItem, "reason"));
    invoiceAllowanceChargeForm.addItemProperty ("Allowance Charge Amount",
                                                new NestedMethodProperty (allowanceChargeItem, "chargeAmount"));

    return invoiceAllowanceChargeForm;

  }

  @SuppressWarnings ("serial")
  static final class InvoiceLineAllowanceChargeFieldFactory implements FormFieldFactory {

    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Charge Indicator".equals (pid)) {
        final Select indicatorSelect = new Select ("Charge or Allowance?");
        indicatorSelect.setNullSelectionAllowed (false);
        indicatorSelect.addItem (Boolean.TRUE);
        indicatorSelect.addItem (Boolean.FALSE);
        indicatorSelect.setItemCaption (Boolean.TRUE, "Charge");
        indicatorSelect.setItemCaption (Boolean.FALSE, "Allowance");

        return indicatorSelect;
      }

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        tf.addListener (new FieldEvents.FocusListener () {
          @Override
          public void focus (final FocusEvent event) {
            tf.selectAll ();
          }
        });
      }
      return field;
    }
  }

  @Override
  public InvoiceAllowanceChargeAdapter createItem () {
    final InvoiceAllowanceChargeAdapter ac = new InvoiceAllowanceChargeAdapter ();

    // ac.setID (new IDType ());
    ac.setIndicator (Boolean.FALSE);
    ac.setReason ("");
    ac.setChargeAmount (BigDecimal.ZERO);

    ac.getAmount ().setCurrencyID (CurrencyCodeContentType.valueOf (invoice.getDocumentCurrencyCode ().getValue ()));

    return ac;

  }

  @Override
  public void cloneItem (final InvoiceAllowanceChargeAdapter srcItem, final InvoiceAllowanceChargeAdapter dstItem) {

    // dstItem.setAllowanceChargeID (srcItem.getAllowanceChargeID ());
    dstItem.setIndicator (srcItem.getIndicator ());
    dstItem.setReason (srcItem.getReason ());
    dstItem.setChargeAmount (srcItem.getChargeAmount ());
  }

}
