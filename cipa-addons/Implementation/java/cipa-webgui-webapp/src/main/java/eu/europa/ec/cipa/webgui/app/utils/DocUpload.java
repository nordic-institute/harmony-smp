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
