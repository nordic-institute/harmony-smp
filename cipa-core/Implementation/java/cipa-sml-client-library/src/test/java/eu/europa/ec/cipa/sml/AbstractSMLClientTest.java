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
package eu.europa.ec.cipa.sml;

import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.junit.BeforeClass;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.random.VerySecureRandom;

import eu.europa.ec.cipa.peppol.security.DoNothingTrustManager;
import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;

/**
 * This class tests the URL connection to the SML that is secured with client
 * certificates.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@DevelopersNote ("You need to create a keystore file !")
public abstract class AbstractSMLClientTest {
  public static final ISMLInfo SML_INFO = ESML.DEVELOPMENT_LOCAL;

  private static final String KEYSTORE_PATH = "keys/sml_client_keystore.jks";
  private static final String KEYSTORE_PASSWORD = "peppol";

  @BeforeClass
  public static final void initSSL () throws Exception {
    initSSL (SML_INFO);
  }

  public static final void initSSL (final ISMLInfo aSMLInfo) throws Exception {
    if (aSMLInfo.requiresClientCertificate ()) {
      if (false) {
        // initialize debug properties
        System.setProperty ("javax.net.debug", "SSL,handshake");
        System.setProperty ("java.security.debug", "all");
      }

      // Main key storage
      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (KEYSTORE_PATH, KEYSTORE_PASSWORD);

      // Key manager
      final KeyManagerFactory aKeyManagerFactory = KeyManagerFactory.getInstance ("SunX509");
      aKeyManagerFactory.init (aKeyStore, KEYSTORE_PASSWORD.toCharArray ());

      // Trust manager

      // Assign key manager and empty trust manager to SSL context
      final SSLContext aSSLCtx = SSLContext.getInstance ("TLS");
      aSSLCtx.init (aKeyManagerFactory.getKeyManagers (),
                    new TrustManager [] { new DoNothingTrustManager () },
                    VerySecureRandom.getInstance ());
      HttpsURLConnection.setDefaultSSLSocketFactory (aSSLCtx.getSocketFactory ());
    }
  }
}
