package eu.europa.ec.cipa.webgui.app;

import java.io.Serializable;
import java.util.List;


import com.vaadin.data.util.BeanItemContainer;

import eu.europa.ec.cipa.webgui.app.login.UserFolder;
import eu.europa.ec.cipa.webgui.app.login.UserFolderManager;

public class InvoiceBeanContainer extends BeanItemContainer <InvoiceBean> implements Serializable {

  private static final long serialVersionUID = -8111876763881309783L;

  public InvoiceBeanContainer () throws InstantiationException, IllegalAccessException {
    super (InvoiceBean.class);
  }

  public static InvoiceBeanContainer readInvoicesFromFolder (final UserFolderManager <?> m, final UserFolder folder) {

    try {
      final InvoiceBeanContainer container = new InvoiceBeanContainer ();
      final List <InvoiceBean> invoices = m.getInvoicesFromUserFolder (folder);
      if (invoices != null) {
        for (final InvoiceBean invoiceBean : invoices) {
          /*
           * InvoiceBean bean = new InvoiceBean(); if (invoice.getID() != null)
           * bean.setInvoiceID(invoice.getID().getValue()); if
           * (invoice.getIssueDate() != null)
           * bean.setInvoiceDate(invoice.getIssueDate().getValue()); if
           * (invoice.getAccountingCustomerParty() != null)
           * bean.setInvoiceCustomer
           * (invoice.getAccountingCustomerParty().getParty
           * ().getPartyName().get(0).getName().getValue()); if
           * (invoice.getAccountingSupplierParty() != null)
           * bean.setInvoiceSupplier
           * (invoice.getAccountingSupplierParty().getParty
           * ().getPartyName().get(0).getName().getValue());
           * container.addBean(bean);
           */
          container.addBean (invoiceBean);
        }

        return container;
      }

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
