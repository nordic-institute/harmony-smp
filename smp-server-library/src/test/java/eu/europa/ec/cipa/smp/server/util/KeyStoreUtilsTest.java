/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.cipa.smp.server.security.KeyStoreUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for class {@link KeyStoreUtils}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@SuppressWarnings ("deprecation")
public final class KeyStoreUtilsTest {
  @BeforeClass
  public static void init () {
    Security.addProvider (new BouncyCastleProvider ());
  }

  public KeyPair createKeyPair (final int nKeySizeInBits) throws Exception {
    final KeyPairGenerator aGenerator = KeyPairGenerator.getInstance ("RSA");
    aGenerator.initialize (nKeySizeInBits);
    final KeyPair keyPair = aGenerator.generateKeyPair ();
    return keyPair;
  }

  public static X509Certificate createX509V1Certificate (final KeyPair aKeyPair) throws Exception {
    // generate the certificate
    final X509V1CertificateGenerator certGen = new X509V1CertificateGenerator ();
    certGen.setSerialNumber (BigInteger.valueOf (System.currentTimeMillis ()));
    certGen.setIssuerDN (new X500Principal ("CN=Test Certificate"));
    certGen.setNotBefore (new Date (System.currentTimeMillis () - 50000));
    certGen.setNotAfter (new Date (System.currentTimeMillis () + 50000));
    certGen.setSubjectDN (new X500Principal ("CN=Test Certificate"));
    certGen.setPublicKey (aKeyPair.getPublic ());
    certGen.setSignatureAlgorithm ("SHA256WithRSAEncryption");
    return certGen.generate (aKeyPair.getPrivate (), "BC");
  }

  @Test
  public void testAll () throws Exception {
    final KeyPair aKeyPair = createKeyPair (1024);
    final Certificate [] certs = { createX509V1Certificate (aKeyPair), createX509V1Certificate (aKeyPair) };

    KeyStore ks = KeyStoreUtils.loadKeyStore ("keystores/keystore-no-pw.jks", (String) null);
    assertEquals (KeyStoreUtils.KEYSTORE_TYPE_JKS, ks.getType ());
    assertEquals (1, newList (ks.aliases ()).size ());
    assertTrue (ks.containsAlias ("1"));
    final Certificate c1 = ks.getCertificate ("1");
    assertNotNull (c1);
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    ks = KeyStoreUtils.loadKeyStore ("keystores/keystore-pw-peppol.jks", (String) null);
    assertEquals (1, newList (ks.aliases ()).size ());
    assertTrue (ks.containsAlias ("1"));
    final Certificate c2 = ks.getCertificate ("1");
    assertNotNull (c2);
    assertEquals (c1, c2);
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    ks = KeyStoreUtils.loadKeyStore ("keystores/keystore-pw-peppol.jks", "peppol");
    assertEquals (1, newList (ks.aliases ()).size ());
    assertTrue (ks.containsAlias ("1"));
    final Certificate c3 = ks.getCertificate ("1");
    assertNotNull (c3);
    assertEquals (c2, c3);
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    try {
      // Non-existing file
      KeyStoreUtils.loadKeyStore ("keystores/keystore-not-existing.jks", (String) null);
      fail ();
    }
    catch (final IllegalArgumentException ex) {}

    try {
      // Invalid password
      KeyStoreUtils.loadKeyStore ("keystores/keystore-pw-peppol.jks", "wrongpw");
      fail ();
    }
    catch (final IOException ex) {}
  }

  @Nonnull
  public static <ELEMENTTYPE> List<ELEMENTTYPE> newList (@Nullable final Enumeration<? extends ELEMENTTYPE> aEnum)
  {
    final List <ELEMENTTYPE> ret = new ArrayList<ELEMENTTYPE>();
    if (aEnum != null)
      while (aEnum.hasMoreElements ())
        ret.add (aEnum.nextElement ());
    return ret;
  }

  @Test
  public void testLoadTrustStore () throws Exception {
    // Load trust store
    final KeyStore aTrustStore = KeyStoreUtils.loadKeyStore (KeyStoreUtils.TRUSTSTORE_CLASSPATH,"peppol");
    assertNotNull (aTrustStore);

    // Ensure all name entries are contained
    assertNotNull (aTrustStore.getCertificate (KeyStoreUtils.TRUSTSTORE_ALIAS_AP_OPENPEPPOL));
    assertNotNull (aTrustStore.getCertificate (KeyStoreUtils.TRUSTSTORE_ALIAS_SMP_OPENPEPPOL));

    // System.out.println (SystemProperties.getJavaVersion ());
    final X509Certificate aCertAPOld = (X509Certificate) aTrustStore.getCertificate (KeyStoreUtils.TRUSTSTORE_ALIAS_AP_OPENPEPPOL);
    final String sIssuerName = aCertAPOld.getIssuerX500Principal ().getName ();
    assertEquals ("CN=PEPPOL Root CA,O=NATIONAL IT AND TELECOM AGENCY,C=DK", sIssuerName);
    final String sSubjectName = aCertAPOld.getSubjectX500Principal ().getName ();
    assertEquals ("CN=PEPPOL ACCESS POINT CA,O=NATIONAL IT AND TELECOM AGENCY,C=DK", sSubjectName);
  }
}
