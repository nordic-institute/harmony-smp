package at.peppol.webgui.app.components.adapters;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineIDType;

public class InvoiceLineOrderReferenceAdapter extends OrderLineReferenceType implements Adapter {

	String tableID = "";
	
	public InvoiceLineOrderReferenceAdapter() {
		LineIDType id = new LineIDType();
		this.setLineID(id);
	}
	
	public InvoiceLineOrderReferenceAdapter(OrderLineReferenceType ac) {
		this.setLineID(ac.getLineID());
	}
	
	@Override
	public String getIDAdapter() {
		return tableID;
	}

	@Override
	public void setIDAdapter(String id) {
		tableID = id;

	}
	
	@Override
	public void setID(IDType id) {
		// TODO Auto-generated method stub

	}
	
	public void setInvLineOrderLineID(String id) {
		getLineID().setValue(id);
	}
	
	public String getInvLineOrderLineID() {
		return getLineID().getValue();
	}

}
