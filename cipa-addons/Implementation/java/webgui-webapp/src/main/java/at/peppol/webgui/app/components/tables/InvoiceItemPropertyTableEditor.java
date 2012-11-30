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

import at.peppol.webgui.app.components.adapters.InvoiceItemPropertyAdapter;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;

public class InvoiceItemPropertyTableEditor extends
		TableEditor<ItemPropertyType, InvoiceItemPropertyAdapter> {

	public InvoiceItemPropertyTableEditor(boolean editMode) {
		super(editMode);
	}

	@Override
	public Form createTableForm(InvoiceItemPropertyAdapter itemPropertyBean,
			List<ItemPropertyType> invoiceList) {
		
		final Form invoiceItemPropertyForm = new Form(new FormLayout(), new ItemPropertyFieldFactory());
	    invoiceItemPropertyForm.setImmediate(true);

	    NestedMethodProperty mp = new NestedMethodProperty(itemPropertyBean, "tableLineID");
	    if(!editMode){
	      itemPropertyBean.setTableLineID(String.valueOf (invoiceList.size ()+1));
	    }
	    else {
	      mp.setReadOnly (true);
	    }
	    
	    //invoiceItemPropertyForm.addItemProperty ("Line ID #", new NestedMethodProperty(itemPropertyBean, "ID.value") );
	    //invoiceItemPropertyForm.addItemProperty ("Line ID #", mp );
	    invoiceItemPropertyForm.addItemProperty ("Additional Item Property Name", new NestedMethodProperty(itemPropertyBean, "ItemPropertyName") );
	    invoiceItemPropertyForm.addItemProperty ("Additional Item Property Value", new NestedMethodProperty(itemPropertyBean, "ItemPropertyValue") );

	    return invoiceItemPropertyForm;
	}

	class ItemPropertyFieldFactory implements FormFieldFactory {

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
	public InvoiceItemPropertyAdapter createItem() {
		InvoiceItemPropertyAdapter ac = new InvoiceItemPropertyAdapter();
	    
	    ac.setTableLineID ("");
	    ac.setItemPropertyName ("");
	    ac.setItemPropertyValue ("");
	    
	    return ac;
	}

	@Override
	public void cloneItem(InvoiceItemPropertyAdapter srcItem,
			InvoiceItemPropertyAdapter dstItem) {
		
		dstItem.setTableLineID (srcItem.getTableLineID ());
	    dstItem.setItemPropertyName (srcItem.getItemPropertyName ());
	    dstItem.setItemPropertyValue (srcItem.getItemPropertyValue ());
	}

}
