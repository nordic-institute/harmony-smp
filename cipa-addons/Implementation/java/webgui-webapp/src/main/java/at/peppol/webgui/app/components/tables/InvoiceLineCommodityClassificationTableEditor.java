package at.peppol.webgui.app.components.tables;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import at.peppol.webgui.app.components.TaxSchemeSelect;
import at.peppol.webgui.app.components.adapters.CommodityClassificationAdapter;
import at.peppol.webgui.app.components.adapters.InvoiceLineOrderReferenceAdapter;
import at.peppol.webgui.app.components.tables.InvoiceItemPropertyTableEditor.ItemPropertyFieldFactory;

public class InvoiceLineCommodityClassificationTableEditor extends
		GenericTableEditor<CommodityClassificationType, CommodityClassificationAdapter> {

	public InvoiceLineCommodityClassificationTableEditor(boolean editMode) {
		super(editMode);
	}

	@Override
	public Form createTableForm(CommodityClassificationAdapter t,
			List<CommodityClassificationType> invoiceList) {
		
		final Form invoiceLineCommodityForm = new Form(new FormLayout(), new CommodityFormFactory());
		invoiceLineCommodityForm.setImmediate(true);

	    //NestedMethodProperty mp = new NestedMethodProperty(t, "tableLineID");
	    if(!editMode){
	      t.setIDAdapter(String.valueOf (invoiceList.size ()+1));
	    }
	    else {
	      //mp.setReadOnly (true);
	    }
	    
	    //invoiceItemPropertyForm.addItemProperty ("Line ID #", new NestedMethodProperty(itemPropertyBean, "ID.value") );
	    //invoiceItemPropertyForm.addItemProperty ("Line ID #", mp );
	    invoiceLineCommodityForm.addItemProperty ("Commodity Code", new NestedMethodProperty(t, "InvLineCommodityCode") );

	    return invoiceLineCommodityForm;
	}
	
	class CommodityFormFactory implements FormFieldFactory {

	    @Override
	    public Field createField(Item item, Object propertyId, Component uiContext) {
	      // Identify the fields by their Property ID.
	      String pid = (String) propertyId;

	      Field field = DefaultFieldFactory.get().createField(item,propertyId, uiContext);
	      if (field instanceof AbstractTextField){
	          ((AbstractTextField) field).setNullRepresentation("");
	          final AbstractTextField tf = (AbstractTextField) field;
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


	@Override
	public CommodityClassificationAdapter createItem() {
		CommodityClassificationAdapter ac = new CommodityClassificationAdapter();
		
		ac.setInvLineCommodityCode("");
		
		return ac;
	}

	@Override
	public void cloneItem(CommodityClassificationAdapter srcItem,
			CommodityClassificationAdapter dstItem) {
		
		dstItem.setIDAdapter(srcItem.getIDAdapter());
		dstItem.setInvLineCommodityCode(srcItem.getInvLineCommodityCode());
		
	}

}
