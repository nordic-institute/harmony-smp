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

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import at.peppol.webgui.app.components.adapters.InvoiceAllowanceChargeAdapter;
import at.peppol.webgui.app.components.adapters.InvoiceItemPropertyAdapter;
import at.peppol.webgui.app.components.tables.InvoiceAdditionalDocRefTableEditor;
import at.peppol.webgui.app.components.tables.InvoiceItemPropertyTable;
import at.peppol.webgui.app.components.tables.InvoiceItemPropertyTableEditor;
import at.peppol.webgui.app.components.tables.InvoiceLineAllowanceChargeTable;
import at.peppol.webgui.app.components.tables.InvoiceLineAllowanceChargeTableEditor;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class InvoiceLineAllowanceChargeForm extends Panel {
  private final String prefix;
  
  private InvoiceType inv;
  private List<AllowanceChargeType> lineAllowanceChargeList;
  private InvoiceAllowanceChargeAdapter allowanceChargeBean;
  
  private InvoiceAllowanceChargeAdapter originalItem;

  private boolean editMode;
  
  public InvoiceLineAllowanceChargeTable table;
  private VerticalLayout hiddenContent;

  
  public InvoiceLineAllowanceChargeForm(String prefix, List<AllowanceChargeType> lineAllowanceChargeList) {
      this.prefix = prefix;
      this.lineAllowanceChargeList = lineAllowanceChargeList;
      editMode = false;
      
      initElements();
  }
  
  public InvoiceLineAllowanceChargeForm(String prefix, List<AllowanceChargeType> lineAllowanceChargeList, InvoiceType inv) {
	  this.prefix = prefix;
      this.lineAllowanceChargeList = lineAllowanceChargeList;
      editMode = false;
      this.inv = inv;
      
      initElements();
  }
  
  public InvoiceLineAllowanceChargeTable getTable() {
	  return table;
  }
  
  private void initElements() {

    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);
    
    table = new InvoiceLineAllowanceChargeTable(lineAllowanceChargeList);
    table.setSelectable(true);
    table.setImmediate(true);
    table.setNullSelectionAllowed(false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (false);
    table.addStyleName ("striped strong");
        
    VerticalLayout tableContainer = new VerticalLayout();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);
    
    Button addButton = new Button("Add new");
    Button editButton = new Button("Edit selected");
    Button deleteButton = new Button("Delete selected");
    
    VerticalLayout buttonsContainer = new VerticalLayout();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addButton);
    buttonsContainer.addComponent (editButton);
    buttonsContainer.addComponent (deleteButton);
    
    InvoiceLineAllowanceChargeTableEditor editor = new InvoiceLineAllowanceChargeTableEditor(editMode ,inv);
    Label label = new Label("<h3>Adding allowance/charge line</h3>", Label.CONTENT_XHTML);
    addButton.addListener(editor.addButtonListener(editButton, deleteButton, hiddenContent, table, lineAllowanceChargeList, label));
    label = new Label("<h3>Edit allowance/charge line</h3>", Label.CONTENT_XHTML);
    editButton.addListener(editor.editButtonListener(addButton, deleteButton, hiddenContent, table, lineAllowanceChargeList, label));
    deleteButton.addListener(editor.deleteButtonListener(table));

    Panel outerPanel = new Panel(prefix + " Allowances/Charges"); 
    //outerPanel.setStyleName("light");     
   
    // ---- HIDDEN FORM BEGINS -----
    VerticalLayout formLayout = new VerticalLayout();
    formLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);    
    // ---- HIDDEN FORM ENDS -----
    
    grid.setSizeUndefined();
    grid.addComponent(tableContainer, 0, 0);
    grid.addComponent(buttonsContainer, 1, 0); 
    
    outerPanel.addComponent (grid);
    outerPanel.addComponent (formLayout);
    outerLayout.addComponent(outerPanel);
    outerPanel.requestRepaintAll();
    
    VerticalLayout mainLayout = new VerticalLayout();
    final VerticalLayout showHideContentLayout = new VerticalLayout();
    showHideContentLayout.addComponent(outerPanel);
    HorizontalLayout showHideButtonLayout = new HorizontalLayout();
    Button btn = new Button("Show/Hide Allowances/Charges",new Button.ClickListener(){
      @Override
      public void buttonClick (ClickEvent event) {
        // TODO Auto-generated method stub
        showHideContentLayout.setVisible(!showHideContentLayout.isVisible());
      }
    });
    showHideButtonLayout.setWidth("100%");
    showHideButtonLayout.addComponent(btn);
    showHideButtonLayout.setComponentAlignment (btn, Alignment.MIDDLE_RIGHT);
    
    //mainLayout.addComponent(showHideButtonLayout);
    mainLayout.addComponent(showHideContentLayout);
    //showHideContentLayout.setVisible(false);    
    
    addComponent(mainLayout);

  }  
  
  class AllowanceChargeFieldFactory implements FormFieldFactory {

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
      // Identify the fields by their Property ID.
      String pid = (String) propertyId;

      Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
      if (field instanceof AbstractTextField){
          ((AbstractTextField) field).setNullRepresentation("");
      }
      
      return field;
    }
 }    
  
}



