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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ContractType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PeriodType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AccountingCostType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ContractTypeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentTypeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EndDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StartDateType;
import un.unece.uncefact.codelist.specification.ianamimemediatype._2003.BinaryObjectMimeCodeContentType;

import at.peppol.webgui.app.components.PartyDetailForm.PartyFieldFactory;
import at.peppol.webgui.app.components.adapters.InvoiceAdditionalDocRefAdapter;
import at.peppol.webgui.app.components.tables.InvoiceAdditionalDocRefTable;
import at.peppol.webgui.app.components.tables.InvoiceAdditionalDocRefTableEditor;
import at.peppol.webgui.app.utils.DocUpload;
import at.peppol.webgui.app.utils.ReceiverClass;
import at.peppol.webgui.app.validator.RequiredFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * @author Jerouris
 */
@SuppressWarnings("serial")
public class TabInvoiceHeader extends Form {
  private InvoiceTabForm parent;

  private List <DocumentReferenceType> additionalDocRefList;
  private InvoiceAdditionalDocRefAdapter additionalDocRefItem;
  
  private InvoiceAdditionalDocRefAdapter originalItem;

  private boolean addMode;
  private boolean editMode;
  
  public InvoiceAdditionalDocRefTable table;
  private VerticalLayout hiddenContent;

  public TabInvoiceHeader(InvoiceTabForm parent) {
    this.parent = parent;
    addMode = false;
    editMode = false;    
    initElements();
    
    parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
  }

  private void initElements() {
    additionalDocRefList = parent.getInvoice().getAdditionalDocumentReference ();
    setWidth("100%");
    setHeight("100%");
    //final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    //outerLayout.setMargin(true);
    //outerLayout.setSpacing(true);
        
    //grid that contains "Details", "Contract", "Order"
    final GridLayout topGridLayout = new GridLayout(2, 2);
    //topGridLayout.setSizeFull();
    topGridLayout.setMargin(true);
    topGridLayout.setSpacing(true);
        
    hiddenContent = new VerticalLayout();
    hiddenContent.setSpacing (true);
    hiddenContent.setMargin (true);    
    
    final Panel outerPanel = new Panel("Invoice Header");
    //outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerPanel.setContent(outerLayout);
    //outerLayout.addComponent(outerPanel);
    
    VerticalLayout tabLayout = new VerticalLayout();
    tabLayout.addComponent(outerPanel);
    
    outerLayout.addComponent(topGridLayout);
    
    final Panel invoiceDetailsPanel = new Panel("Invoice Header Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setWidth("50%");
    //invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceTopForm());
    topGridLayout.addComponent(invoiceDetailsPanel, 0, 0);
   
    final Panel orderReferencePanel = new Panel("Referencing Order");
    orderReferencePanel.setStyleName("light");
    orderReferencePanel.setWidth("50%");
    //orderReferencePanel.setSizeFull();
    orderReferencePanel.addComponent(createInvoiceOrderReferenceForm());
    topGridLayout.addComponent(orderReferencePanel, 0, 1);
    
    final VerticalLayout tableVerticalLayout = new VerticalLayout();
    //tableVerticalLayout.setSpacing (true);
    tableVerticalLayout.setMargin (true);
    outerLayout.addComponent(tableVerticalLayout);
    
    table = new InvoiceAdditionalDocRefTable(parent.getInvoice().getAdditionalDocumentReference ());
    table.setSelectable(true);
    table.setImmediate(true);
    table.setNullSelectionAllowed(false);
    table.setHeight (150, UNITS_PIXELS);
    table.setSizeFull();
    //table.setWidth("300px");
    table.setFooterVisible (false);
    table.addStyleName ("striped strong");
    
    Panel tablePanel = new Panel("Relevant Documents");
    tablePanel.setStyleName("light");
    tablePanel.setWidth("60%");
    tableVerticalLayout.addComponent(tablePanel);
    
    GridLayout h = new GridLayout(2,2);
    h.setMargin(true);
    h.setSpacing(true);
    tablePanel.setContent(h);
    h.addComponent(table,0,0);
    h.setColumnExpandRatio(0, 3);
    h.setColumnExpandRatio(1, 1);
    h.setSizeFull();
    
    Button addButton = new Button("Add new");
    Button editButton = new Button("Edit selected");
    Button deleteButton = new Button("Delete selected");
    
    VerticalLayout buttonsContainer = new VerticalLayout();
    buttonsContainer.setSpacing (true);
    buttonsContainer.addComponent (addButton);
    buttonsContainer.addComponent (editButton);
    buttonsContainer.addComponent (deleteButton);
    
    InvoiceAdditionalDocRefTableEditor editor = new InvoiceAdditionalDocRefTableEditor(editMode);
    Label label = new Label("<h3>Adding new relevant document</h3>", Label.CONTENT_XHTML);
    addButton.addListener(editor.addButtonListener(editButton, deleteButton, hiddenContent, table, additionalDocRefList, label));
    label = new Label("<h3>Edit relevant document</h3>", Label.CONTENT_XHTML);
    editButton.addListener(editor.editButtonListener(addButton, deleteButton, hiddenContent, table, additionalDocRefList, label));
    deleteButton.addListener(editor.deleteButtonListener(table));
    
    final Button addContractReferenceBtn = new Button("Add Contract Reference");
    final Button removeContractReferenceBtn = new Button("Remove Contract Reference");
    removeContractReferenceBtn.setVisible(false);
    addContractReferenceBtn.setStyleName("marginLeft");
    removeContractReferenceBtn.setStyleName("marginLeft");
    
    addContractReferenceBtn.addListener(new Button.ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			Panel panel = createInvoiceContractReference(removeContractReferenceBtn);
			topGridLayout.removeComponent(1, 0);
			topGridLayout.addComponent(panel,1,0);
			removeContractReferenceBtn.setVisible(true);
		}
	});
    
    removeContractReferenceBtn.addListener(new Button.ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			//remove the legal entity component panel
			Component c = removeContractReferenceBtn.getParent().getParent();
			topGridLayout.removeComponent(c);
			if (parent.getInvoice().getContractDocumentReference().size() > 0) {
				parent.getInvoice().getContractDocumentReference().remove(0);
			}
			
			topGridLayout.addComponent(addContractReferenceBtn, 1, 0);
		}
	});
    
    h.addComponent(buttonsContainer,1,0);
    
    topGridLayout.addComponent(addContractReferenceBtn, 1, 0);
        
    // ---- HIDDEN FORM BEGINS -----
    VerticalLayout formLayout = new VerticalLayout();
    formLayout.addComponent(hiddenContent);
    hiddenContent.setVisible(false);    

    h.addComponent(formLayout,0,1);
    // ---- HIDDEN FORM ENDS -----
    
    setLayout(tabLayout);
  }

  public Form createInvoiceTopForm() {
    final Form invoiceTopForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceTopForm.setImmediate(true);
    invoiceTopForm.setSizeFull();
      
    parent.getInvoice().setID (new IDType ());
    invoiceTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (parent.getInvoice().getID (), "value"));
        
    parent.getInvoice().setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    //parent.getInvoice().getDocumentCurrencyCode().setValue("EUR");
    invoiceTopForm.addItemProperty ("Currency", new NestedMethodProperty (parent.getInvoice().getDocumentCurrencyCode (), "value"));

    Date issueDate = new Date ();
    parent.getInvoice().setIssueDate (new IssueDateType ());
    invoiceTopForm.addItemProperty ("Issue Date", new ObjectProperty <Date> (issueDate));
   
    parent.getInvoice().getNote ().add (new NoteType ());
    invoiceTopForm.addItemProperty ("Invoice Note", new NestedMethodProperty (parent.getInvoice().getNote ().get (0), "value"));
    
    Date taxPointDate = new Date ();
    invoiceTopForm.addItemProperty ("Tax Point Date", new ObjectProperty <Date> (issueDate));
    
    parent.getInvoice().setAccountingCost (new AccountingCostType ());
    invoiceTopForm.addItemProperty ("Accounting Cost", new NestedMethodProperty (parent.getInvoice().getAccountingCost (), "value"));
    
    Date startDate = new Date ();
    invoiceTopForm.addItemProperty ("Invoice Period Start Date", new ObjectProperty <Date> (issueDate));

    Date endDate = new Date ();
    invoiceTopForm.addItemProperty ("Invoice Period End Date", new ObjectProperty <Date> (issueDate));
    
    
    return invoiceTopForm;
  }

  public Panel createInvoiceContractReference(Button removeButton) {
	  Panel contractReferencePanel = new Panel("Contract Reference");
	  contractReferencePanel.setStyleName("light");
	  //contractReferencePanel.setSizeFull();
	  
	  PropertysetItem contractReferenceItemSet = new PropertysetItem();
      
	  DocumentReferenceType dr = new DocumentReferenceType ();
	  dr.setID (new IDType ());
	  dr.setDocumentType (new DocumentTypeType ());
	  
	  //add the contract document reference
	  parent.getInvoice().getContractDocumentReference().add(dr);
	  
	  contractReferenceItemSet.addItemProperty ("Contract document reference ID", new NestedMethodProperty(parent.getInvoice().getContractDocumentReference().get(0).getID(), "value"));
	  contractReferenceItemSet.addItemProperty ("Contract document reference type", new NestedMethodProperty(parent.getInvoice().getContractDocumentReference().get(0).getDocumentType(), "value"));
	  
	  Form contractReferenceForm = new Form();
	  contractReferenceForm.setFormFieldFactory(new InvoiceFieldFactory());
	  contractReferenceForm.setItemDataSource(contractReferenceItemSet);
	  contractReferenceForm.setImmediate(true);
	  
	  contractReferencePanel.addComponent(contractReferenceForm);
	  contractReferencePanel.addComponent(removeButton);
	  
	  
	  return contractReferencePanel;
  }
  
  public Form createInvoiceOrderReferenceForm() {
    final Form invoiceOrderRefForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceOrderRefForm.setImmediate(true);
        
    OrderReferenceType rt = new OrderReferenceType ();
    rt.setID (new IDType ());
    parent.getInvoice().setOrderReference (rt);

    //DocumentReferenceType dr = new DocumentReferenceType ();
    //dr.setID (new IDType ());
    //dr.setDocumentType (new DocumentTypeType ());
    
    //parent.getInvoice().getContractDocumentReference ().add (dr);
    
    invoiceOrderRefForm.addItemProperty ("Order Reference ID", new NestedMethodProperty (parent.getInvoice().getOrderReference ().getID (), "value"));
    //invoiceOrderRefForm.addItemProperty ("Document Reference ID", new NestedMethodProperty (parent.getInvoice().getContractDocumentReference ().get(0).getID (), "value"));
    //invoiceOrderRefForm.addItemProperty ("Document Reference Type", new NestedMethodProperty (parent.getInvoice().getContractDocumentReference ().get(0).getDocumentType (), "value"));

    return invoiceOrderRefForm;
  }
  
  @SuppressWarnings ("serial")
  class InvoiceFieldFactory implements FormFieldFactory {
	  final PopupDateField startDateField = new PopupDateField("Invoice Period Start Date");
	  
    @Override
    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
    	final String pid;
    	
      if (propertyId instanceof String) {
    	  pid = (String) propertyId;
      }
      else {
    	  pid = ((Label)propertyId).toString();
      }
      
      if ("Currency".equals(pid)) {
        final CurrencySelect curSelect = new CurrencySelect("Currency");
        return curSelect;
      }
      
      if ("Issue Date".equals(pid)) {
        final PopupDateField issueDateField = new PopupDateField("Issue Date");
        issueDateField.setValue(new Date());
        issueDateField.setResolution(DateField.RESOLUTION_DAY);
        issueDateField.addListener(new ValueChangeListener() {

          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date issueDate = (Date) issueDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(issueDate);

              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));

              parent.getInvoice().getIssueDate().setValue(XMLDate);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
       

        return issueDateField;
      }
      
      if ("Tax Point Date".equals(pid)) {
        final PopupDateField taxPointDateField = new PopupDateField("Tax Point Date");
        taxPointDateField.setValue(new Date());
        taxPointDateField.setResolution(DateField.RESOLUTION_DAY);
        taxPointDateField.addListener(new ValueChangeListener() {
          
          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date taxPointDate = (Date) taxPointDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(taxPointDate);
              
              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));
              
              parent.getInvoice().getIssueDate().setValue(XMLDate);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
        
        
        return taxPointDateField;
      }
      
      
      if ("Invoice Period Start Date".equals(pid)) {
        //final PopupDateField startDateField = new PopupDateField("Invoice Period Start Date");
    	startDateField.setValue(new Date());
        startDateField.setResolution(DateField.RESOLUTION_DAY);
        startDateField.addListener(new ValueChangeListener() {
          
          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date startDate = (Date) startDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(startDate);
              
              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));
              
              parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
              StartDateType sdt = new StartDateType ();
              sdt.setValue (XMLDate);
              parent.getInvoice().getInvoicePeriod ().get (0).setStartDate (sdt);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
        return startDateField;
      }
      
      if ("Invoice Period End Date".equals(pid)) {
        final PopupDateField endDateField = new PopupDateField("Invoice Period End Date");
        endDateField.setValue(new Date());
        //endDateField.setValue(startDateField.getValue());
        endDateField.setResolution(DateField.RESOLUTION_DAY);
        endDateField.addListener(new ValueChangeListener() {
          
          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date endDate = (Date) endDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(endDate);
              
              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));
              
              if (((Date)endDateField.getValue()).before((Date)startDateField.getValue())) {
            	  //endDateField.setValue(startDateField.getValue());
            	  endDateField.setComponentError(new UserError("End date must be later than start date"));
              }
              else {
            	  //parent.getInvoice().getInvoicePeriod ().add (new PeriodType());
            	  EndDateType edt = new EndDateType ();
            	  edt.setValue (XMLDate);
            	  if (parent.getInvoice().getInvoicePeriod().size() > 0)
            		  parent.getInvoice().getInvoicePeriod().get(0).setEndDate(edt);
            	  endDateField.setComponentError(null);
              }
              
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });      
        return endDateField;
      }      
      
      final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
          ((AbstractTextField) field).setNullRepresentation("");
          
          final AbstractTextField tf = (AbstractTextField) field;
          if ("Invoice ID".equals(pid)) {
          	tf.setRequired(true);
          	tf.addListener(new RequiredFieldListener(tf,pid));
          	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
          }
          /*else if ("Order Reference ID".equals(pid)) {
        	tf.setRequired(true);
          	tf.addListener(new RequiredFieldListener(tf,pid));
          	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
          }*/
          else if ("Contract document reference ID".equals(pid)) {
          	tf.setRequired(true);
            tf.addListener(new RequiredFieldListener(tf,pid));
            ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
          }
      }
      return field;
    }
  }
}
