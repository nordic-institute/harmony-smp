package at.peppol.webgui.app.components.adapters;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;

public interface Adapter {

  String getIDAdapter ();

  void setIDAdapter (String id);

  void setID (IDType id);

}
