/**
 * (C) Copyright 2018 - European Commission | CEF eDelivery
 * <p>
 * Licensed under the EUPL, Version 1.2 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * \BDMSL\bdmsl-parent-pom\LICENSE-EUPL-v1.2.pdf or https://joinup.ec.europa.eu/sites/default/files/custom-page/attachment/eupl_v1.2_en.pdf
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * * @author Flávio W. R. Santos - CEF-EDELIVERY-SUPPORT@ec.europa.eu
 **/
package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.utils.SecurityUtilsTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.*;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.HTTP_PROXY_USER;
import static org.junit.Assert.*;

public class ConfigurationDAOImplTest extends AbstractBaseDao {


    @Autowired
    private ConfigurationDao configurationDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void before() throws IOException {
        resetKeystore();
        // make sure sql properties are loaded
        configurationDao.reloadPropertiesFromDatabase();
    }

    @Test
    public void testFindConfigurationProperty() {
        Optional<DBConfiguration> resultBO = configurationDao.findConfigurationProperty(SMPPropertyEnum.CONFIGURATION_DIR.getProperty());

        //THEN
        assertTrue(resultBO.isPresent());
        assertEquals("./target/keystores/", resultBO.get().getValue());
    }

    @Test
    public void testFindConfigurationPropertyNotFound() {
        //GIVE - WHEN
        Optional<DBConfiguration> resultBO = configurationDao.findConfigurationProperty("NotFoundProperty");

        //THEN
        assertFalse(resultBO.isPresent());
    }

    @Test
    public void testLastUpdateOK() {
        //GIVE - WHEN
        LocalDateTime lastUpdate = configurationDao.getLastUpdate();

        //THEN
        assertNotNull(lastUpdate);
        assertTrue(LocalDateTime.now().isAfter(lastUpdate));
    }

    @Test
    public void testSetNewProperty() {
        // given
        LocalDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "TestUser";
        String propertyDesc = "Test description";
        //WHEN
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.SML_PROXY_USER,
                propertyValue, propertyDesc);

        //THEN
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.SML_PROXY_USER);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue, configuration.get().getValue());
        assertEquals(propertyDesc, configuration.get().getDescription());
        assertTrue(lastUpdate.isBefore(configurationDao.getLastUpdate()));
    }

    @Test
    public void testUpdateProperty() {
        // given
        LocalDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "TestUser";
        String propertyValue2 = "TestUser2";
        String propertyDesc = "Test description";
        String propertyDesc2 = "Test description2";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.SML_PROXY_USER,
                propertyValue, propertyDesc);

        // when
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.SML_PROXY_USER,
                propertyValue2, propertyDesc2);
        //then
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.SML_PROXY_USER);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue2, configuration.get().getValue());
        assertEquals(propertyDesc2, configuration.get().getDescription());
        assertTrue(lastUpdate.isBefore(configurationDao.getLastUpdate()));
    }

    @Test

    public void testDeleteProperty() {
        // given
        LocalDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "TestUser";
        String propertyDesc = "Test description";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.SML_PROXY_USER,
                propertyValue, propertyDesc);
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.SML_PROXY_USER);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue, configuration.get().getValue());
        // when
        configurationDao.deletePropertyFromDatabase(SMPPropertyEnum.SML_PROXY_USER);


        //then
        Optional<DBConfiguration> result = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.SML_PROXY_USER);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetCachedProperty() {
        String path = configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR);

        assertEquals("./target/keystores/", path);
    }

    @Test
    public void testGetCachedPropertyValue() {
        Object objPath = configurationDao.getCachedPropertyValue(SMPPropertyEnum.CONFIGURATION_DIR);

        assertNotNull(objPath);
        assertEquals(File.class, objPath.getClass());
    }

    @Test
    public void testReloadPropertiesFromDatabase() {

        // give
        String path = configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR);
        Object objPath = configurationDao.getCachedPropertyValue(SMPPropertyEnum.CONFIGURATION_DIR);
        LocalDateTime localDateTime = configurationDao.getLastUpdate();
        // set new value
        String pathNew = Paths.get("src", "test", "resources", "keystores").toFile().getAbsolutePath();
        assertNotEquals(path, pathNew);
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.CONFIGURATION_DIR, pathNew, "New configuration path");
        // assert value is not yet changed
        assertEquals(path, configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR));

        // when
        configurationDao.reloadPropertiesFromDatabase();

        // then
        assertEquals(pathNew, configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR));
        assertTrue(localDateTime.isBefore(configurationDao.getLastUpdate()));
        assertNotEquals(objPath, configurationDao.getCachedPropertyValue(SMPPropertyEnum.CONFIGURATION_DIR));
    }


    @Test
    public void testRefreshPropertiesWithReload() {

        // give
        String path = configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR);
        Object objPath = configurationDao.getCachedPropertyValue(SMPPropertyEnum.CONFIGURATION_DIR);
        LocalDateTime localDateTime = configurationDao.getLastUpdate();
        // set new value
        String pathNew = Paths.get("src", "test", "resources", "keystores").toFile().getAbsolutePath();
        assertNotEquals(path, pathNew);
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.CONFIGURATION_DIR, pathNew, "New configuration path");
        // assert value is not yet changed
        assertEquals(path, configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR));


        // when
        ReflectionTestUtils.setField(configurationDao, "lastUpdate", null);
        configurationDao.refreshProperties();

        // then
        assertEquals(pathNew, configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR));
        assertTrue(localDateTime.isBefore(configurationDao.getLastUpdate()));
        assertNotEquals(objPath, configurationDao.getCachedPropertyValue(SMPPropertyEnum.CONFIGURATION_DIR));
    }

    @Test
    public void testRefreshEncryptProperties() {

        // give
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "}";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.KEYSTORE_PASSWORD,
                newDBTestPassword + "", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.TRUSTSTORE_PASSWORD,
                newDBTestPassword + "", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_PASSWORD,
                newDBTestPassword + "", "");

        // when
        configurationDao.refreshProperties();
        // read properties again from database!
        configurationDao.reloadPropertiesFromDatabase();

        // then
        String dbKeystorePassword = configurationDao.getCachedProperty(SMPPropertyEnum.KEYSTORE_PASSWORD);
        String dbTruststorePassword = configurationDao.getCachedProperty(SMPPropertyEnum.TRUSTSTORE_PASSWORD);
        String dbProxyPassword = configurationDao.getCachedProperty(SMPPropertyEnum.HTTP_PROXY_PASSWORD);
        // then
        assertNotEquals(newDBTestPassword, dbKeystorePassword);
        assertNotEquals(newDBTestPassword, dbTruststorePassword);
        assertNotEquals(newDBTestPassword, dbProxyPassword);

        // value is the actual password
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.KEYSTORE_PASSWORD));
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.TRUSTSTORE_PASSWORD));
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.HTTP_PROXY_PASSWORD));

        // test decrypt
        File encryptionKey = (File) configurationDao.getCachedPropertyValue(SMPPropertyEnum.ENCRYPTION_FILENAME);
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, dbKeystorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.TRUSTSTORE_PASSWORD, dbTruststorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.HTTP_PROXY_PASSWORD, dbProxyPassword, encryptionKey));
    }

    @Test
    public void encryptDefault() throws IOException {
        // given
        File f = SecurityUtilsTest.generateRandomPrivateKey();
        String password = "TEST11002password1@!." + System.currentTimeMillis();

        // when
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, password, f);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
    }

    @Test
    public void encryptDefaultError() throws IOException {
        // given
        File f = new File("no.key");
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        // then
        expectedException.expect(SMPRuntimeException.class);
        expectedException.expectMessage("Error occurred while encrypting the property:");

        // when
        configurationDao.encryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, password, f);
    }

    @Test
    public void decryptDefault() throws IOException {
        // given
        File f = SecurityUtilsTest.generateRandomPrivateKey();
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, password, f);

        // when
        String decPassword = configurationDao.decryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, encPassword, f);
        //then
        assertNotNull(decPassword);
        assertEquals(password, decPassword);
    }

    @Test
    public void decryptDefaultError() throws IOException {
        // given
        File f = SecurityUtilsTest.generateRandomPrivateKey();
        File fErr = new File("no.key");
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, password, f);

        // then
        expectedException.expect(SMPRuntimeException.class);
        expectedException.expectMessage("Error occurred while decrypting the property:");
        // when
        configurationDao.decryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, encPassword, fErr);

    }

    @Test
    public void encryptWithSetupKeyWithoutIV() {
        // given
        File keyFile = resourceDirectory.resolve("encryptionKey.key").toFile();
        String password = "test123";

        // when
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, password, keyFile);
        String decPassword = configurationDao.decryptString(SMPPropertyEnum.SML_KEYSTORE_PASSWORD, encPassword, keyFile);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
        assertEquals(password, decPassword);
    }

    @Test
    public void encryptWithSetupKeyWitIV() {
        // given
        File keyFile = resourceDirectory.resolve("masterKeyWithIV.key").toFile();
        String password = "test123";

        // when
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, password, keyFile);
        String decPassword = configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, encPassword, keyFile);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
        assertEquals(password, decPassword);
    }


    @Test
    public void testRetrieveNonEncryptedPassword() {
        // given
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "}";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.KEYSTORE_PASSWORD,
                newDBTestPassword + "", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.TRUSTSTORE_PASSWORD,
                newDBTestPassword + "", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_PASSWORD,
                newDBTestPassword + "", "");


        configurationDao.reloadPropertiesFromDatabase();

        assertEquals(newDBTestPassword, configurationDao.getCachedProperty(SMPPropertyEnum.KEYSTORE_PASSWORD));
        assertEquals(newDBTestPassword, configurationDao.getCachedProperty(SMPPropertyEnum.TRUSTSTORE_PASSWORD));
        assertEquals(newDBTestPassword, configurationDao.getCachedProperty(SMPPropertyEnum.HTTP_PROXY_PASSWORD));
        // value is the actual password
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.KEYSTORE_PASSWORD));
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.TRUSTSTORE_PASSWORD));
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.HTTP_PROXY_PASSWORD));
    }

    @Test
    public void testGetNonEncryptedValue() {
        // given
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "}";
        String newDBTestPassword2 = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "} ";
        // when
        String value = SecurityUtils.getNonEncryptedValue(newDBTestPassword);
        String value2 = SecurityUtils.getNonEncryptedValue(newDBTestPassword2);

        assertEquals(newTestPassword, value);
        assertEquals(newTestPassword, value2);
    }

    @Test
    @Transactional
    public void testUpdateEncryptedValues() {
        // given
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "}";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.KEYSTORE_PASSWORD,
                newDBTestPassword + "", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.TRUSTSTORE_PASSWORD,
                newDBTestPassword + "", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_PASSWORD,
                newDBTestPassword + "", "");

        configurationDao.reloadPropertiesFromDatabase();

        // when
        configurationDao.updateCurrentEncryptedValues();
        configurationDao.reloadPropertiesFromDatabase();

        String dbKeystorePassword = configurationDao.getCachedProperty(SMPPropertyEnum.KEYSTORE_PASSWORD);
        String dbTruststorePassword = configurationDao.getCachedProperty(SMPPropertyEnum.TRUSTSTORE_PASSWORD);
        String dbProxyPassword = configurationDao.getCachedProperty(SMPPropertyEnum.HTTP_PROXY_PASSWORD);
        // then
        assertNotEquals(newDBTestPassword, dbKeystorePassword);
        assertNotEquals(newDBTestPassword, dbTruststorePassword);
        assertNotEquals(newDBTestPassword, dbProxyPassword);

        // value is the actual password
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.KEYSTORE_PASSWORD));
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.TRUSTSTORE_PASSWORD));
        assertEquals(newTestPassword, configurationDao.getCachedPropertyValue(SMPPropertyEnum.HTTP_PROXY_PASSWORD));

        // test decrypt
        File encryptionKey = (File) configurationDao.getCachedPropertyValue(SMPPropertyEnum.ENCRYPTION_FILENAME);
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, dbKeystorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.TRUSTSTORE_PASSWORD, dbTruststorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.HTTP_PROXY_PASSWORD, dbProxyPassword, encryptionKey));

    }

    @Test
    public void testUpdateDeprecatedValues() {
        // given
        Properties properties = new Properties();
        properties.setProperty(SML_PROXY_HOST.getProperty(), UUID.randomUUID().toString());
        properties.setProperty(SML_PROXY_PORT.getProperty(), UUID.randomUUID().toString());
        properties.setProperty(SML_PROXY_USER.getProperty(), UUID.randomUUID().toString());
        properties.setProperty(SML_PROXY_PASSWORD.getProperty(), UUID.randomUUID().toString());

        //when
        configurationDao.updateDeprecatedValues(properties);

        // then
        assertEquals(properties.getProperty(HTTP_PROXY_HOST.getProperty()), properties.getProperty(SML_PROXY_HOST.getProperty()));
        assertEquals(properties.getProperty(HTTP_PROXY_PORT.getProperty()), properties.getProperty(SML_PROXY_PORT.getProperty()));
        assertEquals(properties.getProperty(HTTP_PROXY_USER.getProperty()), properties.getProperty(SML_PROXY_USER.getProperty()));
        assertEquals(properties.getProperty(HTTP_PROXY_PASSWORD.getProperty()), properties.getProperty(SML_PROXY_PASSWORD.getProperty()));
    }
}