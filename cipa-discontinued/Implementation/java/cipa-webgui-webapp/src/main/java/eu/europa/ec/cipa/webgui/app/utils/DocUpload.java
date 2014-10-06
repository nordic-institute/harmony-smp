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
package eu.europa.ec.cipa.webgui.app.utils;

import java.io.File;
import java.io.FileInputStream;

import com.vaadin.ui.Upload;

@SuppressWarnings ("serial")
public class DocUpload extends Upload implements Upload.SucceededListener, Upload.FailedListener {

  String uploadDir;
  String filename;
  String mimeType;
  byte [] byteArray;

  public DocUpload (final String dir) {
    super (null, new ReceiverClass ());
    setImmediate (true);
    setButtonCaption ("Attach file (optional)");
    uploadDir = dir;
    if (!uploadDir.endsWith ("/"))
      uploadDir = uploadDir + "/";

    ((ReceiverClass) getReceiver ()).setUploadDir (uploadDir);

    addListener ((Upload.SucceededListener) this);
    addListener ((Upload.FailedListener) this);

  }

  public void uploadSucceeded (final Upload.SucceededEvent event) {
    final File file = new File (uploadDir + event.getFilename ());
    this.filename = event.getFilename ();
    // this.mimeType = event.getMIMEType();
    this.mimeType = ((ReceiverClass) this.getReceiver ()).getMimeType ();
    getByteArrayFromFile (file);

  }

  public void uploadFailed (final Upload.FailedEvent event) {}

  public void getByteArrayFromFile (final File file) {
    byteArray = new byte [(int) file.length ()];
    try {
      final FileInputStream fileInputStream = new FileInputStream (file);
      fileInputStream.read (byteArray);
      fileInputStream.close ();
    }
    catch (final Exception e) {
      e.printStackTrace ();
    }
  }

  public String getFilename () {
    return filename;
  }

  public String getMimeType () {
    return mimeType;
  }

  public byte [] getByteArray () {
    return byteArray;
  }

}
