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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.oclc.purl.dsdl.svrl.FailedAssertType;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.error.EErrorLevel;

/**
 * Miscellaneous utility methods for handling Schematron output (SVRL).
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SVRLUtils {
  private SVRLUtils () {}

  /**
   * Get a list of all failed assertions in a given schematron output.
   * 
   * @param aSchematronOutput
   *        The schematron output to be used. May not be <code>null</code>.
   * @return A non-<code>null</code> list with all failed assertions.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <SVRLFailedAssert> getAllFailedAssertions (@Nonnull final SchematronOutputType aSchematronOutput) {
    final List <SVRLFailedAssert> ret = new ArrayList <SVRLFailedAssert> ();
    for (final Object aObj : aSchematronOutput.getActivePatternAndFiredRuleAndFailedAssert ())
      if (aObj instanceof FailedAssertType)
        ret.add (new SVRLFailedAssert ((FailedAssertType) aObj));
    return ret;
  }

  /**
   * Get a list of all failed assertions in a given schematron output, with an
   * error level equally or more severe than the passed error level.
   * 
   * @param aSchematronOutput
   *        The schematron output to be used. May not be <code>null</code>.
   * @param eErrorLevel
   *        Minimum error level to be queried
   * @return A non-<code>null</code> list with all failed assertions.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <SVRLFailedAssert> getAllFailedAssertionsMoreOrEqualSevereThan (@Nonnull final SchematronOutputType aSchematronOutput,
                                                                                     @Nonnull final EErrorLevel eErrorLevel) {
    final List <SVRLFailedAssert> ret = new ArrayList <SVRLFailedAssert> ();
    for (final Object aObj : aSchematronOutput.getActivePatternAndFiredRuleAndFailedAssert ())
      if (aObj instanceof FailedAssertType) {
        final SVRLFailedAssert aFA = new SVRLFailedAssert ((FailedAssertType) aObj);
        if (aFA.getFlag ().isMoreOrEqualSevereThan (eErrorLevel))
          ret.add (aFA);
      }
    return ret;
  }

  /**
   * Get the error level associated with a single failed assertion.
   * 
   * @param aFailedAssert
   *        The failed assert to be queried. May not be <code>null</code>.
   * @return The error level and never <code>null</code>.
   */
  @Nonnull
  public static EErrorLevel getErrorLevelFromFailedAssert (@Nonnull final FailedAssertType aFailedAssert) {
    final String sFlag = aFailedAssert.getFlag ();
    if (sFlag.equalsIgnoreCase ("warning") || sFlag.equalsIgnoreCase ("warn"))
      return EErrorLevel.WARN;
    if (sFlag.equalsIgnoreCase ("error") || sFlag.equalsIgnoreCase ("err"))
      return EErrorLevel.ERROR;
    if (sFlag.equalsIgnoreCase ("fatal") ||
        sFlag.equalsIgnoreCase ("fatal_error") ||
        sFlag.equalsIgnoreCase ("fatalerror"))
      return EErrorLevel.FATAL_ERROR;
    throw new IllegalArgumentException ("Cannot convert the SVRL failed assertion flag '" +
                                        sFlag +
                                        "' to an error level. Please extend the preceeding list!");
  }
}
