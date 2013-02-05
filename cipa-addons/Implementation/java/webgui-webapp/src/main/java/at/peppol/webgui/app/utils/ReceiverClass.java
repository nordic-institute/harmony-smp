package at.peppol.webgui.app.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Upload.Receiver;

@SuppressWarnings ("serial")
public class ReceiverClass implements Receiver {

  private String uploadDir;
  private String mimeType;

  public ReceiverClass () {}

  @Override
  public OutputStream receiveUpload (final String filename, final String mimeType) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream (uploadDir + filename);
      this.mimeType = mimeType;
    }
    catch (final java.io.FileNotFoundException e) {
      e.printStackTrace ();
      return null;
    }

    return fos;
  }

  public void setUploadDir (final String dir) {
    uploadDir = dir;
  }

  public String getMimeType () {
    return mimeType;
  }
}
