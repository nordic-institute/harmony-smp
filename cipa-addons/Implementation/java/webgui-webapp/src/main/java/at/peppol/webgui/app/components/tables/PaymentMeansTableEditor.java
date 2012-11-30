package at.peppol.webgui.app.components.tables;

import java.util.Date;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;

import at.peppol.webgui.app.components.PartyAgencyIDSelect;
import at.peppol.webgui.app.components.PaymentMeansSelect;
import at.peppol.webgui.app.components.adapters.Adapter;
import at.peppol.webgui.app.components.adapters.PaymentMeansAdapter;

public class PaymentMeansTableEditor extends TableEditor<PaymentMeansType, PaymentMeansAdapter> {

	public PaymentMeansTableEditor(boolean editMode) {
		super(editMode);
	}
	
	@Override
	public void cloneItem(PaymentMeansAdapter srcItem, PaymentMeansAdapter dstItem) {
		dstItem.setIDAdapter(srcItem.getIDAdapter());
		dstItem.setBranchIDAdapter(srcItem.getBranchIDAdapter());
		dstItem.setFinancialAccountIDAdapter(srcItem.getFinancialAccountIDAdapter());
		dstItem.setInstitutionIDAdapter(srcItem.getInstitutionIDAdapter());
		dstItem.setPaymentDueDateAdapter(srcItem.getPaymentDueDateAdapter());
		dstItem.setPaymentChannelCodeAdapter(srcItem.getPaymentChannelCodeAdapter());
		dstItem.setPaymentMeansCodeAdapter(srcItem.getPaymentMeansCodeAdapter());
	}
	
	@Override
	public PaymentMeansAdapter createItem() {
		return new PaymentMeansAdapter();
	}
		
	@Override
	public Form createTableForm(PaymentMeansAdapter paymentMeansAdapterItem, List<PaymentMeansType> paymentMeansList) {
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
	
	@SuppressWarnings ("serial")
	  class InvoicePaymentFieldFactory implements FormFieldFactory {

	    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
	        // Identify the fields by their Property ID.
	        final String pid = (String) propertyId;
	        if ("Payment Due Date".equals(pid)) {
	          final PopupDateField dueDateField = new PopupDateField("Payment Due Date");
	          dueDateField.setValue(new Date());
	          dueDateField.setResolution(DateField.RESOLUTION_DAY);
	          
	          /*dueDateField.addListener(new ValueChangeListener() {
	            @Override
	            public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
	              try {
	                final Date dueDate = (Date) dueDateField.getValue();
	                final GregorianCalendar greg = new GregorianCalendar();
	                greg.setTime(dueDate);

	                // Workaround to print only the date and not the time.
	                final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
	                XMLDate.setYear(greg.get(Calendar.YEAR));
	                XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
	                XMLDate.setDay(greg.get(Calendar.DATE));

	                parent.getInvoice().getPaymentMeans ().add (new PaymentMeansType ());
	                PaymentDueDateType sdt = new PaymentDueDateType ();
	                sdt.setValue (XMLDate);
	                parent.getInvoice().getPaymentMeans ().get (0).setPaymentDueDate (sdt);
	              } catch (final DatatypeConfigurationException ex) {
	                Logger.getLogger(TabInvoiceHeader.class.getName()).log(Level.SEVERE, null, ex);
	              }
	            }
	          });*/
	          
	          return dueDateField;
	        }
	        
	        if ("Payment Means Code".equals(pid)) {
	            PaymentMeansSelect select = new PaymentMeansSelect(pid);
	            return select;
	        }
	        
	        
	/*        if ("Branch ID".equals(pid)) {
	        	Table tab = new Table("Label of table");
	        	// Define table columns. 
	        	tab.addContainerProperty(
	        	    "date",     Date.class,   null, "Date",         null, null);
	        	tab.addContainerProperty(
	        	    "quantity", Double.class, null, "Quantity (l)", null, null);
	        	tab.addContainerProperty(
	        	    "price",    Double.class, null, "Price (e/l)",  null, null);
	        	tab.addContainerProperty(
	        	    "total",    Double.class, null, "Total (e)",    null, null);
	        	
	        	return tab;
	        }
	*/
	        
	        final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
	        if (field instanceof AbstractTextField) {
	            ((AbstractTextField) field).setNullRepresentation("");
	        }
	        return field;
	    }
	  }
}
