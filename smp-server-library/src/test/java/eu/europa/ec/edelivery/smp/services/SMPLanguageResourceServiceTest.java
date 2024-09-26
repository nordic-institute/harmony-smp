/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static eu.europa.ec.edelivery.smp.services.SMPLanguageResourceService.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 5.1
 * @author Joze RIHTARSIC
 */
class SMPLanguageResourceServiceTest {
    File localeFolder = new File("target/locales");
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    SMPLanguageResourceService testInstance = new SMPLanguageResourceService(configurationService, resourcePatternResolver);

    @BeforeEach
    void setUp() throws IOException {
        // clean the folder
        if (localeFolder.exists() ) {
            // first delete all files and then empty folders
            Files.walk(localeFolder.toPath())
                    .map( Path::toFile )
                    .sorted( Comparator.comparing( File::isDirectory ) )
                    .forEach( File::delete );
        }
        Mockito.when(configurationService.getLocaleFolder()).thenReturn(localeFolder);
    }

    @Test
    void testMergeAddMissingProperty() {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("key1", "value1");
        targetNode.put("key2", "value2");

        ObjectNode referenceNode = objectMapper.createObjectNode();
        referenceNode.put("key1", "value-001");
        referenceNode.put("key2", "value-002");
        referenceNode.put("key3", "value-003");

        // when
        boolean result = mergeTranslationJson(targetNode, referenceNode);

        // then
        assertTrue(result);
        assertEquals("value1", targetNode.get("key1").asText());
        assertEquals("value2", targetNode.get("key2").asText());
        assertEquals("value-003", targetNode.get("key3").asText());
    }

    @Test
    void testMergeAddMissingProperty2() {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("key1", "value1");
        targetNode.put("key2", "");

        ObjectNode referenceNode = objectMapper.createObjectNode();
        referenceNode.put("key1", "value-001");
        referenceNode.put("key2", "value-002");

        // when
        boolean result = mergeTranslationJson(targetNode, referenceNode);

        // then
        assertFalse(result);
        assertEquals("value1", targetNode.get("key1").asText());
        assertEquals("", targetNode.get("key2").asText());
        assertFalse(targetNode.has("key3"));
    }

    @Test
    void updateLocalesOnDiskCleanFolder() {
        // given
        assertFalse(localeFolder.exists());
        // when
        testInstance.updateLocalesOnDisk();
        // then
        assertTrue(localeFolder.exists());
        File[] files = localeFolder.listFiles();
        assertNotNull(files);
        assertEquals(2, files.length);
        assertEquals(LANGUAGE_FILENAME_UI_PREFIX + "en.json", files[0].getName());
        assertEquals(LANGUAGE_FILENAME_MAIL_PREFIX + "en.json", files[1].getName());
    }

    @Test
    void updateLocalesOnDiskOverwrite() throws IOException {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        localeFolder.mkdirs();

        String testText = "This must NOT be overwritten";
        String testKey = "column.selection.link.all";
        Path pathToFile = Paths.get(localeFolder.getAbsolutePath(), LANGUAGE_FILENAME_UI_PREFIX + "en.json");
        assertFalse(pathToFile.toFile().exists());
        // add one property
        ObjectNode testNode = objectMapper.createObjectNode();
        testNode.put(testKey, testText);
        objectMapper.writeValue(pathToFile.toFile(), testNode);
        assertTrue(pathToFile.toFile().exists());

        // when
        testInstance.updateLocalesOnDisk();
        // then
        assertTrue(localeFolder.exists());
        File[] files = localeFolder.listFiles();
        assertNotNull(files);
        assertEquals(2, files.length);
        assertEquals(LANGUAGE_FILENAME_UI_PREFIX + "en.json", files[0].getName());
        assertEquals(LANGUAGE_FILENAME_MAIL_PREFIX + "en.json", files[1].getName());

        JsonNode result = objectMapper.readTree(pathToFile.toFile());
        assertEquals(testText, result.get(testKey).asText());
        // 3 properties are added by the updateLocalesOnDisk method
        // from the classpath resource META-INF/resources/ui/assets/i18n/en.json
        assertEquals(4, result.size());
    }
}
