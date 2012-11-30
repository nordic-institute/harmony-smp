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

import javax.annotation.Nullable;

import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

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
  /*
   * Wildcard user.
   */
  private static final String SML_ID = "wildcard-user1";

  private static final String SMP_1_LOGICAL_ADDRESS = "http://ec2-174-129-36-65.compute-1.amazonaws.com";
  private static final String SMP_1_PHYSICAL_ADDRESS = "174.129.36.65";
  private static final String SMP_1_LOGICAL_ADDRESS_VALIDATION = "ec2-174-129-36-65.compute-1.amazonaws.com.";

  private static final String SMP_2_LOGICAL_ADDRESS = "http://ec2-174-129-190-34.compute-1.amazonaws.com";
  private static final String SMP_2_PHYSICAL_ADDRESS = "174.129.190.34";
  private static final String SMP_2_LOGICAL_ADDRESS_VALIDATION = "ec2-174-129-190-34.compute-1.amazonaws.com.";

  private static final String PI_VALUE = "0088:1111100001111";
  private static final String PI_SCHEME = "dns-actorid-test";
  private static final String PI_WILDCARD_SCHEME = "wildcard-actorid-allowed";

  private static final String INTERNAL_DNS_SERVER = "blixdns1";

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
  private static String _DNSLookup (final String host) throws Exception {
    // Wait to let dns propagate : DNS TTL = 60 secs
    Thread.sleep (10000);

    final SimpleResolver resolver = new SimpleResolver (INTERNAL_DNS_SERVER);

    final Lookup lookup = new Lookup (host, Type.ANY);
    lookup.setResolver (resolver);
    lookup.setCache (null);

    final Record [] records = lookup.run ();

    if (records == null || records.length == 0)
      return null;

    if (records[0] instanceof CNAMERecord)
      return ((CNAMERecord) records[0]).getAlias ().toString ();

    if (records[0] instanceof ARecord) {
      final InetAddress inetAddress = ((ARecord) records[0]).getAddress ();
      return inetAddress.getHostAddress ();
    }

    return records[0].toString ();
  }

  @Before
  public void setupSMPBeforeTests () throws Exception {
    final ManageServiceMetadataServiceCaller manageServiceMetaData = new ManageServiceMetadataServiceCaller (SML_INFO);

    manageServiceMetaData.create (SML_ID, SMP_1_PHYSICAL_ADDRESS, SMP_1_LOGICAL_ADDRESS);
  }

  @After
  public void deleteSMPAfterTests () throws Exception {
    final ManageServiceMetadataServiceCaller manageServiceMetaData = new ManageServiceMetadataServiceCaller (SML_INFO);

    try {
      manageServiceMetaData.delete (SML_ID);
    }
    catch (final NotFoundFault e) {
      // this is ok
    }
  }

  // SMP

  @Test
  public void verifySMPInDNS () throws Exception {
    // @Before creates new SMP!

    // verify created
    final String publisher = _DNSLookupPublisher (SML_ID);
    assertEquals (SMP_1_LOGICAL_ADDRESS_VALIDATION, publisher);

    // Update SML address
    final ManageServiceMetadataServiceCaller manageServiceMetaData = new ManageServiceMetadataServiceCaller (SML_INFO);

    manageServiceMetaData.update (SML_ID, SMP_2_PHYSICAL_ADDRESS, SMP_2_LOGICAL_ADDRESS);

    // verify update
    final String updatedPublisher = _DNSLookupPublisher (SML_ID);
    assertEquals (SMP_2_LOGICAL_ADDRESS_VALIDATION, updatedPublisher);

    // Delete SML
    manageServiceMetaData.delete (SML_ID);

    // verify delete
    final String deletedPublisher = _DNSLookupPublisher (SML_ID);
    assertNull (deletedPublisher);
  }

  // PI

  @Test
  public void verifyParticipantIdentifierInDNS () throws Exception {
    // @Before creates new SMP!

    // create PI
    final ManageParticipantIdentifierServiceCaller client = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (PI_SCHEME, PI_VALUE);
    client.create (SML_ID, aPI);

    // verify PI in DNS
    final String host = _DNSLookupPI (aPI);
    assertEquals (SML_ID + "." + SML_INFO.getPublisherDNSName (), host);

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
    client.create (SML_ID, aPI);

    // verify that PI can be found in Wildcard domain.
    final String piHost = _DNSLookupPI (new SimpleParticipantIdentifier (PI_WILDCARD_SCHEME, PI_VALUE));
    assertEquals (SML_ID + "." + SML_INFO.getPublisherDNSName (), piHost);

    // verify that Wildcard can be found
    final String wildHost = _DNSLookupPI (aPI);
    assertEquals (SML_ID + "." + SML_INFO.getPublisherDNSName (), wildHost);

    // delete wildcard
    client.delete (aPI);

    // verify deleted
    final String deletedHost = _DNSLookupPI (aPI);
    assertNull (deletedHost);
  }
}
