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
package eu.europa.ec.cipa.validation.pyramid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.validation.generic.IXMLValidator;
import eu.europa.ec.cipa.validation.generic.XMLSchemaValidator;
import eu.europa.ec.cipa.validation.generic.XMLSchematronValidator;
import eu.europa.ec.cipa.validation.rules.EValidationArtefact;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationArtefact;
import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

/**
 * Second version of the validation pyramid - can handle industry and entity
 * specific artifacts much better. By default this validation pyramid only
 * auto-determines the first 4 levels (technical structure, transaction
 * requirements, profile requirements and legal requirements). The industry and
 * entity specific artifacts must be added manually.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public class ValidationPyramid2 extends AbstractValidationPyramid {
  private final IValidationDocumentType m_aValidationDocType;
  private final IValidationTransaction m_aValidationTransaction;
  private final Locale m_aValidationCountry;
  private final List <ValidationPyramidLayer> m_aValidationLayers = new ArrayList <ValidationPyramidLayer> ();

  /**
   * Create a new validation pyramid that is country independent and handles all
   * available levels.
   * 
   * @param aValidationDocumentType
   *        Document type. Determines the
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid2 (@Nonnull final IValidationDocumentType aValidationDocumentType,
                             @Nonnull final IValidationTransaction aValidationTransaction) {
    this (aValidationDocumentType, aValidationTransaction, null);
  }

  /**
   * Create a new validation pyramid that handles all country-unspecific levels.
   * 
   * @param aValidationDocumentType
   *        Document type. Determines the
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid2 (@Nonnull final IValidationDocumentType aValidationDocumentType,
                             @Nonnull final IValidationTransaction aValidationTransaction,
                             @Nullable final Locale aValidationCountry) {
    if (aValidationDocumentType == null)
      throw new NullPointerException ("documentType");
    if (aValidationTransaction == null)
      throw new NullPointerException ("transaction");

    m_aValidationDocType = aValidationDocumentType;
    m_aValidationTransaction = aValidationTransaction;
    m_aValidationCountry = aValidationCountry;

    // Check if an XML schema is present for the technical structure
    final Schema aXMLSchema = aValidationDocumentType.getSchema ();
    if (aXMLSchema != null) {
      // Add the XML schema validator first
      final XMLSchemaValidator aValidator = new XMLSchemaValidator (aXMLSchema);
      // true: If the XSD validation fails no Schematron validation is needed
      addValidationLayer (new ValidationPyramidLayer (EValidationLevel.TECHNICAL_STRUCTURE, aValidator, true));
    }

    final Locale aLookupCountry = m_aValidationCountry == null ? CGlobal.LOCALE_INDEPENDENT : m_aValidationCountry;

    // Iterate over all country independent validation levels in the correct
    // order + legal requirements - this means industry specific rules are not
    // automatically added!
    for (final IValidationLevel eLevel : EValidationLevel.getAllLevelsInValidationOrder ())
      if (eLevel.isLowerOrEqualLevelThan (EValidationLevel.LEGAL_REQUIREMENTS)) {
        // Determine all validation artefacts that match
        for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (eLevel,
                                                                                                m_aValidationDocType,
                                                                                                aLookupCountry)) {
          // Get the Schematron SCH for the specified transaction in this level
          final IReadableResource aSCH = eArtefact.getValidationSchematronResource (m_aValidationTransaction);
          if (aSCH != null) {
            // We found a matching layer
            addValidationLayer (new ValidationPyramidLayer (eLevel,
                                                            XMLSchematronValidator.createFromSCHPure (aSCH),
                                                            false));
          }
        }
      }
  }

  /**
   * Add a new validation layer
   * 
   * @param aLayer
   *        The layer to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addValidationLayer (@Nonnull final ValidationPyramidLayer aLayer) {
    if (aLayer == null)
      throw new NullPointerException ("layer");

    m_aValidationLayers.add (aLayer);
    // Sort validation layers
    Collections.sort (m_aValidationLayers, new ComparatorValidationPyramidLayerByLevel ());
    return this;
  }

  /**
   * Add an industry specific XML Schema to the validation
   * 
   * @param aXSD
   *        The XML schema to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addIndustrySpecificXSDLayer (@Nonnull final IReadableResource aXSD) {
    if (aXSD == null)
      throw new NullPointerException ("Schematron Resource");

    return addIndustrySpecificLayer (new XMLSchemaValidator (aXSD));
  }

  /**
   * Add an industry specific Schematron to the validation
   * 
   * @param aSCH
   *        The Schematron to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addIndustrySpecificSchematronLayer (@Nonnull final IReadableResource aSCH) {
    if (aSCH == null)
      throw new NullPointerException ("Schematron Resource");

    return addIndustrySpecificLayer (XMLSchematronValidator.createFromSCHPure (aSCH));
  }

  /**
   * Add an industry specific layer to the validation
   * 
   * @param aValidator
   *        The validator to be applied on this layer. May not be
   *        <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addIndustrySpecificLayer (@Nonnull final IXMLValidator aValidator) {
    if (aValidator == null)
      throw new NullPointerException ("Validator");

    final ValidationPyramidLayer aLayer = new ValidationPyramidLayer (EValidationLevel.INDUSTRY_SPECIFIC,
                                                                      aValidator,
                                                                      false);
    return addValidationLayer (aLayer);
  }

  /**
   * Add an entity specific XML Schema to the validation
   * 
   * @param aXSD
   *        The XML schema to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addEntitySpecificXSDLayer (@Nonnull final IReadableResource aXSD) {
    if (aXSD == null)
      throw new NullPointerException ("XSD Resource");

    return addEntitySpecificLayer (new XMLSchemaValidator (aXSD));
  }

  /**
   * Add an entity specific Schematron to the validation
   * 
   * @param aSCH
   *        The Schematron to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addEntitySpecificSchematronLayer (@Nonnull final IReadableResource aSCH) {
    if (aSCH == null)
      throw new NullPointerException ("Schematron Resource");

    return addEntitySpecificLayer (XMLSchematronValidator.createFromSCHPure (aSCH));
  }

  /**
   * Add an entity specific layer to the validation
   * 
   * @param aValidator
   *        The validator to be applied on this layer. May not be
   *        <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addEntitySpecificLayer (@Nonnull final IXMLValidator aValidator) {
    if (aValidator == null)
      throw new NullPointerException ("Validator");

    final ValidationPyramidLayer aLayer = new ValidationPyramidLayer (EValidationLevel.ENTITY_SPECIFC,
                                                                      aValidator,
                                                                      false);
    return addValidationLayer (aLayer);
  }

  @Nonnull
  public IValidationDocumentType getValidationDocumentType () {
    return m_aValidationDocType;
  }

  @Nonnull
  public IValidationTransaction getValidationTransaction () {
    return m_aValidationTransaction;
  }

  public boolean isValidationCountryIndependent () {
    return m_aValidationCountry == null;
  }

  @Nullable
  public Locale getValidationCountry () {
    return m_aValidationCountry;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <ValidationPyramidLayer> getAllValidationLayers () {
    return ContainerHelper.newList (m_aValidationLayers);
  }

  @Nonnull
  public ValidationPyramidResult applyValidation (final String sResourceName, @Nonnull final Source aXML) {
    if (aXML == null)
      throw new NullPointerException ("XML");

    final ValidationPyramidResult ret = new ValidationPyramidResult (m_aValidationDocType,
                                                                     m_aValidationTransaction,
                                                                     m_aValidationCountry);

    final int nMaxLayers = m_aValidationLayers.size ();
    int nLayerIndex = 0;

    // For all validation layers
    for (final ValidationPyramidLayer aValidationLayer : m_aValidationLayers) {
      // The validator to use
      final IXMLValidator aValidator = aValidationLayer.getValidator ();

      // Perform the validation
      final IResourceErrorGroup aErrors = aValidator.validateXMLInstance (sResourceName, aXML);

      // Add the single result to the validation pyramid
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
