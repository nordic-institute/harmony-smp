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

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.upload.UploadManager;
import at.peppol.webgui.upload.UploadedResource;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class InvoiceUploadWindow extends VerticalLayout implements Upload.Receiver {

  private static final Logger logger = LoggerFactory.getLogger (InvoiceUploadWindow.class);
  private final Window subwindow;
  private final Label status = new Label ("Please select a file to upload");
  private final ProgressIndicator pi = new ProgressIndicator ();
  private final HorizontalLayout progressLayout = new HorizontalLayout ();
  private final Upload upload = new Upload (null, this);
  private UploadedResource ur;

  public InvoiceUploadWindow () {
    this.setSpacing (true);
    subwindow = new Window ("Upload Invoice");
    subwindow.addComponent (status);
    subwindow.addComponent (progressLayout);
    subwindow.addComponent (upload);
    subwindow.addStyleName ("upload-popup");
    subwindow.setModal (true);

    upload.setImmediate (true);
    upload.setButtonCaption ("Select local invoice");

    progressLayout.setSpacing (true);
    progressLayout.setVisible (false);
    progressLayout.addComponent (pi);
    progressLayout.setComponentAlignment (pi, Alignment.MIDDLE_LEFT);

    final Button cancelProcessing = new Button ("Cancel");
    cancelProcessing.addListener (new Button.ClickListener () {

      @Override
      public void buttonClick (final com.vaadin.ui.Button.ClickEvent event) {
        upload.interruptUpload ();
      }
    });
    cancelProcessing.setStyleName ("small");
    progressLayout.addComponent (cancelProcessing);
    upload.addListener (new Upload.StartedListener () {

      @Override
      public void uploadStarted (final StartedEvent event) {
        // This method gets called immediately after upload is started
        upload.setVisible (false);
        progressLayout.setVisible (true);
        pi.setValue (Float.valueOf (0f));
        pi.setPollingInterval (500);
        status.setValue ("Uploading file \"" + event.getFilename () + "\"");
      }
    });

    upload.addListener (new Upload.ProgressListener () {

      public void updateProgress (final long readBytes, final long contentLength) {
        // This method gets called several times during the update
        pi.setValue (new Float (readBytes / (float) contentLength));
      }
    });

    upload.addListener (new Upload.SucceededListener () {

      @Override
      public void uploadSucceeded (final SucceededEvent event) {
        // This method gets called when the upload finished successfully
        status.setValue ("Uploading file \"" + event.getFilename () + "\" succeeded");
        if (ur != null) {
          ur.setSuccess (true);
        }
        else {
          logger.warn ("Invoice upload succeeded, but no Upload request present!");
        }
      }
    });

    upload.addListener (new Upload.FailedListener () {

      @Override
      public void uploadFailed (final FailedEvent event) {
        status.setValue ("Uploading interrupted");
        if (ur != null) {
          ur.setSuccess (false);
        }
        else {
          logger.warn ("Invoice upload failed, but no Upload request present!");
        }
      }
    });

    upload.addListener (new Upload.FinishedListener () {

      @Override
      public void uploadFinished (final FinishedEvent event) {
        // This method gets called always when the upload finished, either
        // succeeding or failing
        progressLayout.setVisible (false);
        upload.setVisible (true);
        upload.setCaption ("Select another file more filesss");
      }
    });

  }

  @Override
  public Window getWindow () {
    return this.subwindow;
  }

  public OutputStream receiveUpload (final String filename, final String mimeType) {
    ur = UploadManager.getInstance ().createManagedResource (filename);
    return ur.createOutputStream ();
  }
}
