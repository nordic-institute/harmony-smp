package eu.europa.ec.cipa.validation.rules;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;

/**
 * Base interface for a single validation level.
 * 
 * @author Philip Helger
 */
public interface IValidationLevel extends IHasID <String> {
  /**
   * @return The ID of this level. Mainly to be used for serialization.
   */
  @Nonnull
  @Nonempty
  String getID ();

  /**
   * @return The int level representation of this level. The lower the number
   *         the more generic are the validation rules. This number is only
   *         present for easy ordering of the validation level and does not
   *         serve any other purpose.
   */
  @Nonnegative
  int getLevel ();

  /**
   * Check if this level is lower than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is lower than the passed level,
   *         <code>false</code> otherwise.
   */
  boolean isLowerLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * Check if this level is lower or equal than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is lower or equal than the passed
   *         level, <code>false</code> otherwise.
   */
  boolean isLowerOrEqualLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * Check if this level is higher than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is higher than the passed level,
   *         <code>false</code> otherwise.
   */
  boolean isHigherLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * Check if this level is higher or equal than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is higher or equal than the passed
   *         level, <code>false</code> otherwise.
   */
  boolean isHigherOrEqualLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * @return <code>true</code> if this level can have country specific
   *         artefacts. <code>false</code> if this level is country independent!
   */
  boolean canHaveCountrySpecificArtefacts ();
}
