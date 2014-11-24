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
package eu.europa.ec.cipa.dispatcher.ocsp;

import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.state.EValidity;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;

public class OCSPValidator
{
  /**
   * Logger to follow this class behavior.
   */
  private static final Logger s_aLogger = LoggerFactory.getLogger (OCSPValidator.class);

  private static Properties properties = PropertiesUtil.getProperties (null);

  /**
   * This method validates the X.509 Certificate.
   *
   * @param aCert
   * @param sTrustStorePath
   * @return {@link EValidity}
   */
  @Nonnull
  public static boolean certificateValidate (final X509Certificate aCert)
  {

    final String trustStorePassword = properties.getProperty (PropertiesUtil.KEYSTORE_PASS);
    final String truststorePath = properties.getProperty (PropertiesUtil.KEYSTORE_PATH);

    try
    {
      // Load keystore
      final KeyStore trustStore = KeyStoreUtils.loadKeyStore (truststorePath, trustStorePassword);

      // Get certificate by alias;
      final String truststoreAlias = properties.getProperty (PropertiesUtil.KEYSTORE_AP_CA_ALIAS);
      final X509Certificate aRootCert = (X509Certificate) trustStore.getCertificate (truststoreAlias);

      if (aRootCert == null)
      {
        s_aLogger.error ("Failed to resolve trust store alias '" + truststoreAlias + "'");
      }
      else
      {
        // Get the responder URL from the configuration
        final String DEFAULT_RESPONDER_URL_NEW = "http://pki-ocsp.symauth.com:80";
        final String responderURL = properties.getProperty (PropertiesUtil.OCSP_RESPONDER_URL);
        if (StringHelper.hasNoText (responderURL))
        {
          // Error
          s_aLogger.error ("No OCSP responder URL configured (property '" + PropertiesUtil.OCSP_RESPONDER_URL + "')");
        }
        else
        {
          // Start the actual OCSP check
          boolean result = false;
          if (aRootCert != null)
            result = OCSPValidator.check (aCert, aRootCert, responderURL);
          return result;
        }
      }
    }
    catch (final Exception ex)
    {
      s_aLogger.error ("Error validating certificate in trust store '" + truststorePath + "'", ex);
    }

    return false;
  }

  /**
   * Compares a thing to another thing.
   *
   * @param aCertificate
   *        Certificate to check.
   * @param aTrustedCert
   *        Trusted Certificate.
   * @param sResponderUrl
   *        URL which responses.
   * @return {@link EValidity}
   */
  @Nonnull
  public static boolean check (@Nonnull final X509Certificate aCertificate,
                               @Nonnull final X509Certificate aTrustedCert,
                               final String sResponderUrl)
  {

    try
    {  	
      // Instantiate a CertificateFactory for X.509
      final CertificateFactory cf = CertificateFactory.getInstance ("X.509");

      // Extract the certification path from the List of Certificates
      final CertPath cp = cf.generateCertPath (ContainerHelper.newList (aCertificate));

      // Create CertPathValidator that implements the "PKIX" algorithm
      final CertPathValidator cpv = CertPathValidator.getInstance ("PKIX");

      // Set the Trust anchor
      final TrustAnchor aTrustAnchor = new TrustAnchor (aTrustedCert, null);

      // Set the PKIX parameters
      final PKIXParameters aParams = new PKIXParameters (ContainerHelper.newSet (aTrustAnchor));
      Security.setProperty("ocsp.enable", "true");
      aParams.setRevocationEnabled (true);

      // Validate and obtain results
      final PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate (cp, aParams);
      result.getPolicyTree ();
      result.getPublicKey ();
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Certificate " + aCertificate.getSerialNumber () + " is OCSP valid");
      return true;
    }
    catch (final NoSuchAlgorithmException e)
    {
      s_aLogger.error ("Internal error", e);
    }
    catch (final InvalidAlgorithmParameterException ex)
    {
      s_aLogger.error ("Internal error", ex);
    }
    catch (final CertificateException ex)
    {
      s_aLogger.error ("Certificate error", ex);
    }
    catch (final CertPathValidatorException cpve)
    {
      if (cpve.getCause () instanceof UnknownHostException)
      {
        // Happens when we're offline
        if (GlobalDebug.isDebugMode ())
        {
          s_aLogger.warn ("OCSP not checked, because we're offline. Since we're in debug mode this is OK...");
          return true;
        }
      }
      s_aLogger.error ("Validation failure, cert[" + cpve.getIndex () + "]: " + cpve.getMessage (), cpve);
    }
    return false;
  }
}
