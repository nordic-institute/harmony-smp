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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link DNSUtils}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DNSUtilsTest {
  private static final String [] validPublisherId = { "SMPID", "SMP-ID1", "SMP.ID", "1" };
  private static final String [] wrongPublisherId = { null,
                                                     "",
                                                     "SMP_ID1",
                                                     "SMP ID",
                                                     "SMP:ID",
                                                     "SMP&ID",
                                                     "SMP-ID-",
                                                     "-SMP-ID",
                                                     "SMP..ID",
                                                     "SMP-ID.",
                                                     ".SMP-ID",
                                                     "x.64-Chars-0123456789012345678901234567890123456789012345678901234" };

  @Test
  public void testIsValidHostname () {
    for (final String host : validPublisherId)
      assertTrue (DNSUtils.isValidHostname (host));

    //
    for (final String host : wrongPublisherId)
      assertFalse (DNSUtils.isValidHostname (host));
  }

  @Test
  public void testGetIdentifierHashValueFromDnsName () {
    String sHash = DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                               "smj.peppolcentral.org.");
    assertEquals ("2203787b450615cd3f1f07e64e1ea683", sHash);

    sHash = DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                        "peppolcentral.org.");
    assertEquals ("2203787b450615cd3f1f07e64e1ea683", sHash);

    // bad cases
    // not matching SML domain in DNS name
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.orgggg.",
                                                            "smj.peppolcentral.org."));
    // Invalid identifier scheme
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea683.iso6523-nonactorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    // No separator
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea683-iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    // prefix invalid
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("b-2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("C-2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("-2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("2203787b450615cd3f1f07e64e1ea683.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    // Invalid hash length
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea68.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
    // Invalid hash characters
    assertNull (DNSUtils.getIdentifierHashValueFromDnsName ("B-2203787b450615cd3f1f07e64e1ea68g.iso6523-actorid-upis.smj.peppolcentral.org.",
                                                            "smj.peppolcentral.org."));
  }
}
