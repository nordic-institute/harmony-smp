package eu.europa.ec.cipa.smp.client.tools;

import java.io.File;

import com.phloc.commons.base64.Base64;
import com.phloc.commons.io.file.SimpleFileIO;

public class MainCreateBase64EncodedCertificate {
  public static void main (final String [] args) {
    final File aFile = new File ("src/test/resources/SMP_PEPPOL_SML_PEPPOL_SERVICE_METADATA_PUBLISHER_TEST_CA.cer");
    System.out.println (Base64.encodeBytes (SimpleFileIO.readFileBytes (aFile)));
  }
}
