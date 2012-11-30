/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.peppol.validation.schematron.svrl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import at.peppol.validation.schematron.ISchematronResource;
import at.peppol.validation.schematron.svrl.SVRLReader;
import at.peppol.validation.schematron.xslt.SchematronResourceSCH;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;

/**
 * Test class for class {@link SVRLReader}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SVRLReaderTest {
  private static final String VALID_SCHEMATRON = "testdata/valid01.sch.xml";
  private static final String VALID_XMLINSTANCE = "testdata/valid01.xml";

  @Test
  public void testReadValid () throws Exception {
    final ISchematronResource aSV = SchematronResourceSCH.fromClassPath (VALID_SCHEMATRON);
    assertNotNull ("Failed to parse Schematron", aSV);
    final Document aDoc = aSV.applySchematronValidation (new ClassPathResource (VALID_XMLINSTANCE));
    assertNotNull ("Failed to parse demo XML", aDoc);

    if (false) {
      GlobalDebug.setDebugModeDirect (true);
      System.out.println (XMLWriter.getXMLString (aDoc));
    }
    final SchematronOutputType aSO = SVRLReader.readXML (aDoc);
    assertNotNull ("Failed to parse Schematron output", aSO);

    assertNotNull (SVRLReader.readXML (XMLReader.readXMLDOM (new ClassPathResource ("testdata/test1.svrl"))));
  }

  @Test
  public void testReadInvalidSchematron () {
    try {
      // Read null
      SVRLReader.readXML ((Node) null);
      fail ();
    }
    catch (final NullPointerException ex) {}

    try {
      // Read empty XML
      SVRLReader.readXML (XMLFactory.newDocument ());
      fail ();
    }
    catch (final NullPointerException ex) {}

    // Read XML that is not SVRL
    final SchematronOutputType aSVRL = SVRLReader.readXML (new ClassPathResource ("testdata/goodOrder01.xml"));
    assertNull (aSVRL);
  }
}
