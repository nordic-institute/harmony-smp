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
package eu.europa.ec.cipa.validation.generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;

import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.ResourceError;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.error.ResourceLocation;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.SchematronHelper;
import com.phloc.schematron.xslt.SchematronResourceSCH;
import com.phloc.schematron.xslt.SchematronResourceXSLT;

/**
 * Implementation of the {@link IXMLValidator} for XML Schema.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class XMLSchematronValidator extends AbstractXMLValidator {
  private static final Logger s_aLogger = LoggerFactory.getLogger (XMLSchematronValidator.class);

  private final ISchematronResource m_aSchematronRes;

  public XMLSchematronValidator (@Nonnull final ISchematronResource aSchematronRes) {
    if (aSchematronRes == null)
      throw new NullPointerException ("schematronResource");
    if (!aSchematronRes.isValidSchematron ())
      throw new IllegalArgumentException ("Passed schematronResource is invalid Schematron: " + aSchematronRes);
    m_aSchematronRes = aSchematronRes;
  }

  @Nonnull
  public EXMLValidationType getValidationType () {
    return EXMLValidationType.SCHEMATRON;
  }

  @Nonnull
  public IReadableResource getValidatingResource () {
    return m_aSchematronRes.getResource ();
  }

  @Nonnull
  public IResourceErrorGroup validateXMLInstance (@Nullable final String sResourceName, @Nonnull final Source aXML) {
    SchematronOutputType aSVRL = null;
    String sErrorMsg = "Internal error";
    try {
      aSVRL = SchematronHelper.applySchematron (m_aSchematronRes, aXML);
    }
    catch (final IllegalArgumentException ex) {
      // Validation failed - whysoever
      s_aLogger.error ("Failed to apply Schematron", ex);
      sErrorMsg += ": resolve any previous errors and try again";
    }
    if (aSVRL == null)
      return new ResourceErrorGroup (new ResourceError (new ResourceLocation (sResourceName),
                                                        EErrorLevel.FATAL_ERROR,
                                                        sErrorMsg));

    // Returns a list of SVRLResourceError objects!
    return SchematronHelper.convertToResourceErrorGroup (aSVRL, sResourceName);
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("schematronRes", m_aSchematronRes).toString ();
  }

  @Nonnull
  public static XMLSchematronValidator createFromXSLT (@Nonnull final IReadableResource aXSLT) {
    return new XMLSchematronValidator (new SchematronResourceXSLT (aXSLT));
  }

  @Nonnull
  public static XMLSchematronValidator createFromSCH (@Nonnull final IReadableResource aSCH) {
    return new XMLSchematronValidator (new SchematronResourceSCH (aSCH));
  }
}
