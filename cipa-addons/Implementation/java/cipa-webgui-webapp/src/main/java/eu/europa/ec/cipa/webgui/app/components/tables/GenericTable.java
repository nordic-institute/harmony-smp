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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

import eu.europa.ec.cipa.webgui.app.components.adapters.Adapter;

@SuppressWarnings ("serial")
public class GenericTable <Ttype, Tadapter extends Adapter> extends Table {
  protected List <Ttype> linesFromInvoice;
  protected BeanItemContainer <Tadapter> tableLines;
  private final List <String> visibleHeaderNames = new ArrayList <String> ();

  public GenericTable () {}

  public void addPropertyWithHeader (final String property, final String headerName) {
    tableLines.addNestedContainerProperty (property);
    setColumnHeader (property, headerName);
    visibleHeaderNames.add (property);
  }

  public BeanItemContainer <Tadapter> getTableLines () {
    return tableLines;
  }

  public void setDefinedPropertiesAsVisible () {
    setVisibleColumns (visibleHeaderNames.toArray ());
  }

  public void addLine (final Tadapter pms) {
    linesFromInvoice.add ((Ttype) pms);
    tableLines.addBean (pms);
    // System.out.println(pms.hashCode());
  }

  public void setLineItem (final String lineID, final Tadapter pms) {
    /*
     * Tadapter originalItem = getItemWithID(lineID); if (originalItem != null)
     * { int index = linesFromInvoice.indexOf(originalItem);
     * System.out.println("Index is "+index); if (index > -1) {
     * linesFromInvoice.set(index, (Ttype)pms); int tableIndex =
     * tableLines.indexOfId(originalItem);
     * System.out.println("Table index is "+tableIndex);
     * tableLines.removeItem(originalItem); tableLines.addItemAt(tableIndex,
     * pms); } }
     */
  }

  public void setLine (final String lineID, final Tadapter pms) {
    // use for editing....
    final int index = getIndexFromID (lineID);
    if (index > -1) {
      linesFromInvoice.set (index, (Ttype) pms);
      // Tadapter tableItem = tableLines.getIdByIndex(index);
      // tableLines.get

      tableLines.removeAllItems ();
      final Iterator <Ttype> iterator = linesFromInvoice.iterator ();
      while (iterator.hasNext ()) {
        final Object ac = iterator.next ();
        tableLines.addBean ((Tadapter) ac);
      }
    }
  }

  /*
   * public void removeLine(String lineID) { Iterator<Ttype> iterator =
   * linesFromInvoice.iterator (); while (iterator.hasNext()) { Adapter ac =
   * (Adapter) iterator.next(); if (ac.getIDAdapter().equals (lineID)) {
   * tableLines.removeItem (ac); linesFromInvoice.remove (ac); break; } } }
   */
  public void removeLine (final String lineID) {
    String id = "";
    int index = 0;

    for (int i = 0; i < linesFromInvoice.size (); i++) {
      final Tadapter ac = (Tadapter) linesFromInvoice.get (i);
      if (ac.getIDAdapter ().equals (lineID)) {
        linesFromInvoice.remove (ac);
        tableLines.removeItem (ac);
        index = i;
        id = ac.getIDAdapter ();
        break;
      }
    }
    if (!id.equals (""))
      for (int i = index; i < linesFromInvoice.size (); i++) {
        final Tadapter ac = (Tadapter) linesFromInvoice.get (i);
        linesFromInvoice.remove (ac);
        tableLines.removeItem (ac);
        ac.setIDAdapter (id);
        linesFromInvoice.add (i, (Ttype) ac);
        tableLines.addBean (ac);
        id = String.valueOf ((Integer.valueOf (id).intValue () + 1));
        // tableLines.getItem(ac).getBean().setTableLineID(String.valueOf(count));
      }

  }

  public Tadapter getItemWithID (final String id) {
    final Collection <Tadapter> collection = tableLines.getItemIds ();
    for (final Tadapter ac : collection) {
      if (ac.getIDAdapter ().equals (id)) {
        return ac;
      }
    }
    return null;
  }

  public int getIndexFromID (final String lineID) {
    final Collection <Tadapter> collection = tableLines.getItemIds ();
    for (final Tadapter ac : collection) {
      if (ac.getIDAdapter ().equals (lineID)) {
        final int index = linesFromInvoice.indexOf (ac);
        return index;
      }
    }
    return -1;
    /*
     * Iterator <Ttype> iterator = linesFromInvoice.iterator (); while
     * (iterator.hasNext()) { Tadapter ac = (Tadapter) iterator.next(); if
     * (ac.getIDAdapter().equals (lineID)) { int index =
     * linesFromInvoice.indexOf (ac); return index; } } return -1;
     */
  }

  /*
   * public Adapter getEntryFromID(String lineID) { Iterator <Ttype> iterator =
   * linesFromInvoice.iterator (); while (iterator.hasNext()) { Adapter ac =
   * (Adapter) iterator.next(); if (ac.getIDAdapter().equals (lineID)) { return
   * ac; } } return null; }
   */
  public Tadapter getInstanceOfTadapter (final Class <Tadapter> aClass) {
    try {
      return aClass.newInstance ();
    }
    catch (final InstantiationException e) {
      e.printStackTrace ();
    }
    catch (final IllegalAccessException e) {
      e.printStackTrace ();
    }
    return null;
  }
}
