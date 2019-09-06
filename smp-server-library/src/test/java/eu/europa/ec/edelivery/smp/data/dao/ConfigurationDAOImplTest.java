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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigurationDAOImplTest extends AbstractBaseDao {


    @Autowired
    private ConfigurationDao configurationDao;

    @Captor
    ArgumentCaptor<Properties> captorOfProperties;

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
        configurationDao.refreshProperties();

        // then
        assertEquals(pathNew, configurationDao.getCachedProperty(SMPPropertyEnum.CONFIGURATION_DIR));
        assertTrue(localDateTime.isBefore(configurationDao.getLastUpdate()));
        assertNotEquals(objPath, configurationDao.getCachedPropertyValue(SMPPropertyEnum.CONFIGURATION_DIR));
    }

}
