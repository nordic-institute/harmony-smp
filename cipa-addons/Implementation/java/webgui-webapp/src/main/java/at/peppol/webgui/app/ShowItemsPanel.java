package at.peppol.webgui.app;

import at.peppol.webgui.app.login.UserFolder;
import at.peppol.webgui.app.login.UserFolderManager;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

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
