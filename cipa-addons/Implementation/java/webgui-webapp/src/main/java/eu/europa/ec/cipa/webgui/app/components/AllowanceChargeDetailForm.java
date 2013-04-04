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
package eu.europa.ec.cipa.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceChargeReasonType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.BaseAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.MultiplierFactorNumericType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;

import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceAllowanceChargeAdapter;

@SuppressWarnings ("serial")
public class AllowanceChargeDetailForm extends Panel {

  private final InvoiceAllowanceChargeAdapter allowanceChargeBean;
  private final String titlePrefix;

  public AllowanceChargeDetailForm (final String titlePrefix, final InvoiceAllowanceChargeAdapter allowanceChargeBean) {
    this.titlePrefix = titlePrefix;
    this.allowanceChargeBean = allowanceChargeBean;

    initElements ();
  }

  private void initElements () {
    setCaption (titlePrefix + " Allowance / Charge");
    setStyleName ("light");

    final PropertysetItem itemSet = new PropertysetItem ();

    // initialize
    final InvoiceAllowanceChargeAdapter ac = new InvoiceAllowanceChargeAdapter ();
    ac.setChargeIndicator (new ChargeIndicatorType ());
    ac.setAllowanceChargeReason (new AllowanceChargeReasonType ());
    ac.setMultiplierFactorNumeric (new MultiplierFactorNumericType ());
    ac.setAmount (new AmountType ());
    ac.setBaseAmount (new BaseAmountType ());

    // make fields
    itemSet.addItemProperty ("Allowance/Charge Indicator", new NestedMethodProperty (ac, "indicator"));
    itemSet.addItemProperty ("Allowance/Charge Reason", new NestedMethodProperty (ac, "reason"));
    // itemSet.addItemProperty ("Allowance/Charge Multiplier Factor", new
    // NestedMethodProperty(ac,
    // "InvLinePriceAllowanceChargeMultiplierFactorNumeric") );
    itemSet.addItemProperty ("Allowance/Charge Amount", new NestedMethodProperty (ac, "chargeAmount"));
    // itemSet.addItemProperty ("Allowance/Charge Base Amount", new
    // NestedMethodProperty(ac, "InvLinePriceAllowanceChargeBaseAmount") );

    // make form
    final Form allowanceChargeForm = new Form ();
    allowanceChargeForm.setFormFieldFactory (new AllowanceChargeFieldFactory ());
    allowanceChargeForm.setItemDataSource (itemSet);
    allowanceChargeForm.setImmediate (true);

    addComponent (allowanceChargeForm);
  }

  class AllowanceChargeFieldFactory implements FormFieldFactory {

    @Override
    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;
      if ("Allowance/Charge Indicator".equals (pid)) {
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
      }

      return field;
    }
  }

}
