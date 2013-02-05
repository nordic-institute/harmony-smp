package at.peppol.webgui.app;

import java.io.Serializable;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import at.peppol.webgui.app.login.UserFolder;
import at.peppol.webgui.app.login.UserFolderManager;

import com.vaadin.data.util.BeanItemContainer;

public class InvoiceBeanContainer extends BeanItemContainer<InvoiceBean> implements
	Serializable {

	private static final long serialVersionUID = -8111876763881309783L;

	public InvoiceBeanContainer() throws InstantiationException, IllegalAccessException {
		super(InvoiceBean.class);
	}
	
	public static InvoiceBeanContainer readInvoicesFromFolder(UserFolderManager<?> m, UserFolder folder) {
		
		try {
			InvoiceBeanContainer container = new InvoiceBeanContainer();
			List<InvoiceBean> invoices = m.getInvoicesFromUserFolder(folder);
			if (invoices != null) {
				for (InvoiceBean invoiceBean : invoices) {
					/*InvoiceBean bean = new InvoiceBean();
					if (invoice.getID() != null)
						bean.setInvoiceID(invoice.getID().getValue());
					if (invoice.getIssueDate() != null)
						bean.setInvoiceDate(invoice.getIssueDate().getValue());
					if (invoice.getAccountingCustomerParty() != null)
						bean.setInvoiceCustomer(invoice.getAccountingCustomerParty().getParty().getPartyName().get(0).getName().getValue());
					if (invoice.getAccountingSupplierParty() != null)
						bean.setInvoiceSupplier(invoice.getAccountingSupplierParty().getParty().getPartyName().get(0).getName().getValue());
				
					container.addBean(bean);*/
					container.addBean(invoiceBean);
				}
				
				return container;
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
