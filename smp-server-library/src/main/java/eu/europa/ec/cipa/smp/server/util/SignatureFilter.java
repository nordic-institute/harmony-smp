/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.cipa.smp.server.security.KeyStoreUtils;
import eu.europa.ec.cipa.smp.server.security.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * This class adds a XML DSIG to successful GET's for SignedServiceMetadata
 * objects.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Deprecated // TODO: This is no longer a filter, refactor this garbage.
@Component
public final class SignatureFilter{
  public static final String CONFIG_XMLDSIG_KEYSTORE_CLASSPATH = "xmldsig.keystore.classpath";
  public static final String CONFIG_XMLDSIG_KEYSTORE_PASSWORD = "xmldsig.keystore.password";
  public static final String CONFIG_XMLDSIG_KEYSTORE_KEY_ALIAS = "xmldsig.keystore.key.alias";
  public static final String CONFIG_XMLDSIG_KEYSTORE_KEY_PASSWORD = "xmldsig.keystore.key.password";

  private static final Logger s_aLogger = LoggerFactory.getLogger (SignatureFilter.class);

  private KeyStore.PrivateKeyEntry m_aKeyEntry;
  private X509Certificate m_aCert;
  private Signer signer;

  @Autowired
  public SignatureFilter (ConfigFile configFile) {
    // Load the KeyStore and get the signing key and certificate.
    try {
      final String sKeyStoreClassPath = configFile.getString (CONFIG_XMLDSIG_KEYSTORE_CLASSPATH);
      final String sKeyStorePassword = configFile.getString (CONFIG_XMLDSIG_KEYSTORE_PASSWORD);
      final String sKeyStoreKeyAlias = configFile.getString (CONFIG_XMLDSIG_KEYSTORE_KEY_ALIAS);
      final char [] aKeyStoreKeyPassword = configFile.getCharArray (CONFIG_XMLDSIG_KEYSTORE_KEY_PASSWORD);

      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (sKeyStoreClassPath, sKeyStorePassword);
      final KeyStore.Entry aEntry = aKeyStore.getEntry (sKeyStoreKeyAlias,
                                                        new KeyStore.PasswordProtection (aKeyStoreKeyPassword));
      if (aEntry == null) {
        // Alias not found
        throw new IllegalStateException ("Failed to find key store alias '" +
                                         sKeyStoreKeyAlias +
                                         "' in keystore '" +
                                         sKeyStorePassword +
                                         "'. Does the alias exist? Is the password correct?");
      }
      if (!(aEntry instanceof KeyStore.PrivateKeyEntry)) {
        // Not a private key
        throw new IllegalStateException ("The keystore alias '" +
                                         sKeyStoreKeyAlias +
                                         "' was found in keystore '" +
                                         sKeyStorePassword +
                                         "' but it is not a private key! The internal type is " +
                                         aEntry.getClass ().getName ());
      }
      m_aKeyEntry = (KeyStore.PrivateKeyEntry) aEntry;
      m_aCert = (X509Certificate) m_aKeyEntry.getCertificate ();
      s_aLogger.info ("Signature filter initialized with keystore '" +
                      sKeyStoreClassPath +
                      "' and alias '" +
                      sKeyStoreKeyAlias +
                      "'");

      signer = new Signer(m_aKeyEntry.getPrivateKey(),m_aCert);
/*
      if (false) {
        // Enable XMLDsig debugging
        java.util.logging.LogManager.getLogManager ()
                                    .readConfiguration (new StringInputStream ("handlers=java.util.logging.ConsoleHandler\r\n"
                                                                                   + ".level=FINEST\r\n"
                                                                                   + "java.util.logging.ConsoleHandler.level=FINEST\r\n"
                                                                                   + "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter",
                                                                               CCharset.CHARSET_ISO_8859_1_OBJ));
        java.util.logging.Logger.getLogger ("org.jcp.xml.dsig.internal.level").setLevel (java.util.logging.Level.FINER);
        java.util.logging.Logger.getLogger ("org.apache.xml.internal.security.level")
                                .setLevel (java.util.logging.Level.FINER);
        java.util.logging.Logger.getLogger ("com.sun.org.apache.xml.internal.security.level")
                                .setLevel (java.util.logging.Level.FINER);
      }
      */
    }
    catch (final Throwable t) {
      s_aLogger.error ("Error in constructor of SignatureFilter", t);
      throw new IllegalStateException ("Error in constructor of SignatureFilter", t);
    }
  }

  public void sign(Document serviceMetadataDoc){
    try {
      signer.signXML(serviceMetadataDoc.getDocumentElement());
    } catch (Exception e) {
      throw new RuntimeException("Could not sign serviceMetadata response", e);
    }
  }

}