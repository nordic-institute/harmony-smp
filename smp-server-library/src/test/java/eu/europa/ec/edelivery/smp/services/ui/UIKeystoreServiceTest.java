package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.config.PropertiesKeystoreTestConfig;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.net.ssl.KeyManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UIKeystoreService.class, SecurityUtilsServices.class, ConversionTestConfig.class, PropertiesKeystoreTestConfig.class})
public class UIKeystoreServiceTest {

    public static final String S_ALIAS = "single_domain_key";
    Path resourceDirectory = Paths.get("src", "test", "resources",  "keystores");
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    protected UIKeystoreService testInstance;

    @Autowired
    protected PropertiesKeystoreTestConfig propertiesKeystoreTestConfig;

    @Before
    public void setup() throws IOException {
        propertiesKeystoreTestConfig.resetKeystore();
        testInstance.refreshData();
    }

    @Test
    public void testGetKeystoreEntriesList() {
        List<CertificateRO> lst = testInstance.getKeystoreEntriesList();
        assertEquals(1, lst.size());
        assertEquals(S_ALIAS, lst.get(0).getAlias());
    }

    @Test
    public void testGetSingleKey() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        Key key = testInstance.getKey(S_ALIAS);
        // then
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    public void testGetSingleCertificate() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        X509Certificate certificate = testInstance.getCert(S_ALIAS);
        // then
        assertNotNull(certificate);
        assertEquals("C=BE,O=European Commision,OU=DIGIT,CN=SMP Mock Services", certificate.getSubjectDN().getName());
    }

    @Test
    public void testGetSingleKeyNullAlias() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        Key key = testInstance.getKey(null);
        // then
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    public void testGetSingleCertificateNullAlias() {
        // given when
        assertEquals(1, testInstance.getKeystoreEntriesList().size());
        X509Certificate certificate = testInstance.getCert(null);
        // then
        assertNotNull(certificate);
        assertEquals("C=BE,O=European Commision,OU=DIGIT,CN=SMP Mock Services", certificate.getSubjectDN().getName());
    }

    @Test
    public void testGetKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given when
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        Key key = testInstance.getKey(S_ALIAS);
        // then
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    public void testGetCertificate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given when
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        X509Certificate certificate = testInstance.getCert(S_ALIAS);
        // then
        assertNotNull(certificate);
        assertEquals("C=BE,O=European Commision,OU=DIGIT,CN=SMP Mock Services", certificate.getSubjectDN().getName());
    }

    @Test
    public void testImportPCKSKeystore() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        KeyStore  keystore =  loadKeystore("test-import.p12","NewPassword1234", "PKCS12" );
        assertEquals(1,testInstance.getKeystoreEntriesList().size());
        // when
        testInstance.importKeys(keystore, "NewPassword1234");
        // then
        assertEquals(3,testInstance.getKeystoreEntriesList().size());
    }

    @Test
    public void testImportKeystoreTwice() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        assertEquals(3,testInstance.getKeystoreEntriesList().size());
        // when
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        // then
        assertEquals(5,testInstance.getKeystoreEntriesList().size());
    }

    @Test
    public void testImportJKSKeystore() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        KeyStore  keystore =  loadKeystore("test-import.jks","NewPassword1234", "JKS" );
        assertEquals(1,testInstance.getKeystoreEntriesList().size());
        // when
        testInstance.importKeys(keystore, "NewPassword1234");
        // then
        assertEquals(3,testInstance.getKeystoreEntriesList().size());
    }


    @Test
    public void testDeleteKeyTestCertificate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        assertNotNull( testInstance.getCert(S_ALIAS));
        assertNotNull( testInstance.getKey(S_ALIAS));

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("Wrong configuration, missing key pair from keystore or wrong alias: "+S_ALIAS);
        //when
        testInstance.deleteKey(S_ALIAS);

        // then
        assertEquals(2, testInstance.getKeystoreEntriesList().size());
        assertNull( testInstance.getCert(S_ALIAS));
    }

    @Test
    public void testDeleteKeyTestKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        assertEquals(3, testInstance.getKeystoreEntriesList().size());
        assertNotNull( testInstance.getCert(S_ALIAS));
        assertNotNull( testInstance.getKey(S_ALIAS));

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("Wrong configuration, missing key pair from keystore or wrong alias: "+S_ALIAS);
        //when
        testInstance.deleteKey(S_ALIAS);

        // then
        assertEquals(2, testInstance.getKeystoreEntriesList().size());
        assertNull( testInstance.getKey(S_ALIAS));
    }


    private KeyStore loadKeystore(String keystoreName, String password, String type) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        // Load the KeyStore and get the signing key and certificate.
        File keystoreFilePath = new File(resourceDirectory.toFile(),  keystoreName);

        KeyStore keyStore = null;
        try (InputStream keystoreInputStream = new FileInputStream(keystoreFilePath)) {
            keyStore = KeyStore.getInstance(type);
            keyStore.load(keystoreInputStream, password.toCharArray());
        }
        return keyStore;
    }

    @Test
    public void testDetectKeystoreChangeForEntryList() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");
        assertEquals(3,testInstance.getKeystoreEntriesList().size());

        // when
        propertiesKeystoreTestConfig.resetKeystore();
        // then
        assertEquals(1, testInstance.getKeystoreEntriesList().size());

    }

    @Test
    public void testDetectKeystoreChangeForKeyManagers() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        // given
        KeyManager km = testInstance.getKeyManagers()[0];
        testInstance.importKeys(loadKeystore("test-import.jks","NewPassword1234", "JKS" ), "NewPassword1234");

        // keymanager is updated
        assertNotEquals(km, testInstance.getKeyManagers()[0]);
        km = testInstance.getKeyManagers()[0];

        // when just changing the file
        propertiesKeystoreTestConfig.resetKeystore();
        // then 
        assertNotEquals(km, testInstance.getKeyManagers()[0]);
    }


}