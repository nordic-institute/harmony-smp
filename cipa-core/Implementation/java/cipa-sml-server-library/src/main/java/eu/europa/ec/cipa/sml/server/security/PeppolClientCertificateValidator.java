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

import java.security.cert.CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * Extract certificates from HTTP requests. These are the client certificates
 * submitted by the user.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class PeppolClientCertificateValidator {
  private static final String CONFIG_SML_CLIENT_CERTISSUER = "sml.client.certissuer";
  private static final String CONFIG_SML_CLIENT_CERTISSUER_NEW = "sml.client.certissuer.new";
  private static final Logger s_aLogger = LoggerFactory.getLogger (PeppolClientCertificateValidator.class);

  private PeppolClientCertificateValidator () {}

  /**
   * @param aCert
   *        The certificate to validate. May not be <code>null</code>.
   * @param aTrustedRootCert
   *        The trusted root certificate. E.g. the PEPPOL or the OpenPEPPOL SMP
   *        root certificate.
   * @param aCRLs
   *        A non-<code>null</code> list with revocation lists to handle
   * @param aDT
   *        The date and time which should be used for checking. May be
   *        <code>null</code> to indicate "now".
   * @return <code>null</code> in case of success!
   */
  @Nullable
  private static String _verifyCertificate (@Nonnull final X509Certificate aCert,
                                            @Nonnull final X509Certificate aTrustedRootCert,
                                            @Nonnull final Collection <CRL> aCRLs,
                                            @Nullable final Date aDT) {
    if (aCert.hasUnsupportedCriticalExtension ())
      return "Certificate has unsupported critical extension";

    // Verify the current certificate using the issuer certificate
    try {
      aCert.verify (aTrustedRootCert.getPublicKey ());
    }
    catch (final Exception ex) {
      return ex.getMessage ();
    }

    // Check timely validity (at a certain date/time or simply now)
    try {
      if (aDT != null)
        aCert.checkValidity (aDT);
      else
        aCert.checkValidity ();
    }
    catch (final Exception e) {
      return e.getMessage ();
    }

    // Check passed revocation lists
    if (aCRLs != null)
      for (final CRL aCRL : aCRLs)
        if (aCRL.isRevoked (aCert))
          return "Certificate is revoked according to " + aCRL;

    // null means OK :)
    return null;
  }

  /**
   * Extract certificates from request and validate them.
   * 
   * @param aHttpRequest
   *        The HTTP request to use.
   * @return <code>true</code> if valid, <code>false</code> otherwise.
   */
  public static boolean isClientCertificateValid (@Nonnull final HttpServletRequest aHttpRequest) {
    // This is how to get client certificate from request
    final Object aValue = aHttpRequest.getAttribute ("javax.servlet.request.X509Certificate");
    if (aValue == null) {
      s_aLogger.warn ("No client certificates present in the request");
      return false;
    }

    // type check
    if (!(aValue instanceof X509Certificate []))
      throw new IllegalStateException ("Request value is not of type X509Certificate[] but of " + aValue.getClass ());

    // Main checking
    return isClientCertificateValid ((X509Certificate []) aValue);
  }

  public static boolean isClientCertificateValid (@Nullable final X509Certificate [] aRequestCerts) {
    if (ArrayHelper.isEmpty (aRequestCerts)) {
      // Empty array
      s_aLogger.warn ("No client certificates passed for validation");
      return false;
    }

    // TODO: determine CRLs
    final Collection <CRL> aCRLs = new ArrayList <CRL> ();
    final Date aNow = new Date ();

    // final CertificateFactory certFactory = CertificateFactory.getInstance
    // ("X.509");
    // final URL crlURL = new URL (crlURLString);
    // final InputStream crlStream = crlURL.openStream ();
    // final X509CRL crl = (X509CRL) certFactory.generateCRL (crlStream);
    // crl.verify (issuerCertificate.getPublicKey ());
    // final boolean revoked = crl.isRevoked (certificate);

    // OK, we have a non-empty, type checked Certificate array

    // Build list of principals to search - this is assumed to be more safe than
    // to simply search by name!
    final List <X500Principal> aSearchIssuers = new ArrayList <X500Principal> ();
    {
      // Find the certificate that is issued by
      final String sIssuerToSearch = ConfigFile.getInstance ().getString (CONFIG_SML_CLIENT_CERTISSUER);
      if (StringHelper.hasNoText (sIssuerToSearch))
        throw new IllegalStateException ("The configuration file is missing the entry '" +
                                         CONFIG_SML_CLIENT_CERTISSUER +
                                         "'");
      // Throws a runtime exception on syntax error anyway :)
      aSearchIssuers.add (new X500Principal (sIssuerToSearch));

      // Alternative (optional)
      final String sAlternativeIssuerToSearch = ConfigFile.getInstance ().getString (CONFIG_SML_CLIENT_CERTISSUER_NEW);
      if (StringHelper.hasText (sAlternativeIssuerToSearch)) {
        // Throws a runtime exception on syntax error anyway :)
        aSearchIssuers.add (new X500Principal (sAlternativeIssuerToSearch));
      }

      s_aLogger.info ("Searching for the following certificate issuer(s): " + aSearchIssuers);
    }

    X509Certificate aCertToVerify = null;
    {
      for (final X509Certificate aCert : aRequestCerts) {
        final X500Principal aIssuer = aCert.getIssuerX500Principal ();
        s_aLogger.info ("  Found the following certificate issuer: '" + aIssuer + "'");
        if (aSearchIssuers.contains (aIssuer)) {
          s_aLogger.info ("    and using it!");
          aCertToVerify = aCert;
          break;
        }
      }
      // Do we have a certificate to verify?
      if (aCertToVerify == null)
        throw new IllegalStateException ("Found no certificate that was issued by the specified issuers.");
    }

    // This is the main verification process against the PEPPOL SMP root
    // certificate
    final X509Certificate aPeppolRootCert = PeppolRootCertificateProvider.getPeppolSMPRootCertificate ();
    final String sPeppolVerifyMsg = _verifyCertificate (aCertToVerify, aPeppolRootCert, aCRLs, aNow);
    if (sPeppolVerifyMsg == null) {
      // Passed certificate is a PEPPOL certificate
      s_aLogger.info ("  Passed certificate is a PEPPOL certificate");
      return true;
    }

    // This is the main verification process against the OpenPEPPOL SMP root
    // certificate
    final X509Certificate aOpenPeppolRootCert = PeppolRootCertificateProvider.getOpenPeppolSMPRootCertificate ();
    final String sOpenPeppolVerifyMsg = _verifyCertificate (aCertToVerify, aOpenPeppolRootCert, aCRLs, aNow);
    if (sOpenPeppolVerifyMsg == null) {
      // Passed certificate is an OpenPEPPOL certificate
      s_aLogger.info ("  Passed certificate is an OpenPEPPOL certificate");
      return true;
    }

    s_aLogger.warn ("Client certificate is not a PEPPOL certificate: " +
                    sPeppolVerifyMsg +
                    "; root certificate serial=" +
                    aPeppolRootCert.getSerialNumber ().toString (16) +
                    "; root certficate issuer=" +
                    aPeppolRootCert.getIssuerX500Principal ().getName ());
    s_aLogger.warn ("Client certificate is also not an OpenPEPPOL certificate: " +
                    sOpenPeppolVerifyMsg +
                    "; root certificate serial=" +
                    aOpenPeppolRootCert.getSerialNumber ().toString (16) +
                    "; root certficate issuer=" +
                    aOpenPeppolRootCert.getIssuerX500Principal ().getName ());
    return false;
  }
}
