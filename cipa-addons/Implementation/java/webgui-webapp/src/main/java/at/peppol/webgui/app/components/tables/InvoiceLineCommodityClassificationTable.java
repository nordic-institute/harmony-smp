package at.peppol.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import at.peppol.webgui.app.components.adapters.CommodityClassificationAdapter;

import com.vaadin.data.util.BeanItemContainer;

@SuppressWarnings ("serial")
public class InvoiceLineCommodityClassificationTable extends
                                                    GenericTable <CommodityClassificationType, CommodityClassificationAdapter> {

  public InvoiceLineCommodityClassificationTable (final List <CommodityClassificationType> list) {
    linesFromInvoice = list;

    tableLines = new BeanItemContainer <CommodityClassificationAdapter> (CommodityClassificationAdapter.class);

    for (int i = 0; i < list.size (); i++) {
      final CommodityClassificationAdapter bean = new CommodityClassificationAdapter (list.get (i));
      bean.setIDAdapter (String.valueOf (i + 1));
      tableLines.addBean (bean);
    }
    setContainerDataSource (tableLines);

    addPropertyWithHeader ("IDAdapter", "#");
    addPropertyWithHeader ("InvLineCommodityCode", "Order ID");

    setDefinedPropertiesAsVisible ();
    setPageLength (4);
    setFooterVisible (false);
  }

}
