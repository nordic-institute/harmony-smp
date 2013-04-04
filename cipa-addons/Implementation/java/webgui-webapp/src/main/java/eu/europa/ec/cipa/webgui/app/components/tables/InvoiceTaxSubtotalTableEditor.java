package eu.europa.ec.cipa.webgui.app.components.tables;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import eu.europa.ec.cipa.webgui.app.components.TaxCategoryIDSelect;
import eu.europa.ec.cipa.webgui.app.components.TaxExemptionReasonCodeSelect;
import eu.europa.ec.cipa.webgui.app.components.TaxSchemeSelect;
import eu.europa.ec.cipa.webgui.app.components.TabInvoiceLine.EUGEN_T10_R018;
import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceTaxSubtotalAdapter;
import eu.europa.ec.cipa.webgui.app.validator.PositiveValueValidator;
import eu.europa.ec.cipa.webgui.app.validator.RequiredFieldListener;
import eu.europa.ec.cipa.webgui.app.validator.ValidatorsList;

public class InvoiceTaxSubtotalTableEditor extends GenericTableEditor <TaxSubtotalType, InvoiceTaxSubtotalAdapter> {

  GenericTable <TaxSubtotalType, InvoiceTaxSubtotalAdapter> table;

  public InvoiceTaxSubtotalTableEditor (final boolean editMode) {
    super (editMode);
  }

  @Override
  public Form createTableForm (final InvoiceTaxSubtotalAdapter taxSubtotalItem, final List <TaxSubtotalType> invoiceList) {

    final Form invoiceTaxSubtotalForm = new Form (new FormLayout (), new InvoiceTaxTotalFieldFactory ());
    invoiceTaxSubtotalForm.setImmediate (true);

    if (!editMode) {
      taxSubtotalItem.setIDAdapter (String.valueOf (invoiceList.size () + 1));
    }

    invoiceTaxSubtotalForm.addItemProperty ("Taxable Amount", new NestedMethodProperty (taxSubtotalItem,
                                                                                        "TaxSubTotalTaxableAmount"));
    invoiceTaxSubtotalForm.addItemProperty ("Tax Amount", new NestedMethodProperty (taxSubtotalItem,
                                                                                    "TaxSubTotalTaxAmount"));
    invoiceTaxSubtotalForm.addItemProperty ("Tax Category ID", new NestedMethodProperty (taxSubtotalItem,
                                                                                         "TaxSubTotalCategoryID"));
    invoiceTaxSubtotalForm.addItemProperty ("Tax Scheme ID",
                                            new NestedMethodProperty (taxSubtotalItem, "TaxSubTotalCategoryTaxSchemeID"));
    invoiceTaxSubtotalForm.addItemProperty ("Tax Category Percent",
                                            new NestedMethodProperty (taxSubtotalItem, "TaxSubTotalCategoryPercent"));
    invoiceTaxSubtotalForm.addItemProperty ("Tax Exemption Reason Code",
                                            new NestedMethodProperty (taxSubtotalItem,
                                                                      "TaxSubTotalCategoryExemptionReasonCode"));
    invoiceTaxSubtotalForm.addItemProperty ("Tax Exemption Reason",
                                            new NestedMethodProperty (taxSubtotalItem,
                                                                      "TaxSubTotalCategoryExemptionReason"));
    invoiceTaxSubtotalForm.getItemProperty ("Tax Category Percent").setValue ("23");

    final Field tax = invoiceTaxSubtotalForm.getField ("Tax Amount");
    final Field percent = invoiceTaxSubtotalForm.getField ("Tax Category Percent");
    final Field amount = invoiceTaxSubtotalForm.getField ("Taxable Amount");
    final ValueChangeListener l = new CalcTaxAmount (amount, percent, tax);
    percent.addListener (l);
    amount.addListener (l);

    return invoiceTaxSubtotalForm;

  }

  @SuppressWarnings ("serial")
  class InvoiceTaxTotalFieldFactory implements FormFieldFactory {

    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Tax Scheme ID".equals (pid)) {
        final TaxSchemeSelect taxSchemeSelect = new TaxSchemeSelect (pid);
        taxSchemeSelect.setRequired (true);
        return taxSchemeSelect;
      }
      if ("Tax Category ID".equals (pid)) {
        final TaxCategoryIDSelect taxCategoryIDSelect = new TaxCategoryIDSelect (pid);
        taxCategoryIDSelect.setRequired (true);
        return taxCategoryIDSelect;
      }
      if ("Tax Exemption Reason Code".equals (pid)) {
        final TaxExemptionReasonCodeSelect taxExemptionSelect = new TaxExemptionReasonCodeSelect (pid);
        return taxExemptionSelect;
      }

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        if ("Tax Total Amount".equals (pid)) {
          tf.setRequired (true);
          tf.addValidator (new PositiveValueValidator ());
          tf.addListener (new RequiredFieldListener (tf, pid));
          // tf.addListener(new RequiredNumericalFieldListener(tf,pid));
          // tf.addListener(new PositiveValueListener(tf,pid));
          ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
        }
        if ("Taxable Amount".equals (pid)) {
          tf.setRequired (true);
          tf.addValidator (new PositiveValueValidator ());
          tf.addListener (new RequiredFieldListener (tf, pid));
          // tf.addListener(new RequiredNumericalFieldListener(tf,pid));
          // tf.addListener(new PositiveValueListener(tf,pid));
          ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
        }
        if ("Tax Amount".equals (pid)) {
          tf.setRequired (true);
          tf.addValidator (new PositiveValueValidator ());
          tf.addListener (new RequiredFieldListener (tf, pid));
          // tf.addListener(new RequiredNumericalFieldListener(tf,pid));
          // tf.addListener(new PositiveValueListener(tf,pid));
          ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
        }
        if ("Tax Category Percent".equals (pid)) {
          tf.setRequired (true);
          tf.addListener (new RequiredFieldListener (tf, pid));
          tf.addValidator (new PositiveValueValidator ());
          // tf.addListener(new RequiredNumericalFieldListener(tf,pid));
          // tf.addListener(new PositiveValueListener(tf,pid));
          ValidatorsList.addListeners ((Collection <BlurListener>) tf.getListeners (BlurEvent.class));
        }

        tf.addListener (new FieldEvents.FocusListener () {
          @Override
          public void focus (final FocusEvent event) {
            tf.selectAll ();
          }
        });
        return tf;
      }
      return field;
    }
  }

  @Override
  public Button.ClickListener addButtonListener (final Button editButton,
                                                 final Button deleteButton,
                                                 final Layout hiddenContent,
                                                 final GenericTable <TaxSubtotalType, InvoiceTaxSubtotalAdapter> table,
                                                 final List <TaxSubtotalType> invoiceList,
                                                 final Label label) {

    this.table = table;

    final Button.ClickListener b = new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        // addMode = true;
        editButton.setEnabled (false);
        deleteButton.setEnabled (false);
        hiddenContent.removeAllComponents ();

        final InvoiceTaxSubtotalAdapter adapterItem = createItem ();

        hiddenContent.addComponent (label);
        final Form taxSubTotalForm = createTableForm (adapterItem, invoiceList);
        hiddenContent.addComponent (taxSubTotalForm);

        final Select f = (Select) taxSubTotalForm.getField ("Tax Category ID");
        final AbstractTextField f2 = (AbstractTextField) taxSubTotalForm.getField ("Tax Exemption Reason");
        final EUGEN_T10_R009 listener = new EUGEN_T10_R009 (adapterItem);
        f.addListener (listener);
        f2.addListener (listener);

        // add the listeners for VAT AE tax total amount
        final EUGEN_T10_R018 eugen_t10_r018 = new EUGEN_T10_R018 (taxSubTotalForm,
                                                                  "Tax Scheme ID",
                                                                  "Tax Category ID",
                                                                  "Tax Amount");
        taxSubTotalForm.getField ("Tax Scheme ID").addListener (eugen_t10_r018);
        taxSubTotalForm.getField ("Tax Category ID").addListener (eugen_t10_r018);

        final AbstractTextField percent = (AbstractTextField) taxSubTotalForm.getField ("Tax Category Percent");

        final Button saveNewLine = new Button ("Save");

        saveNewLine.addListener (new Button.ClickListener () {
          @Override
          public void buttonClick (final ClickEvent event) {
            boolean error = false;
            if (adapterItem.getIDAdapter () != null) {
              if (!adapterItem.getIDAdapter ().equals ("")) {
                if (listener.isError ()) {
                  f2.setComponentError (new UserError ("You should provide an exemption reason"));
                  error = true;
                }
                else
                  if (percent.getValue ().equals ("")) {
                    percent.setComponentError (new UserError ("You should provide a percentage"));
                    error = true;
                  }
                  else
                    if (f.getValue () == null) {
                      f.setComponentError (new UserError ("You should provide a Tax ID"));
                      error = true;
                    }
                    else
                      if (f.getValue ().equals ("")) {
                        f.setComponentError (new UserError ("You should provide a Tax ID"));
                        error = true;
                      }
                      else {
                        try {
                          taxSubTotalForm.validate ();
                          // deleteNullFields(taxSubTotalForm, invoiceList,
                          // adapterItem);
                          adapterItem.setEmptyAsNull ();
                          error = false;
                          f2.setComponentError (null);
                          table.addLine (adapterItem);
                          // hide form
                          hiddenContent.setVisible (false);
                        }
                        catch (final InvalidValueException e) {}
                      }
              }
              else {
                hiddenContent.getWindow ().showNotification ("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
              }
            }
            else {
              hiddenContent.getWindow ().showNotification ("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
            }
            if (!error) {
              editButton.setEnabled (true);
              deleteButton.setEnabled (true);

              // delete invoicetype fields that have no value

            }
          }
        });

        // Save new line button
        final HorizontalLayout buttonLayout = new HorizontalLayout ();
        buttonLayout.setSpacing (true);
        buttonLayout.setMargin (true);
        buttonLayout.addComponent (saveNewLine);

        buttonLayout.addComponent (new Button ("Cancel", new Button.ClickListener () {
          @Override
          public void buttonClick (final ClickEvent event) {
            editButton.setEnabled (true);
            deleteButton.setEnabled (true);
            hiddenContent.removeAllComponents ();
            // hide form
            taxSubTotalForm.discard ();
            hiddenContent.setVisible (false);
            // addMode = false;
          }
        }));

        hiddenContent.addComponent (buttonLayout);

        // hiddenContent.setVisible(!hiddenContent.isVisible());
        hiddenContent.setVisible (true);
      }
    };

    return b;
  }

  @Override
  public Button.ClickListener editButtonListener (final Button addButton,
                                                  final Button deleteButton,
                                                  final Layout hiddenContent,
                                                  final GenericTable <TaxSubtotalType, InvoiceTaxSubtotalAdapter> table,
                                                  final List <TaxSubtotalType> invoiceList,
                                                  final Label label) {

    final Button.ClickListener b = new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        final Object rowId = table.getValue (); // get the selected rows id
        if (rowId != null) {
          if (table.getContainerProperty (rowId, "IDAdapter") != null) {
            hiddenContent.removeAllComponents ();
            editMode = true;
            addButton.setEnabled (false);
            deleteButton.setEnabled (false);

            final String sid = (String) table.getContainerProperty (rowId, "IDAdapter").getValue ();

            // get selected item
            final InvoiceTaxSubtotalAdapter adapterItem = (InvoiceTaxSubtotalAdapter) invoiceList.get (table.getIndexFromID (sid));
            // paymentMeansAdapterItem = table.getEntryFromID(sid);

            // clone it to original item
            final InvoiceTaxSubtotalAdapter originalItem = createItem ();
            cloneItem (adapterItem, originalItem);

            // Label formLabel = new Label("<h3>Edit payment means line</h3>",
            // Label.CONTENT_XHTML);

            hiddenContent.addComponent (label);
            final Form paymentMeansForm = createTableForm (adapterItem, invoiceList);
            hiddenContent.addComponent (paymentMeansForm);

            final Select f = (Select) paymentMeansForm.getField ("Tax Category ID");
            final AbstractTextField f2 = (AbstractTextField) paymentMeansForm.getField ("Tax Exemption Reason");
            final EUGEN_T10_R009 listener = new EUGEN_T10_R009 (adapterItem);
            f.addListener (listener);
            f2.addListener (listener);

            // add the listeners for VAT AE tax total amount
            final EUGEN_T10_R018 eugen_t10_r018 = new EUGEN_T10_R018 (paymentMeansForm,
                                                                      "Tax Scheme ID",
                                                                      "Tax Category ID",
                                                                      "Tax Amount");
            paymentMeansForm.getField ("Tax Scheme ID").addListener (eugen_t10_r018);
            paymentMeansForm.getField ("Tax Category ID").addListener (eugen_t10_r018);

            final AbstractTextField percent = (AbstractTextField) paymentMeansForm.getField ("Tax Category Percent");

            // Save new line button
            final HorizontalLayout buttonLayout = new HorizontalLayout ();
            buttonLayout.setSpacing (true);
            buttonLayout.addComponent (new Button ("Save changes", new Button.ClickListener () {
              @Override
              public void buttonClick (final ClickEvent event) {
                boolean error = false;
                if (listener.isError ()) {
                  f2.setComponentError (new UserError ("You should provide an exemption reason"));
                  error = true;
                }
                else
                  if (percent.getValue ().equals ("")) {
                    percent.setComponentError (new UserError ("You should provide a percentage"));
                    error = true;
                  }
                  else
                    if (f.getValue () == null) {
                      f.setComponentError (new UserError ("You should provide a Tax ID"));
                      error = true;
                    }
                    else
                      if (f.getValue ().equals ("")) {
                        f.setComponentError (new UserError ("You should provide a Tax ID"));
                        error = true;
                      }
                      else {
                        try {
                          paymentMeansForm.validate ();
                          error = false;
                          // paymentMeansForm.commit();
                          table.setLine (sid, adapterItem);
                          // hide form
                          hiddenContent.setVisible (false);
                          editMode = false;
                          addButton.setEnabled (true);
                          deleteButton.setEnabled (true);
                        }
                        catch (final InvalidValueException e) {}
                      }
              }
            }));
            buttonLayout.addComponent (new Button ("Cancel editing", new Button.ClickListener () {
              @Override
              public void buttonClick (final ClickEvent event) {
                // paymentMeansForm.discard();
                table.setLine (sid, originalItem);
                // hide form
                hiddenContent.removeAllComponents ();
                hiddenContent.setVisible (false);
                editMode = false;
                addButton.setEnabled (true);
                deleteButton.setEnabled (true);
              }
            }));

            hiddenContent.addComponent (buttonLayout);
            hiddenContent.setVisible (true);
          }
          else {
            hiddenContent.getWindow ().showNotification ("No table line is selected",
                                                         Window.Notification.TYPE_TRAY_NOTIFICATION);
          }
        }
        else {
          hiddenContent.getWindow ().showNotification ("No table line is selected",
                                                       Window.Notification.TYPE_TRAY_NOTIFICATION);
        }
      }

    };

    return b;
  }

  @Override
  public InvoiceTaxSubtotalAdapter createItem () {
    final InvoiceTaxSubtotalAdapter ac = new InvoiceTaxSubtotalAdapter ();

    // ac.setTableLineID ("");
    // ac.setIDAdapter ("");
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
  public void cloneItem (final InvoiceTaxSubtotalAdapter srcItem, final InvoiceTaxSubtotalAdapter dstItem) {

    // dstItem.setTableLineID (srcItem.getTableLineID ());
    dstItem.setIDAdapter (srcItem.getIDAdapter ());
    dstItem.setTaxSubTotalTaxAmount (srcItem.getTaxSubTotalTaxAmount ());
    dstItem.setTaxSubTotalTaxableAmount (srcItem.getTaxSubTotalTaxableAmount ());
    dstItem.setTaxSubTotalCategoryID (srcItem.getTaxSubTotalCategoryID ());
    dstItem.setTaxSubTotalCategoryPercent (srcItem.getTaxSubTotalCategoryPercent ());
    dstItem.setTaxSubTotalCategoryExemptionReasonCode (srcItem.getTaxSubTotalCategoryExemptionReasonCode ());
    dstItem.setTaxSubTotalCategoryExemptionReason (srcItem.getTaxSubTotalCategoryExemptionReason ());
    dstItem.setTaxSubTotalCategoryTaxSchemeID (srcItem.getTaxSubTotalCategoryTaxSchemeID ());
  }

  public void deleteNullFields (final Form form, final List <TaxSubtotalType> list, final InvoiceTaxSubtotalAdapter item) {
    if (form.getField ("Tax Exemption Reason Code").getValue () == null) {
      item.setTaxSubTotalCategoryExemptionReasonCode (null);
      System.out.println ("inside code null");
    }
    else
      if (form.getField ("Tax Exemption Reason Code").getValue ().equals ("")) {
        item.setTaxSubTotalCategoryExemptionReasonCode (null);
        System.out.println ("inside code empty");
      }
    if (form.getField ("Tax Exemption Reason").getValue () == null) {
      item.setTaxSubTotalCategoryExemptionReason (null);
      System.out.println ("inside reason null");
    }
    else
      if (form.getField ("Tax Exemption Reason").getValue ().equals ("")) {
        item.setTaxSubTotalCategoryExemptionReason (null);
        System.out.println ("inside reason empty");
      }
  }

  public class EUGEN_T10_R009 implements ValueChangeListener {

    InvoiceTaxSubtotalAdapter item;
    boolean error;

    public EUGEN_T10_R009 (final InvoiceTaxSubtotalAdapter item) {
      this.item = item;
      error = false;
    }

    @Override
    public void valueChange (final com.vaadin.data.Property.ValueChangeEvent event) {

      final String value = item.getTaxSubTotalCategoryID ();

      error = false;
      if (value.equals ("E")) {
        if (item.getTaxSubTotalCategoryTaxSchemeID ().equals ("VAT")) {
          if (item.getTaxSubTotalCategoryExemptionReason ().trim ().equals ("")) {
            error = true;
          }
        }
      }
    }

    public boolean isError () {
      return error;
    }

    public void setError (final boolean error) {
      this.error = error;
    }

  }

  public class CalcTaxAmount implements ValueChangeListener {

    Field amountField, percentField, taxField;

    public CalcTaxAmount (final Field amount, final Field percent, final Field tax) {
      this.amountField = amount;
      this.percentField = percent;
      this.taxField = tax;
    }

    @Override
    public void valueChange (final ValueChangeEvent event) {
      // TODO Auto-generated method stub
      final BigDecimal hund = new BigDecimal ("100.00");
      final BigDecimal a = new BigDecimal ((String) amountField.getValue ());
      final BigDecimal p = new BigDecimal ((String) percentField.getValue ());

      taxField.setValue (a.multiply (p.divide (hund)));
    }

  }
}
