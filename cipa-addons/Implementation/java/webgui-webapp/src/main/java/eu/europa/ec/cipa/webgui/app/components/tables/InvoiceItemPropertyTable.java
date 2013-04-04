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
package eu.europa.ec.cipa.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemPropertyType;

import com.vaadin.data.util.BeanItemContainer;

import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceItemPropertyAdapter;

@SuppressWarnings ("serial")
public class InvoiceItemPropertyTable extends GenericTable <ItemPropertyType, InvoiceItemPropertyAdapter> {

  public InvoiceItemPropertyTable (final List <ItemPropertyType> list) {
    linesFromInvoice = list;

    tableLines = new BeanItemContainer <InvoiceItemPropertyAdapter> (InvoiceItemPropertyAdapter.class);

    for (int i = 0; i < list.size (); i++) {
      final InvoiceItemPropertyAdapter bean = new InvoiceItemPropertyAdapter (list.get (i));
      bean.setTableLineID (String.valueOf (i + 1));
      tableLines.addBean (bean);
    }
    setContainerDataSource (tableLines);

    addPropertyWithHeader ("TableLineID", "# ID");
    addPropertyWithHeader ("ItemPropertyName", "Item Property Name");
    addPropertyWithHeader ("ItemPropertyValue", "Item Property Value");

    setDefinedPropertiesAsVisible ();
    setPageLength (4);
    setFooterVisible (false);
  }
  /*
   * private void addPropertyWithHeader(String property, String headerName) {
   * tableLines.addNestedContainerProperty(property); setColumnHeader(property,
   * headerName); visibleHeaderNames.add(property); } private void
   * setDefinedPropertiesAsVisible() {
   * setVisibleColumns(visibleHeaderNames.toArray()); } public void
   * addItemPropertyLine(InvoiceItemPropertyAdapter invln) {
   * itemPropertyLines.add(invln); tableLines.addBean(invln); } public void
   * setItemPropertyLine(String lineID, InvoiceItemPropertyAdapter ln) { //use
   * for editing.... if(getIndexFromID(lineID) > -1){ itemPropertyLines.set
   * (getIndexFromID(lineID), ln); //TODO: Better way to "refresh" the table?
   * //tableLines.addBean(ln); tableLines.removeAllItems (); Iterator
   * <ItemPropertyType> iterator = itemPropertyLines.iterator (); int count=1;
   * while (iterator.hasNext()) { InvoiceItemPropertyAdapter ac =
   * (InvoiceItemPropertyAdapter)iterator.next();
   * ac.setTableLineID(String.valueOf(count++)); tableLines.addBean(ac); } } }
   * public void removeItemPropertyLine(String lineID) { String id=""; int
   * index=0; String message = ""; message += "lineID="+lineID+"\n"; for (int
   * i=0;i<itemPropertyLines.size();i++) { InvoiceItemPropertyAdapter ac =
   * (InvoiceItemPropertyAdapter)itemPropertyLines.get(i); message +=
   * "rowID="+ac.getTableLineID()+"\n"; }
   * getWindow().showNotification(message,Notification.TYPE_TRAY_NOTIFICATION);
   * for (int i=0;i<itemPropertyLines.size();i++) { InvoiceItemPropertyAdapter
   * ac = (InvoiceItemPropertyAdapter)itemPropertyLines.get(i); if
   * (ac.getTableLineID ().equals (lineID)) { tableLines.removeItem (ac);
   * itemPropertyLines.remove (ac); index = i; id = ac.getTableLineID(); break;
   * } } if (!id.equals("")) for (int i=index;i<itemPropertyLines.size();i++) {
   * InvoiceItemPropertyAdapter ac =
   * (InvoiceItemPropertyAdapter)itemPropertyLines.get(i);
   * tableLines.removeItem(ac); itemPropertyLines.remove(ac); ac = new
   * InvoiceItemPropertyAdapter(ac); ac.setTableLineID(id);
   * tableLines.addBean(ac); itemPropertyLines.add(i,ac); id =
   * String.valueOf((Integer.valueOf(id).intValue() + 1));
   * //tableLines.getItem(ac).getBean().setTableLineID(String.valueOf(count)); }
   * } public int getIndexFromID(String lineID) { Iterator <ItemPropertyType>
   * iterator = itemPropertyLines.iterator (); while (iterator.hasNext()) {
   * InvoiceItemPropertyAdapter ac = (InvoiceItemPropertyAdapter)
   * iterator.next(); if (ac.getTableLineID ().equals (lineID)) { int index =
   * itemPropertyLines.indexOf (ac); return index; } } return -1; }
   */

}
