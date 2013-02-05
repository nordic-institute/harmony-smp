package at.peppol.webgui.app;

import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class InvoiceBean {
  String folderEntryID;
  InvoiceType invoice;

  public InvoiceType getInvoice () {
    return invoice;
  }

  public void setInvoice (final InvoiceType invoice) {
    this.invoice = invoice;
  }

  public InvoiceBean (final InvoiceType invoice) {
    this.invoice = invoice;
  }

  public String getFolderEntryID () {
    return folderEntryID;
  }

  public void setFolderEntryID (final String folderEntryID) {
    this.folderEntryID = folderEntryID;
  }

  public String getInvoiceID () {
    String invoiceID = null;
    if (invoice.getID () != null) {
      invoiceID = invoice.getID ().getValue ();
    }
    return invoiceID;
  }

  public void setInvoiceID (final String invoiceID) {}

  public XMLGregorianCalendar getInvoiceDate () {
    XMLGregorianCalendar invoiceDate = null;
    if (invoice.getIssueDate () != null)
      invoiceDate = invoice.getIssueDate ().getValue ();

    return invoiceDate;
  }

  public void setInvoiceDate (final XMLGregorianCalendar invoiceDate) {}

  public String getInvoiceSupplier () {
    String invoiceSupplier = null;
    if (invoice.getAccountingSupplierParty ().getParty ().getPartyIdentification ().get (0).getID () != null)
      invoiceSupplier = invoice.getAccountingSupplierParty ()
                               .getParty ()
                               .getPartyIdentification ()
                               .get (0)
                               .getID ()
                               .getValue ();
    return invoiceSupplier;
  }

  public void setInvoiceSupplier (final String invoiceSupplier) {}

  public String getInvoiceCustomer () {
    String invoiceCustomer = null;
    if (invoice.getAccountingCustomerParty ().getParty ().getPartyIdentification ().get (0).getID () != null)
      invoiceCustomer = invoice.getAccountingCustomerParty ()
                               .getParty ()
                               .getPartyIdentification ()
                               .get (0)
                               .getID ()
                               .getValue ();
    return invoiceCustomer;
  }

  public void setInvoiceCustomer (final String invoiceCustomer) {}
}
