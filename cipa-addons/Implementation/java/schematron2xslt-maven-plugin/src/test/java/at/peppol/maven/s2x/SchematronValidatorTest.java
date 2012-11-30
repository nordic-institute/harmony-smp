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

package at.peppol.maven.s2x;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.validation.schematron.xslt.ISchematronXSLTProvider;
import at.peppol.validation.schematron.xslt.SchematronResourceSCHCache;

import com.phloc.commons.error.IResourceError;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.transform.CollectingTransformErrorListener;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SchematronValidatorTest {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SchematronValidatorTest.class);

  @Test
  public void testXSLTPreprocessor () {
    final CollectingTransformErrorListener aCEH = new CollectingTransformErrorListener ();
    final ISchematronXSLTProvider aPreprocessor = SchematronResourceSCHCache.createSchematronXSLTProvider (new ClassPathResource ("test-sch/BII03_en.sch"),
                                                                                                           aCEH,
                                                                                                           null);
    assertNotNull (aPreprocessor);
    assertTrue (aPreprocessor.isValidSchematron ());
    assertNotNull (aPreprocessor.getXSLTDocument ());
    for (final IResourceError aError : aCEH.getResourceErrors ())
      s_aLogger.info ("!!" + aError.getAsString (Locale.US));
    s_aLogger.info ("!!" + XMLWriter.getXMLString (aPreprocessor.getXSLTDocument ()));
  }
}
