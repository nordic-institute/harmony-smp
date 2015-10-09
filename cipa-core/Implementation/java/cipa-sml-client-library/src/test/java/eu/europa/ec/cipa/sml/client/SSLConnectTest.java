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
package eu.europa.ec.cipa.sml.client;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.random.VerySecureRandom;

import eu.europa.ec.cipa.peppol.security.DoNothingTrustManager;
import eu.europa.ec.cipa.peppol.security.HostnameVerifierAlwaysTrue;
import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;
import eu.europa.ec.cipa.sml.AbstractSMLClientTest;

/**
 * This class tests the URL connection to the SML that is secured with client
 * certificates.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class SSLConnectTest extends AbstractSMLClientTest {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SSLConnectTest.class);

  @Test
  public void testConnect () throws Exception {
    System.setProperty ("http.proxyHost", "172.30.9.12");
    System.setProperty ("http.proxyPort", "8080");
    System.setProperty ("https.proxyHost", "172.30.9.12");
    System.setProperty ("https.proxyPort", "8080");
    // System.setProperty ("https.protocols", "TLSv1");
    // System.setProperty ("https.protocols", "SSLv3");
    if (false)
      System.setProperty ("javax.net.debug", "ssl");

    // Load the client certificate
    final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore ("keys/sml_client_keystore.jks", "peppol");
    final KeyManagerFactory aKMF = KeyManagerFactory.getInstance ("SunX509");
    aKMF.init (aKeyStore, "peppol".toCharArray ());

    // Trust all
    final TrustManager [] aTrustMgrs = new TrustManager [] { new DoNothingTrustManager (false) };

    // SSL context
    final SSLContext aSSLContext = SSLContext.getInstance ("SSL");
    aSSLContext.init (aKMF.getKeyManagers (), aTrustMgrs, VerySecureRandom.getInstance ());

    // Configure and open connection
    final HttpsURLConnection aURLConn = (HttpsURLConnection) new URL ("https://sml.peppolcentral.org/").openConnection ();
    aURLConn.setSSLSocketFactory (aSSLContext.getSocketFactory ());
    aURLConn.setHostnameVerifier (new HostnameVerifierAlwaysTrue (true));
    aURLConn.setRequestMethod ("GET");

    // Debug status on URL connection
    if (true) {
      s_aLogger.info ("Status code:  " + aURLConn.getResponseCode ());
      s_aLogger.info ("Cipher suite: " + aURLConn.getCipherSuite ());
      s_aLogger.info ("Encoding:     " + aURLConn.getContentEncoding ());
      if (false) {
        int i = 0;
        for (final Certificate aCert : aURLConn.getServerCertificates ()) {
          s_aLogger.info (" Cert " + (++i) + ":");
          s_aLogger.info ("  Cert type:  " + aCert.getType ());
          s_aLogger.info ("  Hash code:  " + aCert.hashCode ());
          s_aLogger.info ("  Algorithm:  " + aCert.getPublicKey ().getAlgorithm ());
          s_aLogger.info ("  Format:     " + aCert.getPublicKey ().getFormat ());
          if (aCert instanceof X509Certificate) {
            final X509Certificate aX509 = (X509Certificate) aCert;
            s_aLogger.info ("   Principal: " + aX509.getIssuerX500Principal ());
            s_aLogger.info ("   Subject:   " + aX509.getSubjectX500Principal ());
          }
        }
      }
    }

    try {
      // Show success
      final String sResult = StreamUtils.getAllBytesAsString (aURLConn.getInputStream (), CCharset.CHARSET_UTF_8_OBJ);
      s_aLogger.info ("\n" + sResult);
    }
    catch (final IOException ex) {
      // Show error
      final String sError = StreamUtils.getAllBytesAsString (aURLConn.getErrorStream (), CCharset.CHARSET_UTF_8_OBJ);
      s_aLogger.info ("\n" + sError);
    }
  }
}