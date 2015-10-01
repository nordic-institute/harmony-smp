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
package eu.europa.ec.cipa.peppol.utils;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.streams.StringInputStream;
import com.helger.commons.string.StringHelper;

/**
 * Some utility methods handling certificates.
 *
 * @author Philip Helger
 */
@Immutable
public final class CertificateUtils {
  public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
  public static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

  @PresentForCodeCoverage
  private static final CertificateUtils s_aInstance = new CertificateUtils ();

  private CertificateUtils () {}

  @Nonnull
  private static String _ensureBeginAndEndArePresent (@Nonnull final String sCertString) {
    String sRealCertString = sCertString;
    // Check without newline in case there are blanks between the string the
    // certificate
    if (!sRealCertString.startsWith (BEGIN_CERTIFICATE))
      sRealCertString = BEGIN_CERTIFICATE + "\n" + sRealCertString;
    if (!sRealCertString.trim ().endsWith (END_CERTIFICATE))
      sRealCertString += "\n" + END_CERTIFICATE;
    return sRealCertString;
  }

  /**
   * Convert the passed String to an X.509 certificate.
   *
   * @param sCertString
   *        The original text string. May be <code>null</code> or empty. The
   *        String must be ISO-8859-1 encoded for the binary certificate to be
   *        read!
   * @return <code>null</code> if the passed string is <code>null</code> or
   *         empty
   * @throws CertificateException
   *         In case the passed string cannot be converted to an X.509
   *         certificate.
   */
  @Nullable
  public static X509Certificate convertStringToCertficate (@Nullable final String sCertString) throws CertificateException {
    if (StringHelper.hasNoText (sCertString)) {
      // No string -> no certificate
      return null;
    }

    final CertificateFactory aCertificateFactory = CertificateFactory.getInstance ("X.509");

    // Convert certificate string to an object
    try {
      final String sRealCertString = _ensureBeginAndEndArePresent (sCertString);
      return (X509Certificate) aCertificateFactory.generateCertificate (new StringInputStream (sRealCertString,
                                                                                               CCharset.CHARSET_ISO_8859_1_OBJ));
    }
    catch (final CertificateException ex) {
      // In some weird configurations, the result string is a hex encoded
      // certificate instead of the string
      // -> Try to work around it
      String sHexDecodedString;
      try {
        sHexDecodedString = new String (StringHelper.getHexDecoded (sCertString), CCharset.CHARSET_ISO_8859_1_OBJ);
      }
      catch (final IllegalArgumentException ex2) {
        // Can happen, when the source string has an odd length (like 3 or 117).
        // In this case the original exception is rethrown
        throw ex;
      }
      final String sRealCertString = _ensureBeginAndEndArePresent (sHexDecodedString);
      return (X509Certificate) aCertificateFactory.generateCertificate (new StringInputStream (sRealCertString,
                                                                                               CCharset.CHARSET_ISO_8859_1_OBJ));
    }
  }
}