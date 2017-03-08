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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.europa.ec.cipa.smp.server.data.dbms.model.CommonColumnsLengths;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import static org.junit.Assert.*;

/**
 * Test class for class {@link IdentifierUtils}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class IdentifierUtilsTest {
  private static final String [] PARTICIPANT_SCHEME_VALID = { "bdxr-actorid-upis",
                                                             "bdxr-ACTORID-UPIS",
                                                             CommonColumnsLengths.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME,
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
  public void testAreIdentifiersEqualPariticpantIdentifier () {
    final ParticipantIdentifierType aPI1 = new ParticipantIdentifierType(null, "0088:123abc");

    final ParticipantIdentifierType aPI3a = new ParticipantIdentifierType("iso6523-actorid-upis","0088:123456");
    final ParticipantIdentifierType aPI3b = new ParticipantIdentifierType ("my-actorid-scheme", "0088:12345");
    assertTrue (IdentifierUtils.areIdentifiersEqual (aPI1, aPI1));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI1, aPI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI1, aPI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aPI3a, aPI3b));

    try {
      IdentifierUtils.areIdentifiersEqual (aPI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aPI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  public void testAreIdentifiersEqualDocumentIdentifier () {
    final DocumentIdentifier aDI1 = new DocumentIdentifier(null, "urn:doc:anydoc");
    final DocumentIdentifier aDI2 = new DocumentIdentifier(null, "urn:doc:anydoc");
    final DocumentIdentifier aDI3a = new DocumentIdentifier(null, "urn:doc:anyotherdoc");
    final DocumentIdentifier aDI3b = new DocumentIdentifier ("my-docid-test", "urn:doc:anyotherdoc");
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI1, aDI1));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI1, aDI2));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aDI2, aDI1));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI1, aDI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI1, aDI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI2, aDI3a));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI2, aDI3b));
    assertFalse (IdentifierUtils.areIdentifiersEqual (aDI3a, aDI3b));

    try {
      IdentifierUtils.areIdentifiersEqual (aDI1, null);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aDI1);
      fail ("null parameter not allowed");
    }
    catch (final NullPointerException ex) {
      // expected
    }
  }

  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void getIdentifierURIEncoded () {
    final ParticipantIdentifierType aPI = new ParticipantIdentifierType("0088:123abc", "iso6523-actorid-upis");
    assertEquals ("iso6523-actorid-upis::0088:123abc", IdentifierUtils.getIdentifierURIEncoded (aPI));

    final DocumentIdentifier aDI = new DocumentIdentifier("urn:doc:anydoc", "busdox-docid-qns");
    assertEquals ("busdox-docid-qns::urn:doc:anydoc", IdentifierUtils.getIdentifierURIEncoded (aDI));

    try {
      IdentifierUtils.getIdentifierURIEncoded (new ParticipantIdentifierType (null, "0088:12345"));
      fail ("Empty scheme should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      IdentifierUtils.getIdentifierURIEncoded (new ParticipantIdentifierType ("0088:12345", ""));
      fail ("Empty scheme should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      IdentifierUtils.getIdentifierURIEncoded (new ParticipantIdentifierType("iso6523-actorid-upis",null));
      fail ("null value should trigger an error!");
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }
  }
}
