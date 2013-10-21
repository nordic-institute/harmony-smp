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
package eu.europa.ec.cipa.validation.rules;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.compare.AbstractIntegerComparator;
import com.phloc.commons.compare.ESortOrder;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.lang.EnumHelper;

import eu.europa.ec.cipa.validation.generic.EXMLValidationType;

/**
 * This enum represents the validation hierarchy. The hierarchy must be iterated
 * from top to bottom.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EValidationLevel implements IHasID <String> {
  /** Technical structure validation (e.g. XML schema) - never country specific */
  TECHNICAL_STRUCTURE ("technical", 10, EXMLValidationType.XSD),

  /**
   * Validation rules based on BII transaction requirements - never country
   * specific
   */
  TRANSACTION_REQUIREMENTS ("transaction", 20, EXMLValidationType.SCHEMATRON),

  /**
   * Validation rules based on BII profile (=process) requirements - never
   * country specific
   */
  PROFILE_REQUIREMENTS ("profile", 30, EXMLValidationType.SCHEMATRON),

  /** Validation rules based on legal obligations - maybe country specific */
  LEGAL_REQUIREMENTS ("legal", 40, EXMLValidationType.SCHEMATRON),

  /** Industry specific validation rules - maybe country specific */
  INDUSTRY_SPECIFIC ("industry", 50, EXMLValidationType.SCHEMATRON),

  /**
   * Entity (=company) specific validation rules - this level represents
   * bilateral agreements - maybe country specific
   */
  ENTITY_SPECIFC ("entity", 60, EXMLValidationType.SCHEMATRON);

  public static final class ComparatorLevel extends AbstractIntegerComparator <EValidationLevel> {
    public ComparatorLevel (@Nonnull final ESortOrder eSortOrder) {
      super (eSortOrder);
    }

    @Override
    protected long asLong (final EValidationLevel eLevel) {
      return eLevel.getLevel ();
    }
  }

  private final String m_sID;
  private final int m_nLevel;
  private final EXMLValidationType m_eValidationType;

  private EValidationLevel (@Nonnull @Nonempty final String sID,
                            @Nonnegative final int nLevel,
                            @Nonnull final EXMLValidationType eValidationType) {
    m_sID = sID;
    m_nLevel = nLevel;
    m_eValidationType = eValidationType;
  }

  /**
   * @return The ID of this level. Mainly to be used for serialization.
   */
  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  /**
   * @return The int level representation of this level. The lower the number
   *         the more generic are the validation rules. This number is only
   *         present for easy ordering of the validation level and does not
   *         serve any other purpose.
   */
  @Nonnegative
  public int getLevel () {
    return m_nLevel;
  }

  /**
   * Check if this level is lower than the passed level.
   * 
   * @param eLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is lower than the passed level,
   *         <code>false</code> otherwise.
   */
  public boolean isLowerLevelThan (@Nonnull final EValidationLevel eLevel) {
    return m_nLevel < eLevel.getLevel ();
  }

  /**
   * Check if this level is lower or equal than the passed level.
   * 
   * @param eLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is lower or equal than the passed
   *         level, <code>false</code> otherwise.
   */
  public boolean isLowerOrEqualLevelThan (@Nonnull final EValidationLevel eLevel) {
    return m_nLevel <= eLevel.getLevel ();
  }

  /**
   * Check if this level is higher than the passed level.
   * 
   * @param eLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is higher than the passed level,
   *         <code>false</code> otherwise.
   */
  public boolean isHigherLevelThan (@Nonnull final EValidationLevel eLevel) {
    return m_nLevel > eLevel.getLevel ();
  }

  /**
   * Check if this level is higher or equal than the passed level.
   * 
   * @param eLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is higher or equal than the passed
   *         level, <code>false</code> otherwise.
   */
  public boolean isHigherOrEqualLevelThan (@Nonnull final EValidationLevel eLevel) {
    return m_nLevel >= eLevel.getLevel ();
  }

  /**
   * @return <code>true</code> if this level can have country specific
   *         artefacts. <code>false</code> if this level is country independent!
   */
  public boolean canHaveCountrySpecificArtefacts () {
    return isHigherOrEqualLevelThan (LEGAL_REQUIREMENTS);
  }

  /**
   * @return The type of validation to be applied on this level - XML Schema or
   *         Schematron. Never <code>null</code>.
   */
  @Nonnull
  public EXMLValidationType getValidationType () {
    return m_eValidationType;
  }

  @Nullable
  public static EValidationLevel getFromIDOrNull (@Nullable final String sID) {
    return EnumHelper.getFromIDOrNull (EValidationLevel.class, sID);
  }

  /**
   * @return All validation levels in the order they must be executed. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsInValidationOrder () {
    return ContainerHelper.newList (values ());
  }

  /**
   * Get the passed validation levels in the order they should be executed.
   * 
   * @param aLevels
   *        The validation levels to be ordered
   * @return All validation levels in the order they must be executed.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsInValidationOrder (@Nullable final EValidationLevel... aLevels) {
    return ContainerHelper.getSortedInline (ContainerHelper.newList (aLevels),
                                            new ComparatorLevel (ESortOrder.ASCENDING));
  }

  /**
   * Get a list of all validation levels that support country specific
   * artefacts.
   * 
   * @return All validation levels having country specific artefacts. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsSupportingCountrySpecificArtefacts () {
    final List <EValidationLevel> ret = new ArrayList <EValidationLevel> ();
    for (final EValidationLevel eValidationLevel : EValidationLevel.values ())
      if (eValidationLevel.canHaveCountrySpecificArtefacts ())
        ret.add (eValidationLevel);
    return ret;
  }

  /**
   * Get a list of all validation levels that do NOT support country specific
   * artefacts.
   * 
   * @return All validation levels NOT having country specific artefacts. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsNotSupportingCountrySpecificArtefacts () {
    final List <EValidationLevel> ret = new ArrayList <EValidationLevel> ();
    for (final EValidationLevel eValidationLevel : EValidationLevel.values ())
      if (!eValidationLevel.canHaveCountrySpecificArtefacts ())
        ret.add (eValidationLevel);
    return ret;
  }
}
