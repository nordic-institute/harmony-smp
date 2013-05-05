package eu.europa.ec.cipa.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;

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

import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceItemPropertyAdapter;

public class InvoiceItemPropertyTableEditor extends GenericTableEditor <ItemPropertyType, InvoiceItemPropertyAdapter> {

  public InvoiceItemPropertyTableEditor (final boolean editMode) {
    super (editMode);
  }

  @Override
  public Form createTableForm (final InvoiceItemPropertyAdapter itemPropertyBean,
                               final List <ItemPropertyType> invoiceList) {

    final Form invoiceItemPropertyForm = new Form (new FormLayout (), new ItemPropertyFieldFactory ());
    invoiceItemPropertyForm.setImmediate (true);

    final NestedMethodProperty mp = new NestedMethodProperty (itemPropertyBean, "tableLineID");
    if (!editMode) {
      itemPropertyBean.setTableLineID (String.valueOf (invoiceList.size () + 1));
    }
    else {
      mp.setReadOnly (true);
    }

    // invoiceItemPropertyForm.addItemProperty ("Line ID #", new
    // NestedMethodProperty(itemPropertyBean, "ID.value") );
    // invoiceItemPropertyForm.addItemProperty ("Line ID #", mp );
    invoiceItemPropertyForm.addItemProperty ("Additional Item Property Name",
                                             new NestedMethodProperty (itemPropertyBean, "ItemPropertyName"));
    invoiceItemPropertyForm.addItemProperty ("Additional Item Property Value",
                                             new NestedMethodProperty (itemPropertyBean, "ItemPropertyValue"));

    return invoiceItemPropertyForm;
  }

  class ItemPropertyFieldFactory implements FormFieldFactory {

    @Override
    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        tf.addListener (new FieldEvents.FocusListener () {
          @Override
          public void focus (final FocusEvent event) {
            tf.selectAll ();
          }
        });
      }

      return field;
    }
  }

  @Override
  public InvoiceItemPropertyAdapter createItem () {
    final InvoiceItemPropertyAdapter ac = new InvoiceItemPropertyAdapter ();

    ac.setTableLineID ("");
    ac.setItemPropertyName ("");
    ac.setItemPropertyValue ("");

    return ac;
  }

  @Override
  public void cloneItem (final InvoiceItemPropertyAdapter srcItem, final InvoiceItemPropertyAdapter dstItem) {

    dstItem.setTableLineID (srcItem.getTableLineID ());
    dstItem.setItemPropertyName (srcItem.getItemPropertyName ());
    dstItem.setItemPropertyValue (srcItem.getItemPropertyValue ());
  }

}
