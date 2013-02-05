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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;

import at.peppol.webgui.app.components.adapters.InvoiceLineAdapter;
import at.peppol.webgui.app.components.tables.InvoiceLineTable;
import at.peppol.webgui.app.utils.Utils;
import at.peppol.webgui.app.validator.PositiveValueListener;
import at.peppol.webgui.app.validator.PositiveValueValidator;
import at.peppol.webgui.app.validator.RequiredFieldListener;
import at.peppol.webgui.app.validator.RequiredNumericalFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.terminal.UserError;
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
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings ("serial")
public class TabInvoiceLine extends Form {
  private final InvoiceTabForm parent;
  private List <InvoiceLineType> invoiceLineList;
  private InvoiceLineAdapter invoiceLineItem;

  private InvoiceLineAdapter originalItem;

  private boolean addMode;
  private boolean editMode;

  public InvoiceLineTable table;
  private VerticalLayout hiddenContent;

  public TabInvoiceLine (final InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;
    initElements ();
  }

  public List<InvoiceLineType> getInvoiceLineList() {
	  return invoiceLineList;
  }
  
  public InvoiceLineTable getTable() {
	  return table;
  }
  
  
  private void initElements () {
    invoiceLineList = parent.getInvoice ().getInvoiceLine ();

    final GridLayout grid = new GridLayout (4, 4);
    final VerticalLayout outerLayout = new VerticalLayout ();
    hiddenContent = new VerticalLayout ();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);

    table = new InvoiceLineTable(parent.getInvoice ().getInvoiceLine ());
    table.setSelectable (true);
    table.setImmediate (true);
    table.setNullSelectionAllowed (false);
    table.setHeight (150, UNITS_PIXELS);
    table.setFooterVisible (false);
    table.addStyleName ("striped strong");

    //table.addListener(parent.new LinesTotalAmountListener());
    //table.addListener(parent.new TaxExclusiveAmountListener());
    
    final VerticalLayout tableContainer = new VerticalLayout ();
    tableContainer.addComponent (table);
    tableContainer.setMargin (false, true, false, false);

    // buttons Add, Edit, Delete
    final Button addBtn = new Button("Add new");
    final Button editBtn = new Button("Edit selected");
    final Button deleteBtn = new Button ("Delete Selected");
    
    addBtn.addListener(new Button.ClickListener () {
        @Override
        public void buttonClick (final Button.ClickEvent event) {
          addBtn.setEnabled(false);
          editBtn.setEnabled(false);
          deleteBtn.setEnabled(false);
          //addMode = true;
          hiddenContent.removeAllComponents ();
          invoiceLineItem = createInvoiceLineItem ();

          final Label formLabel = new Label ("<h3>Adding new invoice line</h3>", Label.CONTENT_XHTML);

          hiddenContent.addComponent (formLabel);
          final Form form = createInvoiceLineMainForm ();
          hiddenContent.addComponent (form);

          HorizontalLayout h1 = new HorizontalLayout();
          h1.setSpacing(true);
          h1.setMargin(true);
          
          // Set invoiceLine 0..N cardinality panels
          //final Panel itemPropertyPanel = new ItemPropertyForm ("Additional",
          //                                                      invoiceLineItem.getInvLineAdditionalItemPropertyList ());
          final ItemPropertyForm itemPropertyPanel = new ItemPropertyForm ("Additional",
                                                                invoiceLineItem.getInvLineAdditionalItemPropertyList ());
          h1.addComponent (itemPropertyPanel);
          
          //add the allowance/charge indicator 0..N cardinality
          final InvoiceLineAllowanceChargeForm lineAllowanceChargePanel = new InvoiceLineAllowanceChargeForm("", 
          														invoiceLineItem.getAllowanceCharge(),
          														parent.getInvoice());
          
          //add the listeners for line extension amount calculation
          BIIRULE_T10_R018 biirule_t10_r018 = new BIIRULE_T10_R018(invoiceLineItem, form);
          form.getField("Price Amount").addListener(biirule_t10_r018);
          form.getField("Base Quantity").addListener(biirule_t10_r018);
          form.getField("Invoiced Quantity").addListener(biirule_t10_r018);
          lineAllowanceChargePanel.getTable().addListener((ItemSetChangeListener)biirule_t10_r018);
          
          //add the listeners for VAT AE tax total amount
          EUGEN_T10_R018 eugen_t10_r018 = new EUGEN_T10_R018(form, "Tax Scheme ID","Tax Category ID","Tax Total Amount");
          form.getField("Tax Scheme ID").addListener(eugen_t10_r018);
          form.getField("Tax Category ID").addListener(eugen_t10_r018);
          
          h1.addComponent (lineAllowanceChargePanel);
          
          HorizontalLayout h2 = new HorizontalLayout();
          h2.setSpacing(true);
          h2.setMargin(true);
          
          final Panel lineOrderPanel = new InvoiceLineOrderForm("", 
  				invoiceLineItem.getInvLineOrderList());
          
          h2.addComponent (lineOrderPanel);
          
          final Panel lineCommodityPanel = new InvoiceLineCommodityClassificationForm("", 
          		invoiceLineItem.getInvLineCommodityClassificationList());
          
          h2.addComponent (lineCommodityPanel);
          
          hiddenContent.addComponent(h1);
          hiddenContent.addComponent(h2);
          
          // Save new line button
          final HorizontalLayout buttonLayout = new HorizontalLayout ();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent (new Button ("Save invoice line", new Button.ClickListener () {
            @Override
            public void buttonClick (final ClickEvent event) {
          	  AbstractTextField itemName = (AbstractTextField)form.getField("Item Name");
          	  itemName.setMaxLength(50);
          	  
          	  if (itemName.getValue().toString().length() > 50) {
          		  //itemName.setComponentError(new UserError("Item Name should not be more than 50 characters"));
          		  itemName.setValue(itemName.getValue().toString().substring(0, 49));
          		  getWindow().showNotification("Item Name truncated to 50 chars", Notification.TYPE_TRAY_NOTIFICATION);
          	  }
          	  
          	  try {
          		  /*Collection<String> props = (Collection<String>) form.getItemPropertyIds();
          		  List<Field> fields = new ArrayList<Field>();
          		  for (String property : props) {
          			  fields.add(form.getField(property));
          		  }
          		  List<BlurListener> listeners = new ArrayList<BlurListener>();
          		  for (Field f : fields) {
          			  if (f instanceof AbstractTextField) {
          				  AbstractTextField ff = (AbstractTextField)f;
          				  listeners.addAll((Collection<BlurListener>) ff.getListeners(BlurEvent.class));
          			  }
          		  }
          		  ValidatorsList.validateListenersNotify(listeners);
          		  form.validate();*/
          		  Utils.validateFormFields(form);
          		  //form.commit();
             	  // update table (and consequently add new item to invoiceList list)
              	  table.addLine(invoiceLineItem);
             		  //hide form
              	  hiddenContent.setVisible (false);
              	  //addMode = false;
              	  addBtn.setEnabled(true);
              	  editBtn.setEnabled(true);
                  deleteBtn.setEnabled(true);
              	  //itemName.setComponentError(null);
          	  }catch (InvalidValueException e) {
          		  getWindow().showNotification("Invoice line has errors", Notification.TYPE_TRAY_NOTIFICATION);
          	  }
          	  
            }
          }));
          buttonLayout.addComponent (new Button ("Cancel", new Button.ClickListener () {
            @Override
            public void buttonClick (final ClickEvent event) {
            	addBtn.setEnabled(true);
            	editBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            	hiddenContent.removeAllComponents ();
              // hide form
              hiddenContent.setVisible (false);
              addMode = false;
            }
          }));

          hiddenContent.addComponent (buttonLayout);

          // hiddenContent.setVisible(!hiddenContent.isVisible());
          hiddenContent.setVisible (true);
        }
      });
    
    editBtn.addListener(new Button.ClickListener () {
      @Override
      public void buttonClick (final Button.ClickEvent event) {
        final Object rowId = table.getValue (); // get the selected rows id
        if (rowId != null) {
        	addBtn.setEnabled(true);
        	editBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
          
          final String sid = (String) table.getContainerProperty (rowId, "ID.value").getValue ();
          // TODO: PUT THIS IN FUNCTION BEGINS
          editMode = true;
          hiddenContent.removeAllComponents ();

          // get selected item
          invoiceLineItem = (InvoiceLineAdapter) invoiceLineList.get (table.getIndexFromID (sid));
          // clone it to original item
          originalItem = new InvoiceLineAdapter ();
          cloneInvoiceLineItem (invoiceLineItem, originalItem);

          final Label formLabel = new Label ("<h3>Editing invoice line</h3>", Label.CONTENT_XHTML);

          hiddenContent.addComponent (formLabel);
          final Form form = createInvoiceLineMainForm ();
          hiddenContent.addComponent(form);

          HorizontalLayout h1 = new HorizontalLayout();
          h1.setSpacing(true);
          h1.setMargin(true);
          // Set invoiceLine 0..N cardinality panels
          final ItemPropertyForm itemPropertyPanel = new ItemPropertyForm ("Additional",
                                                                invoiceLineItem.getInvLineAdditionalItemPropertyList ());
          h1.addComponent (itemPropertyPanel);
          
          //add the allowance/charge indicator 0..N cardinality
          final InvoiceLineAllowanceChargeForm lineAllowanceChargePanel = new InvoiceLineAllowanceChargeForm("", 
          														invoiceLineItem.getAllowanceCharge(),
          														parent.getInvoice());
          
          //add the listeners for line extension amount calculation
          BIIRULE_T10_R018 biirule_t10_r018 = new BIIRULE_T10_R018(invoiceLineItem, form);
          form.getField("Price Amount").addListener(biirule_t10_r018);
          form.getField("Base Quantity").addListener(biirule_t10_r018);
          lineAllowanceChargePanel.getTable().addListener((ItemSetChangeListener)biirule_t10_r018);
          
          //add the listeners for VAT AE tax total amount
          EUGEN_T10_R018 eugen_t10_r018 = new EUGEN_T10_R018(form, "Tax Scheme ID","Tax Category ID","Tax Total Amount");
          form.getField("Tax Scheme ID").addListener(eugen_t10_r018);
          form.getField("Tax Category ID").addListener(eugen_t10_r018);
          
          h1.addComponent (lineAllowanceChargePanel);
          
          HorizontalLayout h2 = new HorizontalLayout();
          h2.setSpacing(true);
          h2.setMargin(true);
          
          final Panel lineOrderPanel = new InvoiceLineOrderForm("", invoiceLineItem.getInvLineOrderList());
          h2.addComponent (lineOrderPanel);
          
          final Panel lineCommodityPanel = new InvoiceLineCommodityClassificationForm("", invoiceLineItem.getInvLineCommodityClassificationList());
          h2.addComponent (lineCommodityPanel);
                    
          hiddenContent.addComponent(h1);
          hiddenContent.addComponent(h2);
          
          /*// Set invoiceLine 0..N cardinalily panels
          final Panel itemPropertyPanel = new ItemPropertyForm ("Additional",
                                                                invoiceLineItem.getInvLineAdditionalItemPropertyList ());
          hiddenContent.addComponent (itemPropertyPanel);*/

          // Save new line button
          final HorizontalLayout buttonLayout = new HorizontalLayout ();
          buttonLayout.setSpacing (true);
          buttonLayout.addComponent (new Button ("Save changes", new Button.ClickListener () {
            @Override
            public void buttonClick (final ClickEvent event) {
              // update table (and consequently edit item to allowanceChargeList
              // list)
              AbstractTextField itemName = (AbstractTextField)form.getField("Item Name");
          	  itemName.setMaxLength(50);
          	  
          	  if (itemName.getValue().toString().length() > 50) {
          		  //itemName.setComponentError(new UserError("Item Name should not be more than 50 characters"));
          		  itemName.setValue(itemName.getValue().toString().substring(0, 49));
          		  getWindow().showNotification("Item Name truncated to 50 chars", Notification.TYPE_TRAY_NOTIFICATION);
          	  }
          	  
          	  try {
          		  /*Collection<String> props = (Collection<String>) form.getItemPropertyIds();
          		  List<Field> fields = new ArrayList<Field>();
          		  for (String property : props) {
          			  fields.add(form.getField(property));
          		  }
          		  List<BlurListener> listeners = new ArrayList<BlurListener>();
          		  for (Field f : fields) {
          			  if (f instanceof AbstractTextField) {
          				  AbstractTextField ff = (AbstractTextField)f;
          				  listeners.addAll((Collection<BlurListener>) ff.getListeners(BlurEvent.class));
          			  }
          		  }
          		  ValidatorsList.validateListenersNotify(listeners);
          		  form.validate();*/
          		  Utils.validateFormFields(form);
          		  //table.setInvoiceLine (sid, invoiceLineItem);
          		  table.setLine(sid, invoiceLineItem);
          		  addBtn.setEnabled(true);
          		  editBtn.setEnabled(true);
                  deleteBtn.setEnabled(true);
          		  // 	hide form
          		  hiddenContent.setVisible (false);
          		  editMode = false;
          	  } catch  (InvalidValueException e) {
        		  getWindow().showNotification("Invoice line has errors", Notification.TYPE_TRAY_NOTIFICATION);
        	  }
            }
          }));
          buttonLayout.addComponent (new Button ("Cancel editing", new Button.ClickListener () {
            @Override
            public void buttonClick (final ClickEvent event) {
              hiddenContent.removeAllComponents ();
              addBtn.setEnabled(true);
      		  editBtn.setEnabled(true);
              deleteBtn.setEnabled(true);
              //table.setInvoiceLine (sid, originalItem);
              table.setLine(sid, originalItem);
              // hide form
              hiddenContent.setVisible (false);
              editMode = false;
            }
          }));

          hiddenContent.addComponent (buttonLayout);

          // hiddenContent.setVisible(!hiddenContent.isVisible());
          hiddenContent.setVisible (true);
          // TODO: PUT THIS IN FUNCTION ENDS
        }
        else {
          parent.getWindow ().showNotification ("Info",
                                                "No table line is selected",
                                                Window.Notification.TYPE_HUMANIZED_MESSAGE);
        }

      }
    });
    
    deleteBtn.addListener(new Button.ClickListener () {
      @Override
      public void buttonClick (final Button.ClickEvent event) {
        final Object rowId = table.getValue (); // get the selected rows id
        if (rowId != null) {
          if (table.getContainerProperty (rowId, "ID.value").getValue () != null) {
            final String sid = (String) table.getContainerProperty (rowId, "ID.value").getValue ();
            //table.removeInvoiceLine (sid);
            table.removeLine(sid);
          }
        }
        else {
          parent.getWindow ().showNotification ("Info",
                                                "No table line is selected",
                                                Window.Notification.TYPE_HUMANIZED_MESSAGE);

        }
      }
    });

    final VerticalLayout buttonsContainer = new VerticalLayout ();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addBtn);
    buttonsContainer.addComponent (editBtn);
    buttonsContainer.addComponent (deleteBtn);

    final Panel outerPanel = new Panel ("Invoice Line");

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
  
  /*public Form createGridLayoutInvoiceLineForm() {
	  Form form = new Form() {
		  GridLayout layout = new GridLayout(5,10);
		  Panel pricePanel = new Panel();
		  int counter1 = 0;
		  int counter2 = 0;
		  {
			  layout.setSpacing(true);
			  setLayout(layout);
			  HorizontalLayout h = new HorizontalLayout();
			  h.setWidth("50px");
			  layout.addComponent(h,2,0);
		  }
		  
		  @Override
		  protected void attachField(Object propertyId, Field field) {
			  //field.setCaption(null);
			  if ("Line Note".equals(propertyId) || "Invoiced Quantity".equals(propertyId) ||
				  "Line Extension Amount".equals(propertyId) || "Accounting Cost".equals(propertyId) ||
				  "Tax Total Amount".equals(propertyId) || "Item Description".equals(propertyId) ||
				  "Item Name".equals(propertyId) || "Sellers Item ID".equals(propertyId) ||
				  "Tax Category ID".equals(propertyId) || "Tax Category Percent".equals(propertyId) ) {

				  Label fieldLabel = new Label(field.getCaption());
				  //Label fieldLabel = Utils.requiredLabel(field.getCaption());
				  field.setCaption(null);
				  layout.addComponent(fieldLabel, 0, counter1);
				  layout.addComponent(field, 1, counter1);
				  layout.setComponentAlignment(fieldLabel, Alignment.MIDDLE_RIGHT);
				  counter1++;
		      }
			  else {// Single-column fields
				  Label fieldLabel = new Label(field.getCaption());
				  field.setCaption(null);
				  layout.addComponent(fieldLabel, 3, counter2);
				  layout.addComponent(field, 4, counter2);
				  layout.setComponentAlignment(fieldLabel, Alignment.MIDDLE_RIGHT);
				  counter2++;
		      }
		    }  
	  };
	  
	  return form;
  }*/
  
  public Form createGridLayoutInvoiceLineForm() {
	  Form form = new Form() {
		  HorizontalLayout layout = new HorizontalLayout();
		  Panel pricePanel = new Panel("Price");
		  FormLayout f1 = new FormLayout();
		  FormLayout f2 = new FormLayout();
		  Label label = new Label("<h4>Allowances/Charges</h4>", Label.CONTENT_XHTML);
		  {
			  layout.setSpacing(true);
			  layout.setMargin(true);
			  setLayout(layout);
			  HorizontalLayout h = new HorizontalLayout();
			  h.setWidth("50px");
			  pricePanel.setStyleName("light");
			  layout.addComponent(f1);
			  layout.addComponent(h);
			  layout.addComponent(pricePanel);
			  pricePanel.addComponent(f2);
		  }
		  
		  @Override
		  protected void attachField(Object propertyId, Field field) {
			  //field.setCaption(null);
			  if ("Line Note".equals(propertyId) || "Invoiced Quantity".equals(propertyId) ||
				  "Line Extension Amount".equals(propertyId) || "Accounting Cost".equals(propertyId) ||
				  "Tax Total Amount".equals(propertyId) || "Item Description".equals(propertyId) ||
				  "Item Name".equals(propertyId) || "Sellers Item ID".equals(propertyId) ||
				  "Tax Category ID".equals(propertyId) || "Tax Category Percent".equals(propertyId) || 
				  "Standard Item ID".equals(propertyId) || "Tax Scheme ID".equals(propertyId) ||
				  "Measurement Unit".equals(propertyId)) {
				  
				  f1.addComponent(field);
		      }
			  else if ("Price Allowance/Charge Indicator".equals(propertyId) ||
					  "Price Allowance/Charge Reason".equals(propertyId) ||
					  "Price Allowance/Charge Multiplier Factor".equals(propertyId) ||
					  "Price Allowance/Charge Amount".equals(propertyId) ||
					  "Price Allowance/Charge Base Amount".equals(propertyId)){
				  
				  if (f2.getComponentIndex(label) == -1)
					  f2.addComponent(label);
				  
				  if ("Price Allowance/Charge Reason".equals(propertyId))
					  field.setCaption("Reason");
				  else if ("Price Allowance/Charge Multiplier Factor".equals(propertyId))
					  field.setCaption("Multiplier Factor");
				  else if ("Price Allowance/Charge Amount".equals(propertyId))
					  field.setCaption("Amount");
				  else if ("Price Allowance/Charge Base Amount".equals(propertyId))
					  field.setCaption("Base Amount");
				  
				  f2.addComponent(field);
			  }
			  else { //for price amount and base quantity
				  f2.addComponent(field);
			  }
		    }  
	  };
	  
	  return form;
  }


  public Form createInvoiceLineMainForm () {
    //final Form invoiceLineForm = new Form (new FormLayout (), new InvoiceLineFieldFactory ());
    final Form invoiceLineForm = createGridLayoutInvoiceLineForm();
    invoiceLineForm.setFormFieldFactory(new InvoiceLineFieldFactory ());
    invoiceLineForm.setImmediate(true);
    
    final NestedMethodProperty mp = new NestedMethodProperty (invoiceLineItem, "ID.value");
    if (!editMode) {
      final IDType num = new IDType ();
      //num.setValue (String.valueOf (invoiceLineList.size () + 1));
      //invoiceLineItem.setID (num);
      
      int max = 0;
      for (InvoiceLineType line : invoiceLineList) {
    	  if (Integer.parseInt(line.getID().getValue()) > max)
    		  max = Integer.parseInt(line.getID().getValue());
      }
      num.setValue(String.valueOf(max+1));
      invoiceLineItem.setID(num);
    }
    else {
      mp.setReadOnly (true);
    }
    
    // TODO: Redesign (break this function to multiple others...) the form with
    // show/hide panels etc

    // invoiceAllowanceChargeForm.addItemProperty ("Line ID #", new
    // NestedMethodProperty(allowanceChargeItem, "ID.value") );
    //invoiceLineForm.addItemProperty ("Line ID #", mp);
    invoiceLineForm.addItemProperty ("Line Note", new NestedMethodProperty (invoiceLineItem, "invLineNote"));
    invoiceLineForm.addItemProperty ("Invoiced Quantity", new NestedMethodProperty (invoiceLineItem,
                                                                                    "invLineInvoicedQuantity"));
    invoiceLineForm.addItemProperty ("Measurement Unit", new NestedMethodProperty (invoiceLineItem,
            																		"invLineMeasureUnit"));
    invoiceLineForm.addItemProperty ("Line Extension Amount", new NestedMethodProperty (invoiceLineItem,
                                                                                        "invLineLineExtensionAmount"));
    invoiceLineForm.addItemProperty ("Accounting Cost", new NestedMethodProperty (invoiceLineItem,
                                                                                  "invLineAccountingCost"));
    invoiceLineForm.addItemProperty ("Tax Total Amount", new NestedMethodProperty (invoiceLineItem, "InvLineTaxAmount"));
    invoiceLineForm.addItemProperty ("Item Description", new NestedMethodProperty (invoiceLineItem,
                                                                                   "InvLineItemDescription"));
    invoiceLineForm.addItemProperty ("Item Name", new NestedMethodProperty (invoiceLineItem, "InvLineItemName"));
    invoiceLineForm.addItemProperty ("Sellers Item ID", new NestedMethodProperty (invoiceLineItem,
                                                                                  "InvLineItemSellersItemID"));
    invoiceLineForm.addItemProperty ("Standard Item ID", new NestedMethodProperty (invoiceLineItem,
                                                                                   "InvLineItemStandardItemID"));
    invoiceLineForm.addItemProperty ("Tax Category ID", new NestedMethodProperty (invoiceLineItem,
                                                                                  "InvLineItemTaxCategoryID"));
    invoiceLineForm.addItemProperty ("Tax Category Percent", new NestedMethodProperty (invoiceLineItem,
                                                                                       "InvLineItemTaxCategoryPercent"));
    invoiceLineForm.addItemProperty ("Tax Scheme ID",
                                     new NestedMethodProperty (invoiceLineItem, "InvLineItemTaxCategoryTaxSchemeID"));
    invoiceLineForm.addItemProperty ("Price Amount", new NestedMethodProperty (invoiceLineItem, "InvLinePriceAmount"));
    invoiceLineForm.addItemProperty ("Base Quantity", new NestedMethodProperty (invoiceLineItem,
                                                                                "InvLinePriceBaseQuantity"));
    //invoiceLineForm.addItemProperty ("Price Allowance/Charge ID",
    //                                 new NestedMethodProperty (invoiceLineItem, "InvLinePriceAllowanceChargeID"));
    invoiceLineForm.addItemProperty ("Price Allowance/Charge Indicator",
                                     new NestedMethodProperty (invoiceLineItem, "InvLinePriceAllowanceChargeIndicator"));
    invoiceLineForm.addItemProperty ("Price Allowance/Charge Reason",
                                     new NestedMethodProperty (invoiceLineItem, "InvLinePriceAllowanceChargeReason"));
    invoiceLineForm.addItemProperty ("Price Allowance/Charge Multiplier Factor",
                                     new NestedMethodProperty (invoiceLineItem,
                                                               "InvLinePriceAllowanceChargeMultiplierFactorNumeric"));
    invoiceLineForm.addItemProperty ("Price Allowance/Charge Amount",
                                     new NestedMethodProperty (invoiceLineItem, "InvLinePriceAllowanceChargeAmount"));
    invoiceLineForm.addItemProperty ("Price Allowance/Charge Base Amount",
                                     new NestedMethodProperty (invoiceLineItem, "InvLinePriceAllowanceChargeBaseAmount"));

    return invoiceLineForm;
  }

  private InvoiceLineAdapter createInvoiceLineItem () {
    final InvoiceLineAdapter ac = new InvoiceLineAdapter ();

    ac.setID (new IDType ());
    ac.setInvLineNote ("");
    ac.setInvLineInvoicedQuantity (BigDecimal.ZERO);
    ac.setInvLineMeasureUnit(null);
    ac.setInvLineLineExtensionAmount (BigDecimal.ZERO);
    ac.setInvLineAccountingCost ("");
    ac.setInvLineTaxAmount (BigDecimal.ZERO);
    ac.setInvLineItemDescription ("");
    ac.setInvLineItemName ("");
    ac.setInvLineItemSellersItemID ("");
    ac.setInvLineItemStandardItemID ("");
    ac.setInvLineItemTaxCategoryID ("");
    ac.setInvLineItemTaxCategoryPercent (BigDecimal.ZERO);
    ac.setInvLineItemTaxCategoryTaxSchemeID ("");
    ac.setInvLinePriceAmount (BigDecimal.ZERO);
    //ac.setInvLinePriceAmount ("0");
    //ac.setInvLinePriceBaseQuantity (BigDecimal.ZERO);
    ac.setInvLinePriceBaseQuantity (new BigDecimal("1.00"));
    ac.setInvLinePriceAllowanceChargeID ("");
    ac.setInvLinePriceAllowanceChargeIndicator (Boolean.FALSE);
    ac.setInvLinePriceAllowanceChargeReason ("");
    ac.setInvLinePriceAllowanceChargeMultiplierFactorNumeric (BigDecimal.ZERO);
    ac.setInvLinePriceAllowanceChargeAmount (BigDecimal.ZERO);
    ac.setInvLinePriceAllowanceChargeBaseAmount (BigDecimal.ZERO);

    //ac.getInvLineAdditionalItemPropertyList ().add (new ItemPropertyType ());

    return ac;
  }

  private void cloneInvoiceLineItem (final InvoiceLineAdapter srcItem, final InvoiceLineAdapter dstItem) {
    dstItem.setInvLineID (srcItem.getInvLineID ());
    dstItem.setInvLineNote (srcItem.getInvLineNote ());
    dstItem.setInvLineInvoicedQuantity (srcItem.getInvLineInvoicedQuantity ());
    dstItem.setInvLineMeasureUnit(srcItem.getInvLineMeasureUnit());
    dstItem.setInvLineLineExtensionAmount (srcItem.getInvLineLineExtensionAmount ());
    dstItem.setInvLineAccountingCost (srcItem.getInvLineAccountingCost ());
    dstItem.setInvLineTaxAmount (srcItem.getInvLineTaxAmount ());
    dstItem.setInvLineItemDescription (srcItem.getInvLineItemDescription ());
    dstItem.setInvLineItemName (srcItem.getInvLineItemName ());
    dstItem.setInvLineItemSellersItemID (srcItem.getInvLineItemSellersItemID ());
    dstItem.setInvLineItemStandardItemID (srcItem.getInvLineItemStandardItemID ());
    dstItem.setInvLineItemTaxCategoryID (srcItem.getInvLineItemTaxCategoryID ());
    dstItem.setInvLineItemTaxCategoryPercent (srcItem.getInvLineItemTaxCategoryPercent ());
    dstItem.setInvLineItemTaxCategoryTaxSchemeID (srcItem.getInvLineItemTaxCategoryTaxSchemeID ());
    dstItem.setInvLinePriceAmount (srcItem.getInvLinePriceAmount());
    dstItem.setInvLinePriceBaseQuantity (srcItem.getInvLinePriceBaseQuantity ());
    dstItem.setInvLinePriceAllowanceChargeID (srcItem.getInvLinePriceAllowanceChargeID ());
    dstItem.setInvLinePriceAllowanceChargeIndicator (srcItem.getInvLinePriceAllowanceChargeIndicator ());
    dstItem.setInvLinePriceAllowanceChargeReason (srcItem.getInvLinePriceAllowanceChargeReason ());
    dstItem.setInvLinePriceAllowanceChargeMultiplierFactorNumeric (srcItem.getInvLinePriceAllowanceChargeMultiplierFactorNumeric ());
    dstItem.setInvLinePriceAllowanceChargeAmount (srcItem.getInvLinePriceAllowanceChargeAmount ());
    dstItem.setInvLinePriceAllowanceChargeBaseAmount (srcItem.getInvLinePriceAllowanceChargeBaseAmount ());
  }

  class InvoiceLineFieldFactory implements FormFieldFactory {

    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;
      if ("Price Allowance/Charge Indicator".equals (pid)) {
        final Select indicatorSelect = new Select ("Charge or Allowance?");
        indicatorSelect.setNullSelectionAllowed (true);
        indicatorSelect.addItem (Boolean.TRUE);
        indicatorSelect.addItem (Boolean.FALSE);
        indicatorSelect.setItemCaption (Boolean.TRUE, "Charge");
        indicatorSelect.setItemCaption (Boolean.FALSE, "Allowance");

        return indicatorSelect;
      }
      if ("Measurement Unit".equals(pid)) {
          UnitCodeSelect unitCodeSelect = new UnitCodeSelect(pid);
          unitCodeSelect.setRequired(true);
          unitCodeSelect.setNullSelectionAllowed(false);
          unitCodeSelect.addListener(new RequiredFieldListener(unitCodeSelect,pid));
      	  ValidatorsList.addListeners((Collection<BlurListener>) unitCodeSelect.getListeners(BlurEvent.class));
          return unitCodeSelect;
      }
      if ("Tax Scheme ID".equals(pid)) {
          final TaxSchemeSelect taxSchemeSelect = new TaxSchemeSelect(pid);
          taxSchemeSelect.setRequired(true);
          taxSchemeSelect.addListener(new RequiredFieldListener(taxSchemeSelect,pid));
      	  ValidatorsList.addListeners((Collection<BlurListener>) taxSchemeSelect.getListeners(BlurEvent.class));
          return taxSchemeSelect;
      }
      if ("Tax Category ID".equals(pid)) {
          final TaxCategoryIDSelect taxCategoryIDSelect = new TaxCategoryIDSelect(pid);
          taxCategoryIDSelect.setRequired(true);
          taxCategoryIDSelect.addListener(new RequiredFieldListener(taxCategoryIDSelect,pid));
      	  ValidatorsList.addListeners((Collection<BlurListener>) taxCategoryIDSelect.getListeners(BlurEvent.class));
          return taxCategoryIDSelect;
      }
      
      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        if ("Price Amount".equals(pid)) {
        	tf.setRequired(true);
        	tf.addValidator(new PositiveValueValidator());
        	tf.addListener(new RequiredFieldListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Item Name".equals(pid)) {
        	tf.setRequired(true);
        	tf.addListener(new RequiredFieldListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Line Extension Amount".equals(pid)) {
        	tf.setRequired(true);
        	tf.addValidator(new PositiveValueValidator());
        	tf.addListener(new RequiredFieldListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Base Quantity".equals(pid)) {
        	tf.setRequired(true);
        	tf.addValidator(new PositiveValueValidator());
        	tf.addListener(new RequiredFieldListener(tf,pid));
        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
        }
        else if ("Tax Total Amount".equals(pid)) {
        	tf.addValidator(new PositiveValueValidator());
        }
        else if ("Invoiced Quantity".equals(pid)) {
        	tf.addValidator(new PositiveValueValidator());
        }
        else if ("Tax Category Percent".equals(pid)) {
        	tf.addValidator(new PositiveValueValidator());
        }
        else if ("Price Allowance/Charge Multiplier Factor".equals(pid)) {
        	tf.addValidator(new PositiveValueValidator());
        }
        else if ("Price Allowance/Charge Amount".equals(pid)) {
        	tf.addValidator(new PositiveValueValidator());
        }
        else if ("Price Allowance/Charge Base Amount".equals(pid)) {
        	tf.addValidator(new PositiveValueValidator());
        }
        
        tf.addListener(new FieldEvents.FocusListener() {
      	  @Override
      	  public void focus(FocusEvent event) {
      		  tf.selectAll();
      	  }
	    });
      }
      return field;
    }
  }
  
  public class BIIRULE_T10_R018 implements ValueChangeListener, ItemSetChangeListener {

	  InvoiceLineAdapter line;
	  Form form;
	  
	  public BIIRULE_T10_R018(InvoiceLineAdapter line, Form form) {
		  this.line = line;
		  this.form = form;
	  }
	  
	  public void calc() {
		Field lineExtensionAmount = form.getField("Line Extension Amount");
			
		BigDecimal price = line.getInvLinePriceAmount();
		BigDecimal baseQuantity = line.getInvLinePriceBaseQuantity();
		BigDecimal invoicedQuantity = line.getInvLineInvoicedQuantity();
		List<AllowanceChargeType> list = line.getAllowanceCharge();
			
		BigDecimal amount = price.divide(baseQuantity).multiply(invoicedQuantity);
		amount.setScale(2, BigDecimal.ROUND_HALF_UP);
			
		for (AllowanceChargeType ac : list) {
			if (ac.getChargeIndicator().isValue())
				amount = amount.add(ac.getAmount().getValue());
			else
				amount = amount.subtract(ac.getAmount().getValue());
		}
			
		if (amount.doubleValue() < 0)
			lineExtensionAmount.setValue(new BigDecimal(0.00));
		else
			lineExtensionAmount.setValue(amount);
	  }
	  
	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		calc();
	}
	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		calc();
	}
		  
  }
  
  public static class EUGEN_T10_R018 implements ValueChangeListener {
	  
	  Form form;
	  String taxSchemeField, taxCategoryField, taxTotalField;
	  
	  public EUGEN_T10_R018(Form form, String taxSchemeField, String taxCategoryField, String taxTotalField) {
		  this.form = form;
		  this.taxCategoryField = taxCategoryField;
		  this.taxSchemeField = taxSchemeField;
		  this.taxTotalField = taxTotalField;
	  }
	  
	  @Override
	  public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		  String taxScheme = (String)form.getField(taxSchemeField).getValue();
		  String taxCategory = (String)form.getField(taxCategoryField).getValue();
		  Field taxAmount = form.getField(taxTotalField);
		  
		  if (taxScheme.equals("VAT") && taxCategory.equals("AE")) {
			  taxAmount.setValue("0.00");
			  taxAmount.setReadOnly(true);
		  }
		  else {
			  taxAmount.setReadOnly(false);
		  }
	  }
	  
  }

}
