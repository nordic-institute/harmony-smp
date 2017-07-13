/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.w3c.dom.Element;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class SMPDBUtilsTest {/*
  @Test
  public void getRFC1421CompliantStringWithoutCarriageReturnCharacters () {
    assertNull (SMPDBUtils.getRFC1421CompliantStringWithoutCarriageReturnCharacters(null));
    assertEquals ("", SMPDBUtils.getRFC1421CompliantStringWithoutCarriageReturnCharacters(""));

    // for up to 64 chars it makes no difference
    for (int i = 0; i <= 64; ++i) {
      final char [] aChars = new char [i];
      Arrays.fill (aChars, 'a');
      final String sText = new String (aChars);
      assertEquals (sText, SMPDBUtils.getRFC1421CompliantStringWithoutCarriageReturnCharacters(sText));
    }

    final String sLong = "123456789012345678901234567890123456789012345678901234567890abcd"
                         + "123456789012345678901234567890123456789012345678901234567890abcd"
                         + "xyz";
    final String sFormatted = SMPDBUtils.getRFC1421CompliantStringWithoutCarriageReturnCharacters(sLong);
    assertEquals ("123456789012345678901234567890123456789012345678901234567890abcd\n"
                  + "123456789012345678901234567890123456789012345678901234567890abcd\n"
                  + "xyz", sFormatted);
  }

  @Test
  public void testConvertFromString () {
    // Use elements
    final String sXML = "<any xmlns=\"urn:foo\"><child>text1</child><child2 /></any>";
    final ExtensionType aExtension = SMPDBUtils.getAsExtensionSafe(sXML);
    assertNotNull (aExtension);
    assertNotNull (aExtension.getAny ());
    assertTrue (aExtension.getAny () instanceof Element);

    assertNull (SMPDBUtils.getAsExtensionSafe((String) null));
    assertNull (SMPDBUtils.getAsExtensionSafe(""));

    // Convert back to String
    final String sXML2 = SMPDBUtils.convert(aExtension);
    assertEquals (sXML, sXML2);

    // Cannot convert non-element
    ExtensionType extension = SMPDBUtils.getAsExtensionSafe("Plain text");
    assertNull(extension);
  }
  */
}
