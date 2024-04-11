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
package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationDAOImplTest extends AbstractBaseDao {


    @Autowired
    private ConfigurationDao configurationDao;

    @BeforeEach
    public void before() throws IOException {
        resetKeystore();

        // make sure sql properties are loaded
        configurationDao.reloadPropertiesFromDatabase();
    }

    @Test
    void testGetSecurityFolder() {
        File folder = configurationDao.getSecurityFolder();

        assertEquals("target/smp", folder.getPath());
    }

    @Test
    void testFindConfigurationPropertyNotFound() {
        //GIVE - WHEN
        Optional<DBConfiguration> resultBO = configurationDao.findConfigurationProperty("NotFoundProperty");

        //THEN
        assertFalse(resultBO.isPresent());
    }

    @Test
    void testLastUpdateOK() {
        //GIVE - WHEN
        OffsetDateTime lastUpdate = configurationDao.getLastUpdate();

        //THEN
        assertNotNull(lastUpdate);
        assertTrue(OffsetDateTime.now().isAfter(lastUpdate));
    }

    @Test
    void testSetNewProperty() {
        // given
        OffsetDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "TestUser";
        String propertyDesc = "Test description";
        //WHEN
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_USER,
                propertyValue, propertyDesc);

        //THEN
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.HTTP_PROXY_USER);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue, configuration.get().getValue());
        assertEquals(propertyDesc, configuration.get().getDescription());
        assertTrue(lastUpdate.isBefore(configurationDao.getLastUpdate()));
    }

    @Test
    void testSetPropertyByStringOk() {
        // given
        OffsetDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "localhost";

        //WHEN
        DBConfiguration result = configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_NO_PROXY_HOSTS.getProperty(), propertyValue);

        //THEN
        assertNotNull(result);
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.HTTP_NO_PROXY_HOSTS);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue, configuration.get().getValue());
        assertTrue(lastUpdate.isBefore(configurationDao.getLastUpdate()));
    }

    @Test
    void testSetPropertyByStringNotExists() {
        // given
        OffsetDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "localhost";

        //WHEN
        DBConfiguration result = configurationDao.setPropertyToDatabase("NotExistingProperty", propertyValue);

        //THEN
        assertNull(result);
    }

    @Test
    void testUpdatePropertyInvalid() {
        //WHEN
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_FORWARDED_HEADERS_ENABLED,
                        "ThisIsNotValidBoolean", null));

        //THEN
        assertNotNull(result);
        assertEquals(ErrorCode.CONFIGURATION_ERROR, result.getErrorCode());
    }

    @Test
    void testUpdateProperty() {
        // given
        OffsetDateTime lastUpdate = configurationDao.getLastUpdate();
        String propertyValue = "TestUser";
        String propertyValue2 = "TestUser2";
        String propertyDesc = "Test description";
        String propertyDesc2 = "Test description2";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_USER,
                propertyValue, propertyDesc);

        // when
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_USER,
                propertyValue2, propertyDesc2);
        //then
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.HTTP_PROXY_USER);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue2, configuration.get().getValue());
        assertEquals(propertyDesc2, configuration.get().getDescription());
        assertTrue(lastUpdate.isBefore(configurationDao.getLastUpdate()));
    }

    @Test
    void testDeleteProperty() {
        // given
        String propertyValue = "TestUser";
        String propertyDesc = "Test description";
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_USER,
                propertyValue, propertyDesc);
        Optional<DBConfiguration> configuration = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.HTTP_PROXY_USER);
        assertTrue(configuration.isPresent());
        assertEquals(propertyValue, configuration.get().getValue());
        // when
        configurationDao.deletePropertyFromDatabase(SMPPropertyEnum.HTTP_PROXY_USER);


        //then
        Optional<DBConfiguration> result = configurationDao.getConfigurationEntityFromDatabase(SMPPropertyEnum.HTTP_PROXY_USER);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCachedProperty() {
        String value = configurationDao.getCachedProperty(SMPPropertyEnum.ALERT_ACCESS_TOKEN_EXPIRED_PERIOD);

        assertEquals("30", value);
    }

    @Test
    void testGetCachedPropertyValue() {
        Object objPath = configurationDao.getCachedPropertyValue(SMPPropertyEnum.ALERT_ACCESS_TOKEN_EXPIRED_PERIOD);

        assertNotNull(objPath);
        assertEquals(Integer.class, objPath.getClass());
    }

    @Test
    void testReloadPropertiesFromDatabase() {

        // give
        configurationDao.setPropertyToDatabase(SMP_CLUSTER_ENABLED, "true", null);

        String testValue = configurationDao.getCachedProperty(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN);
        Object objValue = configurationDao.getCachedPropertyValue(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN);
        OffsetDateTime localDateTime = configurationDao.getLastUpdate();
        // set new value
        String pathNew = "123456";
        assertNotEquals(testValue, pathNew);
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN, pathNew, "New value");
        // assert value is not yet changed
        assertEquals(testValue, configurationDao.getCachedProperty(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN));

        // when
        configurationDao.reloadPropertiesFromDatabase();

        // then
        assertEquals(pathNew, configurationDao.getCachedProperty(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN));
        assertTrue(localDateTime.isBefore(configurationDao.getLastUpdate()));
        assertNotEquals(objValue, configurationDao.getCachedPropertyValue(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN));
    }


    @Test
    void testRefreshPropertiesWithReload() {

        // give
        String testValue = configurationDao.getCachedProperty(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN);
        Object objValue = configurationDao.getCachedPropertyValue(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN);
        OffsetDateTime localDateTime = configurationDao.getLastUpdate();
        // set new value
        String pathNew = "123455";
        assertNotEquals(testValue, pathNew);
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN, pathNew, "New value");
        // assert value is not yet changed
        assertEquals(testValue, configurationDao.getCachedProperty(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN));


        // when
        ReflectionTestUtils.setField(configurationDao, "lastUpdate", null);
        configurationDao.refreshProperties();

        // then
        assertEquals(pathNew, configurationDao.getCachedProperty(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN));
        assertTrue(localDateTime.isBefore(configurationDao.getLastUpdate()));
        assertNotEquals(objValue, configurationDao.getCachedPropertyValue(SMPPropertyEnum.UI_COOKIE_SESSION_IDLE_TIMEOUT_ADMIN));
    }

    @Test
    void testRefreshEncryptProperties() {

        // give
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "}";

        updateOrCreatePropertyToDB(KEYSTORE_PASSWORD, newDBTestPassword);
        updateOrCreatePropertyToDB(TRUSTSTORE_PASSWORD, newDBTestPassword);
        updateOrCreatePropertyToDB(HTTP_PROXY_PASSWORD, newDBTestPassword);
        // when
        configurationDao.refreshAndUpdateProperties();
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
        File encryptionKey = configurationDao.getCachedPropertyValue(SMPPropertyEnum.ENCRYPTION_FILENAME);
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, dbKeystorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.TRUSTSTORE_PASSWORD, dbTruststorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.HTTP_PROXY_PASSWORD, dbProxyPassword, encryptionKey));
    }

    @Test
    void encryptDefault() throws IOException {
        // given
        File f = generateRandomPrivateKey();
        String password = "TEST11002password1@!." + System.currentTimeMillis();

        // when
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, password, f);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
    }

    @Test
    void encryptDefaultError() {
        // given
        File f = new File("no.key");
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> configurationDao.encryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, password, f));
        //then
        assertNotNull(result);
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Error occurred while encrypting the property:"));
    }

    @Test
    void decryptDefault() throws IOException {
        // given
        File f = generateRandomPrivateKey();
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, password, f);

        // when
        String decPassword = configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, encPassword, f);
        //then
        assertNotNull(decPassword);
        assertEquals(password, decPassword);
    }

    @Test
    void decryptDefaultError() throws IOException {
        // given
        File f = generateRandomPrivateKey();
        File fErr = new File("no.key");
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        String encPassword = configurationDao.encryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, password, f);

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, encPassword, fErr));
        //then
        assertNotNull(result);
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Error occurred while decrypting the property:"));
    }

    @Test
    void encryptWithSetupKeyWithoutIV() {
        // given
        File keyFile = resourceDirectory.resolve("encryptionKey.key").toFile();
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
    void encryptWithSetupKeyWitIV() {
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
    void testRetrieveNonEncryptedPassword() {
        // given
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = SecurityUtils.DECRYPTED_TOKEN_PREFIX + newTestPassword + "}";
        updateOrCreatePropertyToDB(KEYSTORE_PASSWORD, newDBTestPassword);
        updateOrCreatePropertyToDB(TRUSTSTORE_PASSWORD, newDBTestPassword);
        updateOrCreatePropertyToDB(HTTP_PROXY_PASSWORD, newDBTestPassword);

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
    void testGetNonEncryptedValue() {
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
    void testUpdateEncryptedValues() {
        // given
        String newTestPassword = UUID.randomUUID().toString();
        String newDBTestPassword = newTestPassword;
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.KEYSTORE_PASSWORD,
                newDBTestPassword, "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.TRUSTSTORE_PASSWORD,
                newDBTestPassword, "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.HTTP_PROXY_PASSWORD,
                newDBTestPassword, "");

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
        File encryptionKey = configurationDao.getCachedPropertyValue(SMPPropertyEnum.ENCRYPTION_FILENAME);
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.KEYSTORE_PASSWORD, dbKeystorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.TRUSTSTORE_PASSWORD, dbTruststorePassword, encryptionKey));
        assertEquals(newTestPassword, configurationDao.decryptString(SMPPropertyEnum.HTTP_PROXY_PASSWORD, dbProxyPassword, encryptionKey));
    }

    @Test
    void testContextRefreshedEvent() {

        configurationDao.setInitializedTime(null);
        assertFalse(configurationDao.isApplicationInitialized());

        // when
        configurationDao.contextRefreshedEvent();
        // then
        assertTrue(configurationDao.isApplicationInitialized());
        assertNotNull(configurationDao.getInitiateDate());
    }


    @Test
    void testContextStopEvent() {
        configurationDao.setInitializedTime(OffsetDateTime.now());
        // when
        configurationDao.contextStopEvent();
        // then
        assertFalse(configurationDao.isApplicationInitialized());
        assertNull(configurationDao.getInitiateDate());
    }

    @Test
    void testGetPendingRestartProperties() {
        // set start  "yesterday" - but all properties have update today!
        configurationDao.setInitializedTime(OffsetDateTime.now().minusDays(1));
        // when
        List<DBConfiguration> restartProp = configurationDao.getPendingRestartProperties();
        // then
        assertFalse(restartProp.isEmpty());
    }

    @Test
    void testUpdateListener() {

        configurationDao.contextRefreshedEvent();
        PropertyUpdateListener listener = Mockito.mock(PropertyUpdateListener.class);
        Mockito.doReturn(Collections.singletonList(SMP_ALERT_BATCH_SIZE)).when(listener).handledProperties();
        Mockito.doNothing().when(listener).updateProperties(Mockito.anyMap());
        ArgumentCaptor<Map<SMPPropertyEnum, Object>> argCaptor = ArgumentCaptor.forClass(Map.class);
        configurationDao.updateListener("testListener", listener);
        // when


        Mockito.verify(listener, Mockito.times(1)).updateProperties(argCaptor.capture());
        assertEquals(1, argCaptor.getValue().size());
        assertTrue(argCaptor.getValue().containsKey(SMP_ALERT_BATCH_SIZE));
    }

    public void updateOrCreatePropertyToDB(SMPPropertyEnum propertyEnum, String value) {
        Optional<DBConfiguration> prop = configurationDao.findConfigurationProperty(propertyEnum.getProperty());
        DBConfiguration dbProp;
        if (prop.isPresent()) {
            dbProp = prop.get();
        } else {
            dbProp = new DBConfiguration();
            dbProp.setProperty(propertyEnum.getProperty());
            dbProp.setDescription(propertyEnum.getDesc());
        }
        dbProp.setValue(value);
        configurationDao.update(dbProp);
    }

    public static File generateRandomPrivateKey() {
        File resource = Paths.get("target", UUID.randomUUID() + ".key").toFile();
        SecurityUtils.generatePrivateSymmetricKey(resource, true);
        return resource;

    }
}
