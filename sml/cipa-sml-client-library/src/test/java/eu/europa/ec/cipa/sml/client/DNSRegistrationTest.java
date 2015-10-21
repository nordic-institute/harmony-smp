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
package eu.europa.ec.cipa.sml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.InetAddress;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.lang.CGStringHelper;

import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.uri.BusdoxURLUtils;
import eu.europa.ec.cipa.sml.AbstractSMLClientTest;

/**
 * This class is for BRZ internal use only!
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class DNSRegistrationTest extends AbstractSMLClientTest {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DNSRegistrationTest.class);

  /*
   * Wildcard user.
   */
  private static final String SMP_ID = "dns-test1";

  private static final String SMP_1_LOGICAL_ADDRESS = "http://mySMP.com";
  private static final String SMP_1_PHYSICAL_ADDRESS = "127.0.0.1";
  private static final String SMP_1_LOGICAL_ADDRESS_VALIDATION = "mySMP.com.";

  private static final String SMP_2_LOGICAL_ADDRESS = "http://mySMP2.com";
  private static final String SMP_2_PHYSICAL_ADDRESS = "127.0.0.1";
  private static final String SMP_2_LOGICAL_ADDRESS_VALIDATION = "mySMP2.com.";

  private static final String PI_VALUE = "0088:1111199991111";
  private static final String PI_SCHEME = "dns-actorid-test";
  private static final String PI_WILDCARD_SCHEME = "wildcard-actorid-allowed";

  private static final String INTERNAL_DNS_SERVER = "blixdns0";

  static {
    System.setProperty ("http.proxyHost", "172.30.9.12");
    System.setProperty ("http.proxyPort", "8080");
    System.setProperty ("https.proxyHost", "172.30.9.12");
    System.setProperty ("https.proxyPort", "8080");
    s_aLogger.info ("Set proxies");
  }

  @Nullable
  private static String _DNSLookupPI (final ParticipantIdentifierType aPI) throws Exception {
    final String host = BusdoxURLUtils.getDNSNameOfParticipant (aPI, SML_INFO);
    return _DNSLookup (host);
  }

  @Nullable
  private static String _DNSLookupPublisher (final String smpId) throws Exception {
    return _DNSLookup (smpId + "." + SML_INFO.getPublisherDNSName ());
  }

  @Nullable
  private static String _DNSLookup (final String sHost) throws Exception {
    // Wait to let dns propagate : DNS TTL = 60 secs
    s_aLogger.info ("Waiting 10 seconds to lookup '" + sHost + "'");
    Thread.sleep (10000);

    final Lookup aDNSLookup = new Lookup (sHost, Type.ANY);
    aDNSLookup.setResolver (new SimpleResolver (INTERNAL_DNS_SERVER));
    aDNSLookup.setCache (null);

    final Record [] aRecords = aDNSLookup.run ();
    s_aLogger.info ("Lookup returned [" + ArrayHelper.getSize (aRecords) + "]: " + Arrays.toString (aRecords));

    if (aRecords == null || aRecords.length == 0)
      return null;

    final Record aRecord = aRecords[0];
    if (aRecord instanceof CNAMERecord)
      return ((CNAMERecord) aRecord).getAlias ().toString ();

    if (aRecord instanceof ARecord) {
      final InetAddress aInetAddress = ((ARecord) aRecord).getAddress ();
      return aInetAddress.getHostAddress ();
    }

    s_aLogger.info ("Unknown record type found: " + CGStringHelper.getClassLocalName (aRecord));
    return aRecord.toString ();
  }

  @Before
  public void setupSMPBeforeTests () throws Exception {
    s_aLogger.info ("Creating an SMP");
    try {
      final ManageServiceMetadataServiceCaller manageServiceMetaData = new ManageServiceMetadataServiceCaller (SML_INFO);

      manageServiceMetaData.create (SMP_ID, SMP_1_PHYSICAL_ADDRESS, SMP_1_LOGICAL_ADDRESS);
      s_aLogger.info ("Created an SMP");
    }
    catch (final Exception ex) {
      s_aLogger.error ("Failed: " + ex.getMessage ());
      throw ex;
    }
  }

  @After
  public void deleteSMPAfterTests () throws Exception {
    s_aLogger.info ("Deleting an SMP");
    final ManageServiceMetadataServiceCaller manageServiceMetaData = new ManageServiceMetadataServiceCaller (SML_INFO);

    try {
      manageServiceMetaData.delete (SMP_ID);
      s_aLogger.info ("Deleted an SMP");
    }
    catch (final NotFoundFault e) {
      // this is ok
    }
    catch (final Exception ex) {
      s_aLogger.error ("Failed: " + ex.getMessage ());
      throw ex;
    }
  }

  // SMP

  @Test
  public void verifySMPInDNS () throws Exception {
    // @Before creates new SMP!

    // verify created
    final String publisher = _DNSLookupPublisher (SMP_ID);
    assertEquals (SMP_1_LOGICAL_ADDRESS_VALIDATION, publisher);

    // Update SML address
    final ManageServiceMetadataServiceCaller manageServiceMetaData = new ManageServiceMetadataServiceCaller (SML_INFO);

    manageServiceMetaData.update (SMP_ID, SMP_2_PHYSICAL_ADDRESS, SMP_2_LOGICAL_ADDRESS);

    // verify update
    final String updatedPublisher = _DNSLookupPublisher (SMP_ID);
    assertEquals (SMP_2_LOGICAL_ADDRESS_VALIDATION, updatedPublisher);

    // Delete SML
    manageServiceMetaData.delete (SMP_ID);

    // verify delete
    final String deletedPublisher = _DNSLookupPublisher (SMP_ID);
    assertNull (deletedPublisher);
  }

  // PI

  @Test
  public void verifyParticipantIdentifierInDNS () throws Exception {
    // @Before creates new SMP!

    // create PI
    final ManageParticipantIdentifierServiceCaller client = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (PI_SCHEME, PI_VALUE);
    client.create (SMP_ID, aPI);

    // verify PI in DNS
    final String host = _DNSLookupPI (aPI);
    assertEquals (SMP_ID + "." + SML_INFO.getPublisherDNSName (), host);

    // delete PI
    client.delete (aPI);

    final String deletedHost = _DNSLookupPI (aPI);
    assertNull (deletedHost);
  }

  // WildCard PI

  @Test
  public void verifyWildcardInDNS () throws Exception {
    // @Before creates new SMP!

    final ManageParticipantIdentifierServiceCaller client = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (PI_WILDCARD_SCHEME, "*");
    client.create (SMP_ID, aPI);

    // verify that PI can be found in Wildcard domain.
    final String piHost = _DNSLookupPI (new SimpleParticipantIdentifier (PI_WILDCARD_SCHEME, PI_VALUE));
    assertEquals (SMP_ID + "." + SML_INFO.getPublisherDNSName (), piHost);

    // verify that Wildcard can be found
    final String wildHost = _DNSLookupPI (aPI);
    assertEquals (SMP_ID + "." + SML_INFO.getPublisherDNSName (), wildHost);

    // delete wildcard
    client.delete (aPI);

    // verify deleted
    final String deletedHost = _DNSLookupPI (aPI);
    assertNull (deletedHost);
  }
}
