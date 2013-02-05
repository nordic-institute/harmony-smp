package at.peppol.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import at.peppol.webgui.app.components.adapters.Adapter;
import at.peppol.webgui.app.components.adapters.PaymentMeansAdapter;
import at.peppol.webgui.app.utils.Utils;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public abstract class GenericTableEditor<Ttype, Tadapter extends Adapter> {
	
	protected boolean editMode;
	protected InvoiceType invoice;
	
	public GenericTableEditor(boolean editMode){
		this.editMode = editMode;
	}
	
	public GenericTableEditor(boolean editMode, InvoiceType inv){
		this.editMode = editMode;
		invoice = inv;
	}
	
	public Button.ClickListener addButtonListener(
			final Button editButton, final Button deleteButton, 
			final Layout hiddenContent, final GenericTable<Ttype,Tadapter> table, 
			final List<Ttype> invoiceList, final Label label) {
		
		Button.ClickListener b = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//addMode = true;
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
		        hiddenContent.removeAllComponents();
		        
		        final Tadapter adapterItem = createItem();
		        
		        hiddenContent.addComponent(label);
		        final Form tableForm = createTableForm(adapterItem, invoiceList);
		        hiddenContent.addComponent(tableForm);
		        
		        final Button saveNewLine = new Button("Save");
		                
		  	  	saveNewLine.addListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						if (adapterItem.getIDAdapter() != null) {
							if (!adapterItem.getIDAdapter().equals("")) {
								try {
									Utils.validateFormFields(tableForm);
									table.addLine(adapterItem);
									table.requestRepaint();
				        			//hide form
				        			hiddenContent.setVisible(false);
								}catch (InvalidValueException e) {
									label.getWindow().showNotification("Form has errors", Notification.TYPE_TRAY_NOTIFICATION);
								}
			        		}
			        		else {
			        			hiddenContent.getWindow().showNotification("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
			        		}
			        	}
			        	else {
			        		hiddenContent.getWindow().showNotification("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
			        	}
			        	
						editButton.setEnabled(true);
			  			deleteButton.setEnabled(true);
					}
				});

		        //Save new line button
		        HorizontalLayout buttonLayout = new HorizontalLayout();
		        buttonLayout.setSpacing (true);
		        buttonLayout.setMargin (true);
		        buttonLayout.addComponent(saveNewLine);

		        buttonLayout.addComponent(new Button("Cancel",new Button.ClickListener(){
		          @Override
		          public void buttonClick (ClickEvent event) {
		        	editButton.setEnabled(true);
		  			deleteButton.setEnabled(true);
		            hiddenContent.removeAllComponents ();
		            //hide form
		            tableForm.discard();
		            hiddenContent.setVisible(false);
		            //addMode = false;
		          }
		        }));
		        
		        hiddenContent.addComponent(buttonLayout);
		        
		        //hiddenContent.setVisible(!hiddenContent.isVisible());
		        hiddenContent.setVisible(true);
		      }
	    };
	    
	    return b;
	}
	
	public Button.ClickListener editButtonListener(
			final Button addButton, final Button deleteButton,
			final Layout hiddenContent, final GenericTable<Ttype,Tadapter> table, 
			final List<Ttype> invoiceList, final Label label) {
		
		Button.ClickListener b = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Object rowId = table.getValue(); // get the selected rows id
		        if(rowId != null){
		        	if (table.getContainerProperty(rowId,"IDAdapter") != null) {
			        	hiddenContent.removeAllComponents ();
			        	editMode = true;
			        	addButton.setEnabled(false);
						deleteButton.setEnabled(false);
			        	
			        	final String sid = (String)table.getContainerProperty(rowId,"IDAdapter").getValue();
			        		          
			        	//get selected item
			        	final Tadapter adapterItem = (Tadapter)invoiceList.get(table.getIndexFromID(sid));
			        	//paymentMeansAdapterItem = table.getEntryFromID(sid);
			        	
			        	//clone it to original item
			        	final Tadapter originalItem = createItem();
			        	cloneItem(adapterItem, originalItem);
			          
			        	//Label formLabel = new Label("<h3>Edit payment means line</h3>", Label.CONTENT_XHTML);
			          
			        	hiddenContent.addComponent(label);
			        	final Form tableForm = createTableForm(adapterItem, invoiceList);
			        	tableForm.setImmediate(true);
			        	hiddenContent.addComponent(tableForm);
			          
			        	//Save new line button
			        	HorizontalLayout buttonLayout = new HorizontalLayout();
			        	buttonLayout.setSpacing (true);
			        	buttonLayout.addComponent(new Button("Save changes",new Button.ClickListener(){
				            @Override
				            public void buttonClick (ClickEvent event) {
				            	//paymentMeansForm.commit();
				            	try {
				            		Utils.validateFormFields(tableForm);
					            	table.setLine(sid, adapterItem);
					            	//hide form
					            	hiddenContent.setVisible(false);
					            	editMode = false;
					            	addButton.setEnabled(true);
									deleteButton.setEnabled(true);
				            	}catch (InvalidValueException e) {
				            		label.getWindow().showNotification("Form has errors", Notification.TYPE_TRAY_NOTIFICATION);
				            	}
				            }
			        	}));
			        	buttonLayout.addComponent(new Button("Cancel editing",new Button.ClickListener(){
				            @Override
				            public void buttonClick (ClickEvent event) {
				            	//paymentMeansForm.discard();
				            	table.setLine(sid, originalItem);
				            	//hide form
				            	hiddenContent.removeAllComponents ();
				            	hiddenContent.setVisible(false);
				            	editMode = false;
				            	addButton.setEnabled(true);
								deleteButton.setEnabled(true);
				            }
			        	}));
			          
			        	hiddenContent.addComponent(buttonLayout);
			        	hiddenContent.setVisible(true);
		        	}
		        	else {
		        		hiddenContent.getWindow ().showNotification("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
		        	}
		        }
		        else {
		        	hiddenContent.getWindow ().showNotification("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
		        }
			}
			
		};
		
		return b;
	}
	
	public Button.ClickListener deleteButtonListener(final GenericTable<Ttype,Tadapter> table) {
		
		Button.ClickListener b = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Object rowId = table.getValue(); // get the selected rows id
		        if(rowId != null){
		        	if (table.getContainerProperty(rowId,"IDAdapter") != null) {
			        	//if(table.getContainerProperty(rowId,"IDAdapter").getValue() != null){
			        		String sid = (String)table.getContainerProperty(rowId,"IDAdapter").getValue();
		        			if (sid != null)
		        				table.removeLine(sid);
			        	//}
		        	}
		        	else {
		        		table.getParent().getWindow ().showNotification("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
		        	}
		        }
		        else {
		        	table.getParent().getWindow ().showNotification("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
		        }
				
			}
		};
		
		return b;
	}
	
	public abstract Form createTableForm(Tadapter t, List<Ttype> invoiceList);
	
	public abstract Tadapter createItem();
	
	public abstract void cloneItem(Tadapter srcItem, Tadapter dstItem);
}
