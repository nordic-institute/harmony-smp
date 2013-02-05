package at.peppol.webgui.app.components.tables;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import at.peppol.webgui.app.components.adapters.InvoiceItemPropertyAdapter;
import at.peppol.webgui.app.components.adapters.InvoiceLineOrderReferenceAdapter;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;

@SuppressWarnings("serial")
public class InvoiceLineOrderReferenceTable extends
		GenericTable<OrderLineReferenceType, InvoiceLineOrderReferenceAdapter> {

	public InvoiceLineOrderReferenceTable(List<OrderLineReferenceType> list) {
		linesFromInvoice = list;
	    
	    tableLines = new BeanItemContainer<InvoiceLineOrderReferenceAdapter>(InvoiceLineOrderReferenceAdapter.class);
	    
	    for (int i=0;i<list.size();i++) {
	    	InvoiceLineOrderReferenceAdapter bean = new InvoiceLineOrderReferenceAdapter(list.get(i));
	    	bean.setIDAdapter(String.valueOf(i+1));
	    	tableLines.addBean(bean);
	    }
	    setContainerDataSource(tableLines);
	    
	    addPropertyWithHeader("IDAdapter", "#");
	    addPropertyWithHeader("InvLineOrderLineID", "Order ID");
	    
	    setDefinedPropertiesAsVisible();
	    setPageLength(4);
	    setFooterVisible(false);
	}
	
}
