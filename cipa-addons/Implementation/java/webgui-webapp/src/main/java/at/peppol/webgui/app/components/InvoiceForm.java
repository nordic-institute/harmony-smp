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
package at.peppol.webgui.app.components;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentCurrencyCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Jerouris
 */
public class InvoiceForm extends Form {
  
  private InvoiceTabForm parent;
    
  public InvoiceForm(InvoiceTabForm parent) {
    this.parent = parent;
    initElements();
  }

  private void initElements() {
    
    final GridLayout grid = new GridLayout(4, 4);
    final VerticalLayout outerLayout = new VerticalLayout();
    
    final Panel outerPanel = new Panel("Invoice Header");
    
    outerPanel.addComponent(grid);
    outerPanel.setScrollable(true);
    outerLayout.addComponent(outerPanel);
    setLayout(outerLayout);
    
    final Panel invoiceDetailsPanel = new Panel("Invoice Details");
    invoiceDetailsPanel.setStyleName("light");
    invoiceDetailsPanel.setSizeFull();
    invoiceDetailsPanel.addComponent(createInvoiceTopForm());
    grid.addComponent(invoiceDetailsPanel, 0, 0, 3, 0);
    grid.setSizeUndefined();
     
    outerPanel.requestRepaintAll();
  }

  public Form createInvoiceTopForm() {
    final Form invoiceTopForm = new Form(new FormLayout(), new InvoiceFieldFactory());
    invoiceTopForm.setImmediate(true);
      
    parent.getInvoice().setID (new IDType ());
    invoiceTopForm.addItemProperty ("Invoice ID", new NestedMethodProperty (parent.getInvoice().getID (), "value"));
        
    parent.getInvoice().setDocumentCurrencyCode (new DocumentCurrencyCodeType ());
    // invoice.getDocumentCurrencyCode().setValue("EUR");

    parent.getInvoice().setIssueDate (new IssueDateType ());
    invoiceTopForm.addItemProperty ("Currency", new NestedMethodProperty (parent.getInvoice().getDocumentCurrencyCode (), "value"));

    final Date issueDate = new Date ();
    invoiceTopForm.addItemProperty ("Issue Date", new ObjectProperty <Date> (issueDate));
    
    return invoiceTopForm;
  }

  @SuppressWarnings ("serial")
  class InvoiceFieldFactory implements FormFieldFactory {

    @Override
    public Field createField(final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      if ("Currency".equals(pid)) {
        final CurrencySelect curSelect = new CurrencySelect("Currency");
        return curSelect;
      }

      if ("Issue Date".equals(pid)) {
        final PopupDateField issueDateField = new PopupDateField("Issue Date");
        issueDateField.setValue(new Date());
        issueDateField.setResolution(DateField.RESOLUTION_DAY);
        issueDateField.addListener(new ValueChangeListener() {

          @Override
          public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
            try {
              final Date issueDate = (Date) issueDateField.getValue();
              final GregorianCalendar greg = new GregorianCalendar();
              greg.setTime(issueDate);

              // Workaround to print only the date and not the time.
              final XMLGregorianCalendar XMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
              XMLDate.setYear(greg.get(Calendar.YEAR));
              XMLDate.setMonth(greg.get(Calendar.MONTH) + 1);
              XMLDate.setDay(greg.get(Calendar.DATE));

              parent.getInvoice().getIssueDate().setValue(XMLDate);
            } catch (final DatatypeConfigurationException ex) {
              Logger.getLogger(InvoiceForm.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        });
        return issueDateField;
      }
      

      final Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
          ((AbstractTextField) field).setNullRepresentation("");
      }
      return field;
    }
  }
}
