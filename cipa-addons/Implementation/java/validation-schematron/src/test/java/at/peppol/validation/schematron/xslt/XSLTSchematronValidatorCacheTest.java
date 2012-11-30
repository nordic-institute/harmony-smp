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

package at.peppol.validation.schematron.xslt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import at.peppol.validation.schematron.ISchematronResource;

import com.phloc.commons.concurrent.ManagedExecutorService;
import com.phloc.commons.error.IResourceError;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.transform.CollectingTransformErrorListener;

/**
 * Test class for class {@link SchematronResourceSCH}
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class XSLTSchematronValidatorCacheTest {
  private static final Logger s_aLogger = LoggerFactory.getLogger (XSLTSchematronValidatorCacheTest.class);
  private static final String VALID_SCHEMATRON = "testdata/valid01.sch.xml";
  private static final String VALID_XMLINSTANCE = "testdata/valid01.xml";

  private static final int RUNS = 1000;

  @Test
  public void testValidSynchronous () throws Exception {
    // Ensure that the Schematron is cached
    SchematronResourceSCH.fromClassPath (VALID_SCHEMATRON);

    final long nStart = System.nanoTime ();
    for (int i = 0; i < RUNS; ++i) {
      final ISchematronResource aSV = SchematronResourceSCH.fromClassPath (VALID_SCHEMATRON);
      final Document aDoc = aSV.applySchematronValidation (new ClassPathResource (VALID_XMLINSTANCE));
      assertNotNull (aDoc);
    }
    final long nEnd = System.nanoTime ();
    s_aLogger.info ("Sync Total: " +
                    ((nEnd - nStart) / 1000) +
                    " microsecs btw. " +
                    ((nEnd - nStart) / 1000 / RUNS) +
                    " microsecs/run");
  }

  @Test
  public void testValidAsynchronous () throws Exception {
    // Ensure that the Schematron is cached
    SchematronResourceSCH.fromClassPath (VALID_SCHEMATRON);

    // Create Thread pool with 5 possible threads
    final ExecutorService aSenderThreadPool = Executors.newFixedThreadPool (5);

    final long nStart = System.nanoTime ();
    for (int i = 0; i < RUNS; ++i) {
      aSenderThreadPool.submit (new Runnable () {
        public void run () {
          try {
            final ISchematronResource aSV = SchematronResourceSCH.fromClassPath (VALID_SCHEMATRON);
            final Document aDoc = aSV.applySchematronValidation (new ClassPathResource (VALID_XMLINSTANCE));
            assertNotNull (aDoc);
          }
          catch (final Exception ex) {
            throw new IllegalStateException (ex);
          }
        }
      });
    }
    new ManagedExecutorService (aSenderThreadPool).shutdownAndWaitUntilAllTasksAreFinished ();
    final long nEnd = System.nanoTime ();
    s_aLogger.info ("Async Total: " +
                    ((nEnd - nStart) / 1000) +
                    " microsecs btw. " +
                    ((nEnd - nStart) / 1000 / RUNS) +
                    " microsecs/run");
  }

  @Test
  public void testInvalidSchematron () {
    assertFalse (new SchematronResourceSCH (new ClassPathResource ("testdata/invalid01.sch.xml")).isValidSchematron ());
    assertFalse (new SchematronResourceSCH (new ClassPathResource ("testdata/invalid02.sch.xml")).isValidSchematron ());
    assertFalse (new SchematronResourceSCH (new ClassPathResource ("testdata/this.file.does.not.exists")).isValidSchematron ());

    assertFalse (new SchematronResourceSCH (new FileSystemResource ("src/test/resources/testdata/invalid01.sch.xml")).isValidSchematron ());
    assertFalse (new SchematronResourceSCH (new FileSystemResource ("src/test/resources/testdata/invalid02.sch.xml")).isValidSchematron ());
    assertFalse (new SchematronResourceSCH (new FileSystemResource ("src/test/resources/testdata/this.file.does.not.exists")).isValidSchematron ());
  }

  @Test
  public void testXSLTPreprocessor () {
    final CollectingTransformErrorListener aCEH = new CollectingTransformErrorListener ();
    final ISchematronXSLTProvider aPreprocessor = SchematronResourceSCHCache.createSchematronXSLTProvider (new ClassPathResource ("testdata-from-xvml/BII03_en.sch"),
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
