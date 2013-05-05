package eu.europa.ec.cipa.sml.server.dns;

import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;

public class MainVerifyDNS {
  public static void main (final String [] args) throws Exception {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    ServletVerifyDNS.verifyAllEntries (aBAOS);

    System.out.println ("=================================================");
    System.out.write (aBAOS.toByteArray ());
    aBAOS.close ();
    aBAOS.flush ();

    // listAllDNSRecords();
  }
}
