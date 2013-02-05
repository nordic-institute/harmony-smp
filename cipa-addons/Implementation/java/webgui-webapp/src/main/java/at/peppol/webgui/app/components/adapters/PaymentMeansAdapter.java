package at.peppol.webgui.app.components.adapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialAccountType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.FinancialInstitutionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentChannelCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentDueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PaymentMeansCodeType;
import at.peppol.webgui.app.utils.Utils;

//import com.phloc.ubl.codelist.EPaymentMeansCode20;

@SuppressWarnings("serial")
public class PaymentMeansAdapter extends PaymentMeansType implements Adapter {
	
	public PaymentMeansAdapter() {
		super();
		this.setPaymentMeansCode(new PaymentMeansCodeType());
		this.setPaymentDueDate(new PaymentDueDateType());
		this.setPaymentChannelCode(new PaymentChannelCodeType());
		this.setID(new IDType());
		
		this.setPayeeFinancialAccount(new FinancialAccountType());
		this.getPayeeFinancialAccount().setID(new IDType());
		this.getPayeeFinancialAccount().setFinancialInstitutionBranch(new BranchType());
		this.getPayeeFinancialAccount().getFinancialInstitutionBranch().setID(new IDType());
		this.getPayeeFinancialAccount().getFinancialInstitutionBranch().setFinancialInstitution(new FinancialInstitutionType());
		this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getFinancialInstitution().setID(new IDType());
	}
	
	public PaymentMeansAdapter(PaymentMeansType type) {
		super();
		if (type.getPaymentMeansCode() != null)
			this.setPaymentMeansCode(type.getPaymentMeansCode());
		else
			this.setPaymentMeansCode(new PaymentMeansCodeType());
		if (type.getPaymentDueDate() != null)
			this.setPaymentDueDate(type.getPaymentDueDate());
		else
			this.setPaymentDueDate(new PaymentDueDateType());
		if (type.getPaymentChannelCode() != null)
			this.setPaymentChannelCode(type.getPaymentChannelCode());
		else
			this.setPaymentChannelCode(new PaymentChannelCodeType());
		if (type.getID() != null)
			this.setID(type.getID());
		else
			this.setID(new IDType());
		
		if (type.getPayeeFinancialAccount() != null) {
			//System.out.println("Account info fetched");
			FinancialAccountType acc = type.getPayeeFinancialAccount();
			this.setPayeeFinancialAccount(acc);
			if (acc.getID() == null)
				this.getPayeeFinancialAccount().setID(new IDType());
			if (acc.getFinancialInstitutionBranch() == null)
				this.getPayeeFinancialAccount().setFinancialInstitutionBranch(new BranchType());
			if (acc.getFinancialInstitutionBranch().getID() == null)
				this.getPayeeFinancialAccount().getFinancialInstitutionBranch().setID(new IDType());
			if (acc.getFinancialInstitutionBranch().getFinancialInstitution() == null)
				this.getPayeeFinancialAccount().getFinancialInstitutionBranch().setFinancialInstitution(new FinancialInstitutionType());
			if (acc.getFinancialInstitutionBranch().getFinancialInstitution().getID() == null)
				this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getFinancialInstitution().setID(new IDType());
		}
		else {
			this.setPayeeFinancialAccount(new FinancialAccountType());
			this.getPayeeFinancialAccount().setID(new IDType());
			this.getPayeeFinancialAccount().setFinancialInstitutionBranch(new BranchType());
			this.getPayeeFinancialAccount().getFinancialInstitutionBranch().setID(new IDType());
			this.getPayeeFinancialAccount().getFinancialInstitutionBranch().setFinancialInstitution(new FinancialInstitutionType());
			this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getFinancialInstitution().setID(new IDType());
		}
	}
	
	
	public void setPaymentMeansCodeAdapter(String code) {
		this.getPaymentMeansCode().setValue(code);
	}
	
	public String getPaymentMeansCodeAdapter() {
		return this.getPaymentMeansCode().getValue();
	}
	
	public void setPaymentDueDateAdapter(Date date) {
		XMLGregorianCalendar XMLDate = Utils.DateToGregorian(date);
		this.getPaymentDueDate().setValue(XMLDate);
	}
	
	public Date getPaymentDueDateAdapter() {
		Date date = new Date();
		XMLGregorianCalendar XMLDate = this.getPaymentDueDate().getValue();
		if (XMLDate != null)
			date = XMLDate.toGregorianCalendar().getTime();
		
        return date;
	}
	
	public void setPaymentDueDateAdapterAsString(Date date) {
		setPaymentDueDateAdapter(date);
	}
	
	public String getPaymentDueDateAdapterAsString() {
		Date date = new Date();
		XMLGregorianCalendar XMLDate = this.getPaymentDueDate().getValue();
		if (XMLDate != null)
			date = XMLDate.toGregorianCalendar().getTime();
		
		DateFormat df2 = new SimpleDateFormat("d/M/yyyy");
        String dateFormat = df2.format(date);
		
        return dateFormat;
	}
		
	public void setPaymentChannelCodeAdapter(String channel) {
		this.getPaymentChannelCode().setValue(channel);
	}
	
	public String getPaymentChannelCodeAdapter() {
		return this.getPaymentChannelCode().getValue();
	}
	
	@Override
	public void setIDAdapter(String id) {
		this.getID().setValue(id);
	}
	
	@Override
	public String getIDAdapter() {
		return this.getID().getValue();
	}
	
	public void setFinancialAccountIDAdapter(String id) {
		this.getPayeeFinancialAccount().getID().setValue(id);
	}
	
	public String getFinancialAccountIDAdapter() {
		return this.getPayeeFinancialAccount().getID().getValue();
	}
	
	public void setBranchIDAdapter(String branch) {
		this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getID().setValue(branch);
	}
	
	public String getBranchIDAdapter() {
		return this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getID().getValue();
	}
	
	public void setInstitutionIDAdapter(String id) {
		this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getFinancialInstitution().getID().setValue(id);
	}
	
	public String getInstitutionIDAdapter() {
		return this.getPayeeFinancialAccount().getFinancialInstitutionBranch().getFinancialInstitution().getID().getValue();
	}
}
