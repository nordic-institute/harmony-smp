package eu.europa.ec.cipa.sml.server.dns;

import java.io.ByteArrayOutputStream;

public class MainListDNS {
  /**
   * Run listing locally. NOTE: DNS ZoneTransfer must be enabled for client.
   * Check your DNS administrator for details
   * 
   * @param args
   * @throws Exception
   */
  public static void main (final String [] args) throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    new ServletListDNS ();
    ServletListDNS.listDNS (baos);

    System.out.println ("=================================================");
    System.out.println (baos.toString ("cp1252"));
  }
}
