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

import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.StreamUtils;

import eu.europa.ec.cipa.sml.AbstractSMLClientTest;

/**
 * This class tests the URL connection to the SML that is secured with client
 * certificates.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class SSLConnectTest extends AbstractSMLClientTest {
  private static final Logger log = LoggerFactory.getLogger (SSLConnectTest.class);

  @Test
  public void testConnect () throws Exception {
    final HttpsURLConnection uc = (HttpsURLConnection) new URL (SML_INFO.getManagementServiceURL () + "/index.jsp").openConnection ();
    uc.setRequestMethod ("GET");

    // Debug status on URL connection
    if (false) {
      log.info ("Status code:  " + uc.getResponseCode ());
      log.info ("Cipher suite: " + uc.getCipherSuite ());
      log.info ("Encoding:     " + uc.getContentEncoding ());
      int i = 0;
      for (final Certificate aCert : uc.getServerCertificates ()) {
        log.info (" Cert " + (++i) + ":");
        log.info ("  Cert type:  " + aCert.getType ());
        log.info ("  Hash code:  " + aCert.hashCode ());
        log.info ("  Algorithm:  " + aCert.getPublicKey ().getAlgorithm ());
        log.info ("  Format:     " + aCert.getPublicKey ().getFormat ());
        if (aCert instanceof X509Certificate) {
          final X509Certificate aX509 = (X509Certificate) aCert;
          log.info ("   Principal: " + aX509.getIssuerX500Principal ());
          log.info ("   Subject:   " + aX509.getSubjectX500Principal ());
        }
      }
    }
    final byte [] b = StreamUtils.getAllBytes (uc.getInputStream ());
    log.info ("\n" + new String (b, CCharset.CHARSET_UTF_8));
  }
}
