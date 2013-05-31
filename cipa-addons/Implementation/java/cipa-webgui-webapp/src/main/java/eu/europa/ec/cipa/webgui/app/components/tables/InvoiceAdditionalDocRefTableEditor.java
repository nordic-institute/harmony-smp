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

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import un.unece.uncefact.codelist.specification.ianamimemediatype._2003.BinaryObjectMimeCodeContentType;

import com.vaadin.data.Item;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Window.Notification;

import eu.europa.ec.cipa.webgui.app.components.adapters.InvoiceAdditionalDocRefAdapter;
import eu.europa.ec.cipa.webgui.app.utils.DocUpload;

public class InvoiceAdditionalDocRefTableEditor extends
                                               GenericTableEditor <DocumentReferenceType, InvoiceAdditionalDocRefAdapter> {

  public InvoiceAdditionalDocRefTableEditor (final boolean editMode) {
    super (editMode);
  }

  @Override
  public Form createTableForm (final InvoiceAdditionalDocRefAdapter additionalDocRefItem,
                               final List <DocumentReferenceType> invoiceList) {

    final Form invoiceAdditionalDocRefForm = new Form (new FormLayout (), new AdditionalDocRefFieldFactory ());
    invoiceAdditionalDocRefForm.setImmediate (true);

    final NestedMethodProperty mp = new NestedMethodProperty (additionalDocRefItem, "AdditionalDocRefID");
    if (!editMode) {
      final IDType num = new IDType ();
      // num.setValue (String.valueOf (additionalDocRefList.size ()+1));
      // additionalDocRefItem.setID(num);

      int max = 0;
      for (final DocumentReferenceType doc : invoiceList) {
        if (Integer.parseInt (doc.getID ().getValue ()) > max)
          max = Integer.parseInt (doc.getID ().getValue ());
      }
      num.setValue (String.valueOf (max + 1));
      additionalDocRefItem.setID (num);
    }
    else {
      mp.setReadOnly (true);
    }

    // invoiceAdditionalDocRefForm.addItemProperty
    // ("Additional Doc Ref Type ID", mp );
    invoiceAdditionalDocRefForm.addItemProperty ("Type of document",
                                                 new NestedMethodProperty (additionalDocRefItem,
                                                                           "AdditionalDocRefDocumentType"));
    // invoiceAdditionalDocRefForm.addItemProperty ("Filename", new
    // NestedMethodProperty(additionalDocRefItem,
    // "AdditionalDocRefEmbeddedDocumentBinaryObject") );
    invoiceAdditionalDocRefForm.addItemProperty ("URI location",
                                                 new NestedMethodProperty (additionalDocRefItem,
                                                                           "AdditionalDocRefExternalReference"));

    return invoiceAdditionalDocRefForm;

  }

  class AdditionalDocRefFieldFactory implements FormFieldFactory {

    @Override
    public Field createField (final Item item, final Object propertyId, final Component uiContext) {
      // Identify the fields by their Property ID.
      final String pid = (String) propertyId;

      final Field field = DefaultFieldFactory.get ().createField (item, propertyId, uiContext);
      if (field instanceof AbstractTextField) {
        ((AbstractTextField) field).setNullRepresentation ("");
        final AbstractTextField tf = (AbstractTextField) field;
        tf.addListener (new FieldEvents.FocusListener () {
          @Override
          public void focus (final FocusEvent event) {
            tf.selectAll ();
          }
        });
      }

      return field;
    }
  }

  @Override
  public InvoiceAdditionalDocRefAdapter createItem () {
    final InvoiceAdditionalDocRefAdapter ac = new InvoiceAdditionalDocRefAdapter ();

    ac.setAdditionalDocRefID ("");
    ac.setAdditionalDocRefDocumentType ("");
    // ac.setAdditionalDocRefEmbeddedDocumentBinaryObject (null);
    ac.setAdditionalDocRefExternalReference ("");

    return ac;
  }

  @Override
  public void cloneItem (final InvoiceAdditionalDocRefAdapter srcItem, final InvoiceAdditionalDocRefAdapter dstItem) {

    dstItem.setAdditionalDocRefID (srcItem.getAdditionalDocRefID ());
    dstItem.setAdditionalDocRefDocumentType (srcItem.getAdditionalDocRefDocumentType ());
    // dstItem.setAdditionalDocRefEmbeddedDocumentBinaryObject
    // (srcItem.getAdditionalDocRefEmbeddedDocumentBinaryObject ());
    dstItem.setAdditionalDocRefExternalReference (srcItem.getAdditionalDocRefExternalReference ());

  }

  @Override
  public Button.ClickListener addButtonListener (final Button editButton,
                                                 final Button deleteButton,
                                                 final Layout hiddenContent,
                                                 final GenericTable <DocumentReferenceType, InvoiceAdditionalDocRefAdapter> table,
                                                 final List <DocumentReferenceType> invoiceList,
                                                 final Label label) {

    final Button.ClickListener b = new Button.ClickListener () {
      @Override
      public void buttonClick (final ClickEvent event) {
        editButton.setEnabled (false);
        deleteButton.setEnabled (false);
        hiddenContent.removeAllComponents ();

        final InvoiceAdditionalDocRefAdapter adapterItem = createItem ();

        hiddenContent.addComponent (label);
        final Form docRefForm = createTableForm (adapterItem, invoiceList);
        hiddenContent.addComponent (docRefForm);

        final Button saveNewLine = new Button ("Save");

        final DocUpload upload = new DocUpload ("uploads/");
        upload.addListener (new Upload.StartedListener () {
          @Override
          public void uploadStarted (final StartedEvent event) {
            saveNewLine.setEnabled (false);
          }

        });
        upload.addListener (new Upload.FinishedListener () {
          @Override
          public void uploadFinished (final FinishedEvent event) {
            saveNewLine.setEnabled (true);
          }
        });

        saveNewLine.addListener (new Button.ClickListener () {
          @Override
          public void buttonClick (final ClickEvent event) {
            if (adapterItem.getAdditionalDocRefID () != null) {
              if (!adapterItem.getAdditionalDocRefID ().equals ("")) {
                // upload.submitUpload(); //this doesn't work. VAADIN problem?
                if (upload.getFilename () != null) {
                  final BinaryObjectMimeCodeContentType mimeType = adapterItem.getBinaryObjectMimeCodeContentType (upload.getMimeType ());
                  hiddenContent.getWindow ().showNotification (upload.getMimeType (),
                                                               Notification.TYPE_TRAY_NOTIFICATION);
                  if (mimeType != null) {
                    adapterItem.setAdditionalDocRefFile (upload.getFilename ());
                    adapterItem.setBinaryObjectMIMEType (mimeType);
                    adapterItem.setBinaryObjectByteArray (upload.getByteArray ());
                  }
                  else {
                    // hiddenContent.getWindow().showNotification("Attachment not MediaType",
                    // Notification.TYPE_TRAY_NOTIFICATION);
                  }
                }

                table.addLine (adapterItem);
                // hide form
                hiddenContent.setVisible (false);
              }
              else
                hiddenContent.getParent ()
                             .getWindow ()
                             .showNotification ("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);
            }
            else
              hiddenContent.getParent ()
                           .getWindow ()
                           .showNotification ("ID is needed", Notification.TYPE_TRAY_NOTIFICATION);

            editButton.setEnabled (true);
            deleteButton.setEnabled (true);
          }
        });

        hiddenContent.addComponent (upload);

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
            upload.interruptUpload ();
            hiddenContent.removeAllComponents ();
            // hide form
            docRefForm.discard ();
            hiddenContent.setVisible (false);
          }
        }));

        hiddenContent.addComponent (buttonLayout);

        // hiddenContent.setVisible(!hiddenContent.isVisible());
        hiddenContent.setVisible (true);

      }
    };

    return b;
  }

}
