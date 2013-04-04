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

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceLineOrderReferenceAdapter;
import eu.europa.ec.cipa.webgui.app.components.tables.InvoiceLineOrderReferenceTable;
import eu.europa.ec.cipa.webgui.app.components.tables.InvoiceLineOrderReferenceTableEditor;

@SuppressWarnings ("serial")
public class InvoiceLineOrderForm extends Panel {
  private final String prefix;

  private InvoiceType inv;
  private final List <OrderLineReferenceType> lineOrderList;
  private InvoiceLineOrderReferenceAdapter lineOrderBean;

  private InvoiceLineOrderReferenceAdapter originalItem;

  private final boolean editMode;

  public InvoiceLineOrderReferenceTable table;
  private VerticalLayout hiddenContent;

  public InvoiceLineOrderForm (final String prefix, final List <OrderLineReferenceType> lineOrderList) {
    this.prefix = prefix;
    this.lineOrderList = lineOrderList;
    editMode = false;

    initElements ();
  }

  public InvoiceLineOrderForm (final String prefix,
                               final List <OrderLineReferenceType> lineOrderList,
                               final InvoiceType inv) {
    this.prefix = prefix;
    this.lineOrderList = lineOrderList;
    editMode = false;
    this.inv = inv;

    initElements ();
  }

  private void initElements () {

    final GridLayout grid = new GridLayout (4, 4);
    final VerticalLayout outerLayout = new VerticalLayout ();
    hiddenContent = new VerticalLayout ();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);

    table = new InvoiceLineOrderReferenceTable (lineOrderList);
    table.setSelectable (true);
    table.setImmediate (true);
    table.setNullSelectionAllowed (false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (false);
    table.addStyleName ("striped strong");

    final VerticalLayout tableContainer = new VerticalLayout ();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);

    final Button addButton = new Button ("Add new");
    final Button editButton = new Button ("Edit selected");
    final Button deleteButton = new Button ("Delete selected");

    final VerticalLayout buttonsContainer = new VerticalLayout ();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addButton);
    buttonsContainer.addComponent (editButton);
    buttonsContainer.addComponent (deleteButton);

    final InvoiceLineOrderReferenceTableEditor editor = new InvoiceLineOrderReferenceTableEditor (editMode);
    Label label = new Label ("<h3>Adding order line</h3>", Label.CONTENT_XHTML);
    addButton.addListener (editor.addButtonListener (editButton,
                                                     deleteButton,
                                                     hiddenContent,
                                                     table,
                                                     lineOrderList,
                                                     label));
    label = new Label ("<h3>Edit order line</h3>", Label.CONTENT_XHTML);
    editButton.addListener (editor.editButtonListener (addButton,
                                                       deleteButton,
                                                       hiddenContent,
                                                       table,
                                                       lineOrderList,
                                                       label));
    deleteButton.addListener (editor.deleteButtonListener (table));

    final Panel outerPanel = new Panel (prefix + " Referencing Orders");
    // outerPanel.setStyleName("light");

    // ---- HIDDEN FORM BEGINS -----
    final VerticalLayout formLayout = new VerticalLayout ();
    formLayout.addComponent (hiddenContent);
    hiddenContent.setVisible (false);
    // ---- HIDDEN FORM ENDS -----

    grid.setSizeUndefined ();
    grid.addComponent (tableContainer, 0, 0);
    grid.addComponent (buttonsContainer, 1, 0);

    outerPanel.addComponent (grid);
    outerPanel.addComponent (formLayout);
    outerLayout.addComponent (outerPanel);
    outerPanel.requestRepaintAll ();

    final VerticalLayout mainLayout = new VerticalLayout ();
    final VerticalLayout showHideContentLayout = new VerticalLayout ();
    showHideContentLayout.addComponent (outerPanel);
    final HorizontalLayout showHideButtonLayout = new HorizontalLayout ();
    final Button btn = new Button ("Show/Hide Allowances/Charges", new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        // TODO Auto-generated method stub
        showHideContentLayout.setVisible (!showHideContentLayout.isVisible ());
      }
    });
    showHideButtonLayout.setWidth ("100%");
    showHideButtonLayout.addComponent (btn);
    showHideButtonLayout.setComponentAlignment (btn, Alignment.MIDDLE_RIGHT);

    // mainLayout.addComponent(showHideButtonLayout);
    mainLayout.addComponent (showHideContentLayout);
    // showHideContentLayout.setVisible(false);

    addComponent (mainLayout);

  }
}
