package at.peppol.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import at.peppol.webgui.app.components.adapters.InvoiceLineOrderReferenceAdapter;

import com.vaadin.data.util.BeanItemContainer;

@SuppressWarnings ("serial")
public class InvoiceLineOrderReferenceTable extends
                                           GenericTable <OrderLineReferenceType, InvoiceLineOrderReferenceAdapter> {

  public InvoiceLineOrderReferenceTable (final List <OrderLineReferenceType> list) {
    linesFromInvoice = list;

    tableLines = new BeanItemContainer <InvoiceLineOrderReferenceAdapter> (InvoiceLineOrderReferenceAdapter.class);

    for (int i = 0; i < list.size (); i++) {
      final InvoiceLineOrderReferenceAdapter bean = new InvoiceLineOrderReferenceAdapter (list.get (i));
      bean.setIDAdapter (String.valueOf (i + 1));
      tableLines.addBean (bean);
    }
    setContainerDataSource (tableLines);

    addPropertyWithHeader ("IDAdapter", "#");
    addPropertyWithHeader ("InvLineOrderLineID", "Order ID");

    setDefinedPropertiesAsVisible ();
    setPageLength (4);
    setFooterVisible (false);
  }

}
