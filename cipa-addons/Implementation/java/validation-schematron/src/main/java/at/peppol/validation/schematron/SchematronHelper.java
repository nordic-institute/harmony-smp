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

package at.peppol.validation.schematron;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Source;

import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;

import at.peppol.validation.schematron.svrl.SVRLFailedAssert;
import at.peppol.validation.schematron.svrl.SVRLReader;
import at.peppol.validation.schematron.svrl.SVRLResourceError;
import at.peppol.validation.schematron.svrl.SVRLUtils;

import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;

/**
 * This is a helper class that provides a way to easily apply an Schematron
 * resource on an XML resource.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SchematronHelper {
  private SchematronHelper () {}

  /**
   * Apply the passed schematron on the passed XML resource using a custom error
   * handler.
   * 
   * @param aSchematron
   *        The Schematron resource. May not be <code>null</code>.
   * @param aXML
   *        The XML resource. May not be <code>null</code>.
   * @return <code>null</code> if either the Schematron or the XML could not be
   *         read.
   * @throws IllegalStateException
   *         if the processing throws an unexpected exception.
   */
  @Nullable
  public static SchematronOutputType applySchematron (@Nonnull final ISchematronResource aSchematron,
                                                      @Nonnull final IReadableResource aXML) {
    if (aSchematron == null)
      throw new NullPointerException ("schematron");
    if (aXML == null)
      throw new NullPointerException ("XML document");

    try {
      // Apply Schematron on XML
      final Document aDoc = aSchematron.applySchematronValidation (aXML);
      if (aDoc != null) {
        // Convert the resulting XML document to a JAXB domain object
        return SVRLReader.readXML (aDoc);
      }
    }
    catch (final Exception ex) {
      throw new IllegalArgumentException ("Failed to apply Schematron " +
                                          aSchematron.getID () +
                                          " onto XML resource " +
                                          aXML.getResourceID (), ex);
    }
    return null;
  }

  /**
   * Apply the passed schematron on the passed XML resource.
   * 
   * @param aSchematron
   *        The Schematron resource. May not be <code>null</code>.
   * @param aXML
   *        The XML resource. May not be <code>null</code>.
   * @return <code>null</code> if either the Schematron or the XML could not be
   *         read.
   * @throws IllegalStateException
   *         if the processing throws an unexpected exception.
   */
  @Nullable
  public static SchematronOutputType applySchematron (@Nonnull final ISchematronResource aSchematron,
                                                      @Nonnull final Source aXML) {
    if (aSchematron == null)
      throw new NullPointerException ("schematron");
    if (aXML == null)
      throw new NullPointerException ("XML document");

    try {
      // Apply Schematron on XML.
      final Document aDoc = aSchematron.applySchematronValidation (aXML);
      if (aDoc != null) {
        // Convert the resulting XML document to a JAXB domain object
        return SVRLReader.readXML (aDoc);
      }
    }
    catch (final Exception ex) {
      throw new IllegalArgumentException ("Failed to apply Schematron " +
                                          aSchematron.getID () +
                                          " onto XML source " +
                                          aXML, ex);
    }
    return null;
  }

  /**
   * Convert a {@link SchematronOutputType} to an {@link IResourceErrorGroup}.
   * 
   * @param aSchematronOutput
   *        The result of Schematron validation
   * @param sResourceName
   *        The name of the resource that was validated (may be a file path
   *        etc.)
   * @return List non-<code>null</code> error list of {@link SVRLResourceError}
   *         objects.
   */
  @Nonnull
  public static IResourceErrorGroup convertToResourceErrorGroup (@Nonnull final SchematronOutputType aSchematronOutput,
                                                                 @Nullable final String sResourceName) {
    if (aSchematronOutput == null)
      throw new NullPointerException ("schematronOutput");

    final ResourceErrorGroup ret = new ResourceErrorGroup ();
    for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSchematronOutput))
      ret.addResourceError (aFailedAssert.getAsResourceError (sResourceName));
    return ret;
  }
}
