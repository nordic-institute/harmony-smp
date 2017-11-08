/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
    return sIdentifierValue1 == null ? sIdentifierValue2 == null : sIdentifierValue1.equalsIgnoreCase (sIdentifierValue2);
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

    if(aIdentifier1==null || aIdentifier2==null){
      throw new IllegalArgumentException("Null identifiers are not allowed");
    }
    // Identifiers are equal, if both scheme and value match case insensitive!
    return stringEquals(aIdentifier1.getScheme(), aIdentifier2.getScheme ()) &&
            stringEquals(aIdentifier1.getValue (), aIdentifier2.getValue ());

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

    if(aIdentifier1==null || aIdentifier2==null){
      throw new IllegalArgumentException("Null identifiers are not allowed");
    }

    // Identifiers are equal, if both scheme and value match case sensitive!
    return stringEquals(aIdentifier1.getScheme(), aIdentifier2.getScheme ()) &&
            stringEquals(aIdentifier1.getValue (), aIdentifier2.getValue ());

  }

  private static boolean stringEquals(String a, String b){
    return a==null ? b==null : a.equalsIgnoreCase(b);
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

    if(aIdentifier1==null || aIdentifier2==null){
      throw new IllegalArgumentException("Null identifiers are not allowed");
    }
    // Identifiers are equal, if both scheme and value match case sensitive!
    return stringEquals(aIdentifier1.getScheme(), aIdentifier2.getScheme ()) &&
            stringEquals(aIdentifier1.getValue (), aIdentifier2.getValue ());

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

  public static String getIdentifierURIEncoded (@Nonnull final ParticipantIdentifierType aIdentifier) {


    final String sScheme = aIdentifier.getScheme ();
    if (StringUtils.isBlank(sScheme))
      throw new IllegalArgumentException ("Passed identifier has an empty scheme: " + aIdentifier);

    final String sValue = aIdentifier.getValue ();
    if (sValue == null)
      throw new IllegalArgumentException ("Passed identifier has a null value: " + aIdentifier);

    // Combine scheme and value
    return sScheme + URL_SCHEME_VALUE_SEPARATOR + sValue;
  }

  @Nonnull

  public static String getIdentifierURIEncoded (@Nonnull final DocumentIdentifier aIdentifier) {


    final String sScheme = aIdentifier.getScheme ();
    if (StringUtils.isBlank(sScheme))
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
}
