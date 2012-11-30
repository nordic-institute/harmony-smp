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

import java.util.Locale;

import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.transform.LoggingTransformErrorListener;
import com.phloc.commons.xml.transform.TransformSourceFactory;
import com.phloc.commons.xml.transform.XMLTransformerFactory;

/**
 * The XSLT preprocessor used to convert a Schematron XML document into an XSLT
 * document. This implementation uses JAXP with Saxon to be used as the
 * respective parser.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
final class SchematronProviderXSLTFromSCH extends AbstractSchematronXSLTProvider {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SchematronProviderXSLTFromSCH.class);

  /**
   * The classpath directory where the Schematron 2 XSLT files reside.
   */
  private static final String SCHEMATRON_DIRECTORY_XSLT2 = "schematron/20100414-xslt2/";

  /**
   * The class path to first XSLT to be applied.
   */
  private static final String XSLT2_STEP1 = SCHEMATRON_DIRECTORY_XSLT2 + "iso_dsdl_include.xsl";

  /**
   * The class path to second XSLT to be applied.
   */
  private static final String XSLT2_STEP2 = SCHEMATRON_DIRECTORY_XSLT2 + "iso_abstract_expand.xsl";

  /**
   * The class path to third and last XSLT to be applied.
   */
  private static final String XSLT2_STEP3 = SCHEMATRON_DIRECTORY_XSLT2 + "iso_svrl_for_xslt2.xsl";

  private static Templates s_aStep1;
  private static Templates s_aStep2;
  private static Templates s_aStep3;

  public SchematronProviderXSLTFromSCH (@Nullable final IReadableResource aSchematronResource,
                                        @Nullable final ErrorListener aCustomErrorListener,
                                        @Nullable final URIResolver aURIResolver) {
    final ErrorListener aErrorListener = aCustomErrorListener != null ? aCustomErrorListener
                                                                     : new LoggingTransformErrorListener (Locale.US);

    try {
      // prepare all steps
      if (s_aStep1 == null)
        s_aStep1 = XMLTransformerFactory.newTemplates (new ClassPathResource (XSLT2_STEP1));
      if (s_aStep2 == null)
        s_aStep2 = XMLTransformerFactory.newTemplates (new ClassPathResource (XSLT2_STEP2));
      if (s_aStep3 == null)
        s_aStep3 = XMLTransformerFactory.newTemplates (new ClassPathResource (XSLT2_STEP3));

      // perform step 1 (Schematron -> ResultStep1)
      final DOMResult aResult1 = new DOMResult ();
      final Transformer aTransformer1 = s_aStep1.newTransformer ();
      aTransformer1.setErrorListener (aErrorListener);
      if (aURIResolver != null)
        aTransformer1.setURIResolver (aURIResolver);
      aTransformer1.transform (TransformSourceFactory.create (aSchematronResource), aResult1);

      // perform step 2 (ResultStep1 -> ResultStep2)
      final DOMResult aResult2 = new DOMResult ();
      final Transformer aTransformer2 = s_aStep2.newTransformer ();
      aTransformer2.setErrorListener (aErrorListener);
      if (aURIResolver != null)
        aTransformer2.setURIResolver (aURIResolver);
      aTransformer2.transform (TransformSourceFactory.create (aResult1.getNode ()), aResult2);

      // perform step 3 (ResultStep2 -> ResultStep3XSL)
      final DOMResult aResult3 = new DOMResult ();
      final Transformer aTransformer3 = s_aStep3.newTransformer ();
      aTransformer3.setErrorListener (aErrorListener);
      if (aURIResolver != null)
        aTransformer3.setURIResolver (aURIResolver);
      aTransformer3.transform (TransformSourceFactory.create (aResult2.getNode ()), aResult3);

      // Save the underlying XSLT document....
      // Note: Saxon 6.5.5 does not allow to clone the document node!!!!
      m_aSchematronXSLTDoc = (Document) aResult3.getNode ();

      // compile result of step 3
      m_aSchematronXSLT = XMLTransformerFactory.newTemplates (TransformSourceFactory.create (m_aSchematronXSLTDoc));
    }
    catch (final Exception ex) {
      s_aLogger.error ("Schematron preprocessor error", ex);
    }
  }
}
