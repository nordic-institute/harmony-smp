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
package eu.europa.ec.cipa.peppol.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.io.streams.StreamUtils;

/**
 * Helper methods to access Java key stores of type JKS (Java KeyStore).
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class KeyStoreUtils {
  /** The classpath entry referencing the global truststore */
  public static final String TRUSTSTORE_CLASSPATH = "truststore/global-truststore.jks";
  /** The password used to access the truststore */
  public static final String TRUSTSTORE_PASSWORD = "peppol";
  /** The truststore alias for the AP certificate */
  public static final String TRUSTSTORE_ALIAS_AP = "peppol access point test ca (peppol root test ca)";
  /** The truststore alias for the SMP certificate */
  public static final String TRUSTSTORE_ALIAS_SMP = "peppol service metadata publisher test ca (peppol root test ca)";
  @Deprecated
  public static final String CLASSPATH_TRUSTSTORE = TRUSTSTORE_CLASSPATH;
  public static final String KEYSTORE_TYPE_JKS = "JKS";

  @PresentForCodeCoverage
  private static final KeyStoreUtils s_aInstance = new KeyStoreUtils ();

  private KeyStoreUtils () {}

  /**
   * Load a key store from a resource.
   * 
   * @param sKeyStorePath
   *        The path pointing to the key store. May not be <code>null</code>.
   * @param sKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no
   *        password is required.
   * @return The Java key-store object.
   * @see KeyStore#load(InputStream, char[])
   */
  @Nonnull
  public static KeyStore loadKeyStore (@Nonnull final String sKeyStorePath, @Nullable final String sKeyStorePassword) throws NoSuchAlgorithmException,
                                                                                                                     CertificateException,
                                                                                                                     IOException {
    return loadKeyStore (sKeyStorePath, sKeyStorePassword == null ? null : sKeyStorePassword.toCharArray ());
  }

  /**
   * Load a key store from a resource.
   * 
   * @param sKeyStorePath
   *        The path pointing to the key store. May not be <code>null</code>.
   * @param aKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no
   *        password is required.
   * @return The Java key-store object.
   * @see KeyStore#load(InputStream, char[])
   */
  @Nonnull
  public static KeyStore loadKeyStore (@Nonnull final String sKeyStorePath, @Nullable final char [] aKeyStorePassword) throws NoSuchAlgorithmException,
                                                                                                                      CertificateException,
                                                                                                                      IOException {
    // Open the resource stream
    InputStream aIS = ClassPathResource.getInputStream (sKeyStorePath);
    if (aIS == null) {
      // Fallback to file system - maybe this helps...
      aIS = new FileSystemResource (sKeyStorePath).getInputStream ();
    }
    if (aIS == null)
      throw new IllegalArgumentException ("Failed to open key store '" + sKeyStorePath + "'");

    try {
      final KeyStore aKeyStore = KeyStore.getInstance (KEYSTORE_TYPE_JKS);
      aKeyStore.load (aIS, aKeyStorePassword);
      return aKeyStore;
    }
    catch (final KeyStoreException ex) {
      throw new IllegalStateException ("No provider can handle JKS key stores! Very weird!", ex);
    }
    finally {
      StreamUtils.close (aIS);
    }
  }

  /**
   * Load a key store from the class path.
   * 
   * @param sKeyStorePath
   *        The path to the key store in the class path. May not be
   *        <code>null</code>.
   * @param sKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no
   *        password is required.
   * @return The Java key-store object.
   */
  @Nonnull
  @Deprecated
  public static KeyStore loadKeyStoreFromClassPath (@Nonnull final String sKeyStorePath,
                                                    @Nullable final String sKeyStorePassword) throws NoSuchAlgorithmException,
                                                                                             CertificateException,
                                                                                             IOException {
    return loadKeyStore (sKeyStorePath, sKeyStorePassword);
  }

  /**
   * Load a key store from the file system.
   * 
   * @param sKeyStoreFile
   *        The path to the key store in the file system. May not be
   *        <code>null</code>.
   * @param sKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no
   *        password is required.
   * @return The Java key-store object.
   */
  @Nonnull
  @Deprecated
  public static KeyStore loadKeyStoreFromFile (@Nonnull final String sKeyStoreFile,
                                               @Nullable final String sKeyStorePassword) throws NoSuchAlgorithmException,
                                                                                        CertificateException,
                                                                                        IOException {
    return loadKeyStore (sKeyStoreFile, sKeyStorePassword);
  }

  /**
   * Create a new key store based on an existing key store
   * 
   * @param aBaseKeyStore
   *        The source key store. May not be <code>null</code>
   * @param sAliasToCopy
   *        The name of the alias in the source key store that should be put in
   *        the new key store
   * @param aAliasPassword
   *        The optional password to access the alias in the source key store.
   *        If it is not <code>null</code> the same password will be used in the
   *        created key store
   * @return The created in-memory key store
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws UnrecoverableEntryException
   * @throws CertificateException
   * @throws IOException
   */
  @Nonnull
  public static KeyStore createKeyStoreWithOnlyOneItem (@Nonnull final KeyStore aBaseKeyStore,
                                                        @Nonnull final String sAliasToCopy,
                                                        @Nullable final char [] aAliasPassword) throws KeyStoreException,
                                                                                               NoSuchAlgorithmException,
                                                                                               UnrecoverableEntryException,
                                                                                               CertificateException,
                                                                                               IOException {
    final KeyStore aKeyStore = KeyStore.getInstance (aBaseKeyStore.getType (), aBaseKeyStore.getProvider ());
    // null stream means: create new key store
    aKeyStore.load (null, null);

    // Do we need a password?
    ProtectionParameter aPP = null;
    if (aAliasPassword != null)
      aPP = new PasswordProtection (aAliasPassword);

    aKeyStore.setEntry (sAliasToCopy, aBaseKeyStore.getEntry (sAliasToCopy, aPP), aPP);
    return aKeyStore;
  }
}
