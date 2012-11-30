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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Nonnull;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Test;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalIdentifierSchemeException;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DNSTest {
  private static final String DNS_ZONE = "smloc.xx";
  private static final String SML_ZONE = "sml";
  private static final String validPublisherId = "SMPID";
  private static final String wrongPublisherId = null;
  private static final String validSchemeIdentifier = CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String wrongSchemeIdentifier = "busdox_actorid_upis";

  @Nonnull
  private static IDNSClient _createDNSClient () {
    return new MockDNSClient ("198.18.0.0", DNS_ZONE, SML_ZONE, 60);
  }

  @Test
  public void testDNSClient () throws Exception {
    final IDNSClient dnsClient = _createDNSClient ();
    assertEquals ("DNS Zone must be 'smloc.xx.'", "smloc.xx.", dnsClient.getDNSZoneName ());
    assertEquals ("SML Zone must be 'sml.smloc.xx.'", "sml.smloc.xx.", dnsClient.getSMLZoneName ());
  }

  @Test
  public void testIsHandledZone () {
    final IDNSClient dnsClient = _createDNSClient ();

    assertTrue (dnsClient.isHandledZone ("b-1234.iso6523-actorid-upis.sml.smloc.xx."));

    assertFalse (dnsClient.isHandledZone ("b-1234.iso6523-actorid-upis.notsml.smloc.xx."));
    assertFalse (dnsClient.isHandledZone ("b-1234.iso6523-actorid-upis.sml.notsmloc.xx."));
    assertFalse (dnsClient.isHandledZone ("b-1234.iso6523-actorid-upis.sml.smloc.notxx."));
  }

  @Test
  public void testSchemeIdentifierOnInsert () throws Exception {

    // Insert OK
    {
      final ParticipantIdentifierType pi = new SimpleParticipantIdentifier (validSchemeIdentifier, "0010:5798000000001");
      _createDNSClient ().createIdentifier (pi, validPublisherId);
    }

    // Insert - WRONG Scheme
    try {
      // Do not use SimpleParticipantIdentifier, because the consistency check
      // in the constructor will acknowledge the wrong scheme directly
      final ParticipantIdentifierType pi = new ParticipantIdentifierType ();
      pi.setScheme (wrongSchemeIdentifier);
      pi.setValue ("0010:5798000000001");

      _createDNSClient ().createIdentifier (pi, validPublisherId);
      fail ("Create Identifier should fail : " + pi.getScheme ());
    }
    catch (final IllegalIdentifierSchemeException e) {
      // OK
    }

    // Insert - WRONG PublisherId
    try {
      final ParticipantIdentifierType pi = new SimpleParticipantIdentifier (validSchemeIdentifier, "0010:5798000000001");

      _createDNSClient ().createIdentifier (pi, wrongPublisherId);
      fail ("Create Identifier should fail : " + pi.getScheme ());
    }
    catch (final IllegalHostnameException e) {
      // OK
    }
  }

  @Test
  public void testParticipantIdentifierDNSName () throws Exception {
    // Insert OK
    final ParticipantIdentifierType pi = SimpleParticipantIdentifier.createWithDefaultScheme ("0010:5798000000001");

    final IDNSClient dnsClient = _createDNSClient ();
    dnsClient.createIdentifier (pi, validPublisherId);
    assertTrue ("Create Identifier ok as expected ", true);

    final String dnsName = dnsClient.getDNSNameOfParticipant (pi);

    assertEquals ("Created DNS Name must match hash from SML Spec : e49b223851f6e97cbfce4f72c3402aac : ",
                  "B-e49b223851f6e97cbfce4f72c3402aac." +
                      CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME +
                      ".sml.smloc.xx.",
                  dnsName);
  }

  @Test
  public void testPublisherAnchor () {
    final IDNSClient dnsClient = _createDNSClient ();
    assertNotNull ("Legal Publisher ", dnsClient.getPublisherAnchorFromDnsName ("SMP-ID1.publisher.sml.smloc.xx."));

    assertNull ("Illegal Publisher ", dnsClient.getPublisherAnchorFromDnsName ("SMP-ID1.notpublisher.sml.smloc.xx."));
  }
}
