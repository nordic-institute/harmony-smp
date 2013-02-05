package at.peppol.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import at.peppol.webgui.app.components.adapters.InvoiceLineOrderReferenceAdapter;

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

public class InvoiceLineOrderReferenceTableEditor extends
                                                 GenericTableEditor <OrderLineReferenceType, InvoiceLineOrderReferenceAdapter> {

  public InvoiceLineOrderReferenceTableEditor (final boolean editMode) {
    super (editMode);
  }

  @Override
  public Form createTableForm (final InvoiceLineOrderReferenceAdapter t, final List <OrderLineReferenceType> invoiceList) {

    final Form invoiceLineOrderForm = new Form (new FormLayout (), new LineOrderFactory ());
    invoiceLineOrderForm.setImmediate (true);

    // NestedMethodProperty mp = new NestedMethodProperty(t, "tableLineID");
    if (!editMode) {
      t.setIDAdapter (String.valueOf (invoiceList.size () + 1));
    }
    else {
      // mp.setReadOnly (true);
    }

    // invoiceItemPropertyForm.addItemProperty ("Line ID #", new
    // NestedMethodProperty(itemPropertyBean, "ID.value") );
    // invoiceItemPropertyForm.addItemProperty ("Line ID #", mp );
    invoiceLineOrderForm.addItemProperty ("Order ID", new NestedMethodProperty (t, "InvLineOrderLineID"));

    return invoiceLineOrderForm;
  }

  class LineOrderFactory implements FormFieldFactory {

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
  public InvoiceLineOrderReferenceAdapter createItem () {
    final InvoiceLineOrderReferenceAdapter ac = new InvoiceLineOrderReferenceAdapter ();

    ac.setInvLineOrderLineID ("");

    return ac;
  }

  @Override
  public void cloneItem (final InvoiceLineOrderReferenceAdapter srcItem, final InvoiceLineOrderReferenceAdapter dstItem) {

    dstItem.setIDAdapter (srcItem.getIDAdapter ());
    dstItem.setInvLineOrderLineID (dstItem.getInvLineOrderLineID ());

  }

}
