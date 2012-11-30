package at.peppol.webgui.app.components.adapters;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.OrderLineReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CommodityCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineIDType;

public class CommodityClassificationAdapter extends CommodityClassificationType implements Adapter {

	String tableID = "";
	
	public CommodityClassificationAdapter() {
		super();
		CommodityCodeType code = new CommodityCodeType(); 
		this.setCommodityCode(code);
	}
	
	public CommodityClassificationAdapter(CommodityClassificationType ac) {
		this.setCommodityCode(ac.getCommodityCode());
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
	
	public void setInvLineCommodityCode(String id) {
		this.getCommodityCode().setValue(id);
	}
	
	public String getInvLineCommodityCode() {
		return getCommodityCode().getValue();
	}

}
