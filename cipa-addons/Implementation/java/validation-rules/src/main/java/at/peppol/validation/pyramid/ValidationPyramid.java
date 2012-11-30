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
package at.peppol.validation.pyramid;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import at.peppol.validation.generic.IXMLValidator;
import at.peppol.validation.generic.XMLSchemaValidator;
import at.peppol.validation.generic.XMLSchematronValidator;
import at.peppol.validation.rules.EValidationArtefact;
import at.peppol.validation.rules.EValidationDocumentType;
import at.peppol.validation.rules.EValidationLevel;
import at.peppol.validation.rules.IValidationDocumentType;
import at.peppol.validation.rules.IValidationTransaction;
import at.peppol.validation.rules.ValidationTransaction;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.xml.transform.ResourceStreamSource;

/**
 * This class represents the PEPPOL validation pyramid. It evaluates all
 * applicable rules for a document based on the specified parameters. All
 * validation levels (see {@link EValidationLevel}) are handled.<br>
 * Note: the profile to be validated is automatically determined from the
 * profile contained in the UBL document. If a certain profile is present, it's
 * rules are applied when the corresponding level is applied.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public class ValidationPyramid {
  private final IValidationDocumentType m_aValidationDocType;
  private final IValidationTransaction m_aValidationTransaction;
  private final Locale m_aValidationCountry;
  private final List <ValidationPyramidLayer> m_aValidationLayers = new ArrayList <ValidationPyramidLayer> ();

  /**
   * Create a new validation pyramid that is country independent and handles all
   * available levels.
   * 
   * @param aValidationDocumentType
   *        Document type. May not be <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                            @Nonnull final IValidationTransaction aValidationTransaction) {
    this (aValidationDocumentType, aValidationTransaction, null);
  }

  /**
   * Create a new validation pyramid that handles all available levels.
   * 
   * @param aValidationDocumentType
   *        Document type. May not be <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                            @Nonnull final IValidationTransaction aValidationTransaction,
                            @Nullable final Locale aValidationCountry) {
    this (aValidationDocumentType,
          aValidationTransaction,
          aValidationCountry,
          EValidationLevel.getAllLevelsInValidationOrder ());
  }

  /**
   * Create a new validation pyramid
   * 
   * @param aValidationDocumentType
   *        Document type. May not be <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent levels.
   * @param aValidationLevelsInOrder
   *        All validation levels to consider in the order they should be
   *        executed. May neither be <code>null</code> nor empty. See
   *        {@link EValidationLevel#getAllLevelsInValidationOrder(EValidationLevel...)}
   *        for ordering of levels.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                            @Nonnull final IValidationTransaction aValidationTransaction,
                            @Nullable final Locale aValidationCountry,
                            @Nonnull @Nonempty final List <EValidationLevel> aValidationLevelsInOrder) {
    if (aValidationDocumentType == null)
      throw new NullPointerException ("documentType");
    if (aValidationTransaction == null)
      throw new NullPointerException ("transaction");
    if (ContainerHelper.isEmpty (aValidationLevelsInOrder))
      throw new IllegalArgumentException ("No validation levels passed!");

    m_aValidationDocType = aValidationDocumentType;
    m_aValidationTransaction = aValidationTransaction;
    m_aValidationCountry = aValidationCountry;

    final Schema aXMLSchema = aValidationDocumentType.getSchema ();
    if (aXMLSchema != null) {
      // Add the XML schema validator first
      final XMLSchemaValidator aValidator = new XMLSchemaValidator (aXMLSchema);
      // true: If the XSD validation fails no Schematron validation is needed
      m_aValidationLayers.add (new ValidationPyramidLayer (EValidationLevel.TECHNICAL_STRUCTURE, aValidator, true));
    }

    final Locale aLookupCountry = m_aValidationCountry == null ? CGlobal.LOCALE_INDEPENDENT : m_aValidationCountry;

    // Iterate over all validation levels in the correct order
    for (final EValidationLevel eLevel : aValidationLevelsInOrder) {
      // Determine all validation artefacts that match
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (eLevel,
                                                                                              m_aValidationDocType,
                                                                                              aLookupCountry)) {
        // Get the Schematron XSLT for the specified transaction in this level
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (m_aValidationTransaction);
        if (aXSLT != null)
          m_aValidationLayers.add (new ValidationPyramidLayer (eLevel,
                                                               XMLSchematronValidator.createFromXSLT (aXSLT),
                                                               false));
      }
    }
  }

  /**
   * @return The validation document to which the pyramid can be applied. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IValidationDocumentType getValidationDocumentType () {
    return m_aValidationDocType;
  }

  /**
   * @return The validation transaction to which the pyramid can be applied.
   *         Never <code>null</code>.
   */
  @Nonnull
  public IValidationTransaction getValidationTransaction () {
    return m_aValidationTransaction;
  }

  /**
   * @return <code>true</code> if the validation pyramid is country independent,
   *         <code>false</code> if a specific country is defined
   * @see #getValidationCountry()
   */
  public boolean isValidationCountryIndependent () {
    return m_aValidationCountry == null;
  }

  /**
   * @return <code>null</code> if no specific country is used in validation.
   * @see #isValidationCountryIndependent()
   */
  @Nullable
  public Locale getValidationCountry () {
    return m_aValidationCountry;
  }

  /**
   * @return A non-<code>null</code> list of all contained validation layers.
   */
  @Nonnull
  @ReturnsImmutableObject
  public List <ValidationPyramidLayer> getAllValidationLayers () {
    return ContainerHelper.makeUnmodifiable (m_aValidationLayers);
  }

  /**
   * Apply the validation pyramid on the passed resource.
   * 
   * @param aRes
   *        The XML resource to apply the validation pyramid on. May not be
   *        <code>null</code>.
   * @return The validation pyramid result. Never <code>null</code>.
   */
  @Nonnull
  public ValidationPyramidResult applyValidation (@Nonnull final IReadableResource aRes) {
    if (aRes == null)
      throw new NullPointerException ("resource");

    return applyValidation (aRes.getPath (), new ResourceStreamSource (aRes));
  }

  /**
   * Apply the pyramid on the passed {@link Source} object.
   * 
   * @param aXML
   *        The XML {@link Source}. IMPORTANT: Must be a {@link Source} that can
   *        be opened multiple times. Using e.g. a StreamSource with a
   *        StringReader will result in an error!
   * @return a non-<code>null</code> validation pyramid result.
   */
  @Nonnull
  public ValidationPyramidResult applyValidation (@Nonnull final Source aXML) {
    return applyValidation (null, aXML);
  }

  /**
   * Apply the pyramid on the passed {@link Source} object.
   * 
   * @param sResourceName
   *        The optional name of the source. Only used for error messages. May
   *        be <code>null</code>.
   * @param aXML
   *        The XML {@link Source}. IMPORTANT: Must be a {@link Source} that can
   *        be opened multiple times. Using e.g. a StreamSource with a
   *        StringReader will result in an error!
   * @return a non-<code>null</code> validation pyramid result.
   */
  @Nonnull
  public ValidationPyramidResult applyValidation (@Nullable final String sResourceName, @Nonnull final Source aXML) {
    if (aXML == null)
      throw new NullPointerException ("XML");

    final ValidationPyramidResult ret = new ValidationPyramidResult (m_aValidationDocType,
                                                                     m_aValidationTransaction,
                                                                     m_aValidationCountry);
    final int nMaxLayers = m_aValidationLayers.size ();
    int nLayerIndex = 0;
    for (final ValidationPyramidLayer aValidationLayer : m_aValidationLayers) {
      final IXMLValidator aValidator = aValidationLayer.getValidator ();
      final IResourceErrorGroup aErrors = aValidator.validateXMLInstance (sResourceName, aXML);
      ret.addValidationResultLayer (new ValidationPyramidResultLayer (aValidationLayer.getValidationLevel (),
                                                                      aValidator.getValidationType (),
                                                                      aValidationLayer.isStopValidatingOnError (),
                                                                      aErrors));
      if (aValidationLayer.isStopValidatingOnError () && aErrors.containsAtLeastOneError ()) {
        // Stop validating the whole pyramid!
        ret.setValidationInterrupted (nLayerIndex < (nMaxLayers - 1));
        break;
      }
      ++nLayerIndex;
    }
    return ret;
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("docType", m_aValidationDocType)
                                       .append ("transaction", m_aValidationTransaction)
                                       .append ("country", m_aValidationCountry)
                                       .append ("layers", m_aValidationLayers)
                                       .toString ();
  }
}
