/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
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
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aPI1);
      fail ("null parameter not allowed");
    }
    catch (final IllegalArgumentException ex) {
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
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      IdentifierUtils.areIdentifiersEqual (null, aDI1);
      fail ("null parameter not allowed");
    }
    catch (final IllegalArgumentException ex) {
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
