package at.peppol.webgui.app.components.tables;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import at.peppol.webgui.app.components.adapters.InvoiceLineOrderReferenceAdapter;
import at.peppol.webgui.app.components.tables.InvoiceItemPropertyTableEditor.ItemPropertyFieldFactory;

public class InvoiceLineOrderReferenceTableEditor extends
		TableEditor<OrderLineReferenceType, InvoiceLineOrderReferenceAdapter> {

	public InvoiceLineOrderReferenceTableEditor(boolean editMode) {
		super(editMode);
	}

	@Override
	public Form createTableForm(InvoiceLineOrderReferenceAdapter t,
			List<OrderLineReferenceType> invoiceList) {
		
		final Form invoiceLineOrderForm = new Form(new FormLayout(), new LineOrderFactory());
		invoiceLineOrderForm.setImmediate(true);

	    //NestedMethodProperty mp = new NestedMethodProperty(t, "tableLineID");
	    if(!editMode){
	      t.setIDAdapter(String.valueOf (invoiceList.size ()+1));
	    }
	    else {
	      //mp.setReadOnly (true);
	    }
	    
	    //invoiceItemPropertyForm.addItemProperty ("Line ID #", new NestedMethodProperty(itemPropertyBean, "ID.value") );
	    //invoiceItemPropertyForm.addItemProperty ("Line ID #", mp );
	    invoiceLineOrderForm.addItemProperty ("Order ID", new NestedMethodProperty(t, "InvLineOrderLineID") );

	    return invoiceLineOrderForm;
	}
	
	class LineOrderFactory implements FormFieldFactory {

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


	@Override
	public InvoiceLineOrderReferenceAdapter createItem() {
		InvoiceLineOrderReferenceAdapter ac = new InvoiceLineOrderReferenceAdapter();
		
		ac.setInvLineOrderLineID("");
		
		return ac;
	}

	@Override
	public void cloneItem(InvoiceLineOrderReferenceAdapter srcItem,
			InvoiceLineOrderReferenceAdapter dstItem) {
		
		dstItem.setIDAdapter(srcItem.getIDAdapter());
		dstItem.setInvLineOrderLineID(dstItem.getInvLineOrderLineID());
		
	}

}
