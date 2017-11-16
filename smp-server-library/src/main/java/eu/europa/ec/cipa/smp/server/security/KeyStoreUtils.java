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
package eu.europa.ec.cipa.smp.server.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.Security;

/**
 * Helper methods to access Java key stores of type JKS (Java KeyStore).
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
public final class KeyStoreUtils {
  /** The classpath entry referencing the global truststore with all entries */
  public static final String TRUSTSTORE_CLASSPATH = "truststore/global-truststore.jks";

  /** The classpath entry referencing the global OpenPEPPOL truststore */
  public static final String TRUSTSTORE_CLASSPATH_OPENPEPPOL = "truststore/global-truststore-openpeppol.jks";

  /** The truststore alias for the OpenPEPPOL root certificate */
  public static final String TRUSTSTORE_ALIAS_ROOT_OPENPEPPOL = "peppol root ca";

  /** The truststore alias for the OpenPEPPOL AP certificate */
  public static final String TRUSTSTORE_ALIAS_AP_OPENPEPPOL = "peppol access point ca (peppol root ca)";

  /** The truststore alias for the OpenPEPPOL SMP certificate */
  public static final String TRUSTSTORE_ALIAS_SMP_OPENPEPPOL = "peppol service metadata publisher ca (peppol root ca)";

  public static final String KEYSTORE_TYPE_JKS = "JKS";
  public static final String KEYSTORE_TYPE_JCEKS = "JCEKS";
  public static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";

  private static final String [] keystoreTypes = { KEYSTORE_TYPE_JKS, KEYSTORE_TYPE_PKCS12, KEYSTORE_TYPE_JCEKS };


  private static final KeyStoreUtils s_aInstance = new KeyStoreUtils ();

  private KeyStoreUtils() {}

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
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   */
  @Nonnull
  public static KeyStore loadKeyStore (@Nonnull final String sKeyStorePath, @Nullable final String sKeyStorePassword) throws GeneralSecurityException,
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
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   */
  @Nonnull
  public static KeyStore loadKeyStore (@Nonnull final String sKeyStorePath, @Nullable final char [] aKeyStorePassword) throws GeneralSecurityException,
                                                                                                                      IOException {

    Security.addProvider (new BouncyCastleProvider ());

    // Open the resource stream
    //InputStream aIS = ClassPathResource.getInputStream (sKeyStorePath);
    InputStream aIS = KeyStoreUtils.class.getResourceAsStream("/"+sKeyStorePath);

    if (aIS == null) {
      // Fallback to file system - maybe this helps...
      //aIS = new FileSystemResource (sKeyStorePath).getInputStream ();
      try {
        aIS = new FileInputStream(sKeyStorePath);
      } catch(Exception e){
        throw new IllegalArgumentException ("Failed to open key store '" + sKeyStorePath + "'", e);
      }finally {
        if(aIS != null){
          aIS.close();
        }
      }
    }

    try {
      KeyStore aKeyStore = null;
      for (final String keystoreType : keystoreTypes) {
        if (keystoreType.equals ("PKCS12") && aKeyStorePassword != null) {
          // BouncyCastle implementation of a keystore allows for the PKCS12
          // keystore to accept trusted certificates. The default Java provider
          // doesn't.
          aKeyStore = KeyStore.getInstance (keystoreType, BouncyCastleProvider.PROVIDER_NAME);
        }
        else {
          // but BouncyCastle doesn't have an implementation for jks! so we try
          // with the default implementation
          aKeyStore = KeyStore.getInstance (keystoreType);
        }
        try {
          aKeyStore.load (aIS, aKeyStorePassword);
          return aKeyStore;
        }finally {
          //if(aIS != null){
              aIS.close();
          //}
        }
        /*
        catch (final IOException e) {
          StreamUtils.close (aIS);
          aIS = ClassPathResource.getInputStream (sKeyStorePath);
          if (aIS == null) {
            // Fallback to file system - maybe this helps...
            aIS = new FileSystemResource (sKeyStorePath).getInputStream ();
          }
        }*/

        // } catch (final KeyStoreException ex) {
        // throw new
        // IllegalStateException("No provider can handle JKS key stores! Very weird!",
        // ex);
        // }
      }
      throw new IllegalStateException ("No provider can handle JKS key stores! Very weird!");
    }
    finally {
      if(aIS != null){
        aIS.close();
      }
      //StreamUtils.close (aIS);
    }

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
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   */
  @Nonnull
  public static KeyStore createKeyStoreWithOnlyOneItem (@Nonnull final KeyStore aBaseKeyStore,
                                                        @Nonnull final String sAliasToCopy,
                                                        @Nullable final char [] aAliasPassword) throws GeneralSecurityException,
                                                                                               IOException {
    /*ValueEnforcer.notNull (aBaseKeyStore, "BaseKeyStore");
    ValueEnforcer.notNull (sAliasToCopy, "AliasToCopy");*/

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
