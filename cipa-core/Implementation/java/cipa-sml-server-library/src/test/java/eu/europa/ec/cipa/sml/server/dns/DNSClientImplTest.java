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
package eu.europa.ec.cipa.sml.server.dns;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Ignore;
import org.junit.Test;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

/**
 * Test class for class {@link DNSClientImpl} - for BRZ internal usage only!
 * 
 * @author Philip Helger
 */
public class DNSClientImplTest {
  private static final String SMP_TEST_NAME = "BRZ-DNS-TEST";

  @Nonnull
  private DNSClientImpl _createDNS () {
    return new DNSClientImpl ("blixdns0", "peppolcentral.org.", "smk.peppolcentral.org.", 60);
  }

  @Test
  @Ignore
  public void testCreate () throws Exception {
    final DNSClientImpl aClient = _createDNS ();
    aClient.createPublisherAnchor (SMP_TEST_NAME, "127.0.0.1");
  }

  @Nonnull
  private static String _getAsString (@Nullable final Record [] aRecords) {
    final StringBuilder aSB = new StringBuilder ();
    if (aRecords == null)
      aSB.append ("null");
    else
      for (final Record aRecord : aRecords) {
        final String sText = aRecord.toString ().replace ('\t', ' ');
        if (aSB.length () > 0)
          aSB.append ('\n');
        aSB.append (sText);
      }
    return aSB.toString ();
  }

  @Test
  @Ignore
  public void testFetch () throws Exception {
    final DNSClientImpl aClient = _createDNS ();
    {
      final Lookup aLookup = new Lookup (aClient.createPublisherDNSName (SMP_TEST_NAME), Type.ANY);
      aLookup.setResolver (new SimpleResolver ("cna-gdwi-0.cna.at"));
      aLookup.setCache (null);
      final Record [] aRecords = aLookup.run ();
      System.out.println ("0er: " + _getAsString (aRecords));
    }
    {
      final Lookup aLookup = new Lookup (aClient.createPublisherDNSName (SMP_TEST_NAME), Type.ANY);
      aLookup.setResolver (new SimpleResolver ("cna-gdwi-1.cna.at"));
      aLookup.setCache (null);
      final Record [] aRecords = aLookup.run ();
      System.out.println ("1er: " + _getAsString (aRecords));
    }
    {
      final Lookup aLookup = new Lookup (aClient.createPublisherDNSName (SMP_TEST_NAME), Type.ANY);
      aLookup.setResolver (new SimpleResolver ("cna-gdwi-2.cna.at"));
      aLookup.setCache (null);
      final Record [] aRecords = aLookup.run ();
      System.out.println ("2er: " + _getAsString (aRecords));
    }
  }

  @Test
  @Ignore
  public void testDelete () throws Exception {
    final DNSClientImpl aClient = _createDNS ();
    aClient.deletePublisherAnchor (SMP_TEST_NAME);
  }
}
