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
package eu.europa.ec.cipa.webgui.app.components.tables;

import java.util.List;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import eu.europa.ec.cipa.webgui.app.components.adapters.Adapter;
import eu.europa.ec.cipa.webgui.app.utils.Utils;

public abstract class GenericTableEditor <Ttype, Tadapter extends Adapter> {

  protected boolean editMode;
  protected InvoiceType invoice;

  public GenericTableEditor (final boolean editMode) {
    this.editMode = editMode;
  }

  public GenericTableEditor (final boolean editMode, final InvoiceType inv) {
    this.editMode = editMode;
    invoice = inv;
  }

  public Button.ClickListener addButtonListener (final Button editButton,
                                                 final Button deleteButton,
                                                 final Layout hiddenContent,
                                                 final GenericTable <Ttype, Tadapter> table,
                                                 final List <Ttype> invoiceList,
                                                 final Label label) {

    final Button.ClickListener b = new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        // addMode = true;
        editButton.setEnabled (false);
        deleteButton.setEnabled (false);
        hiddenContent.removeAllComponents ();

        final Tadapter adapterItem = createItem ();

        hiddenContent.addComponent (label);
        final Form tableForm = createTableForm (adapterItem, invoiceList);
        hiddenContent.addComponent (tableForm);

        final Button saveNewLine = new Button ("Save");

        saveNewLine.addListener (new Button.ClickListener () {
          @Override
          public void buttonClick (final ClickEvent event) {
            if (adapterItem.getIDAdapter () != null) {
              if (!adapterItem.getIDAdapter ().equals ("")) {
                try {
                  Utils.validateFormFields (tableForm);
                  table.addLine (adapterItem);
                  table.requestRepaint ();
                  // hide form
                  hiddenContent.setVisible (false);
                }
                catch (final InvalidValueException e) {
                  label.getWindow ().showNotification ("Form has errors", Notification.TYPE_TRAY_NOTIFICATION);
                }
              }
              else {
                hiddenContent.getWindow ().showNotification ("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
              }
            }
            else {
              hiddenContent.getWindow ().showNotification ("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
            }

            editButton.setEnabled (true);
            deleteButton.setEnabled (true);
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
            tableForm.discard ();
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

  public Button.ClickListener editButtonListener (final Button addButton,
                                                  final Button deleteButton,
                                                  final Layout hiddenContent,
                                                  final GenericTable <Ttype, Tadapter> table,
                                                  final List <Ttype> invoiceList,
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
            final Tadapter adapterItem = (Tadapter) invoiceList.get (table.getIndexFromID (sid));
            // paymentMeansAdapterItem = table.getEntryFromID(sid);

            // clone it to original item
            final Tadapter originalItem = createItem ();
            cloneItem (adapterItem, originalItem);

            // Label formLabel = new Label("<h3>Edit payment means line</h3>",
            // Label.CONTENT_XHTML);

            hiddenContent.addComponent (label);
            final Form tableForm = createTableForm (adapterItem, invoiceList);
            tableForm.setImmediate (true);
            hiddenContent.addComponent (tableForm);

            // Save new line button
            final HorizontalLayout buttonLayout = new HorizontalLayout ();
            buttonLayout.setSpacing (true);
            buttonLayout.addComponent (new Button ("Save changes", new Button.ClickListener () {
              @Override
              public void buttonClick (final ClickEvent event) {
                // paymentMeansForm.commit();
                try {
                  Utils.validateFormFields (tableForm);
                  table.setLine (sid, adapterItem);
                  // hide form
                  hiddenContent.setVisible (false);
                  editMode = false;
                  addButton.setEnabled (true);
                  deleteButton.setEnabled (true);
                }
                catch (final InvalidValueException e) {
                  label.getWindow ().showNotification ("Form has errors", Notification.TYPE_TRAY_NOTIFICATION);
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

  public Button.ClickListener deleteButtonListener (final GenericTable <Ttype, Tadapter> table) {

    final Button.ClickListener b = new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        final Object rowId = table.getValue (); // get the selected rows id
        if (rowId != null) {
          if (table.getContainerProperty (rowId, "IDAdapter") != null) {
            // if(table.getContainerProperty(rowId,"IDAdapter").getValue() !=
            // null){
            final String sid = (String) table.getContainerProperty (rowId, "IDAdapter").getValue ();
            if (sid != null)
              table.removeLine (sid);
            // }
          }
          else {
            table.getParent ()
                 .getWindow ()
                 .showNotification ("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
          }
        }
        else {
          table.getParent ()
               .getWindow ()
               .showNotification ("No table line is selected", Window.Notification.TYPE_TRAY_NOTIFICATION);
        }

      }
    };

    return b;
  }

  public abstract Form createTableForm (Tadapter t, List <Ttype> invoiceList);

  public abstract Tadapter createItem ();

  public abstract void cloneItem (Tadapter srcItem, Tadapter dstItem);
}
