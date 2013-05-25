package eu.europa.ec.cipa.sml.server.dns;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for class {@link DNSClientImpl} - for BRZ internal usage only!
 * 
 * @author Philip Helger
 */
public class DNSClientImplTest {
  @Test
  @Ignore
  public void testCreateDelete () throws Exception {
    final DNSClientImpl aClient = new DNSClientImpl ("blixdns0", "peppolcentral.org.", "smk.peppolcentral.org.", 60);
    aClient.createPublisherAnchor ("BRZ-DNS-TEST", "http://127.0.0.1");
    aClient.deletePublisherAnchor ("BRZ-DNS-TEST");
  }
}
