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
package at.peppol.webgui.app.components;

import java.math.BigDecimal;
import java.util.Collection;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableRoundingAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PrepaidAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxExclusiveAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxInclusiveAmountType;

import at.peppol.webgui.app.validator.PositiveValueListener;
import at.peppol.webgui.app.validator.PositiveValueValidator;
import at.peppol.webgui.app.validator.RequiredFieldListener;
import at.peppol.webgui.app.validator.RequiredNumericalFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class TabInvoiceMonetaryTotal extends Form {
  private final InvoiceTabForm parent;

  private MonetaryTotalType monetaryTotal;

  public TabInvoiceMonetaryTotal (final InvoiceTabForm parent) {
    this.parent = parent;
    initElements ();
  }

  private void initElements () {
    // monetaryTotal = parent.getInvoice().getLegalMonetaryTotal ();
    monetaryTotal = createMonetaryTotal ();
    parent.getInvoice ().setLegalMonetaryTotal (monetaryTotal);

    final GridLayout grid = new GridLayout (4, 4);
    final VerticalLayout outerLayout = new VerticalLayout ();

    final Panel outerPanel = new Panel ("Monetary Total");
    outerPanel.addComponent (grid);
    outerPanel.setScrollable (true);
    outerLayout.addComponent (outerPanel);

    final Panel invoiceDetailsPanel = new Panel ("Monetary Total Details");
    invoiceDetailsPanel.setStyleName ("light");
    invoiceDetailsPanel.setSizeFull ();
    invoiceDetailsPanel.addComponent (createInvoiceMonetaryTotalTopForm ());
    grid.addComponent (invoiceDetailsPanel, 0, 0, 3, 0);
    grid.setSizeUndefined ();

    setLayout (outerLayout);
    outerPanel.requestRepaintAll ();
  }

  private MonetaryTotalType createMonetaryTotal () {
    final MonetaryTotalType mt = new MonetaryTotalType ();
    mt.setLineExtensionAmount (new LineExtensionAmountType ());
    mt.getLineExtensionAmount ().setValue (BigDecimal.ZERO);

    mt.setTaxExclusiveAmount (new TaxExclusiveAmountType ());
    mt.getTaxExclusiveAmount ().setValue (BigDecimal.ZERO);

    mt.setTaxInclusiveAmount (new TaxInclusiveAmountType ());
    mt.getTaxInclusiveAmount ().setValue (BigDecimal.ZERO);

    mt.setAllowanceTotalAmount (new AllowanceTotalAmountType ());
    mt.getAllowanceTotalAmount ().setValue (BigDecimal.ZERO);

    mt.setChargeTotalAmount (new ChargeTotalAmountType ());
    mt.getChargeTotalAmount ().setValue (BigDecimal.ZERO);

    mt.setPrepaidAmount (new PrepaidAmountType ());
    mt.getPrepaidAmount ().setValue (BigDecimal.ZERO);

    mt.setPayableRoundingAmount (new PayableRoundingAmountType ());
    mt.getPayableRoundingAmount ().setValue (BigDecimal.ZERO);

    mt.setPayableAmount (new PayableAmountType ());
    mt.getPayableAmount ().setValue (BigDecimal.ZERO);

    return mt;
  }

  public Form createInvoiceMonetaryTotalTopForm () {
    final Form invoiceMonetaryTotalTopForm = new Form (new FormLayout (), new InvoiceMonetaryTotalFieldFactory ());
    invoiceMonetaryTotalTopForm.setImmediate (true);

    // TODO: Update fields automatically. Make them read only !
    invoiceMonetaryTotalTopForm.addItemProperty ("Line Extension Amount",
                                                 new NestedMethodProperty (monetaryTotal.getLineExtensionAmount (),
                                                                           "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Tax Exclusive Amount",
                                                 new NestedMethodProperty (monetaryTotal.getTaxExclusiveAmount (),
                                                                           "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Tax Inclusive Amount",
                                                 new NestedMethodProperty (monetaryTotal.getTaxInclusiveAmount (),
                                                                           "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Allowance Total Amount",
                                                 new NestedMethodProperty (monetaryTotal.getAllowanceTotalAmount (),
                                                                           "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Charge Total Amount",
                                                 new NestedMethodProperty (monetaryTotal.getChargeTotalAmount (),
                                                                           "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Prepaid Amount",
                                                 new NestedMethodProperty (monetaryTotal.getPrepaidAmount (), "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Payable Rounding Amount",
                                                 new NestedMethodProperty (monetaryTotal.getPayableRoundingAmount (),
                                                                           "value"));
    invoiceMonetaryTotalTopForm.addItemProperty ("Payable Amount",
                                                 new NestedMethodProperty (monetaryTotal.getPayableAmount (), "value"));

    return invoiceMonetaryTotalTopForm;
  }

  @SuppressWarnings ("serial")
  class InvoiceMonetaryTotalFieldFactory implements FormFieldFactory {

    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        if ("Line Extension Amount".equals(pid)) {
        	tf.setRequired(true);
        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Tax Exclusive Amount".equals(pid)) {
        	tf.setRequired(true);
        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Tax Inclusive Amount".equals(pid)) {
        	tf.setRequired(true);
        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
        	tf.addListener(new PositiveValueListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Payable Amount".equals(pid)) {
        	tf.setRequired(true);
        	tf.setRequiredError("required field");
        	//tf.addValidator(new PositiveValueValidator());
        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
        	tf.addListener(new PositiveValueListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
      }
      return field;
    }
  }
}
