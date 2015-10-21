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
package eu.europa.ec.cipa.sml.server.security;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.collections.CollectionHelper;

/**
 * Extract certificate principal from HTTP request.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ClientUniqueIDProvider {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ClientUniqueIDProvider.class);

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final ClientUniqueIDProvider s_aInstance = new ClientUniqueIDProvider ();

  private ClientUniqueIDProvider () {}

  /**
   * Extract the client unique ID from the certificate.<br>
   * Note: this assumes that a single root certificate is present. Otherwise the
   * unique ID should also contain the subject DN and serial of the issuer!
   *
   * @param aHttpRequest
   *        The HTTP request to use.
   * @return <code>null</code> if some error occurred.
   */
  @Nullable
  public static String getClientUniqueID (@Nonnull final HttpServletRequest aHttpRequest) {
    final Object aValue = aHttpRequest.getAttribute ("javax.servlet.request.X509Certificate");
    if (aValue == null) {
      s_aLogger.warn ("No client certificates present in the request");
      return null;
    }
    if (!(aValue instanceof X509Certificate []))
      throw new IllegalStateException ("Request value is not of type X509Certificate[] but of " + aValue.getClass ());
    return getClientUniqueID ((X509Certificate []) aValue);
  }

  @Nullable
  public static String getClientUniqueID (@Nullable final X509Certificate [] aRequestCerts) {
    if (ArrayHelper.isEmpty (aRequestCerts)) {
      // Empty array
      return null;
    }

    // Find all certificates that are not issuer to another certificate
    final List <X509Certificate> aNonIssuerCertList = new ArrayList <X509Certificate> ();
    for (final X509Certificate aRequestCert : aRequestCerts) {
      final X500Principal aSubject = aRequestCert.getSubjectX500Principal ();

      // Search for the issuer of the current certificate
      boolean bFound = false;
      for (final X509Certificate aIssuerCert : aRequestCerts)
        if (aSubject.equals (aIssuerCert.getIssuerX500Principal ())) {
          bFound = true;
          break;
        }
      if (!bFound)
        aNonIssuerCertList.add (aRequestCert);
    }

    // Do we have exactly 1 certificate to verify?
    if (aNonIssuerCertList.size () != 1)
      throw new IllegalStateException ("Found " +
                                       aNonIssuerCertList.size () +
                                       " certificates that are not issuer certificates!");

    final X509Certificate aNonIssuerCert = CollectionHelper.getFirstElement (aNonIssuerCertList);
    return getClientUniqueID (aNonIssuerCert);
  }

  @Nullable
  static String getClientUniqueID (@Nullable final X509Certificate aCert) {
    try {
      // subject principal name must be in the order CN=XX,O=YY,C=ZZ
      // In some JDK versions it is O=YY,CN=XX,C=ZZ instead (e.g. 1.6.0_45)
      final LdapName aLdapName = new LdapName (aCert.getSubjectX500Principal ().getName ());

      // Make a map from type to name
      final Map <String, Rdn> aParts = new HashMap <String, Rdn> ();
      for (final Rdn aRdn : aLdapName.getRdns ())
        aParts.put (aRdn.getType (), aRdn);

      // Re-order - least important item comes first (=reverse order)!
      final String sSubjectName = new LdapName (CollectionHelper.newList (aParts.get ("C"),
                                                                          aParts.get ("O"),
                                                                          aParts.get ("CN"))).toString ();

      // subject-name + ":" + serial number hexstring
      return sSubjectName + ':' + aCert.getSerialNumber ().toString (16);
    }
    catch (final Exception ex) {
      s_aLogger.error ("Failed to parse '" + aCert.getSubjectX500Principal ().getName () + "'", ex);
      return null;
    }
  }
}
