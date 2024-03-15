/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import javax.net.ssl.KeyManager;
import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {UIKeystoreService.class, ConversionTestConfig.class,
        ConfigurationService.class})
public class UIKeystoreServiceTest extends AbstractServiceIntegrationTest {

    public static final String S_ALIAS = "single_domain_key";


    public static final X500Principal CERT_SUBJECT_X500PRINCIPAL = new X500Principal("CN=SMP Mock Services, OU=DIGIT, O=European Commision, C=BE");

    @Autowired
    protected UIKeystoreService testInstance;

    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);


    @BeforeEach
    public void setup() throws IOException {
        // restore keystore
        resetKeystore();
        ReflectionTestUtils.setField(testInstance, "configurationService", configurationService);

        // set keystore properties
        File keystoreFile = new File(targetDirectory.toFile(), "smp-keystore.jks");
        Mockito.doReturn(keystoreFile).when(configurationService).getKeystoreFile();
        Mockito.doReturn(targetDirectory.toFile()).when(configurationService).getSecurityFolder();
        Mockito.doReturn("test123").when(configurationService).getKeystoreCredentialToken();
        testInstance.refreshData();
    }

    @Test
    void testGetKeystoreEntriesList() {
        List<CertificateRO> lst = testInstance.getKeystoreEntriesList();
        assertEquals(1, lst.size());
        assertEquals(S_ALIAS, lst.get(0).getAlias());
    }

    @Test
    void testGetSingleKey() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        Key key = testInstance.getKey(S_ALIAS);
        // then
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void testGetSingleCertificate() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        X509Certificate certificate = testInstance.getCert(S_ALIAS);
        // then
        assertNotNull(certificate);
        assertEquals(CERT_SUBJECT_X500PRINCIPAL, certificate.getSubjectX500Principal());
    }

    @Test
    void testGetSingleKeyNullAlias() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        Key key = testInstance.getKey(null);
        // then
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void testGetSingleCertificateNullAlias() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        X509Certificate certificate = testInstance.getCert(null);
        // then
        assertNotNull(certificate);

        assertEquals(CERT_SUBJECT_X500PRINCIPAL, certificate.getSubjectX500Principal());
    }

    @Test
    void testGetKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given when
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        Key key = testInstance.getKey(S_ALIAS);
        // then
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void testGetCertificate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given when
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        X509Certificate certificate = testInstance.getCert(S_ALIAS);
        // then
        assertNotNull(certificate);
        assertEquals(CERT_SUBJECT_X500PRINCIPAL, certificate.getSubjectX500Principal());
    }

    @Test
    void testImportPCKSKeystore() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        KeyStore keystore = loadKeystore("test-import.p12", "NewPassword1234", "PKCS12");
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        // when
        testInstance.importKeys(keystore, "NewPassword1234");
        // then
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
    }

    @Test
    @Disabled("This test is not working on gitlab")
    void testImportKeystoreTwice() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        // when
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        // then
        assertEquals(5, testInstance.getKeystoreEntriesList().size());
    }

    @Test
    void testImportJKSKeystore() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        KeyStore keystore = loadKeystore("test-import.jks", "NewPassword1234", "JKS");
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        // when
        testInstance.importKeys(keystore, "NewPassword1234");
        // then
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
    }


    @Test
    void testDeleteKeyTestCertificate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        assertNotNull(testInstance.getCert(S_ALIAS));
        assertNotNull(testInstance.getKey(S_ALIAS));
        testInstance.deleteKey(S_ALIAS);
        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.getCert(S_ALIAS));

        MatcherAssert.assertThat(result.getMessage(),
                CoreMatchers.containsString("Wrong configuration, missing key pair from keystore or wrong alias: " + S_ALIAS));
    }

    @Test
    void testDeleteKeyTestKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        assertNotNull(testInstance.getCert(S_ALIAS));
        assertNotNull(testInstance.getKey(S_ALIAS));
        testInstance.deleteKey(S_ALIAS);
        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.getKey(S_ALIAS));

        MatcherAssert.assertThat(result.getMessage(),
                CoreMatchers.containsString("Wrong configuration, missing key pair from keystore or wrong alias: " + S_ALIAS));
    }

    private KeyStore loadKeystore(String keystoreName, String password, String type) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        // Load the KeyStore and get the signing key and certificate.
        File keystoreFilePath = new File(resourceDirectory.toFile(), keystoreName);

        KeyStore keyStore;
        try (InputStream keystoreInputStream = Files.newInputStream(keystoreFilePath.toPath())) {
            keyStore = KeyStore.getInstance(type);
            keyStore.load(keystoreInputStream, password.toCharArray());
        }
        return keyStore;
    }

    @Test
    void testDetectKeystoreChangeForEntryList() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());

        // when
        resetKeystore();
        // then
        assertEquals(1, testInstance.getKeystoreEntriesList().size());

    }

    @Test
    void testDetectKeystoreChangeForKeyManagers() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        KeyManager km = testInstance.getKeyManagers()[0];
        testInstance.importKeys(loadKeystore("test-import.jks", "NewPassword1234", "JKS"), "NewPassword1234");

        // keymanager is updated
        assertNotEquals(km, testInstance.getKeyManagers()[0]);
        km = testInstance.getKeyManagers()[0];

        // when just changing the file
        resetKeystore();
        // then 
        assertNotEquals(km, testInstance.getKeyManagers()[0]);
    }

    @Test
    void testFindDuplicateCertificate() throws Exception {
        // given
        KeyStore keyStore = loadKeystore("test-import.jks", "NewPassword1234", "JKS");
        KeyStore sameKeyStore = keyStore;
        testInstance.importKeys(keyStore, "NewPassword1234");

        // when
        Set<String> duplicateCertificates = testInstance.findDuplicateCertificates(keyStore);

        // then
        assertEquals(new HashSet<>(Arrays.asList("testcertificatea", "testcertificateb")), duplicateCertificates);
    }
}
