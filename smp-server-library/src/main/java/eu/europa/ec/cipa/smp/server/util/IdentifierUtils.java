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
package eu.europa.ec.cipa.smp.server.util;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.string.StringHelper;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Locale;

import static eu.europa.ec.cipa.smp.server.data.dbms.model.CommonColumnsLengths.URL_SCHEME_VALUE_SEPARATOR;

/**
 * This class contains several identifier related utility methods.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
public final class IdentifierUtils {
  public static final boolean DEFAULT_CHARSET_CHECKS_DISABLED = false;

  private IdentifierUtils() {}

  /**
   * According to the specification, two participant identifiers are equal if
   * their parts are equal case insensitive.
   *
   * @param sIdentifierValue1
   *        First identifier value to compare. May be <code>null</code>.
   * @param sIdentifierValue2
   *        Second identifier value to compare. May be <code>null</code>.
   * @return <code>true</code> if the identifier values are equal,
   *         <code>false</code> otherwise. If both are <code>null</code> they
   *         are considered equal.
   */
  public static boolean areParticipantIdentifierValuesEqual (@Nullable final String sIdentifierValue1,
                                                             @Nullable final String sIdentifierValue2) {
    // equal case insensitive!
    return EqualsUtils.nullSafeEqualsIgnoreCase (sIdentifierValue1, sIdentifierValue2);
  }

  /**
   * According to the specification, two participant identifiers are equal if
   * their parts are equal case insensitive.
   *
   * @param aIdentifier1
   *        First identifier to compare. May not be null.
   * @param aIdentifier2
   *        Second identifier to compare. May not be null.
   * @return <code>true</code> if the identifiers are equal, <code>false</code>
   *         otherwise.
   */
  public static boolean areIdentifiersEqual (@Nonnull final ParticipantIdentifierType aIdentifier1,
                                             @Nonnull final ParticipantIdentifierType aIdentifier2) {
    ValueEnforcer.notNull (aIdentifier1, "ParticipantIdentifier1");
    ValueEnforcer.notNull (aIdentifier2, "ParticipantIdentifier2");

    // Identifiers are equal, if both scheme and value match case insensitive!
    return EqualsUtils.nullSafeEqualsIgnoreCase (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.nullSafeEqualsIgnoreCase (aIdentifier1.getValue (), aIdentifier2.getValue ());
  }

  /**
   * According to the specification, two document identifiers are equal if their
   * parts are equal case sensitive.
   *
   * @param aIdentifier1
   *        First identifier to compare. May not be null.
   * @param aIdentifier2
   *        Second identifier to compare. May not be null.
   * @return <code>true</code> if the identifiers are equal, <code>false</code>
   *         otherwise.
   */
  public static boolean areIdentifiersEqual (@Nonnull final DocumentIdentifier aIdentifier1,
                                             @Nonnull final DocumentIdentifier aIdentifier2) {
    ValueEnforcer.notNull (aIdentifier1, "DocumentTypeIdentifier1");
    ValueEnforcer.notNull (aIdentifier2, "DocumentTypeIdentifier2");

    // Identifiers are equal, if both scheme and value match case sensitive!
    return EqualsUtils.equals (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.equals (aIdentifier1.getValue (), aIdentifier2.getValue ());
  }

  /**
   * According to the specification, two process identifiers are equal if their
   * parts are equal case sensitive.
   *
   * @param aIdentifier1
   *        First identifier to compare. May not be null.
   * @param aIdentifier2
   *        Second identifier to compare. May not be null.
   * @return <code>true</code> if the identifiers are equal, <code>false</code>
   *         otherwise.
   */
  public static boolean areIdentifiersEqual (@Nonnull final ProcessIdentifier aIdentifier1,
                                             @Nonnull final ProcessIdentifier aIdentifier2) {
    ValueEnforcer.notNull (aIdentifier1, "ProcessIdentifier1");
    ValueEnforcer.notNull (aIdentifier2, "ProcessIdentifier2");

    // Identifiers are equal, if both scheme and value match case sensitive!
    return EqualsUtils.equals (aIdentifier1.getScheme (), aIdentifier2.getScheme ()) &&
           EqualsUtils.equals (aIdentifier1.getValue (), aIdentifier2.getValue ());
  }

  /**
   * Get the identifier suitable for an URI but NOT percent encoded.
   *
   * @param aIdentifier
   *        The identifier to be encoded. May not be <code>null</code>.
   * @return The URI encoded participant identifier (scheme::value). Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public static String getIdentifierURIEncoded (@Nonnull final ParticipantIdentifierType aIdentifier) {
    ValueEnforcer.notNull (aIdentifier, "Identifier");

    final String sScheme = aIdentifier.getScheme ();
    if (StringHelper.hasNoText (sScheme))
      throw new IllegalArgumentException ("Passed identifier has an empty scheme: " + aIdentifier);

    final String sValue = aIdentifier.getValue ();
    if (sValue == null)
      throw new IllegalArgumentException ("Passed identifier has a null value: " + aIdentifier);

    // Combine scheme and value
    return sScheme + URL_SCHEME_VALUE_SEPARATOR + sValue;
  }

  @Nonnull
  @Nonempty
  public static String getIdentifierURIEncoded (@Nonnull final DocumentIdentifier aIdentifier) {
    ValueEnforcer.notNull (aIdentifier, "Identifier");

    final String sScheme = aIdentifier.getScheme ();
    if (StringHelper.hasNoText (sScheme))
      throw new IllegalArgumentException ("Passed identifier has an empty scheme: " + aIdentifier);

    final String sValue = aIdentifier.getValue ();
    if (sValue == null)
      throw new IllegalArgumentException ("Passed identifier has a null value: " + aIdentifier);

    // Combine scheme and value
    return sScheme + URL_SCHEME_VALUE_SEPARATOR + sValue;
  }

  /**
   * Get the identifier suitable for an URI and percent encoded.
   *
   * @param aIdentifier
   *        The identifier to be encoded. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static String getIdentifierURIPercentEncoded (@Nonnull final ParticipantIdentifierType aIdentifier) {
    final String sURIEncoded = getIdentifierURIEncoded (aIdentifier);
    return BusdoxURLUtils.createPercentEncodedURL (sURIEncoded);
  }

  @Nonnull
  public static String getIdentifierURIPercentEncoded (@Nonnull final DocumentIdentifier aIdentifier) {
    final String sURIEncoded = getIdentifierURIEncoded (aIdentifier);
    return BusdoxURLUtils.createPercentEncodedURL (sURIEncoded);
  }

  /**
   * Central method for unifying participant identifier values for storage in a
   * DB, as participant identifier values need to be handled case-insensitive.
   * This method can be applied both to participant identifier schemes and
   * business identifier values.
   *
   * @param sValue
   *        The DB identifier value to unify. May be <code>null</code>.
   * @return <code>null</code> if the passed value is <code>null</code>
   */
  @Nullable
  public static String getUnifiedParticipantDBValue (@Nullable final String sValue) {
    return sValue == null ? null : sValue.toLowerCase (Locale.US);
  }








}
