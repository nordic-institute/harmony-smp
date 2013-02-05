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
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;

import at.peppol.webgui.app.components.InvoiceTabForm.TaxExclusiveAmountListener;
import at.peppol.webgui.app.components.adapters.InvoiceAllowanceChargeAdapter;
import at.peppol.webgui.app.components.tables.InvoiceAllowanceChargeTable;
import at.peppol.webgui.app.components.tables.InvoiceAllowanceChargeTableEditor;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class TabInvoiceAllowanceCharge extends Form {
  private final InvoiceTabForm parent;
  private List <AllowanceChargeType> allowanceChargeList;
  private InvoiceAllowanceChargeAdapter allowanceChargeItem;

  private InvoiceAllowanceChargeAdapter originalItem;

  private boolean editMode;

  public InvoiceAllowanceChargeTable table;
  private VerticalLayout hiddenContent;

  public InvoiceAllowanceChargeTable getTable() {
	  return table;
  }
  
  public TabInvoiceAllowanceCharge (final InvoiceTabForm parent) {
    this.parent = parent;
    editMode = false;
    initElements ();
  }

  private void initElements () {
    allowanceChargeList = parent.getInvoice ().getAllowanceCharge ();

    final GridLayout grid = new GridLayout (4, 4);
    final VerticalLayout outerLayout = new VerticalLayout ();
    hiddenContent = new VerticalLayout ();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);

    table = new InvoiceAllowanceChargeTable (parent.getInvoice ().getAllowanceCharge ());
    table.setSelectable (true);
    table.setImmediate (true);
    table.setNullSelectionAllowed (false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (true);
    table.addStyleName ("striped strong");
    
    //table.addListener(parent.new TaxExclusiveAmountListener());

    final VerticalLayout tableContainer = new VerticalLayout ();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);
    
    Button addButton = new Button("Add New");
    Button editButton = new Button("Edit selected");
    Button deleteButton = new Button("Delete selected");
    
    final VerticalLayout buttonsContainer = new VerticalLayout ();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addButton);
    buttonsContainer.addComponent (editButton);
    buttonsContainer.addComponent (deleteButton);

    InvoiceAllowanceChargeTableEditor editor = new InvoiceAllowanceChargeTableEditor(editMode, parent.getInvoice());
    Label label = new Label("<h3>Adding new allowance/charge line</h3>", Label.CONTENT_XHTML);
    addButton.addListener(editor.addButtonListener(editButton, deleteButton, hiddenContent, table, allowanceChargeList ,label));
    label = new Label("<h3>Edit allowance/charge line</h3>", Label.CONTENT_XHTML);
    editButton.addListener(editor.editButtonListener(addButton, deleteButton, hiddenContent, table, allowanceChargeList, label));
    deleteButton.addListener(editor.deleteButtonListener(table));

    final Panel outerPanel = new Panel ("Allowance Charge");

    grid.addComponent (tableContainer, 0, 0);
    grid.addComponent (buttonsContainer, 1, 0);

    outerPanel.addComponent (grid);
    outerLayout.addComponent (outerPanel);

    // ---- HIDDEN FORM BEGINS -----
    final VerticalLayout formLayout = new VerticalLayout ();
    formLayout.addComponent (hiddenContent);
    hiddenContent.setVisible (false);
    outerLayout.addComponent (formLayout);
    // ---- HIDDEN FORM ENDS -----

    setLayout (outerLayout);
    grid.setSizeUndefined ();
    outerPanel.requestRepaintAll ();
  }

  static final class InvoiceAllowanceChargeFieldFactory implements FormFieldFactory {

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
      }
      return field;
    }
  }
}
