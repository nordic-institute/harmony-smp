package eu.europa.ec.cipa.webgui.app.components.adapters;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineIDType;

public class InvoiceLineOrderReferenceAdapter extends OrderLineReferenceType implements Adapter {

  String tableID = "";

  public InvoiceLineOrderReferenceAdapter () {
    final LineIDType id = new LineIDType ();
    this.setLineID (id);
  }

  public InvoiceLineOrderReferenceAdapter (final OrderLineReferenceType ac) {
    if (ac.getLineID () != null)
      this.setLineID (ac.getLineID ());
    else
      this.setLineID (new LineIDType ());
  }

  @Override
  public String getIDAdapter () {
    return tableID;
  }

  @Override
  public void setIDAdapter (final String id) {
    tableID = id;

  }

  @Override
  public void setID (final IDType id) {
    // TODO Auto-generated method stub

  }

  public void setInvLineOrderLineID (final String id) {
    getLineID ().setValue (id);
  }

  public String getInvLineOrderLineID () {
    return getLineID ().getValue ();
  }

}
