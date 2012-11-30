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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialInstitutionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentTermsType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentChannelCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentDueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentMeansCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.RegistrationNameType;
import un.unece.uncefact.codelist.specification.ianamimemediatype._2003.BinaryObjectMimeCodeContentType;

import at.peppol.webgui.app.components.adapters.Adapter;
import at.peppol.webgui.app.components.adapters.PaymentMeansAdapter;
import at.peppol.webgui.app.components.tables.GenericTable;
import at.peppol.webgui.app.components.tables.PaymentMeansTable;
import at.peppol.webgui.app.components.tables.PaymentMeansTableEditor;
import at.peppol.webgui.app.utils.DocUpload;
import at.peppol.webgui.app.validator.RequiredFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class TabInvoicePayment extends Form {
  private InvoiceTabForm parent;
  
  private PaymentMeansAdapter originalItem;
  private PaymentMeansAdapter paymentMeansAdapterItem;
  
  private List<PaymentMeansType> paymentMeansList;
  //private PaymentMeansType paymentMeansItem;

  //private List<PaymentTermsType> paymentTermsList;
  private PaymentTermsType paymentTermsItem;
  
  private PartyType payeeParty;
  private PartyDetailForm payeeForm;
  
  private VerticalLayout hiddenContent;
  private PaymentMeansTable table;
  
  private boolean editMode;
  
  public TabInvoicePayment(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  @SuppressWarnings("serial")
  private void initElements() {
	editMode = false;
    paymentMeansList = parent.getInvoice().getPaymentMeans();
    //paymentMeansItem = createPaymentMeansItem();
    //paymentMeansList.add (paymentMeansItem);

    //paymentTermsList = parent.getInvoice().getPaymentTerms ();
    //PaymentTermsType pt = new PaymentTermsType();
    //paymentTermsList.add (pt);
    
    paymentTermsItem = new PaymentTermsType();
    paymentTermsItem.getNote().add(new NoteType());
    parent.getInvoice().getPaymentTerms().add(paymentTermsItem);
        
    //payeeParty = parent.getInvoice().getPayeeParty ();
    payeeParty = createPayeePartyItem ();
    parent.getInvoice().setPayeeParty(payeeParty);
    //payeeParty = new PartyType();
    //payeeParty.setParty(new PartyType());
    
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);
    
    final GridLayout grid = new GridLayout(2, 2);
    grid.setSpacing (true);
    //grid.setMargin(true);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    final Panel outerPanel = new Panel("Payment");
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    
    final Panel invoiceDetailsPanel = new Panel("Payment Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    //invoiceDetailsPanel.addComponent(createInvoicePaymentTopForm());
    //grid.addComponent(invoiceDetailsPanel, 0, 0);
    
    final Panel payeePartyPanel = new Panel("Payee Details");
    payeePartyPanel.setStyleName("light");
    payeePartyPanel.setSizeFull();
    payeePartyPanel.addComponent(createInvoicePayeePartyForm());
    //payeeForm = new PartyDetailForm("Payee", payeeParty);
    //payeePartyPanel.addComponent(payeeForm);
    grid.addComponent(payeePartyPanel, 0, 0);
    
    final Panel paymentTermsPanel = new Panel("Payment Terms");
    paymentTermsPanel.setStyleName("light");
    paymentTermsPanel.setSizeFull();
    paymentTermsPanel.addComponent(createInvoicePaymentTermsForm());
    grid.addComponent(paymentTermsPanel, 1, 0);
    
    final Panel paymentMeansPanel = new Panel("Payment Means");
    VerticalLayout paymentMeansLayout = new VerticalLayout();
    paymentMeansPanel.setContent(paymentMeansLayout);
    paymentMeansPanel.setStyleName("light");
    paymentMeansPanel.setSizeFull();
    paymentMeansLayout.setSpacing(true);
    paymentMeansLayout.setMargin(true);
    
    table = new PaymentMeansTable(paymentMeansList);
    table.setSelectable(true);
    table.setImmediate(true);
    table.setNullSelectionAllowed(false);
    table.setHeight (200, UNITS_PIXELS);
    table.setSizeFull();
    table.setWidth("80%");
    table.setFooterVisible (false);
    table.addStyleName ("striped strong");
    
    HorizontalLayout tableLayout = new HorizontalLayout();
    paymentMeansLayout.addComponent(tableLayout);
    paymentMeansLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);
    
    VerticalLayout tableButtonsLayout = new VerticalLayout();
    tableButtonsLayout.setSpacing(true);
    tableButtonsLayout.setMargin(true);
    final Button addButton = new Button("Add new");
    final Button editButton = new Button("Edit selected");
    final Button deleteButton = new Button("Delete selected");
    tableButtonsLayout.addComponent(addButton);
    tableButtonsLayout.addComponent(editButton);
    tableButtonsLayout.addComponent(deleteButton);
    
    tableLayout.addComponent(table);
    tableLayout.addComponent(tableButtonsLayout);
    
    outerPanel.addComponent(paymentMeansPanel);
    
    grid.setSizeUndefined();
    
    PaymentMeansTableEditor editor = new PaymentMeansTableEditor(editMode);
    Label label = new Label("<h3>Adding new payments means</h3>", Label.CONTENT_XHTML);
    addButton.addListener(editor.addButtonListener(editButton, deleteButton, hiddenContent, table, paymentMeansList, label));
    label = new Label("<h3>Edit payment means line</h3>", Label.CONTENT_XHTML);
    editButton.addListener(editor.editButtonListener(addButton, deleteButton, hiddenContent, table, paymentMeansList, label));
    deleteButton.addListener(editor.deleteButtonListener(table));
    
    setLayout(outerLayout);
    outerPanel.requestRepaintAll();
  }
  
  private PartyType createPayeePartyItem() {
    PartyType pt = new PartyType();
    
    PartyIdentificationType partyID = new PartyIdentificationType();
    partyID.setID(new IDType());
    pt.getPartyIdentification().add(partyID);
    
    PartyNameType partyName = new PartyNameType();
    partyName.setName(new NameType());
    pt.getPartyName().add(partyName);
    
    PartyLegalEntityType legalEntity = new PartyLegalEntityType();
    legalEntity.setCompanyID(new CompanyIDType());
    pt.getPartyLegalEntity().add(legalEntity);
    
    return pt;
  }
  
/*  public Form createInvoicePaymentTopForm() {
    final Form invoicePaymentTopForm = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
    invoicePaymentTopForm.setImmediate(true);
      
    invoicePaymentTopForm.addItemProperty ("Payment ID", new NestedMethodProperty(paymentMeansItem.getID (), "value") );
    invoicePaymentTopForm.addItemProperty ("Payment Means Code", new NestedMethodProperty(paymentMeansItem.getPaymentMeansCode (), "value") );
    final Date paymentDueDate = new Date ();
    invoicePaymentTopForm.addItemProperty ("Payment Due Date", new ObjectProperty <Date> (paymentDueDate));    
    invoicePaymentTopForm.addItemProperty ("Payment Channel Code", new NestedMethodProperty(paymentMeansItem.getPaymentChannelCode (), "value") );    
    invoicePaymentTopForm.addItemProperty ("Financial Account ID", new NestedMethodProperty(paymentMeansItem.getPayeeFinancialAccount (), "ID.value") );    
    invoicePaymentTopForm.addItemProperty ("Financial Branch ID", new NestedMethodProperty(paymentMeansItem.getPayeeFinancialAccount ().getFinancialInstitutionBranch (), "ID.value") );    
    invoicePaymentTopForm.addItemProperty ("Financial Institution ID", new NestedMethodProperty(paymentMeansItem.getPayeeFinancialAccount ().getFinancialInstitutionBranch ().getFinancialInstitution (), "ID.value") );    
    
    return invoicePaymentTopForm;
  }  
*/ 
  public Form createInvoicePayeePartyForm() {
    final Form invoicePayeePartyForm = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
    invoicePayeePartyForm.setImmediate(true);
    
    invoicePayeePartyForm.addItemProperty("Payee ID", new NestedMethodProperty(payeeParty.getPartyIdentification().get(0), "ID.value"));
    invoicePayeePartyForm.addItemProperty("Payee Name", new NestedMethodProperty(payeeParty.getPartyName().get(0), "Name.value"));
    invoicePayeePartyForm.addItemProperty("Legal Entity ID", new NestedMethodProperty(payeeParty.getPartyLegalEntity().get(0), "CompanyID.value"));
    
    return invoicePayeePartyForm;

  }    
  
  public Form createInvoicePaymentTermsForm() {
    final Form invoicePaymentTermsForm = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
    invoicePaymentTermsForm.setImmediate(true);
    
    invoicePaymentTermsForm.addItemProperty("Payment Terms", new NestedMethodProperty(paymentTermsItem.getNote().get(0), "value"));
    
    return invoicePaymentTermsForm;

  }
  
/*  public Form createInvoicePaymentMeansForm() {
	  Form form = new Form(new FormLayout(), new InvoicePaymentFieldFactory());
	  form.setImmediate(true);
	  
	  //automatically set the id
	  if (!editMode) {
		  IDType num = new IDType();
	      //num.setValue (String.valueOf (paymentMeansList.size ()+1));
	      //paymentMeansAdapterItem.setID(num);
	      int max = 0;
	      for (PaymentMeansType payment : paymentMeansList) {
	    	  if (Integer.parseInt(payment.getID().getValue()) > max)
	    		  max = Integer.parseInt(payment.getID().getValue());
	      }
	      num.setValue(String.valueOf(max+1));
	      paymentMeansAdapterItem.setID(num);
	  }
	  
      form.addItemProperty("Payment Means Code", new NestedMethodProperty(paymentMeansAdapterItem, "PaymentMeansCodeAdapter"));
      form.addItemProperty("Payment Due Date", new NestedMethodProperty(paymentMeansAdapterItem, "PaymentDueDateAdapter"));
      form.addItemProperty("Payment Channel Code", new NestedMethodProperty(paymentMeansAdapterItem, "PaymentChannelCodeAdapter"));
      form.addItemProperty("Account Number", new NestedMethodProperty(paymentMeansAdapterItem, "FinancialAccountIDAdapter"));
      form.addItemProperty("Branch ID", new NestedMethodProperty(paymentMeansAdapterItem, "BranchIDAdapter"));
      form.addItemProperty("Financial Institution ID", new NestedMethodProperty(paymentMeansAdapterItem, "InstitutionIDAdapter"));
      
	  return form;
  }
*/
  
  @SuppressWarnings ("serial")
  class InvoicePaymentFieldFactory implements FormFieldFactory {

    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
        // Identify the fields by their Property ID.
        final String pid = (String) propertyId;
        
        if ("Payee ID".equals(pid)) {
            final PartyAgencyIDSelect select = new PartyAgencyIDSelect(pid);
            select.addListener(new Property.ValueChangeListener() {

                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                	payeeParty.getPartyIdentification().get(0)
                            .getID()
                            .setSchemeAgencyName(select.getSelectedAgencyName());
                }
            });
            return select;
        }
        
        if ("Payment Terms".equals(pid)) {
        	TextArea area = new TextArea(pid);
        	area.setWordwrap(true);
        	area.setRows(3);
        	area.setNullRepresentation("");
        	return area;
        }
        
        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if (field instanceof AbstractTextField) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
        return field;
    }
  }    
}
