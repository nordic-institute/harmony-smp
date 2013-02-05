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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app.components.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.InvoiceLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;

import at.peppol.webgui.app.components.adapters.InvoiceAdditionalDocRefAdapter;
import at.peppol.webgui.app.components.adapters.InvoiceAllowanceChargeAdapter;
import at.peppol.webgui.app.components.adapters.InvoiceLineAdapter;
import at.peppol.webgui.app.components.adapters.PaymentMeansAdapter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

/**
 *
 * @author Jerouris
 */

@SuppressWarnings ("serial")
public class InvoiceLineTable extends GenericTable<InvoiceLineType, InvoiceLineAdapter> {

/*  private final List<InvoiceLineType> invoiceLines;
  private final BeanItemContainer<InvoiceLineAdapter> tableLines =
          new BeanItemContainer<InvoiceLineAdapter>(InvoiceLineAdapter.class);
  private final List<String> visibleHeaderNames = new ArrayList<String>();*/
  

  public InvoiceLineTable(List<InvoiceLineType> items) {
	  linesFromInvoice = items;
	  
	  tableLines = new BeanItemContainer<InvoiceLineAdapter>(InvoiceLineAdapter.class);
	  
	  if (linesFromInvoice.size() > 0) {
		  for (int i=0;i<linesFromInvoice.size();i++) {
			  InvoiceLineType type = linesFromInvoice.get(i); 
			  InvoiceLineAdapter item = new InvoiceLineAdapter(type);
			  tableLines.addBean(item);
			  linesFromInvoice.set(i, item);
		  }
	  }
	  
	  //this.invoiceLines = items;
	  setContainerDataSource(tableLines);

	  addPropertyWithHeader("ID.value", "# ID");
	  addPropertyWithHeader("invLineItemName", "Item Name");
	  addPropertyWithHeader("invLineInvoicedQuantity", "Invoiced Quantity");
	  addPropertyWithHeader("invLineLineExtensionAmount", "Line Extension Amount");
	  addPropertyWithHeader("InvLineTaxAmount", "Tax Total Amount");

	  setDefinedPropertiesAsVisible();
	  setPageLength(4);
	  
	  tableLines.addNestedContainerProperty("CommonCurrency");
  }
/*
  private void addPropertyWithHeader(String property, String headerName) {
    tableLines.addNestedContainerProperty(property);
    setColumnHeader(property, headerName);
    visibleHeaderNames.add(property);
  }

  private void setDefinedPropertiesAsVisible() {
    setVisibleColumns(visibleHeaderNames.toArray());
  }

  public void addInvoiceLine(InvoiceLineAdapter invln) {
    invoiceLines.add(invln);
    tableLines.addBean(invln);   
  }

  
  public void setInvoiceLine(String lineID, InvoiceLineAdapter ln) {
    //use for editing....
    if(getIndexFromID(lineID) > -1){
      invoiceLines.set (getIndexFromID(lineID), ln);
      
      //TODO: Better way to "refresh" the table?
      //tableLines.addBean(ln);
      tableLines.removeAllItems ();
      Iterator<InvoiceLineType> iterator = invoiceLines.iterator ();
      while (iterator.hasNext()) {
        InvoiceLineType ac = iterator.next();
        tableLines.addBean ((InvoiceLineAdapter) ac);
      }
    }
  }  
  
  public void removeInvoiceLine(String lineID) {
    Iterator<InvoiceLineType> iterator = invoiceLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceLineType ac = iterator.next();
      if (ac.getID ().getValue ().equals (lineID)) {
    	invoiceLines.remove (ac);
    	tableLines.removeItem (ac);
        break;
        
      }
    }
  }
  
  public int getIndexFromID(String lineID) {
    Iterator<InvoiceLineType> iterator = invoiceLines.iterator ();
    while (iterator.hasNext()) {
      InvoiceLineType ac = iterator.next();
      if (ac.getID ().getValue ().equals (lineID)) {
        int index = invoiceLines.indexOf (ac);
        return index;
      }
    }    
    return -1;
  }  
  */
}
