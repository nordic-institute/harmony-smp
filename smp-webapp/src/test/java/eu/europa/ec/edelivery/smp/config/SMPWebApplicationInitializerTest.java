/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.utils.ExtLibraryClassLoader;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SMPWebApplicationInitializerTest {

    SMPWebApplicationInitializer testInstance = new SMPWebApplicationInitializer();

    @Test
    public void logBuildProperties() {
        Logger log = Mockito.mock(Logger.class);
        testInstance.logBuildProperties(log, "/test-application.properties");

        ArgumentCaptor<String> template = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> version = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> date = ArgumentCaptor.forClass(String.class);

        Mockito.verify(log, Mockito.times(2)).info(Mockito.anyString());
        Mockito.verify(log, Mockito.times(1)).info(template.capture(),
                name.capture(),
                version.capture(),
                date.capture() );

        assertEquals(SMPWebApplicationInitializer.VERSION_LOG_TEMPLATE, template.getValue());
        assertEquals("eDelivery test SMP", name.getValue());
        assertEquals("1.0", version.getValue());
        assertEquals("2022-05-05T12:00:00Z", date.getValue());
    }

    @Test
    public void createLibraryClassLoaderNotExists() {
        ExtLibraryClassLoader loader= testInstance.createLibraryClassLoader(new File("FileNotExists"));
        assertNull(loader);
    }

    @Test
    public void createLibraryClassLoaderIsNotFolder() {
        ExtLibraryClassLoader loader= testInstance.createLibraryClassLoader(new File("./pom.xml"));
        assertNull(loader);
    }

    @Test
    public void createLibraryClassLoader() {
        // folder contains one library jar simple-extension.jar with the resource logback-test.xml
        Path path = Paths.get("src","test","resources", "test-libs");
        ExtLibraryClassLoader loader= testInstance.createLibraryClassLoader(path.toFile());
        assertNotNull(loader);
        assertNotNull(loader.getResource("logback-test.xml"));
    }
}
