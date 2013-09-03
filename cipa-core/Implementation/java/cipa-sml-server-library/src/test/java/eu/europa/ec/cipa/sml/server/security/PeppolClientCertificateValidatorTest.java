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

import static org.junit.Assert.assertTrue;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;

/**
 * Test class for class {@link PeppolClientCertificateValidator}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class PeppolClientCertificateValidatorTest {
  @Test
  public void testValidation () throws Exception {

    // Main key storage
    KeyStore aKeyStore = null;
    try {
      aKeyStore = KeyStoreUtils.loadKeyStore ("keys/sml2.jks", "peppol");
    }
    catch (final IllegalArgumentException ex) {
      // Keystore does not exist - skip test
      return;
    }

    // Extract all certificates from the key store
    final List <X509Certificate> aCerts = new ArrayList <X509Certificate> ();
    final Enumeration <String> aEnum = aKeyStore.aliases ();
    while (aEnum.hasMoreElements ()) {
      final String sAlias = aEnum.nextElement ();
      final Certificate aCert = aKeyStore.getCertificate (sAlias);
      if (aCert instanceof X509Certificate)
        aCerts.add ((X509Certificate) aCert);
    }

    // There must be a certificate
    assertTrue (!aCerts.isEmpty ());

    // Convert to array
    final X509Certificate [] aCertArray = aCerts.toArray (new X509Certificate [aCerts.size ()]);

    // And test
    assertTrue (PeppolClientCertificateValidator.isClientCertificateValid (aCertArray));
  }
}
