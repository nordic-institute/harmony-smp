/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This class contains several identifier related utility methods.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
public final class IdentifierUtils {

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


  private static boolean stringEquals(String a, String b){
    return a==null ? b==null : a.equalsIgnoreCase(b);
  }




}
