package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.h2.engine.DbSettings;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.net.ssl.KeyManagerFactory;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class PropertiesConfigTest {

    SecurityUtilsServices  securityUtilsServices = new SecurityUtilsServices();

    PropertiesConfig testInstance = new PropertiesConfig();


    @Test
    public void calculateSettingsPathSMLKeystore() {
        // given
        Properties p = new Properties();
        p.setProperty(SMPPropertyEnum.SML_KEYSTORE_PATH.getProperty(), "testSMLFolder/keystore.jks");
        // when
        File f = testInstance.calculateSettingsPath(p);
        // then
        assertEquals("testSMLFolder", f.getName());
    }

    @Test
    public void calculateSettingsPathKeystore() {
        // given
        Properties p = new Properties();
        p.setProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH.getProperty(), "testFolder/keystore.jks");
        p.setProperty(SMPPropertyEnum.SML_KEYSTORE_PATH.getProperty(), "testSMLFolder/keystore.jks");
        // when
        File f = testInstance.calculateSettingsPath(p);
        // then
        assertEquals("testFolder", f.getName());
    }

    @Test
    public void calculateSettingsPathKeystoreNoValue() {
        // given
        Properties p = new Properties();
        // when
        File f = testInstance.calculateSettingsPath(p);
        // then
        assertEquals((new File("")).getAbsolutePath(), f.getAbsolutePath());
    }

    @Test
    public void initNewValues() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
    // copy folder
        Path sourceFile = Paths.get("src", "test", "resources",  "keystores", "smp-keystore_multiple_domains.jks");
        Path targetFile = Paths.get("target","keystores","test-init-prop.jks");
        FileUtils.copyFile(sourceFile.toFile(), targetFile.toFile());

        EntityManager em = Mockito.mock(EntityManager.class);
        Properties fileSettings = new Properties();
        fileSettings.setProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH.getProperty(), targetFile.toFile().getAbsolutePath());
        fileSettings.setProperty(SMPPropertyEnum.SIGNATURE_KEYSTORE_PASSWORD.getProperty(),"test123");
        Properties dbSettings = new Properties();
        testInstance.initNewValues(em, fileSettings, dbSettings);

        assertEquals(4, dbSettings.size());
        Mockito.verify(em, times(5)).persist(any()); // five times - save also non encrypted message

        assertTrue( dbSettings.containsKey(SMPPropertyEnum.ENCRYPTION_FILENAME.getProperty()));
        assertTrue( dbSettings.containsKey(SMPPropertyEnum.CONFIGURATION_DIR.getProperty()));
        assertTrue( dbSettings.containsKey(SMPPropertyEnum.KEYSTORE_PASSWORD.getProperty()));
        assertTrue( dbSettings.containsKey(SMPPropertyEnum.KEYSTORE_FILENAME.getProperty()));
        String passEnc = dbSettings.getProperty(SMPPropertyEnum.KEYSTORE_PASSWORD.getProperty());
        String confDir = dbSettings.getProperty(SMPPropertyEnum.CONFIGURATION_DIR.getProperty());
        String encFilePath = confDir+ File.separator  + dbSettings.getProperty(SMPPropertyEnum.ENCRYPTION_FILENAME.getProperty());
        String keystoreFilePath = confDir+ File.separator  +dbSettings.getProperty(SMPPropertyEnum.KEYSTORE_FILENAME.getProperty());
        File encFile = new File( encFilePath);
        File keystoreFile = new File( keystoreFilePath);
        assertTrue(encFile.exists());
        assertTrue(keystoreFile.exists());

        String passwd = securityUtilsServices.decrypt(encFile, passEnc);
        assertNotNull(passwd);
        // load keystore
        KeyStore keyStore = null;
        try (InputStream keystoreInputStream = new FileInputStream(keystoreFile)) {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInputStream, passwd.toCharArray());
        }
        assertTrue(keyStore.size()>0);
    }

    @Test
    public void createDBEntry() {
        // given
        DBConfiguration entry = testInstance.createDBEntry("key", "value", "desc");
        // then
        assertEquals("key", entry.getProperty());
        assertEquals("value", entry.getValue());
        assertEquals("desc", entry.getDescription());
    }


    @Test
    public void createDBEntryProperty() {
        // given
        DBConfiguration entry = testInstance.createDBEntry(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH, "value");
        // then
        assertEquals(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH.getProperty(), entry.getProperty());
        assertEquals("value", entry.getValue());
        assertEquals(SMPPropertyEnum.SIGNATURE_KEYSTORE_PATH.getDesc(), entry.getDescription());
    }

}