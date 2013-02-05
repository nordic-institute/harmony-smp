package at.peppol.webgui.app.components.adapters;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CommodityCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;

public class CommodityClassificationAdapter extends CommodityClassificationType implements Adapter {

  String tableID = "";

  public CommodityClassificationAdapter () {
    super ();
    final CommodityCodeType code = new CommodityCodeType ();
    this.setCommodityCode (code);
  }

  public CommodityClassificationAdapter (final CommodityClassificationType ac) {
    if (ac.getCommodityCode () != null)
      this.setCommodityCode (ac.getCommodityCode ());
    else
      this.setCommodityCode (new CommodityCodeType ());
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

  public void setInvLineCommodityCode (final String id) {
    this.getCommodityCode ().setValue (id);
  }

  public String getInvLineCommodityCode () {
    return getCommodityCode ().getValue ();
  }

}
