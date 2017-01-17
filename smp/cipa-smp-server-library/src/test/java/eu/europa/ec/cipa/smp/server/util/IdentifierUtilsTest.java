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
package eu.europa.ec.cipa.smp.server.util;

import com.helger.commons.string.StringHelper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for class {@link eu.europa.ec.cipa.peppol.identifier.IdentifierUtils}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class IdentifierUtilsTest {
  private static final String [] PARTICIPANT_SCHEME_VALID = { "bdxr-actorid-upis",
                                                             "bdxr-ACTORID-UPIS",
                                                             CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME,
                                                             "any-actorid-any",
                                                             "any-ACTORID-any" };
  private static final String [] PARTIFCIPANT_SCHEME_INVALID = { null,
                                                                "",
                                                                "bdxr_actorid_upis",
                                                                "bdxr-notactorid-upis",
                                                                "-actorid-upis",
                                                                "actorid-upis",
                                                                "bdxr-actorid-",
                                                                "bdxr-actorid",
                                                                "any-domain_actorid_any-type",
                                                                "any-nonactoid-anybutmuchtoooooooooooooooooooooooolong" };

  @Test
  public void testIsValidParticipantIdentifierScheme () {
    // valid
    for (final String scheme : PARTICIPANT_SCHEME_VALID)
      assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierScheme (scheme));

    // invalid
    for (final String scheme : PARTIFCIPANT_SCHEME_INVALID)
      assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierScheme (scheme));
  }

  @Test
  public void testAreIdentifiersEqualPariticpantIdentifier () {
    final SimpleParticipantIdentifier aPI1 = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123abc");
    final SimpleParticipantIdentifier aPI2 = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123ABC");
    final SimpleParticipantIdentifier aPI3a = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123456");
    final SimpleParticipantIdentifier aPI3b = new SimpleParticipantIdentifier ("my-actorid-scheme", "0088:12345");
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI1, aPI1));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI1, aPI2));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI2, aPI1));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI1, aPI3a));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI1, aPI3b));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI2, aPI3a));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI2, aPI3b));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI3a, aPI3b));

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aPI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (null, aPI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  public void testAreIdentifiersEqualDocumentIdentifier () {
    final SimpleDocumentTypeIdentifier aDI1 = SimpleDocumentTypeIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleDocumentTypeIdentifier aDI2 = SimpleDocumentTypeIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleDocumentTypeIdentifier aDI3a = SimpleDocumentTypeIdentifier.createWithDefaultScheme ("urn:doc:anyotherdoc");
    final SimpleDocumentTypeIdentifier aDI3b = new SimpleDocumentTypeIdentifier ("my-docid-test", "urn:doc:anyotherdoc");
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI1));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI2));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI2, aDI1));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI3a));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI3b));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI2, aDI3a));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI2, aDI3b));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI3a, aDI3b));

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (null, aDI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  public void testAreIdentifiersEqualProcessIdentifier () {
    final SimpleProcessIdentifier aDI1 = SimpleProcessIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleProcessIdentifier aDI2 = SimpleProcessIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    final SimpleProcessIdentifier aDI3a = SimpleProcessIdentifier.createWithDefaultScheme ("urn:doc:anyotherdoc");
    final SimpleProcessIdentifier aDI3b = new SimpleProcessIdentifier ("my-procid-test", "urn:doc:anyotherdoc");
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI1));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI2));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI2, aDI1));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI3a));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, aDI3b));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI2, aDI3a));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI2, aDI3b));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI3a, aDI3b));

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (aDI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.areIdentifiersEqual (null, aDI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void getIdentifierURIEncoded () {
    final SimpleParticipantIdentifier aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123abc");
    assertEquals ("iso6523-actorid-upis::0088:123abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIEncoded (aPI));

    final SimpleDocumentTypeIdentifier aDI = SimpleDocumentTypeIdentifier.createWithDefaultScheme ("urn:doc:anydoc");
    assertEquals ("busdox-docid-qns::urn:doc:anydoc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIEncoded (aDI));

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIEncoded (new SimpleParticipantIdentifier (null, "0088:12345"));
      fail ("Empty scheme should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIEncoded (new SimpleParticipantIdentifier ("", "0088:12345"));
      fail ("Empty scheme should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIEncoded (SimpleParticipantIdentifier.createWithDefaultScheme (null));
      fail ("null value should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }
  }

  @Test
  public void testGetIdentifierURIPercentEncoded () {
    SimpleParticipantIdentifier aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088:123abc");
    assertEquals ("iso6523-actorid-upis%3A%3A0088%3A123abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
    aPI = SimpleParticipantIdentifier.createWithDefaultScheme (EPredefinedIdentifierIssuingAgency.GLN.createIdentifierValue ("123abc"));
    assertEquals ("iso6523-actorid-upis%3A%3A0088%3A123abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
    aPI = EPredefinedIdentifierIssuingAgency.GLN.createParticipantIdentifier ("123abc");
    assertEquals ("iso6523-actorid-upis%3A%3A0088%3A123abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIPercentEncoded (aPI));

    // Different value
    aPI = SimpleParticipantIdentifier.createWithDefaultScheme ("0088/123abc");
    assertEquals ("iso6523-actorid-upis%3A%3A0088%2F123abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getIdentifierURIPercentEncoded (aPI));
  }

  @Test
  public void testIsValidDocumentTypeIdentifierValue () {
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifierValue (null));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifierValue (""));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifierValue ("invoice"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifierValue ("order "));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifierValue (StringHelper.getRepeated ('a',
                                                                                              CIdentifier.MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH)));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifierValue (StringHelper.getRepeated ('a',
                                                                                               CIdentifier.MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH + 1)));
  }

  @Test
  public void testIsValidDocumentTypeIdentifier () {
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier (null));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier (""));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier ("doctype::invoice"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier ("doctype::order "));

    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier ("doctypethatiswaytoolongforwhatisexpected::order"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier ("doctype::" +
                                                                StringHelper.getRepeated ('a',
                                                                                          CIdentifier.MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH + 1)));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier ("doctype:order"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidDocumentTypeIdentifier ("doctypeorder"));
  }

  @Test
  public void testIsValidParticipantIdentifierValue () {
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue (null));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue (""));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue ("9908:976098897"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue ("9908:976098897 "));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue ("990:976098897"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue ("990976098897"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue ("9909:976098896"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue ("9908:976098896"));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue (StringHelper.getRepeated ('a',
                                                                                             CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifierValue (StringHelper.getRepeated ('a',
                                                                                              CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH + 1)));
  }

  @Test
  public void testIsValidParticipantIdentifier () {
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier (null));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier (""));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::9908:976098897"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::9908:976098897 "));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::990:976098897"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::990976098897"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::9909:976098896"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::9908:976098896"));

    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummythatiswaytoolongforwhatisexpected::9908:976098896"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy::" +
                                                               StringHelper.getRepeated ('a',
                                                                                         CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH + 1)));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy:9908:976098896"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidParticipantIdentifier ("any-actorid-dummy9908:976098896"));
  }

  @Test
  public void testIsValidProcessIdentifierValue () {
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifierValue (null));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifierValue (""));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifierValue ("proc1"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifierValue ("proc2 "));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifierValue (StringHelper.getRepeated ('a',
                                                                                         CIdentifier.MAX_PROCESS_IDENTIFIER_VALUE_LENGTH)));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifierValue (StringHelper.getRepeated ('a',
                                                                                          CIdentifier.MAX_PROCESS_IDENTIFIER_VALUE_LENGTH + 1)));
  }

  @Test
  public void testIsValidProcessIdentifier () {
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier (null));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier (""));

    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier ("process::proc1"));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier ("process::proc2 "));

    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier ("processany-actorid-dummythatiswaytoolongforwhatisexpected::proc2"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier ("process::" +
                                                           StringHelper.getRepeated ('a',
                                                                                     CIdentifier.MAX_PROCESS_IDENTIFIER_VALUE_LENGTH + 1)));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier ("process:proc2"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.isValidProcessIdentifier ("processproc2"));
  }

  @Test
  public void testGetUnifiedParticipantDBValue () {
    assertNull (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getUnifiedParticipantDBValue (null));
    assertEquals ("", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getUnifiedParticipantDBValue (""));
    assertEquals ("abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getUnifiedParticipantDBValue ("abc"));
    assertEquals ("abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getUnifiedParticipantDBValue ("ABC"));
    assertEquals ("abc", eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.getUnifiedParticipantDBValue ("AbC"));
  }

  @Test
  public void testHasDefaultParticipantIdentifierScheme () {
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultParticipantIdentifierScheme (SimpleParticipantIdentifier.createWithDefaultScheme ("abc")));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultParticipantIdentifierScheme (new SimpleParticipantIdentifier ("dummy-actorid-upis",
                                                                                                         "abc")));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultParticipantIdentifierScheme (CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME +
                                                                       "::abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultParticipantIdentifierScheme (CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME +
                                                                        ":abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultParticipantIdentifierScheme (CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME +
                                                                        "abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultParticipantIdentifierScheme ("dummy-actorid-upis::abc"));
  }

  @Test
  public void testHasDefaultDocumentTypeIdentifierScheme () {
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultDocumentTypeIdentifierScheme (SimpleDocumentTypeIdentifier.createWithDefaultScheme ("abc")));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultDocumentTypeIdentifierScheme (new SimpleDocumentTypeIdentifier ("doctype",
                                                                                                           "abc")));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultDocumentTypeIdentifierScheme (CIdentifier.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME +
                                                                        "::abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultDocumentTypeIdentifierScheme (CIdentifier.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME +
                                                                         ":abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultDocumentTypeIdentifierScheme (CIdentifier.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME +
                                                                         "abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultDocumentTypeIdentifierScheme ("doctype::abc"));
  }

  @Test
  public void testHasDefaultProcessIdentifierScheme () {
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultProcessIdentifierScheme (SimpleProcessIdentifier.createWithDefaultScheme ("abc")));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultProcessIdentifierScheme (new SimpleProcessIdentifier ("proctype", "abc")));
    assertTrue (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultProcessIdentifierScheme (CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME +
                                                                   "::abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultProcessIdentifierScheme (CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME +
                                                                    ":abc"));
    assertFalse (eu.europa.ec.cipa.peppol.identifier.IdentifierUtils.hasDefaultProcessIdentifierScheme (CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME +
                                                                    "abc"));
    assertFalse (IdentifierUtils.hasDefaultProcessIdentifierScheme ("proctype::abc"));
  }
}
