package at.peppol.webgui.app.components.tables;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;

import at.peppol.webgui.app.components.TaxCategoryIDSelect;
import at.peppol.webgui.app.components.TaxSchemeSelect;
import at.peppol.webgui.app.components.adapters.InvoiceTaxSubtotalAdapter;
import at.peppol.webgui.app.validator.PositiveValueListener;
import at.peppol.webgui.app.validator.RequiredNumericalFieldListener;
import at.peppol.webgui.app.validator.ValidatorsList;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;

public class InvoiceTaxSubtotalTableEditor extends TableEditor<TaxSubtotalType, InvoiceTaxSubtotalAdapter> {

	public InvoiceTaxSubtotalTableEditor(boolean editMode) {
		super(editMode);
	}

	@Override
	public Form createTableForm(InvoiceTaxSubtotalAdapter taxSubtotalItem,
			List<TaxSubtotalType> invoiceList) {
		
		final Form invoiceTaxSubtotalForm = new Form (new FormLayout (), new InvoiceTaxTotalFieldFactory ());
	    invoiceTaxSubtotalForm.setImmediate (true);

	    if (!editMode) {
	    	taxSubtotalItem.setIDAdapter(String.valueOf(invoiceList.size ()+1));
	    }

	    invoiceTaxSubtotalForm.addItemProperty ("Taxable Amount", new NestedMethodProperty (taxSubtotalItem,
	                                                                                        "TaxSubTotalTaxableAmount"));
	    invoiceTaxSubtotalForm.addItemProperty ("Tax Amount", new NestedMethodProperty (taxSubtotalItem,
	                                                                                    "TaxSubTotalTaxAmount"));
	    invoiceTaxSubtotalForm.addItemProperty ("Tax Category ID", new NestedMethodProperty (taxSubtotalItem,
	                                                                                         "TaxSubTotalCategoryID"));
	    invoiceTaxSubtotalForm.addItemProperty ("Tax Category Percent",
	                                            new NestedMethodProperty (taxSubtotalItem, "TaxSubTotalCategoryPercent"));
	    invoiceTaxSubtotalForm.addItemProperty ("Tax Exemption Reason Code",
	                                            new NestedMethodProperty (taxSubtotalItem,
	                                                                      "TaxSubTotalCategoryExemptionReasonCode"));
	    invoiceTaxSubtotalForm.addItemProperty ("Tax Exemption Reason",
	                                            new NestedMethodProperty (taxSubtotalItem,
	                                                                      "TaxSubTotalCategoryExemptionReason"));
	    invoiceTaxSubtotalForm.addItemProperty ("Tax Scheme ID",
	                                            new NestedMethodProperty (taxSubtotalItem, "TaxSubTotalCategoryTaxSchemeID"));

	    return invoiceTaxSubtotalForm;

	}
	
	  @SuppressWarnings ("serial")
	  class InvoiceTaxTotalFieldFactory implements FormFieldFactory {

	    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
	      // Identify the fields by their Property ID.
	      final String pid = (String) propertyId;
	      
	      if ("Tax Scheme ID".equals(pid)) {
	          final TaxSchemeSelect taxSchemeSelect = new TaxSchemeSelect(pid);
	          taxSchemeSelect.setRequired(true);
	          return taxSchemeSelect;
	      }
	      if ("Tax Category ID".equals(pid)) {
	          final TaxCategoryIDSelect taxCategoryIDSelect = new TaxCategoryIDSelect(pid);
	          taxCategoryIDSelect.setRequired(true);
	          return taxCategoryIDSelect;
	      }

	      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
	      if (field instanceof AbstractTextField) {
	        ((AbstractTextField) field).setNullRepresentation ("");
	        final AbstractTextField tf = (AbstractTextField) field;
	        if ("Tax Total Amount".equals(pid)) {
	        	tf.setRequired(true);
	        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
	        	tf.addListener(new PositiveValueListener(tf,pid));
	        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
	        }
	        if ("Taxable Amount".equals(pid)) {
	        	tf.setRequired(true);
	        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
	        	tf.addListener(new PositiveValueListener(tf,pid));
	        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
	        }
	        if ("Tax Amount".equals(pid)) {
	        	tf.setRequired(true);
	        	tf.addListener(new RequiredNumericalFieldListener(tf,pid));
	        	tf.addListener(new PositiveValueListener(tf,pid));
	        	ValidatorsList.addListeners((Collection<BlurListener>) tf.getListeners(BlurEvent.class));
	        }
	      }
	      return field;
	    }
	  }


	@Override
	public InvoiceTaxSubtotalAdapter createItem() {
		final InvoiceTaxSubtotalAdapter ac = new InvoiceTaxSubtotalAdapter ();

	    ac.setTableLineID ("");
	    ac.setTaxSubTotalTaxAmount (BigDecimal.ZERO);
	    ac.setTaxSubTotalTaxableAmount (BigDecimal.ZERO);
	    ac.setTaxSubTotalCategoryID ("");
	    ac.setTaxSubTotalCategoryPercent (BigDecimal.ZERO);
	    ac.setTaxSubTotalCategoryExemptionReasonCode ("");
	    ac.setTaxSubTotalCategoryExemptionReason ("");
	    ac.setTaxSubTotalCategoryTaxSchemeID ("");

	    return ac;
	}

	@Override
	public void cloneItem(InvoiceTaxSubtotalAdapter srcItem,
			InvoiceTaxSubtotalAdapter dstItem) {
	
		dstItem.setTableLineID (srcItem.getTableLineID ());
	    dstItem.setTaxSubTotalTaxAmount (srcItem.getTaxSubTotalTaxAmount ());
	    dstItem.setTaxSubTotalTaxableAmount (srcItem.getTaxSubTotalTaxableAmount ());
	    dstItem.setTaxSubTotalCategoryID (srcItem.getTaxSubTotalCategoryID ());
	    dstItem.setTaxSubTotalCategoryPercent (srcItem.getTaxSubTotalCategoryPercent ());
	    dstItem.setTaxSubTotalCategoryExemptionReasonCode (srcItem.getTaxSubTotalCategoryExemptionReasonCode ());
	    dstItem.setTaxSubTotalCategoryExemptionReason (srcItem.getTaxSubTotalCategoryExemptionReason ());
	    dstItem.setTaxSubTotalCategoryTaxSchemeID (srcItem.getTaxSubTotalCategoryTaxSchemeID ());
	}

}
