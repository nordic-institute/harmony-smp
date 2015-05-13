/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.webgui.app.components.adapters;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ValueType;

@SuppressWarnings ("serial")
public class InvoiceItemPropertyAdapter extends ItemPropertyType implements Adapter {
  private String tableLineID;

  public InvoiceItemPropertyAdapter () {
    tableLineID = "";
    setName (new NameType ());
    setValue (new ValueType ());
  }

  public InvoiceItemPropertyAdapter (final ItemPropertyType item) {
    tableLineID = "";

    if (item.getName () != null)
      this.setName (item.getName ());
    else
      this.setName (new NameType ());

    if (item.getValue () != null)
      this.setValue (item.getValue ());
    else
      this.setValue (new ValueType ());
  }

  public void setID (final IDType id) {}

  public void setIDAdapter (final String id) {
    setTableLineID (id);
  }

  public String getIDAdapter () {
    return getTableLineID ();
  }

  public void setTableLineID (final String v) {
    tableLineID = v;
  }

  public String getTableLineID () {
    return tableLineID;
  }

  public void setItemPropertyName (final String v) {
    getName ().setValue (v);
  }

  public String getItemPropertyName () {
    return getName ().getValue ();
  }

  public void setItemPropertyValue (final String v) {
    getValue ().setValue (v);
  }

  public String getItemPropertyValue () {
    return getValue ().getValue ();
  }

}