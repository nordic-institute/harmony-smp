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
package eu.europa.ec.cipa.smp.client;

import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;
import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Finds and returns a key using the data contained in a {@link KeyInfo} object
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 * @see <a
 *      href="http://java.sun.com/developer/technicalArticles/xml/dig_signature_api/">Programming
 *      with the Java XML Digital Signature API</a>
 */
public final class X509KeySelector extends KeySelector {
  public static final class ConstantKeySelectorResult implements KeySelectorResult {
    private final Key m_aKey;

    public ConstantKeySelectorResult (@Nullable final Key aKey) {
      m_aKey = aKey;
    }

    @Nullable
    public Key getKey () {
      return m_aKey;
    }
  }

  private static ConfigFile s_aConfigFile;

  static {
       /* TODO : This is a quick and dirty hack to allow the use of a configuration file with an other name if it's
        in the classpath (like smp.config.properties or sml.config.properties).
        If the configuration file defined in applicationContext.xml couldn't be found, then the config.properties inside the war is used as a fallback.
        Needs to be properly refactored */
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
      s_aConfigFile = (ConfigFile) context.getBean("configFile");
  }

  private static final String TRUSTSTORE_LOCATION = s_aConfigFile.getString ("truststore.location",
                                                                             KeyStoreUtils.TRUSTSTORE_CLASSPATH);
  private static final String TRUSTSTORE_PASSWORD = s_aConfigFile.getString ("truststore.password",
                                                                             KeyStoreUtils.TRUSTSTORE_PASSWORD);

  public X509KeySelector () {}

  public static boolean algorithmEquals (@Nonnull final String sAlgURI, @Nonnull final String sAlgName) {
    return (sAlgName.equalsIgnoreCase ("DSA") && sAlgURI.equalsIgnoreCase (SignatureMethod.DSA_SHA1)) ||
           (sAlgName.equalsIgnoreCase ("RSA") && sAlgURI.equalsIgnoreCase (SignatureMethod.RSA_SHA1));
  }

  @Override
  public KeySelectorResult select (final KeyInfo aKeyInfo,
                                   final KeySelector.Purpose aPurpose,
                                   final AlgorithmMethod aMethod,
                                   final XMLCryptoContext aCryptoContext) throws KeySelectorException {

    final Iterator <?> ki = aKeyInfo.getContent ().iterator ();
    while (ki.hasNext ()) {
      final XMLStructure info = (XMLStructure) ki.next ();
      if (!(info instanceof X509Data))
        continue;

      final X509Data x509Data = (X509Data) info;
      final Iterator <?> xi = x509Data.getContent ().iterator ();
      while (xi.hasNext ()) {
        final Object o = xi.next ();
        if (!(o instanceof X509Certificate))
          continue;

        final X509Certificate certificate = (X509Certificate) o;
        try {
          // Check if the certificate is expired or active.
          certificate.checkValidity ();

          // Checks whether the certificate is in the trusted store.
          final X509Certificate [] certArray = new X509Certificate [] { certificate };

          final KeyStore ks = KeyStoreUtils.loadKeyStore (TRUSTSTORE_LOCATION, TRUSTSTORE_PASSWORD);
          // The PKIXParameters constructor may fail because:
          // - the trustAnchorsParameter is empty
          final PKIXParameters params = new PKIXParameters (ks);
          params.setRevocationEnabled (false);
          final CertificateFactory aCertificateFactory = CertificateFactory.getInstance ("X509");
          final CertPath aCertPath = aCertificateFactory.generateCertPath (Arrays.asList (certArray));
          final CertPathValidator aPathValidator = CertPathValidator.getInstance ("PKIX");
          aPathValidator.validate (aCertPath, params);

          final PublicKey aPublicKey = certificate.getPublicKey ();

          // Make sure the algorithm is compatible with the method.
          if (algorithmEquals (aMethod.getAlgorithm (), aPublicKey.getAlgorithm ()))
            return new ConstantKeySelectorResult (aPublicKey);
        }
        catch (final Throwable t) {
          throw new KeySelectorException ("Failed to select public key", t);
        }
      }
    }
    throw new KeySelectorException ("No public key found!");
  }
}
