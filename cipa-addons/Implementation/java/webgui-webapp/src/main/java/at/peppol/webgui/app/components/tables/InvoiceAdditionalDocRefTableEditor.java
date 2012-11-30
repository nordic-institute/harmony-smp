package at.peppol.webgui.app.components.tables;

import java.util.List;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Window.Notification;

import at.peppol.webgui.app.components.adapters.InvoiceAdditionalDocRefAdapter;
import at.peppol.webgui.app.utils.DocUpload;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import un.unece.uncefact.codelist.specification.ianamimemediatype._2003.BinaryObjectMimeCodeContentType;

public class InvoiceAdditionalDocRefTableEditor extends
		TableEditor<DocumentReferenceType, InvoiceAdditionalDocRefAdapter> {

	public InvoiceAdditionalDocRefTableEditor(boolean editMode) {
		super(editMode);
	}

	@Override
	public Form createTableForm(InvoiceAdditionalDocRefAdapter additionalDocRefItem,
			List<DocumentReferenceType> invoiceList) {

	    final Form invoiceAdditionalDocRefForm = new Form(new FormLayout());
	    invoiceAdditionalDocRefForm.setImmediate(true);
	    
	    NestedMethodProperty mp = new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefID");
	    if(!editMode){
	      IDType num = new IDType();
	      //num.setValue (String.valueOf (additionalDocRefList.size ()+1));
	      //additionalDocRefItem.setID(num);
	      
	      int max = 0;
	      for (DocumentReferenceType doc : invoiceList) {
	    	  if (Integer.parseInt(doc.getID().getValue()) > max)
	    		  max = Integer.parseInt(doc.getID().getValue());
	      }
	      num.setValue(String.valueOf(max+1));
	      additionalDocRefItem.setID(num);
	    }
	    else {
	      mp.setReadOnly (true);
	    }
	    
	    //invoiceAdditionalDocRefForm.addItemProperty ("Additional Doc Ref Type ID", mp );
	    invoiceAdditionalDocRefForm.addItemProperty ("Type of document", new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefDocumentType") );
	    //invoiceAdditionalDocRefForm.addItemProperty ("Filename", new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefEmbeddedDocumentBinaryObject") );
	    invoiceAdditionalDocRefForm.addItemProperty ("URI location", new NestedMethodProperty(additionalDocRefItem, "AdditionalDocRefExternalReference") );

	    return invoiceAdditionalDocRefForm;

	}

	@Override
	public InvoiceAdditionalDocRefAdapter createItem() {
		InvoiceAdditionalDocRefAdapter ac = new InvoiceAdditionalDocRefAdapter();
		   
	    ac.setAdditionalDocRefID ("");
	    ac.setAdditionalDocRefDocumentType ("");
	    //ac.setAdditionalDocRefEmbeddedDocumentBinaryObject (null);
	    ac.setAdditionalDocRefExternalReference("");
	    
	    return ac;
	}

	@Override
	public void cloneItem(InvoiceAdditionalDocRefAdapter srcItem,
			InvoiceAdditionalDocRefAdapter dstItem) {
		
		dstItem.setAdditionalDocRefID (srcItem.getAdditionalDocRefID ());
	    dstItem.setAdditionalDocRefDocumentType (srcItem.getAdditionalDocRefDocumentType ());
	    //dstItem.setAdditionalDocRefEmbeddedDocumentBinaryObject (srcItem.getAdditionalDocRefEmbeddedDocumentBinaryObject ());
	    dstItem.setAdditionalDocRefExternalReference (srcItem.getAdditionalDocRefExternalReference ());
	 
		
	}
	
	@Override
	public Button.ClickListener addButtonListener(
			final Button editButton, final Button deleteButton,
			final Layout hiddenContent, final GenericTable<DocumentReferenceType,InvoiceAdditionalDocRefAdapter> table, 
			final List<DocumentReferenceType> invoiceList, final Label label) {
		
		Button.ClickListener b = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
		        hiddenContent.removeAllComponents ();
		        
		        final InvoiceAdditionalDocRefAdapter adapterItem = createItem();
		        
		        hiddenContent.addComponent (label);
		        final Form docRefForm = createTableForm(adapterItem, invoiceList);
		        hiddenContent.addComponent(docRefForm);
		        
		        final Button saveNewLine = new Button("Save");
		                        
		  	  	final DocUpload upload = new DocUpload("uploads/");
		  	  	upload.addListener(new Upload.StartedListener() {
					@Override
					public void uploadStarted(StartedEvent event) {
						saveNewLine.setEnabled(false);
					}
		  	  		
		  	  	});
		  	  	upload.addListener(new Upload.FinishedListener() {
					@Override
					public void uploadFinished(FinishedEvent event) {
						saveNewLine.setEnabled(true);	
					}
				});
		  	  	
		  	  	saveNewLine.addListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						if (adapterItem.getAdditionalDocRefID() != null) {
			        		  if (!adapterItem.getAdditionalDocRefID().equals("")) {
			        			  //upload.submitUpload(); //this doesn't work. VAADIN problem?
			        			  if (upload.getFilename() != null) {
			        				  BinaryObjectMimeCodeContentType mimeType = adapterItem.getBinaryObjectMimeCodeContentType(upload.getMimeType());
			        				  hiddenContent.getWindow().showNotification(upload.getMimeType(), Notification.TYPE_TRAY_NOTIFICATION);
			        				  if ( mimeType!= null) {
			        					  adapterItem.setAdditionalDocRefFile(upload.getFilename());
			        					  adapterItem.setBinaryObjectMIMEType(mimeType);
			        					  adapterItem.setBinaryObjectByteArray(upload.getByteArray());
			        				  }
			        				  else {
			        					  //hiddenContent.getWindow().showNotification("Attachment not MediaType", Notification.TYPE_TRAY_NOTIFICATION);
			        				  }
			        			  }
			        			  
			        			  table.addLine (adapterItem);
			        			  //hide form
			        			  hiddenContent.setVisible(false);
			        		  }
			        		  else
			        			  hiddenContent.getParent().getWindow().showNotification("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
			        	  }
			        	  else
			        		  hiddenContent.getParent().getWindow().showNotification("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
						
						editButton.setEnabled(true);
			  			deleteButton.setEnabled(true);
					}
				});

		  	  	
		  	  	hiddenContent.addComponent(upload);
		  	  	
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
		        	upload.interruptUpload();
		            hiddenContent.removeAllComponents ();
		            //hide form
		            docRefForm.discard();
		            hiddenContent.setVisible(false);
		          }
		        }));
		        
		        hiddenContent.addComponent(buttonLayout);
		        
		        //hiddenContent.setVisible(!hiddenContent.isVisible());
		        hiddenContent.setVisible(true);

			}
		};
		
		return b;
	}

}
