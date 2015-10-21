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
package eu.europa.ec.cipa.webgui.app;


import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import eu.europa.ec.cipa.webgui.app.login.UserFolder;
import eu.europa.ec.cipa.webgui.app.login.UserFolderManager;

public class ShowItemsPanel extends HorizontalLayout {

  VerticalLayout mainLayout = new VerticalLayout ();
  UserFolderManager m;
  Table table;

  public ShowItemsPanel (final String title, final UserFolderManager m, final UserFolder folder) {
    super ();
    setWidth ("90%");
    mainLayout.setSpacing (true);
    addComponent (mainLayout);
    this.m = m;
    init (folder);
  }

  public Table getTable () {
    return table;
  }

  public void init (final UserFolder folder) {
    Label header;
    InvoiceBeanContainer bean;
    table = new Table ();

    if (folder == null) {
      header = new Label ("<h3>Invoices in folder \"" + m.getDrafts ().getName () + "\"</h3>", Label.CONTENT_XHTML);
      bean = InvoiceBeanContainer.readInvoicesFromFolder (m, m.getDrafts ());
    }
    else {
      header = new Label ("<h3>Invoices in folder \"" + folder.getName () + "\"</h3>", Label.CONTENT_XHTML);
      bean = InvoiceBeanContainer.readInvoicesFromFolder (m, folder);
    }
    if (bean != null)
      table.setContainerDataSource (bean);
    table.setSelectable (true);
    table.setVisibleColumns (new String [] { "invoiceID", "invoiceDate", "invoiceSupplier", "invoiceCustomer" });
    table.setColumnHeaders (new String [] { "Invoice ID", "Date", "Supplier", "Customer" });
    table.setPageLength (10);
    table.setSizeFull ();
    table.addStyleName ("striped strong");

    final HorizontalLayout buttonsLayout = new HorizontalLayout ();
    buttonsLayout.setMargin (true);
    buttonsLayout.setSpacing (true);

    mainLayout.addComponent (header);
    mainLayout.addComponent (table);
  }

  public void reloadTable (final UserFolder folder) {
    InvoiceBeanContainer bean;
    if (folder == null) {
      bean = InvoiceBeanContainer.readInvoicesFromFolder (m, m.getDrafts ());
    }
    else {
      bean = InvoiceBeanContainer.readInvoicesFromFolder (m, folder);
    }
    if (bean != null)
      table.setContainerDataSource (bean);
    table.setSelectable (true);
    table.setVisibleColumns (new String [] { "invoiceID", "invoiceDate", "invoiceSupplier", "invoiceCustomer" });
    table.setColumnHeaders (new String [] { "Invoice ID", "Date", "Supplier", "Customer" });
    table.setPageLength (10);
    table.setSizeFull ();
    table.addStyleName ("striped strong");
  }
}
